// EnhanceFox AI - Main Controller
// Coordinates UI State, File Uploads, AI Processing, Before/After Slider, Gallery, and Bilingual Engine

document.addEventListener('DOMContentLoaded', () => {
  // Initialize Lucide icons
  if (window.lucide) {
    window.lucide.createIcons();
  }

  // Initialize Translations
  if (window.i18n) {
    window.i18n.applyTranslations();
  }

  // Application State
  const state = {
    currentTab: 'home',
    enhanceSlider: null,
    colorizeSlider: null,
    restoreSlider: null,

    // Current working images
    enhanceBeforeUrl: null,
    enhanceAfterUrl: null,

    colorizeBeforeUrl: null,
    colorizeAfterUrl: null,

    restoreBeforeUrl: null,
    restoreAfterUrl: null,

    // Gallery storage
    gallery: JSON.parse(localStorage.getItem('enhancefox_gallery') || '[]'),
    activeGalleryFilter: 'all'
  };

  // DOM Elements
  const clockEl = document.getElementById('androidClock');
  const toggleViewBtn = document.getElementById('toggleViewBtn');
  const appViewportWrapper = document.getElementById('appViewportWrapper');
  const toggleViewText = document.getElementById('toggleViewText');
  const topLangToggleBtn = document.getElementById('topLangToggleBtn');
  const headerLangBtn = document.getElementById('headerLangBtn');
  const headerSettingsBtn = document.getElementById('headerSettingsBtn');
  const settingsModal = document.getElementById('settingsModal');
  const closeSettingsModal = document.getElementById('closeSettingsModal');
  const saveSettingsBtn = document.getElementById('saveSettingsBtn');
  const settingsLangSelector = document.getElementById('settingsLangSelector');
  const toastContainer = document.getElementById('toastContainer');

  // Navigation Items
  const navItems = document.querySelectorAll('.nav-item');
  const tabContents = document.querySelectorAll('.tab-content');

  // Enhance Elements
  const enhanceDropzone = document.getElementById('enhanceDropzone');
  const enhanceFileInput = document.getElementById('enhanceFileInput');
  const enhanceSamplePortraitBtn = document.getElementById('enhanceSamplePortraitBtn');
  const enhanceCompareWrap = document.getElementById('enhanceCompareWrap');
  const enhanceSliderMount = document.getElementById('enhanceSliderMount');
  const enhanceOptionsCard = document.getElementById('enhanceOptionsCard');
  const enhanceScaleSelector = document.getElementById('enhanceScaleSelector');
  const enhanceStrengthSelector = document.getElementById('enhanceStrengthSelector');
  const checkFaceEnhance = document.getElementById('checkFaceEnhance');
  const checkDenoise = document.getElementById('checkDenoise');
  const startEnhanceBtn = document.getElementById('startEnhanceBtn');
  const enhanceProgressCard = document.getElementById('enhanceProgressCard');
  const enhanceProgressBar = document.getElementById('enhanceProgressBar');
  const enhancePercentText = document.getElementById('enhancePercentText');
  const enhanceActionsWrap = document.getElementById('enhanceActionsWrap');
  const enhanceDownloadBtn = document.getElementById('enhanceDownloadBtn');
  const enhanceSaveGalleryBtn = document.getElementById('enhanceSaveGalleryBtn');
  const enhanceReEditBtn = document.getElementById('enhanceReEditBtn');

  // Colorize Elements
  const colorizeDropzone = document.getElementById('colorizeDropzone');
  const colorizeFileInput = document.getElementById('colorizeFileInput');
  const colorizeSampleVintageBtn = document.getElementById('colorizeSampleVintageBtn');
  const colorizeCompareWrap = document.getElementById('colorizeCompareWrap');
  const colorizeSliderMount = document.getElementById('colorizeSliderMount');
  const colorizeOptionsCard = document.getElementById('colorizeOptionsCard');
  const colorizePaletteSelector = document.getElementById('colorizePaletteSelector');
  const startColorizeBtn = document.getElementById('startColorizeBtn');
  const colorizeProgressCard = document.getElementById('colorizeProgressCard');
  const colorizeProgressBar = document.getElementById('colorizeProgressBar');
  const colorizePercentText = document.getElementById('colorizePercentText');
  const colorizeActionsWrap = document.getElementById('colorizeActionsWrap');
  const colorizeDownloadBtn = document.getElementById('colorizeDownloadBtn');
  const colorizeSaveGalleryBtn = document.getElementById('colorizeSaveGalleryBtn');
  const colorizeReEditBtn = document.getElementById('colorizeReEditBtn');

  // Restore Elements
  const restoreDropzone = document.getElementById('restoreDropzone');
  const restoreFileInput = document.getElementById('restoreFileInput');
  const restoreSampleVintageBtn = document.getElementById('restoreSampleVintageBtn');
  const restoreCompareWrap = document.getElementById('restoreCompareWrap');
  const restoreSliderMount = document.getElementById('restoreSliderMount');
  const restoreOptionsCard = document.getElementById('restoreOptionsCard');
  const checkScratch = document.getElementById('checkScratch');
  const checkDefade = document.getElementById('checkDefade');
  const startRestoreBtn = document.getElementById('startRestoreBtn');
  const restoreProgressCard = document.getElementById('restoreProgressCard');
  const restoreProgressBar = document.getElementById('restoreProgressBar');
  const restorePercentText = document.getElementById('restorePercentText');
  const restoreActionsWrap = document.getElementById('restoreActionsWrap');
  const restoreDownloadBtn = document.getElementById('restoreDownloadBtn');
  const restoreSaveGalleryBtn = document.getElementById('restoreSaveGalleryBtn');
  const restoreReEditBtn = document.getElementById('restoreReEditBtn');

  // Gallery Elements
  const galleryGrid = document.getElementById('galleryGrid');
  const homeRecentPhotosList = document.getElementById('homeRecentPhotosList');
  const galleryFilterSelector = document.getElementById('galleryFilterSelector');
  const clearGalleryBtn = document.getElementById('clearGalleryBtn');

  // =========================================================================
  // 1. Android Clock & Frame Mode Toggle
  // =========================================================================
  function updateClock() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    if (clockEl) clockEl.textContent = `${hours}:${minutes}`;
  }
  updateClock();
  setInterval(updateClock, 10000);

  if (toggleViewBtn) {
    toggleViewBtn.addEventListener('click', () => {
      appViewportWrapper.classList.toggle('fullscreen');
      const isFull = appViewportWrapper.classList.contains('fullscreen');
      toggleViewText.textContent = isFull ? window.i18n.t('toggle_view_phone') : window.i18n.t('toggle_view_full');
      showToast(isFull ? 'Đã chuyển sang chế độ Toàn màn hình' : 'Đã chuyển sang Chế độ điện thoại');
    });
  }

  // =========================================================================
  // 2. Bilingual Switcher (Tiếng Việt <-> English)
  // =========================================================================
  function switchLanguage(lang) {
    if (lang) {
      window.i18n.setLanguage(lang);
    } else {
      window.i18n.toggleLanguage();
    }

    if (settingsLangSelector) {
      settingsLangSelector.querySelectorAll('.segment-btn').forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-lang') === window.i18n.currentLang);
      });
    }

    if (window.lucide) window.lucide.createIcons();
    renderGallery();
    renderRecentPhotos();
    showToast(window.i18n.currentLang === 'vi' ? 'Đã đổi sang Tiếng Việt' : 'Switched to English');
  }

  if (topLangToggleBtn) topLangToggleBtn.addEventListener('click', () => switchLanguage());
  if (headerLangBtn) headerLangBtn.addEventListener('click', () => switchLanguage());

  if (settingsLangSelector) {
    settingsLangSelector.querySelectorAll('.segment-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        switchLanguage(btn.getAttribute('data-lang'));
      });
    });
  }

  // =========================================================================
  // 3. Tab Routing & Navigation
  // =========================================================================
  function switchTab(tabId) {
    state.currentTab = tabId;
    navItems.forEach(item => {
      item.classList.toggle('active', item.getAttribute('data-tab') === tabId);
    });

    tabContents.forEach(content => {
      content.classList.toggle('active', content.id === `tab-${tabId}`);
    });

    if (tabId === 'gallery') renderGallery();
    if (tabId === 'home') renderRecentPhotos();
    if (window.lucide) window.lucide.createIcons();
  }

  navItems.forEach(item => {
    item.addEventListener('click', () => switchTab(item.getAttribute('data-tab')));
  });

  // Home Quick Action Tiles
  document.querySelectorAll('[data-nav]').forEach(el => {
    el.addEventListener('click', () => switchTab(el.getAttribute('data-nav')));
  });

  const homeHeroStartBtn = document.getElementById('homeHeroStartBtn');
  if (homeHeroStartBtn) {
    homeHeroStartBtn.addEventListener('click', () => switchTab('enhance'));
  }

  const homeViewAllBtn = document.getElementById('homeViewAllBtn');
  if (homeViewAllBtn) {
    homeViewAllBtn.addEventListener('click', () => switchTab('gallery'));
  }

  // =========================================================================
  // 4. Toast Notification Engine
  // =========================================================================
  function showToast(message, duration = 2800) {
    if (!toastContainer) return;
    const toast = document.createElement('div');
    toast.className = 'toast-msg';
    toast.textContent = message;
    toastContainer.appendChild(toast);

    if (navigator.vibrate) navigator.vibrate(35);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(-10px)';
      toast.style.transition = 'all 0.2s ease-in';
      setTimeout(() => toast.remove(), 250);
    }, duration);
  }

  // =========================================================================
  // 5. Preset Sample Photos & Home Actions
  // =========================================================================
  const sampleCardPortrait = document.getElementById('sampleCardPortrait');
  if (sampleCardPortrait) {
    sampleCardPortrait.addEventListener('click', () => {
      loadPhotoToEnhance('sample_portrait.jpg');
      switchTab('enhance');
      showToast(window.i18n.t('toast_sample_loaded'));
    });
  }

  const sampleCardVintage = document.getElementById('sampleCardVintage');
  if (sampleCardVintage) {
    sampleCardVintage.addEventListener('click', () => {
      loadPhotoToColorize('sample_vintage.jpg');
      switchTab('colorize');
      showToast(window.i18n.t('toast_sample_loaded'));
    });
  }

  const homeQuickSampleBtn = document.getElementById('homeQuickSampleBtn');
  if (homeQuickSampleBtn) {
    homeQuickSampleBtn.addEventListener('click', () => {
      loadPhotoToEnhance('sample_portrait.jpg');
      switchTab('enhance');
      showToast(window.i18n.t('toast_sample_loaded'));
    });
  }

  // =========================================================================
  // 6. ENHANCE (Làm Nét & Khử Mờ)
  // =========================================================================
  enhanceDropzone.addEventListener('click', (e) => {
    if (e.target !== enhanceSamplePortraitBtn && !enhanceSamplePortraitBtn.contains(e.target)) {
      enhanceFileInput.click();
    }
  });

  enhanceFileInput.addEventListener('change', (e) => {
    if (e.target.files && e.target.files.length > 0) {
      const url = URL.createObjectURL(e.target.files[0]);
      loadPhotoToEnhance(url);
    }
  });

  enhanceSamplePortraitBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    loadPhotoToEnhance('sample_portrait.jpg');
    showToast(window.i18n.t('toast_sample_loaded'));
  });

  function loadPhotoToEnhance(url) {
    state.enhanceBeforeUrl = url;
    state.enhanceAfterUrl = null;

    enhanceDropzone.style.display = 'none';
    enhanceCompareWrap.style.display = 'flex';
    enhanceOptionsCard.style.display = 'block';
    enhanceActionsWrap.style.display = 'none';
    enhanceProgressCard.classList.remove('active');

    if (!state.enhanceSlider) {
      state.enhanceSlider = new window.ComparisonSlider(enhanceSliderMount);
    }
    // Initially display the unenhanced image on both sides until enhanced
    state.enhanceSlider.setImages(url, url);
  }

  // Scale selection
  enhanceScaleSelector.querySelectorAll('.segment-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      enhanceScaleSelector.querySelectorAll('.segment-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
    });
  });

  // Strength selection
  if (enhanceStrengthSelector) {
    enhanceStrengthSelector.querySelectorAll('.segment-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        enhanceStrengthSelector.querySelectorAll('.segment-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
      });
    });
  }

  startEnhanceBtn.addEventListener('click', async () => {
    if (!state.enhanceBeforeUrl) return;

    try {
      startEnhanceBtn.disabled = true;
      enhanceProgressCard.classList.add('active');
      state.enhanceSlider.showScanning(true);
      showToast(window.i18n.t('toast_processing'));

      const scale = parseInt(enhanceScaleSelector.querySelector('.segment-btn.active').getAttribute('data-scale'), 10);
      const strengthActive = enhanceStrengthSelector ? enhanceStrengthSelector.querySelector('.segment-btn.active') : null;
      const strength = strengthActive ? strengthActive.getAttribute('data-strength') : 'high';
      const faceEnhance = checkFaceEnhance.checked;
      const denoise = checkDenoise.checked;

      const res = await window.aiEngine.enhanceImage(state.enhanceBeforeUrl, {
        scale,
        strength,
        faceEnhance,
        denoise
      }, (percent) => {
        enhanceProgressBar.style.width = `${percent}%`;
        enhancePercentText.textContent = `${percent}%`;
      });

      state.enhanceAfterUrl = res.resultUrl;
      state.enhanceSlider.showScanning(false);
      state.enhanceSlider.setImages(state.enhanceBeforeUrl, state.enhanceAfterUrl);

      enhanceProgressCard.classList.remove('active');
      enhanceActionsWrap.style.display = 'flex';
      showToast(window.i18n.t('toast_success'));
    } catch (err) {
      console.error(err);
      state.enhanceSlider.showScanning(false);
      showToast(window.i18n.t('toast_error'));
    } finally {
      startEnhanceBtn.disabled = false;
    }
  });

  enhanceDownloadBtn.addEventListener('click', () => {
    if (state.enhanceAfterUrl) {
      downloadUrl(state.enhanceAfterUrl, `EnhanceFox_HD_${Date.now()}.png`);
      showToast(window.i18n.t('toast_downloaded'));
    }
  });

  enhanceSaveGalleryBtn.addEventListener('click', () => {
    if (state.enhanceAfterUrl) {
      saveToGallery({
        id: 'enh_' + Date.now(),
        type: 'enhance',
        title: 'Enhanced Portrait',
        date: new Date().toLocaleDateString(),
        url: state.enhanceAfterUrl
      });
      showToast(window.i18n.t('toast_saved'));
    }
  });

  enhanceReEditBtn.addEventListener('click', () => {
    enhanceDropzone.style.display = 'flex';
    enhanceCompareWrap.style.display = 'none';
    enhanceActionsWrap.style.display = 'none';
  });

  // =========================================================================
  // 7. COLORIZE (Tô Màu Ảnh Đen Trắng)
  // =========================================================================
  colorizeDropzone.addEventListener('click', (e) => {
    if (e.target !== colorizeSampleVintageBtn && !colorizeSampleVintageBtn.contains(e.target)) {
      colorizeFileInput.click();
    }
  });

  colorizeFileInput.addEventListener('change', (e) => {
    if (e.target.files && e.target.files.length > 0) {
      const url = URL.createObjectURL(e.target.files[0]);
      loadPhotoToColorize(url);
    }
  });

  colorizeSampleVintageBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    loadPhotoToColorize('sample_vintage.jpg');
    showToast(window.i18n.t('toast_sample_loaded'));
  });

  function loadPhotoToColorize(url) {
    state.colorizeBeforeUrl = url;
    state.colorizeAfterUrl = null;

    colorizeDropzone.style.display = 'none';
    colorizeCompareWrap.style.display = 'flex';
    colorizeOptionsCard.style.display = 'block';
    colorizeActionsWrap.style.display = 'none';
    colorizeProgressCard.classList.remove('active');

    if (!state.colorizeSlider) {
      state.colorizeSlider = new window.ComparisonSlider(colorizeSliderMount);
    }
    state.colorizeSlider.setImages(url, url);
  }

  colorizePaletteSelector.querySelectorAll('.segment-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      colorizePaletteSelector.querySelectorAll('.segment-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
    });
  });

  startColorizeBtn.addEventListener('click', async () => {
    if (!state.colorizeBeforeUrl) return;

    try {
      startColorizeBtn.disabled = true;
      colorizeProgressCard.classList.add('active');
      state.colorizeSlider.showScanning(true);
      showToast(window.i18n.t('toast_processing'));

      const palette = colorizePaletteSelector.querySelector('.segment-btn.active').getAttribute('data-palette');
      const res = await window.aiEngine.colorizeImage(state.colorizeBeforeUrl, palette, (percent) => {
        colorizeProgressBar.style.width = `${percent}%`;
        colorizePercentText.textContent = `${percent}%`;
      });

      state.colorizeAfterUrl = res.resultUrl;
      state.colorizeSlider.showScanning(false);
      state.colorizeSlider.setImages(state.colorizeBeforeUrl, state.colorizeAfterUrl);

      colorizeProgressCard.classList.remove('active');
      colorizeActionsWrap.style.display = 'flex';
      showToast(window.i18n.t('toast_success'));
    } catch (err) {
      console.error(err);
      state.colorizeSlider.showScanning(false);
      showToast(window.i18n.t('toast_error'));
    } finally {
      startColorizeBtn.disabled = false;
    }
  });

  colorizeDownloadBtn.addEventListener('click', () => {
    if (state.colorizeAfterUrl) {
      downloadUrl(state.colorizeAfterUrl, `EnhanceFox_Colorized_${Date.now()}.png`);
      showToast(window.i18n.t('toast_downloaded'));
    }
  });

  colorizeSaveGalleryBtn.addEventListener('click', () => {
    if (state.colorizeAfterUrl) {
      saveToGallery({
        id: 'col_' + Date.now(),
        type: 'colorize',
        title: 'Colorized Photo',
        date: new Date().toLocaleDateString(),
        url: state.colorizeAfterUrl
      });
      showToast(window.i18n.t('toast_saved'));
    }
  });

  colorizeReEditBtn.addEventListener('click', () => {
    colorizeDropzone.style.display = 'flex';
    colorizeCompareWrap.style.display = 'none';
    colorizeActionsWrap.style.display = 'none';
  });

  // =========================================================================
  // 8. RESTORE (Phục Chế Ảnh Cũ & Vết Xước)
  // =========================================================================
  restoreDropzone.addEventListener('click', (e) => {
    if (e.target !== restoreSampleVintageBtn && !restoreSampleVintageBtn.contains(e.target)) {
      restoreFileInput.click();
    }
  });

  restoreFileInput.addEventListener('change', (e) => {
    if (e.target.files && e.target.files.length > 0) {
      const url = URL.createObjectURL(e.target.files[0]);
      loadPhotoToRestore(url);
    }
  });

  restoreSampleVintageBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    loadPhotoToRestore('sample_vintage.jpg');
    showToast(window.i18n.t('toast_sample_loaded'));
  });

  function loadPhotoToRestore(url) {
    state.restoreBeforeUrl = url;
    state.restoreAfterUrl = null;

    restoreDropzone.style.display = 'none';
    restoreCompareWrap.style.display = 'flex';
    restoreOptionsCard.style.display = 'block';
    restoreActionsWrap.style.display = 'none';
    restoreProgressCard.classList.remove('active');

    if (!state.restoreSlider) {
      state.restoreSlider = new window.ComparisonSlider(restoreSliderMount);
    }
    state.restoreSlider.setImages(url, url);
  }

  startRestoreBtn.addEventListener('click', async () => {
    if (!state.restoreBeforeUrl) return;

    try {
      startRestoreBtn.disabled = true;
      restoreProgressCard.classList.add('active');
      state.restoreSlider.showScanning(true);
      showToast(window.i18n.t('toast_processing'));

      const removeScratches = checkScratch.checked;
      const defade = checkDefade.checked;

      const res = await window.aiEngine.restoreOldPhoto(state.restoreBeforeUrl, {
        removeScratches,
        defade
      }, (percent) => {
        restoreProgressBar.style.width = `${percent}%`;
        restorePercentText.textContent = `${percent}%`;
      });

      state.restoreAfterUrl = res.resultUrl;
      state.restoreSlider.showScanning(false);
      state.restoreSlider.setImages(state.restoreBeforeUrl, state.restoreAfterUrl);

      restoreProgressCard.classList.remove('active');
      restoreActionsWrap.style.display = 'flex';
      showToast(window.i18n.t('toast_success'));
    } catch (err) {
      console.error(err);
      state.restoreSlider.showScanning(false);
      showToast(window.i18n.t('toast_error'));
    } finally {
      startRestoreBtn.disabled = false;
    }
  });

  restoreDownloadBtn.addEventListener('click', () => {
    if (state.restoreAfterUrl) {
      downloadUrl(state.restoreAfterUrl, `EnhanceFox_Restored_${Date.now()}.png`);
      showToast(window.i18n.t('toast_downloaded'));
    }
  });

  restoreSaveGalleryBtn.addEventListener('click', () => {
    if (state.restoreAfterUrl) {
      saveToGallery({
        id: 'res_' + Date.now(),
        type: 'restore',
        title: 'Restored Vintage Photo',
        date: new Date().toLocaleDateString(),
        url: state.restoreAfterUrl
      });
      showToast(window.i18n.t('toast_saved'));
    }
  });

  restoreReEditBtn.addEventListener('click', () => {
    restoreDropzone.style.display = 'flex';
    restoreCompareWrap.style.display = 'none';
    restoreActionsWrap.style.display = 'none';
  });

  // =========================================================================
  // 9. GALLERY & RECENT LIST
  // =========================================================================
  function saveToGallery(item) {
    state.gallery.unshift(item);
    try {
      localStorage.setItem('enhancefox_gallery', JSON.stringify(state.gallery.slice(0, 30)));
    } catch (e) {
      console.warn("Storage quota exceeded:", e);
    }
    renderGallery();
    renderRecentPhotos();
  }

  galleryFilterSelector.querySelectorAll('.segment-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      galleryFilterSelector.querySelectorAll('.segment-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      state.activeGalleryFilter = btn.getAttribute('data-filter');
      renderGallery();
    });
  });

  function renderGallery() {
    if (!galleryGrid) return;
    galleryGrid.innerHTML = '';

    const filtered = state.activeGalleryFilter === 'all'
      ? state.gallery
      : state.gallery.filter(i => i.type === state.activeGalleryFilter);

    if (filtered.length === 0) {
      galleryGrid.innerHTML = `
        <div style="grid-column: span 2; text-align: center; padding: 40px 16px; color: var(--text-muted); display: flex; flex-direction: column; align-items: center; gap: 10px;">
          <i data-lucide="image" style="width: 44px; height: 44px; color: var(--text-dim);"></i>
          <div style="font-weight: 700; font-size: 15px;">${window.i18n.t('gallery_empty_title')}</div>
          <div style="font-size: 12px;">${window.i18n.t('gallery_empty_desc')}</div>
        </div>
      `;
      if (window.lucide) window.lucide.createIcons();
      return;
    }

    filtered.forEach(item => {
      const card = document.createElement('div');
      card.className = 'gallery-card';
      const badgeText = item.type === 'enhance' ? 'HD' : item.type === 'colorize' ? 'COLOR' : 'RESTORE';
      card.innerHTML = `
        <span class="gallery-card-badge">${badgeText}</span>
        <img src="${item.url}" alt="${item.title}" class="gallery-thumb">
        <div class="gallery-card-actions">
          <span style="font-size: 11px; color: var(--text-muted);">${item.date}</span>
          <div style="display: flex; gap: 4px;">
            <button class="icon-action-btn dl-btn" style="width: 28px; height: 28px;" title="Tải về">
              <i data-lucide="download" style="width: 13px; height: 13px;"></i>
            </button>
            <button class="icon-action-btn del-btn" style="width: 28px; height: 28px; color: var(--danger);" title="Xóa">
              <i data-lucide="trash-2" style="width: 13px; height: 13px;"></i>
            </button>
          </div>
        </div>
      `;

      card.querySelector('.dl-btn').addEventListener('click', (e) => {
        e.stopPropagation();
        downloadUrl(item.url, `${item.title}.png`);
      });

      card.querySelector('.del-btn').addEventListener('click', (e) => {
        e.stopPropagation();
        state.gallery = state.gallery.filter(x => x.id !== item.id);
        localStorage.setItem('enhancefox_gallery', JSON.stringify(state.gallery));
        renderGallery();
        renderRecentPhotos();
        showToast(window.i18n.t('toast_deleted'));
      });

      galleryGrid.appendChild(card);
    });

    if (window.lucide) window.lucide.createIcons();
  }

  function renderRecentPhotos() {
    if (!homeRecentPhotosList) return;
    homeRecentPhotosList.innerHTML = '';
    const recent = state.gallery.slice(0, 2);

    if (recent.length === 0) {
      homeRecentPhotosList.innerHTML = `
        <div style="grid-column: span 2; padding: 20px; text-align: center; font-size: 12px; color: var(--text-muted);">
          ${window.i18n.t('no_recent')}
        </div>
      `;
      return;
    }

    recent.forEach(item => {
      const card = document.createElement('div');
      card.className = 'gallery-card';
      const badgeText = item.type === 'enhance' ? 'HD' : item.type === 'colorize' ? 'COLOR' : 'RESTORE';
      card.innerHTML = `
        <span class="gallery-card-badge">${badgeText}</span>
        <img src="${item.url}" alt="${item.title}" class="gallery-thumb">
        <div class="gallery-card-actions">
          <span style="font-size: 11px; color: var(--text-muted);">${item.date}</span>
          <button class="icon-action-btn dl-btn" style="width: 28px; height: 28px;" title="Tải về">
            <i data-lucide="download" style="width: 13px; height: 13px;"></i>
          </button>
        </div>
      `;
      card.querySelector('.dl-btn').addEventListener('click', () => downloadUrl(item.url, `${item.title}.png`));
      homeRecentPhotosList.appendChild(card);
    });

    if (window.lucide) window.lucide.createIcons();
  }

  clearGalleryBtn.addEventListener('click', () => {
    if (confirm(window.i18n.t('confirm_clear_gallery'))) {
      state.gallery = [];
      localStorage.removeItem('enhancefox_gallery');
      renderGallery();
      renderRecentPhotos();
      showToast(window.i18n.t('toast_deleted'));
    }
  });

  // =========================================================================
  // 10. Modals & Helpers
  // =========================================================================
  headerSettingsBtn.addEventListener('click', () => {
    settingsModal.classList.add('active');
    if (window.lucide) window.lucide.createIcons();
  });

  closeSettingsModal.addEventListener('click', () => settingsModal.classList.remove('active'));
  saveSettingsBtn.addEventListener('click', () => settingsModal.classList.remove('active'));
  settingsModal.addEventListener('click', (e) => {
    if (e.target === settingsModal) settingsModal.classList.remove('active');
  });

  function downloadUrl(url, filename) {
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }

  // Pre-seed initial sample gallery demonstration
  if (state.gallery.length === 0) {
    state.gallery.push({
      id: 'demo_1',
      type: 'enhance',
      title: 'Sample Portrait HD',
      date: new Date().toLocaleDateString(),
      url: 'sample_portrait.jpg'
    });
    renderRecentPhotos();
  }
});
