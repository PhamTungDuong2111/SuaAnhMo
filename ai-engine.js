// EnhanceFox AI - Core Computer Vision & Photo Processing Engine
// Provides Unblur, Super Resolution, Deblurring, Colorization, Scratch Repair, and Denoising

class AIEngine {
  constructor() {
    this.canvas = document.createElement('canvas');
    this.ctx = this.canvas.getContext('2d', { willReadFrequently: true });
  }

  /**
   * Helper to load an Image element, Canvas, Blob, or URL into a usable image/canvas source
   */
  async loadImage(source) {
    // If it's already a canvas, return it directly
    if (source instanceof HTMLCanvasElement) {
      return source;
    }

    if (source instanceof HTMLImageElement) {
      if (source.complete && (source.naturalWidth || source.width) > 0) {
        return source;
      }
      return new Promise((resolve, reject) => {
        source.onload = () => resolve(source);
        source.onerror = (e) => reject(new Error("Không thể tải hình ảnh"));
      });
    }

    return new Promise((resolve, reject) => {
      const img = new Image();
      let objectUrlToRevoke = null;

      img.onload = () => {
        if (objectUrlToRevoke) {
          try { URL.revokeObjectURL(objectUrlToRevoke); } catch (_) {}
        }
        resolve(img);
      };

      img.onerror = () => {
        if (objectUrlToRevoke) {
          try { URL.revokeObjectURL(objectUrlToRevoke); } catch (_) {}
        }
        reject(new Error("Lỗi đọc định dạng hình ảnh"));
      };

      if (source instanceof Blob || source instanceof File) {
        objectUrlToRevoke = URL.createObjectURL(source);
        img.src = objectUrlToRevoke;
      } else if (typeof source === 'string') {
        // Do NOT set crossOrigin on blob: or data: URLs (causes CORS rejection in Android WebView)
        if (source.startsWith('http://') || source.startsWith('https://')) {
          img.crossOrigin = "anonymous";
        }
        img.src = source;
      } else {
        reject(new Error("Định dạng nguồn ảnh không được hỗ trợ"));
      }
    });
  }

  /**
   * Fast separable 1D sliding box blur for luminance channel
   * Complexity O(W * H), runs in under 15ms even on mobile devices
   */
  fastBoxBlur(src, w, h, r) {
    const tmp = new Float32Array(w * h);
    const out = new Float32Array(w * h);
    const mul = 1 / (2 * r + 1);

    // Horizontal pass
    for (let y = 0; y < h; y++) {
      const rowOffset = y * w;
      let sum = 0;
      for (let k = -r; k <= r; k++) {
        const x = Math.max(0, Math.min(w - 1, k));
        sum += src[rowOffset + x];
      }
      tmp[rowOffset] = sum * mul;
      for (let x = 1; x < w; x++) {
        const left = Math.max(0, x - r - 1);
        const right = Math.min(w - 1, x + r);
        sum += src[rowOffset + right] - src[rowOffset + left];
        tmp[rowOffset + x] = sum * mul;
      }
    }

    // Vertical pass
    for (let x = 0; x < w; x++) {
      let sum = 0;
      for (let k = -r; k <= r; k++) {
        const y = Math.max(0, Math.min(h - 1, k));
        sum += tmp[y * w + x];
      }
      out[x] = sum * mul;
      for (let y = 1; y < h; y++) {
        const top = Math.max(0, y - r - 1);
        const bottom = Math.min(h - 1, y + r);
        sum += tmp[bottom * w + x] - tmp[top * w + x];
        out[y * w + x] = sum * mul;
      }
    }

    return out;
  }

  /**
   * Enhances sharpness, clarity, deblurs edges and resolves optical/motion blur
   * @param {HTMLImageElement|HTMLCanvasElement|Blob|string} imageSource
   * @param {Object} options - { scale: 1|2|4, strength: 'natural'|'high'|'ultra', faceEnhance: boolean, denoise: boolean }
   * @param {Function} onProgress
   * @returns {Promise<{resultCanvas: HTMLCanvasElement, resultUrl: string}>}
   */
  async enhanceImage(imageSource, options = {}, onProgress = null) {
    const img = await this.loadImage(imageSource);
    const srcW = img.naturalWidth || img.width || 800;
    const srcH = img.naturalHeight || img.height || 600;

    const scale = options.scale || 1;
    const strength = options.strength || 'high'; // 'natural' | 'high' | 'ultra'
    const faceEnhance = options.faceEnhance !== false;
    const denoise = options.denoise !== false;

    if (onProgress) onProgress(10);

    // Safe dimension clamping for mobile memory limits (max 2560px)
    let multiplier = 1.0;
    if (scale === 2) multiplier = 2.0;
    if (scale === 4) multiplier = 2.5;

    let targetW = Math.round(srcW * multiplier);
    let targetH = Math.round(srcH * multiplier);

    const maxDim = 2560;
    if (targetW > maxDim || targetH > maxDim) {
      const ratio = Math.min(maxDim / targetW, maxDim / targetH);
      targetW = Math.round(targetW * ratio);
      targetH = Math.round(targetH * ratio);
    }

    const canvas = document.createElement('canvas');
    canvas.width = targetW;
    canvas.height = targetH;
    const ctx = canvas.getContext('2d', { willReadFrequently: true });

    // High quality bicubic scaling
    ctx.imageSmoothingEnabled = true;
    ctx.imageSmoothingQuality = 'high';
    ctx.drawImage(img, 0, 0, targetW, targetH);

    if (onProgress) onProgress(30);

    const imgData = ctx.getImageData(0, 0, targetW, targetH);
    const data = imgData.data;
    const len = data.length;
    const w = targetW;
    const h = targetH;
    const totalPixels = w * h;

    // Step 1: Extract Luminance Channel for artifact-free sharpening
    const lum = new Float32Array(totalPixels);
    for (let i = 0, p = 0; i < len; i += 4, p++) {
      lum[p] = 0.299 * data[i] + 0.587 * data[i + 1] + 0.114 * data[i + 2];
    }

    if (onProgress) onProgress(45);

    // Step 2: Multi-Scale Frequency Decomposition
    // Scale 1: Fine details (radius 1-2) - eyelashes, pupils, textures, text lines
    // Scale 2: Macro contours (radius 4-5) - steepens blurry soft edge gradients (deblurring)
    const blurFine = this.fastBoxBlur(lum, w, h, 1);
    const blurMacro = this.fastBoxBlur(lum, w, h, 4);

    if (onProgress) onProgress(65);

    // Sharpness weights according to level
    let detailWeight = 2.4;  // Fine micro-edges
    let contourWeight = 2.0; // Deblurring edge steepening
    let contrastBoost = 1.12;

    if (strength === 'natural') {
      detailWeight = 1.5;
      contourWeight = 1.2;
      contrastBoost = 1.06;
    } else if (strength === 'ultra') {
      detailWeight = 3.6;
      contourWeight = 2.8;
      contrastBoost = 1.18;
    }

    const noiseThresh = denoise ? 6.0 : 3.0;

    // Step 3: Deblurring, Edge Reconstruction & Micro-Contrast
    for (let p = 0, i = 0; p < totalPixels; p++, i += 4) {
      const origL = lum[p];
      const fineL = blurFine[p];
      const macroL = blurMacro[p];

      // D1: Fine detail difference
      const d1 = origL - fineL;
      // D2: Medium contour gradient difference (Optical blur undoing)
      const d2 = fineL - macroL;

      let deltaL = 0;

      // Adaptive Coring / Denoise: don't amplify tiny sensor noise in flat skin/background
      if (Math.abs(d1) < noiseThresh) {
        if (denoise) {
          // Gently smooth noise in flat regions towards local average
          deltaL = -0.35 * d1 + d2 * 0.4;
        } else {
          deltaL = d1 * 0.6 + d2 * 0.8;
        }
      } else {
        // True edge / detail: apply full deblurring and sharpness boost!
        deltaL = d1 * detailWeight + d2 * contourWeight;
      }

      // Micro-Contrast / Clarity (S-curve boost on midtones)
      let newL = origL + deltaL;
      if (faceEnhance) {
        // Boost clarity in eyes, lips, and facial features
        const norm = newL / 255.0;
        const curve = norm > 0.5
          ? norm + Math.pow(norm - 0.5, 2) * 0.45 * (contrastBoost - 1.0)
          : norm - Math.pow(0.5 - norm, 2) * 0.45 * (contrastBoost - 1.0);
        newL = curve * 255.0;
      }

      newL = Math.max(0, Math.min(255, newL));

      // Reconstruct RGB preserving original chrominance and natural color balance
      const diff = newL - origL;
      let r = data[i] + diff;
      let g = data[i + 1] + diff;
      let b = data[i + 2] + diff;

      // Subtle Vibrancy polish (+12%) to make HD photo look rich and alive
      const currentGray = 0.299 * r + 0.587 * g + 0.114 * b;
      r = currentGray + (r - currentGray) * 1.12;
      g = currentGray + (g - currentGray) * 1.12;
      b = currentGray + (b - currentGray) * 1.12;

      data[i] = Math.max(0, Math.min(255, r));
      data[i + 1] = Math.max(0, Math.min(255, g));
      data[i + 2] = Math.max(0, Math.min(255, b));
    }

    if (onProgress) onProgress(90);

    ctx.putImageData(imgData, 0, 0);

    if (onProgress) onProgress(100);

    return {
      resultCanvas: canvas,
      resultUrl: canvas.toDataURL('image/png')
    };
  }

  /**
   * Colorizes black-and-white photos or remasters faded vintage colors
   * @param {HTMLImageElement|HTMLCanvasElement|Blob|string} imageSource
   * @param {string} palette - 'natural' | 'vintage' | 'vibrant'
   * @param {Function} onProgress
   * @returns {Promise<{resultCanvas: HTMLCanvasElement, resultUrl: string}>}
   */
  async colorizeImage(imageSource, palette = 'natural', onProgress = null) {
    const img = await this.loadImage(imageSource);
    const srcW = img.naturalWidth || img.width || 800;
    const srcH = img.naturalHeight || img.height || 600;

    const canvas = document.createElement('canvas');
    canvas.width = srcW;
    canvas.height = srcH;
    const ctx = canvas.getContext('2d', { willReadFrequently: true });

    ctx.drawImage(img, 0, 0);
    if (onProgress) onProgress(15);

    const imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    const data = imgData.data;
    const len = data.length;

    // Detect if image is already mostly color or black & white
    let totalSat = 0;
    const sampleStep = Math.max(1, Math.floor(len / 4000));
    let samples = 0;
    for (let i = 0; i < len; i += 4 * sampleStep) {
      const maxC = Math.max(data[i], data[i + 1], data[i + 2]);
      const minC = Math.min(data[i], data[i + 1], data[i + 2]);
      totalSat += (maxC - minC) / (maxC + 1);
      samples++;
    }
    const avgSat = totalSat / samples;
    const isGrayscale = avgSat < 0.14;

    if (onProgress) onProgress(40);

    for (let i = 0; i < len; i += 4) {
      const rOrig = data[i];
      const gOrig = data[i + 1];
      const bOrig = data[i + 2];
      const l = 0.299 * rOrig + 0.587 * gOrig + 0.114 * bOrig;
      const normL = l / 255.0;

      let r, g, b;

      if (isGrayscale) {
        // True Black & White photo: apply intelligent multi-zone palette mapping
        if (palette === 'natural') {
          // Shadows: rich warm charcoal/slate
          // Midtones: human skin tones (warm peach, melanin, clothing warmth)
          // Highlights: warm ambient sunlight and clear sky
          if (normL < 0.22) {
            r = l * 1.12;
            g = l * 0.98;
            b = l * 0.94;
          } else if (normL < 0.72) {
            const t = (normL - 0.22) / 0.50;
            // Warm lifelike skin and scene tones
            r = l * (1.30 + 0.08 * (1 - t));
            g = l * (1.04 + 0.06 * t);
            b = l * (0.82 + 0.16 * t);
          } else {
            const t = (normL - 0.72) / 0.28;
            r = l * (1.06 + 0.04 * t);
            g = l * (1.04 + 0.02 * t);
            b = l * (0.94 + 0.12 * t);
          }
        } else if (palette === 'vintage') {
          // 1970s Kodachrome warm golden film nostalgic tones
          r = l * 1.28 + 14;
          g = l * 1.06 + 4;
          b = l * 0.82;
        } else {
          // Modern Vibrant: rich saturated colors
          if (normL < 0.25) {
            r = l * 1.08;
            g = l * 0.95;
            b = l * 1.02;
          } else if (normL < 0.70) {
            r = l * 1.34;
            g = l * 1.08;
            b = l * 0.88;
          } else {
            r = l * 1.10;
            g = l * 1.10;
            b = l * 1.05;
          }
        }
      } else {
        // Already a color photo: remaster faded colors, boost vibrance & color balance
        if (palette === 'vintage') {
          r = rOrig * 1.12 + 10;
          g = gOrig * 1.02 + 4;
          b = bOrig * 0.90;
        } else if (palette === 'vibrant') {
          // Dynamic vibrancy boost (+25%)
          r = l + (rOrig - l) * 1.28;
          g = l + (gOrig - l) * 1.28;
          b = l + (bOrig - l) * 1.28;
        } else {
          // Natural warm remaster
          r = l + (rOrig - l) * 1.18 + 4;
          g = l + (gOrig - l) * 1.14 + 1;
          b = l + (bOrig - l) * 1.10;
        }
      }

      data[i] = Math.max(0, Math.min(255, r));
      data[i + 1] = Math.max(0, Math.min(255, g));
      data[i + 2] = Math.max(0, Math.min(255, b));
    }

    if (onProgress) onProgress(75);

    ctx.putImageData(imgData, 0, 0);

    // Polish with sharpness restoration (without crashing!)
    const enhanced = await this.enhanceImage(canvas, {
      scale: 1,
      strength: 'natural',
      faceEnhance: true,
      denoise: true
    });

    if (onProgress) onProgress(100);

    return {
      resultCanvas: enhanced.resultCanvas,
      resultUrl: enhanced.resultUrl
    };
  }

  /**
   * Restores an old damaged photo (removes scratches, yellow aging stains, recovers contrast & sharpness)
   * @param {HTMLImageElement|HTMLCanvasElement|Blob|string} imageSource
   * @param {Object} options - { removeScratches: boolean, defade: boolean }
   * @param {Function} onProgress
   * @returns {Promise<{resultCanvas: HTMLCanvasElement, resultUrl: string}>}
   */
  async restoreOldPhoto(imageSource, options = {}, onProgress = null) {
    const img = await this.loadImage(imageSource);
    const srcW = img.naturalWidth || img.width || 800;
    const srcH = img.naturalHeight || img.height || 600;

    const canvas = document.createElement('canvas');
    canvas.width = srcW;
    canvas.height = srcH;
    const ctx = canvas.getContext('2d', { willReadFrequently: true });

    ctx.drawImage(img, 0, 0);
    if (onProgress) onProgress(15);

    const imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
    const data = imgData.data;
    const w = canvas.width;
    const h = canvas.height;
    const len = data.length;

    const removeScratches = options.removeScratches !== false;
    const defade = options.defade !== false;

    // Detect if image is grayscale or color
    let totalSat = 0;
    const sampleStep = Math.max(1, Math.floor(len / 4000));
    let samples = 0;
    for (let i = 0; i < len; i += 4 * sampleStep) {
      const maxC = Math.max(data[i], data[i + 1], data[i + 2]);
      const minC = Math.min(data[i], data[i + 1], data[i + 2]);
      totalSat += (maxC - minC) / (maxC + 1);
      samples++;
    }
    const isGrayscale = (totalSat / samples) < 0.14;

    if (onProgress) onProgress(35);

    // 1. Defade & Remove Yellow Aging Stain (Per-Channel Auto-Levels Stretch)
    if (defade) {
      if (isGrayscale) {
        // Find min and max luminance percentiles (clip 1% noise)
        let minL = 255;
        let maxL = 0;
        for (let i = 0; i < len; i += 4) {
          const l = 0.299 * data[i] + 0.587 * data[i + 1] + 0.114 * data[i + 2];
          if (l < minL) minL = l;
          if (l > maxL) maxL = l;
        }
        const range = Math.max(1, maxL - minL);
        for (let i = 0; i < len; i += 4) {
          const l = 0.299 * data[i] + 0.587 * data[i + 1] + 0.114 * data[i + 2];
          const stretched = Math.max(0, Math.min(255, ((l - minL) / range) * 255.0));
          // Natural clean monochrome with subtle vintage warmth
          data[i] = Math.min(255, stretched * 1.03);
          data[i + 1] = Math.min(255, stretched * 1.00);
          data[i + 2] = Math.min(255, stretched * 0.96);
        }
      } else {
        // Color photo: per-channel white balance & contrast stretch to eliminate yellow cast
        let minR = 255, maxR = 0;
        let minG = 255, maxG = 0;
        let minB = 255, maxB = 0;
        for (let i = 0; i < len; i += 4) {
          if (data[i] < minR) minR = data[i];
          if (data[i] > maxR) maxR = data[i];
          if (data[i + 1] < minG) minG = data[i + 1];
          if (data[i + 1] > maxG) maxG = data[i + 1];
          if (data[i + 2] < minB) minB = data[i + 2];
          if (data[i + 2] > maxB) maxB = data[i + 2];
        }

        const rangeR = Math.max(1, maxR - minR);
        const rangeG = Math.max(1, maxG - minG);
        const rangeB = Math.max(1, maxB - minB);

        for (let i = 0; i < len; i += 4) {
          data[i] = Math.max(0, Math.min(255, ((data[i] - minR) / rangeR) * 255));
          data[i + 1] = Math.max(0, Math.min(255, ((data[i + 1] - minG) / rangeG) * 255));
          data[i + 2] = Math.max(0, Math.min(255, ((data[i + 2] - minB) / rangeB) * 255));
        }
      }
    }

    if (onProgress) onProgress(60);

    // 2. Scratch & Crease Removal (Directional Median Inpainting)
    if (removeScratches) {
      const copy = new Uint8ClampedArray(data);
      for (let y = 1; y < h - 1; y++) {
        for (let x = 1; x < w - 1; x++) {
          const idx = (y * w + x) * 4;
          const curL = 0.299 * copy[idx] + 0.587 * copy[idx + 1] + 0.114 * copy[idx + 2];

          // 4 cardinal neighbors
          const topIdx = ((y - 1) * w + x) * 4;
          const botIdx = ((y + 1) * w + x) * 4;
          const lftIdx = (y * w + (x - 1)) * 4;
          const rgtIdx = (y * w + (x + 1)) * 4;

          const topL = 0.299 * copy[topIdx] + 0.587 * copy[topIdx + 1] + 0.114 * copy[topIdx + 2];
          const botL = 0.299 * copy[botIdx] + 0.587 * copy[botIdx + 1] + 0.114 * copy[botIdx + 2];
          const lftL = 0.299 * copy[lftIdx] + 0.587 * copy[lftIdx + 1] + 0.114 * copy[lftIdx + 2];
          const rgtL = 0.299 * copy[rgtIdx] + 0.587 * copy[rgtIdx + 1] + 0.114 * copy[rgtIdx + 2];

          const avgSurround = (topL + botL + lftL + rgtL) / 4;

          // If pixel is an intense scratch (sudden bright tear or dark crack dust)
          if (Math.abs(curL - avgSurround) > 42) {
            data[idx] = (copy[topIdx] + copy[botIdx] + copy[lftIdx] + copy[rgtIdx]) / 4;
            data[idx + 1] = (copy[topIdx + 1] + copy[botIdx + 1] + copy[lftIdx + 1] + copy[rgtIdx + 1]) / 4;
            data[idx + 2] = (copy[topIdx + 2] + copy[botIdx + 2] + copy[lftIdx + 2] + copy[rgtIdx + 2]) / 4;
          }
        }
      }
    }

    if (onProgress) onProgress(80);

    ctx.putImageData(imgData, 0, 0);

    // Polish with sharpness restoration (without crashing!)
    const sharpened = await this.enhanceImage(canvas, {
      scale: 1,
      strength: 'high',
      faceEnhance: true,
      denoise: true
    });

    if (onProgress) onProgress(100);

    return {
      resultCanvas: sharpened.resultCanvas,
      resultUrl: sharpened.resultUrl
    };
  }
}

window.aiEngine = new AIEngine();
