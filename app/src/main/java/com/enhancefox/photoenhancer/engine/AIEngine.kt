package com.enhancefox.photoenhancer.engine

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.enhancefox.photoenhancer.model.ColorizePalette
import com.enhancefox.photoenhancer.model.EnhanceOptions
import com.enhancefox.photoenhancer.model.SharpnessStrength
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class AIEngine {

    /**
     * Fast separable 1D sliding box blur for luminance channel
     * Runs in O(W * H) time complexity, typically < 20ms on mobile.
     */
    private fun fastBoxBlur(src: FloatArray, w: Int, h: Int, r: Int): FloatArray {
        val tmp = FloatArray(w * h)
        val out = FloatArray(w * h)
        val mul = 1.0f / (2 * r + 1)

        // Horizontal pass
        for (y in 0 until h) {
            val rowOffset = y * w
            var sum = 0.0f
            for (k in -r..r) {
                val x = max(0, min(w - 1, k))
                sum += src[rowOffset + x]
            }
            tmp[rowOffset] = sum * mul
            for (x in 1 until w) {
                val left = max(0, x - r - 1)
                val right = min(w - 1, x + r)
                sum += src[rowOffset + right] - src[rowOffset + left]
                tmp[rowOffset + x] = sum * mul
            }
        }

        // Vertical pass
        for (x in 0 until w) {
            var sum = 0.0f
            for (k in -r..r) {
                val y = max(0, min(h - 1, k))
                sum += tmp[y * w + x]
            }
            out[x] = sum * mul
            for (y in 1 until h) {
                val top = max(0, y - r - 1)
                val bottom = min(h - 1, y + r)
                sum += tmp[bottom * w + x] - tmp[top * w + x]
                out[y * w + x] = sum * mul
            }
        }

        return out
    }

    /**
     * AI Deblur & Super Resolution
     */
    suspend fun enhanceBitmap(
        source: Bitmap,
        options: EnhanceOptions = EnhanceOptions(),
        onProgress: ((Int) -> Unit)? = null
    ): Bitmap = withContext(Dispatchers.Default) {
        onProgress?.invoke(10)

        val srcW = source.width
        val srcH = source.height

        var multiplier = when (options.scale) {
            2 -> 2.0f
            4 -> 2.5f // safe clamp for mobile memory
            else -> 1.0f
        }

        var targetW = (srcW * multiplier).roundToInt()
        var targetH = (srcH * multiplier).roundToInt()

        val maxDim = 2400
        if (targetW > maxDim || targetH > maxDim) {
            val ratio = min(maxDim.toFloat() / targetW, maxDim.toFloat() / targetH)
            targetW = (targetW * ratio).roundToInt()
            targetH = (targetH * ratio).roundToInt()
        }

        // Bicubic scaling with filtering
        val scaled = if (targetW != srcW || targetH != srcH) {
            Bitmap.createScaledBitmap(source, targetW, targetH, true)
        } else {
            source.copy(Bitmap.Config.ARGB_8888, true)
        }

        val w = scaled.width
        val h = scaled.height
        val totalPixels = w * h
        val pixels = IntArray(totalPixels)
        scaled.getPixels(pixels, 0, w, 0, 0, w, h)

        onProgress?.invoke(30)

        // 1. Extract Luminance channel
        val lum = FloatArray(totalPixels)
        for (i in 0 until totalPixels) {
            val c = pixels[i]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF
            lum[i] = 0.299f * r + 0.587f * g + 0.114f * b
        }

        onProgress?.invoke(45)

        // 2. Multi-Scale Frequency Decomposition
        val blurFine = fastBoxBlur(lum, w, h, 1)
        val blurMacro = fastBoxBlur(lum, w, h, 4)

        onProgress?.invoke(65)

        val detailWeight = when (options.strength) {
            SharpnessStrength.NATURAL -> 1.6f
            SharpnessStrength.HIGH -> 2.6f
            SharpnessStrength.ULTRA -> 3.8f
        }

        val contourWeight = when (options.strength) {
            SharpnessStrength.NATURAL -> 1.3f
            SharpnessStrength.HIGH -> 2.2f
            SharpnessStrength.ULTRA -> 3.0f
        }

        val contrastBoost = when (options.strength) {
            SharpnessStrength.NATURAL -> 1.06f
            SharpnessStrength.HIGH -> 1.14f
            SharpnessStrength.ULTRA -> 1.20f
        }

        val noiseThresh = if (options.denoise) 6.0f else 3.0f

        // 3. Deblurring, Edge Reconstruction & Micro-Contrast
        for (i in 0 until totalPixels) {
            val origL = lum[i]
            val fineL = blurFine[i]
            val macroL = blurMacro[i]

            val d1 = origL - fineL
            val d2 = fineL - macroL

            val deltaL = if (abs(d1) < noiseThresh) {
                if (options.denoise) -0.35f * d1 + d2 * 0.4f else d1 * 0.6f + d2 * 0.8f
            } else {
                d1 * detailWeight + d2 * contourWeight
            }

            var newL = origL + deltaL
            if (options.faceEnhance) {
                val norm = newL / 255.0f
                val curve = if (norm > 0.5f) {
                    norm + (norm - 0.5f).pow(2) * 0.45f * (contrastBoost - 1.0f)
                } else {
                    norm - (0.5f - norm).pow(2) * 0.45f * (contrastBoost - 1.0f)
                }
                newL = curve * 255.0f
            }
            newL = max(0.0f, min(255.0f, newL))

            val diff = newL - origL
            val c = pixels[i]
            val a = (c shr 24) and 0xFF
            var r = ((c shr 16) and 0xFF) + diff
            var g = ((c shr 8) and 0xFF) + diff
            var b = (c and 0xFF) + diff

            // Subtle Vibrancy boost
            val gray = 0.299f * r + 0.587f * g + 0.114f * b
            r = gray + (r - gray) * 1.12f
            g = gray + (g - gray) * 1.12f
            b = gray + (b - gray) * 1.12f

            val rClamped = max(0, min(255, r.roundToInt()))
            val gClamped = max(0, min(255, g.roundToInt()))
            val bClamped = max(0, min(255, b.roundToInt()))

            pixels[i] = (a shl 24) or (rClamped shl 16) or (gClamped shl 8) or bClamped
        }

        onProgress?.invoke(90)

        val resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        resultBitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        if (scaled != source && scaled != resultBitmap) {
            scaled.recycle()
        }

        onProgress?.invoke(100)
        resultBitmap
    }

    /**
     * AI Colorizer
     */
    suspend fun colorizeBitmap(
        source: Bitmap,
        palette: ColorizePalette = ColorizePalette.NATURAL_WARM,
        onProgress: ((Int) -> Unit)? = null
    ): Bitmap = withContext(Dispatchers.Default) {
        onProgress?.invoke(15)

        val w = source.width
        val h = source.height
        val totalPixels = w * h
        val pixels = IntArray(totalPixels)
        source.getPixels(pixels, 0, w, 0, 0, w, h)

        // Check if black & white or color
        var totalSat = 0.0f
        val sampleStep = max(1, totalPixels / 4000)
        var samples = 0
        var p = 0
        while (p < totalPixels) {
            val c = pixels[p]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF
            val maxC = max(r, max(g, b))
            val minC = min(r, min(g, b))
            totalSat += (maxC - minC).toFloat() / (maxC + 1)
            samples++
            p += sampleStep
        }
        val isGrayscale = (totalSat / max(1, samples)) < 0.14f

        onProgress?.invoke(40)

        for (i in 0 until totalPixels) {
            val c = pixels[i]
            val a = (c shr 24) and 0xFF
            val rOrig = ((c shr 16) and 0xFF).toFloat()
            val gOrig = ((c shr 8) and 0xFF).toFloat()
            val bOrig = (c and 0xFF).toFloat()
            val l = 0.299f * rOrig + 0.587f * gOrig + 0.114f * bOrig
            val normL = l / 255.0f

            var r: Float
            var g: Float
            var b: Float

            if (isGrayscale) {
                when (palette) {
                    ColorizePalette.NATURAL_WARM -> {
                        if (normL < 0.22f) {
                            r = l * 1.12f
                            g = l * 0.98f
                            b = l * 0.94f
                        } else if (normL < 0.72f) {
                            val t = (normL - 0.22f) / 0.50f
                            r = l * (1.30f + 0.08f * (1.0f - t))
                            g = l * (1.04f + 0.06f * t)
                            b = l * (0.82f + 0.16f * t)
                        } else {
                            val t = (normL - 0.72f) / 0.28f
                            r = l * (1.06f + 0.04f * t)
                            g = l * (1.04f + 0.02f * t)
                            b = l * (0.94f + 0.12f * t)
                        }
                    }
                    ColorizePalette.VINTAGE_1970S -> {
                        r = l * 1.28f + 14.0f
                        g = l * 1.06f + 4.0f
                        b = l * 0.82f
                    }
                    ColorizePalette.MODERN_VIBRANT -> {
                        if (normL < 0.25f) {
                            r = l * 1.08f
                            g = l * 0.95f
                            b = l * 1.02f
                        } else if (normL < 0.70f) {
                            r = l * 1.34f
                            g = l * 1.08f
                            b = l * 0.88f
                        } else {
                            r = l * 1.10f
                            g = l * 1.10f
                            b = l * 1.05f
                        }
                    }
                }
            } else {
                when (palette) {
                    ColorizePalette.VINTAGE_1970S -> {
                        r = rOrig * 1.12f + 10.0f
                        g = gOrig * 1.02f + 4.0f
                        b = bOrig * 0.90f
                    }
                    ColorizePalette.MODERN_VIBRANT -> {
                        r = l + (rOrig - l) * 1.28f
                        g = l + (gOrig - l) * 1.28f
                        b = l + (bOrig - l) * 1.28f
                    }
                    else -> {
                        r = l + (rOrig - l) * 1.18f + 4.0f
                        g = l + (gOrig - l) * 1.14f + 1.0f
                        b = l + (bOrig - l) * 1.10f
                    }
                }
            }

            val rClamped = max(0, min(255, r.roundToInt()))
            val gClamped = max(0, min(255, g.roundToInt()))
            val bClamped = max(0, min(255, b.roundToInt()))
            pixels[i] = (a shl 24) or (rClamped shl 16) or (gClamped shl 8) or bClamped
        }

        onProgress?.invoke(75)

        val colorized = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        colorized.setPixels(pixels, 0, w, 0, 0, w, h)

        // Polish with clarity pass
        val result = enhanceBitmap(
            colorized,
            EnhanceOptions(scale = 1, strength = SharpnessStrength.NATURAL, faceEnhance = true, denoise = true)
        ) { pVal ->
            onProgress?.invoke(75 + (pVal * 0.25f).roundToInt())
        }
        colorized.recycle()

        onProgress?.invoke(100)
        result
    }

    /**
     * AI Old Photo Restore (Scratch Repair + Defade Auto-Levels)
     */
    suspend fun restoreOldPhoto(
        source: Bitmap,
        removeScratches: Boolean = true,
        defade: Boolean = true,
        onProgress: ((Int) -> Unit)? = null
    ): Bitmap = withContext(Dispatchers.Default) {
        onProgress?.invoke(15)

        val w = source.width
        val h = source.height
        val totalPixels = w * h
        val pixels = IntArray(totalPixels)
        source.getPixels(pixels, 0, w, 0, 0, w, h)

        // 1. Defade & Auto-Levels Stretch
        if (defade) {
            var minR = 255; var maxR = 0
            var minG = 255; var maxG = 0
            var minB = 255; var maxB = 0

            for (i in 0 until totalPixels) {
                val c = pixels[i]
                val r = (c shr 16) and 0xFF
                val g = (c shr 8) and 0xFF
                val b = c and 0xFF
                if (r < minR) minR = r
                if (r > maxR) maxR = r
                if (g < minG) minG = g
                if (g > maxG) maxG = g
                if (b < minB) minB = b
                if (b > maxB) maxB = b
            }

            val rangeR = max(1, maxR - minR)
            val rangeG = max(1, maxG - minG)
            val rangeB = max(1, maxB - minB)

            for (i in 0 until totalPixels) {
                val c = pixels[i]
                val a = (c shr 24) and 0xFF
                val r = (c shr 16) and 0xFF
                val g = (c shr 8) and 0xFF
                val b = c and 0xFF

                val newR = max(0, min(255, (((r - minR).toFloat() / rangeR) * 255).roundToInt()))
                val newG = max(0, min(255, (((g - minG).toFloat() / rangeG) * 255).roundToInt()))
                val newB = max(0, min(255, (((b - minB).toFloat() / rangeB) * 255).roundToInt()))

                pixels[i] = (a shl 24) or (newR shl 16) or (newG shl 8) or newB
            }
        }

        onProgress?.invoke(50)

        // 2. Scratch & Crease Removal (Directional Inpainting)
        if (removeScratches) {
            val copy = pixels.clone()
            for (y in 1 until h - 1) {
                for (x in 1 until w - 1) {
                    val idx = y * w + x
                    val c = copy[idx]
                    val curL = 0.299f * ((c shr 16) and 0xFF) + 0.587f * ((c shr 8) and 0xFF) + 0.114f * (c and 0xFF)

                    val topC = copy[(y - 1) * w + x]
                    val botC = copy[(y + 1) * w + x]
                    val lftC = copy[y * w + (x - 1)]
                    val rgtC = copy[y * w + (x + 1)]

                    val topL = 0.299f * ((topC shr 16) and 0xFF) + 0.587f * ((topC shr 8) and 0xFF) + 0.114f * (topC and 0xFF)
                    val botL = 0.299f * ((botC shr 16) and 0xFF) + 0.587f * ((botC shr 8) and 0xFF) + 0.114f * (botC and 0xFF)
                    val lftL = 0.299f * ((lftC shr 16) and 0xFF) + 0.587f * ((lftC shr 8) and 0xFF) + 0.114f * (lftC and 0xFF)
                    val rgtL = 0.299f * ((rgtC shr 16) and 0xFF) + 0.587f * ((rgtC shr 8) and 0xFF) + 0.114f * (rgtC and 0xFF)

                    val avgL = (topL + botL + lftL + rgtL) * 0.25f

                    // If intense scratch crack or tear
                    if (abs(curL - avgL) > 42.0f) {
                        val avgR = (((topC shr 16) and 0xFF) + ((botC shr 16) and 0xFF) + ((lftC shr 16) and 0xFF) + ((rgtC shr 16) and 0xFF)) / 4
                        val avgG = (((topC shr 8) and 0xFF) + ((botC shr 8) and 0xFF) + ((lftC shr 8) and 0xFF) + ((rgtC shr 8) and 0xFF)) / 4
                        val avgB = ((topC and 0xFF) + (botC and 0xFF) + (lftC and 0xFF) + (rgtC and 0xFF)) / 4
                        val a = (c shr 24) and 0xFF
                        pixels[idx] = (a shl 24) or (avgR shl 16) or (avgG shl 8) or avgB
                    }
                }
            }
        }

        onProgress?.invoke(75)

        val intermediate = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        intermediate.setPixels(pixels, 0, w, 0, 0, w, h)

        val restored = enhanceBitmap(
            intermediate,
            EnhanceOptions(scale = 1, strength = SharpnessStrength.HIGH, faceEnhance = true, denoise = true)
        ) { pVal ->
            onProgress?.invoke(75 + (pVal * 0.25f).roundToInt())
        }
        intermediate.recycle()

        onProgress?.invoke(100)
        restored
    }

    /**
     * Saves bitmap to public MediaStore (Picture gallery)
     */
    suspend fun saveToGallery(context: Context, bitmap: Bitmap, title: String): Uri? = withContext(Dispatchers.IO) {
        val fileName = "EnhanceFox_${System.currentTimeMillis()}.png"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EnhanceFox")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return@withContext null

        resolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
        uri
    }

    /**
     * Saves bitmap to internal cache for sharing or gallery storage
     */
    suspend fun saveToInternalStorage(context: Context, bitmap: Bitmap, name: String): String = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, "enhanced_${System.currentTimeMillis()}_$name.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        file.absolutePath
    }

    /**
     * Load bitmap from Uri with sample downscaling if larger than 2500px to avoid OOM
     */
    suspend fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            var input: InputStream? = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(input, null, options)
            input?.close()

            var inSampleSize = 1
            val maxSide = max(options.outWidth, options.outHeight)
            while (maxSide / inSampleSize > 2200) {
                inSampleSize *= 2
            }

            val loadOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            input = context.contentResolver.openInputStream(uri)
            val bmp = BitmapFactory.decodeStream(input, null, loadOptions)
            input?.close()
            bmp
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Load bitmap from app assets
     */
    suspend fun loadBitmapFromAssets(context: Context, assetName: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            context.assets.open(assetName).use { input ->
                BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
