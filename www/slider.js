// EnhanceFox AI - Interactive Before/After Split Comparison Slider
// Features: Drag Divider, 2.5X Detail Zoom, Hold-to-View-Original, Smooth Touch & Scanning Laser

class ComparisonSlider {
  constructor(containerElement) {
    this.container = containerElement;
    this.sliderHandle = null;
    this.afterImageWrap = null;
    this.beforeImage = null;
    this.afterImage = null;
    this.isDragging = false;
    this.position = 50; // 50% split by default
    this.isZoomed = false;
    this.savedPosition = 50;

    this.initDOM();
    this.initEvents();
  }

  initDOM() {
    this.container.innerHTML = `
      <div class="slider-viewport" id="sliderViewport">
        <!-- Before (Original) Layer -->
        <div class="slider-layer before-layer">
          <img id="sliderImgBefore" src="" alt="Before" draggable="false">
          <div class="slider-badge badge-before" data-i18n="label_before">GỐC</div>
        </div>

        <!-- After (Enhanced) Layer with Dynamic Clip -->
        <div class="slider-layer after-layer" id="afterLayer">
          <img id="sliderImgAfter" src="" alt="After" draggable="false">
          <div class="slider-badge badge-after" data-i18n="label_after">ĐÃ SỬA</div>
        </div>

        <!-- Draggable Divider Line & Handle -->
        <div class="slider-divider" id="sliderDivider" style="left: 50%;">
          <div class="slider-divider-line"></div>
          <div class="slider-handle-knob">
            <i data-lucide="chevrons-left-right" style="width: 18px; height: 18px; color: #fff;"></i>
          </div>
        </div>

        <!-- AI Scanning Laser Beam (Visible during processing) -->
        <div class="ai-scan-laser" id="aiScanLaser" style="display: none;"></div>
      </div>

      <!-- Quick Comparison Toolbar -->
      <div class="slider-toolbar">
        <button type="button" class="slider-tool-btn" id="sliderZoomBtn" title="Phóng to 2.5X để so sánh chi tiết">
          <i data-lucide="zoom-in" style="width: 15px; height: 15px;"></i>
          <span class="zoom-btn-label" data-i18n="btn_zoom_detail">Phóng to 2.5X xem chi tiết</span>
        </button>

        <button type="button" class="slider-tool-btn" id="sliderHoldCompareBtn" title="Chạm giữ để xem ảnh gốc">
          <i data-lucide="eye" style="width: 15px; height: 15px;"></i>
          <span data-i18n="btn_hold_compare">Chạm giữ xem GỐC</span>
        </button>
      </div>
    `;

    this.viewport = this.container.querySelector('#sliderViewport');
    this.afterLayer = this.container.querySelector('#afterLayer');
    this.sliderDivider = this.container.querySelector('#sliderDivider');
    this.imgBefore = this.container.querySelector('#sliderImgBefore');
    this.imgAfter = this.container.querySelector('#sliderImgAfter');
    this.scanLaser = this.container.querySelector('#aiScanLaser');
    this.zoomBtn = this.container.querySelector('#sliderZoomBtn');
    this.zoomBtnLabel = this.container.querySelector('.zoom-btn-label');
    this.holdCompareBtn = this.container.querySelector('#sliderHoldCompareBtn');

    if (window.lucide) window.lucide.createIcons();
    if (window.i18n) window.i18n.applyTranslations();
  }

  setImages(beforeUrl, afterUrl) {
    this.imgBefore.src = beforeUrl;
    this.imgAfter.src = afterUrl;
    this.setZoom(false);
    this.setPosition(50);
  }

  showScanning(isScanning) {
    if (this.scanLaser) {
      this.scanLaser.style.display = isScanning ? 'block' : 'none';
    }
  }

  setZoom(enable) {
    this.isZoomed = !!enable;
    if (this.viewport) {
      this.viewport.classList.toggle('zoomed-in', this.isZoomed);
    }
    if (this.zoomBtn) {
      this.zoomBtn.classList.toggle('active', this.isZoomed);
      const iconEl = this.zoomBtn.querySelector('i');
      if (this.isZoomed) {
        if (iconEl) iconEl.setAttribute('data-lucide', 'zoom-out');
        if (this.zoomBtnLabel) {
          this.zoomBtnLabel.textContent = (window.i18n && window.i18n.currentLang === 'en') ? '1X Normal View' : 'Thu nhỏ 1X';
        }
      } else {
        if (iconEl) iconEl.setAttribute('data-lucide', 'zoom-in');
        if (this.zoomBtnLabel) {
          this.zoomBtnLabel.textContent = (window.i18n && window.i18n.currentLang === 'en') ? '2.5X Detail Zoom' : 'Phóng to 2.5X xem chi tiết';
        }
      }
      if (window.lucide) window.lucide.createIcons();
    }
  }

  toggleZoom() {
    this.setZoom(!this.isZoomed);
  }

  setPosition(percent) {
    this.position = Math.max(0, Math.min(100, percent));
    if (this.afterLayer) {
      // Reveal the enhanced afterLayer from the slider divider to the right edge
      this.afterLayer.style.clipPath = `polygon(${this.position}% 0%, 100% 0%, 100% 100%, ${this.position}% 100%)`;
    }
    if (this.sliderDivider) {
      this.sliderDivider.style.left = `${this.position}%`;
    }
  }

  initEvents() {
    const handleMove = (clientX) => {
      if (!this.isDragging) return;
      const rect = this.viewport.getBoundingClientRect();
      const x = clientX - rect.left;
      const percent = (x / rect.width) * 100;
      this.setPosition(percent);
    };

    // Zoom Button Toggle
    if (this.zoomBtn) {
      this.zoomBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        this.toggleZoom();
      });
    }

    // Hold to compare Original Button
    if (this.holdCompareBtn) {
      const startHold = (e) => {
        e.preventDefault();
        this.savedPosition = this.position;
        this.setPosition(100); // Reveal 100% Before (Original)
        this.holdCompareBtn.classList.add('holding');
      };

      const endHold = (e) => {
        e.preventDefault();
        this.setPosition(this.savedPosition || 50);
        this.holdCompareBtn.classList.remove('holding');
      };

      this.holdCompareBtn.addEventListener('mousedown', startHold);
      window.addEventListener('mouseup', endHold);

      this.holdCompareBtn.addEventListener('touchstart', startHold, { passive: false });
      window.addEventListener('touchend', endHold);
      window.addEventListener('touchcancel', endHold);
    }

    // Mouse Dragging on viewport / divider
    this.viewport.addEventListener('mousedown', (e) => {
      this.isDragging = true;
      handleMove(e.clientX);
    });

    window.addEventListener('mousemove', (e) => {
      if (this.isDragging) {
        handleMove(e.clientX);
      }
    });

    window.addEventListener('mouseup', () => {
      this.isDragging = false;
    });

    // Mobile Touch Dragging
    this.viewport.addEventListener('touchstart', (e) => {
      this.isDragging = true;
      if (e.touches && e.touches[0]) {
        handleMove(e.touches[0].clientX);
      }
    }, { passive: true });

    window.addEventListener('touchmove', (e) => {
      if (this.isDragging && e.touches && e.touches[0]) {
        handleMove(e.touches[0].clientX);
      }
    }, { passive: true });

    window.addEventListener('touchend', () => {
      this.isDragging = false;
    });

    window.addEventListener('touchcancel', () => {
      this.isDragging = false;
    });
  }
}

window.ComparisonSlider = ComparisonSlider;
