// Internationalization (i18n) Module for EnhanceFox AI
// Supports English (en) and Vietnamese (vi)

const translations = {
  vi: {
    app_name: "EnhanceFox AI",
    app_tagline: "Sửa ảnh mờ & Phục chế ảnh cũ chuyên nghiệp",
    package_name: "Gói: com.enhancefox.photoenhancer",
    
    // Navigation
    nav_home: "Trang chủ",
    nav_enhance: "Làm nét",
    nav_colorize: "Tô màu AI",
    nav_restore: "Phục chế",
    nav_gallery: "Thư viện",
    nav_settings: "Cài đặt",
    
    // Status Bar & Controls
    toggle_view_phone: "Chế độ điện thoại",
    toggle_view_full: "Toàn màn hình",
    lang_btn: "Tiếng Việt",
    
    // Home Tab
    hero_badge: "Công nghệ AI Thế hệ mới",
    hero_title: "Làm Nét Ảnh Mờ & Phục Chế Kỷ Niệm",
    hero_desc: "Biến những bức ảnh mờ, nhòe, cũ kỹ thành ảnh có độ nét cực cao HD & 4K chỉ trong tích tắc.",
    hero_btn: "Bắt đầu làm nét ngay",
    hero_sample_btn: "Thử ảnh mẫu",
    
    quick_tools_title: "Công cụ AI nổi bật",
    tool_enhance_title: "Làm nét & Khử mờ",
    tool_enhance_desc: "Tăng độ chi tiết khuôn mặt, sửa ảnh out nét",
    tool_colorize_title: "Tô màu ảnh đen trắng",
    tool_colorize_desc: "Phục chế màu sắc sống động cho ảnh xưa",
    tool_restore_title: "Phục chế ảnh cũ & Vết xước",
    tool_restore_desc: "Xóa nếp gấp, vết xước, khử ố vàng",
    tool_denoise_title: "Khử nhiễu & Làm mịn",
    tool_denoise_desc: "Xóa hạt ISO ban đêm, mịn da tự nhiên",
    
    sample_photos_title: "Ảnh mẫu trải nghiệm nhanh",
    sample_portrait_title: "Ảnh chân dung mờ",
    sample_portrait_desc: "Sửa ảnh nhòe out nét thành chân dung HD",
    sample_vintage_title: "Ảnh đen trắng thập niên 50",
    sample_vintage_desc: "Khử vết xước cũ và tô màu sống động",
    
    recent_title: "Ảnh đã xử lý gần đây",
    no_recent: "Chưa có ảnh nào. Hãy chọn hoặc tải ảnh lên để trải nghiệm!",
    view_all: "Xem tất cả",
    
    // Enhance Tab (Làm nét)
    enhance_upload_title: "Chọn ảnh cần làm nét & sửa mờ",
    enhance_upload_hint: "Chạm để duyệt hoặc kéo thả ảnh vào đây (JPG, PNG, WEBP)",
    btn_use_sample_portrait: "Dùng ảnh chân dung mẫu",
    enhance_options_title: "Tùy chọn độ phân giải AI",
    opt_clarity_hd: "HD Tiêu chuẩn (Làm nét tự nhiên)",
    opt_clarity_2x: "Ultra 2X (Tăng nét gấp đôi)",
    opt_clarity_4x: "Studio 4K (Siêu phân giải khuôn mặt)",
    opt_sharpness_level: "Mức độ làm nét & Khử mờ:",
    strength_natural: "Tự nhiên",
    strength_high: "Sắc nét cao (Khuyên dùng)",
    strength_ultra: "Cực nét Ultra HD",
    btn_zoom_detail: "Phóng to 2.5X xem chi tiết",
    btn_zoom_reset: "Thu nhỏ 1X",
    btn_hold_compare: "Chạm giữ xem GỐC",
    opt_face_beautify: "Làm đẹp chi tiết mắt & môi",
    opt_noise_reduction: "Khử hạt nhiễu nền",
    btn_start_enhance: "Bắt đầu sửa ảnh mờ",
    btn_enhancing: "AI đang phân tích & tái tạo nét...",
    
    // Colorize Tab (Tô màu)
    colorize_title: "Tô màu ảnh đen trắng bằng AI",
    colorize_hint: "Biến những bức ảnh trắng đen xưa thành ảnh màu chân thực",
    btn_use_sample_vintage: "Dùng ảnh xưa mẫu",
    colorize_palette_label: "Tông màu phục chế:",
    palette_natural: "Tự nhiên ấm áp (Khuyên dùng)",
    palette_vintage: "Màu hoài niệm thập niên 70",
    palette_vibrant: "Tươi tắn hiện đại",
    btn_start_colorize: "Bắt đầu tô màu AI",
    btn_colorizing: "AI đang nhận diện & tô màu...",
    
    // Restore Tab (Phục chế ảnh cũ)
    restore_title: "Phục chế ảnh cũ & Tẩy vết xước",
    restore_hint: "Sửa ảnh ố vàng, xóa nếp gấp rách, vết xước trên giấy ảnh cũ",
    restore_mode_scratch: "Xóa vết xước & nếp gấp",
    restore_mode_defade: "Khử ố vàng & cân bằng độ sáng",
    restore_mode_sharp: "Tái tạo độ nét gốc",
    btn_start_restore: "Phục chế ảnh cũ ngay",
    btn_restoring: "Đang phục chế ảnh...",
    
    // Comparison Slider & Viewer
    compare_title: "So sánh Trước & Sau",
    label_before: "GỐC",
    label_after: "ĐÃ SỬA",
    btn_download: "Lưu ảnh về máy",
    btn_save_gallery: "Lưu vào Thư viện",
    btn_share: "Chia sẻ",
    btn_re_edit: "Chọn ảnh khác",
    tip_slider: "Kéo vạch ở giữa sang trái / phải để so sánh độ nét",
    
    // Gallery Tab
    gallery_title: "Thư viện ảnh của bạn",
    tab_all: "Tất cả",
    tab_enhanced: "Đã làm nét",
    tab_colorized: "Đã tô màu",
    tab_restored: "Đã phục chế",
    gallery_empty_title: "Thư viện trống",
    gallery_empty_desc: "Những bức ảnh bạn sửa hoặc phục chế sẽ tự động lưu tại đây",
    btn_clear_gallery: "Xóa toàn bộ thư viện",
    confirm_clear_gallery: "Bạn có chắc muốn xóa tất cả ảnh trong thư viện?",
    
    // Settings Modal
    settings_title: "Cài đặt EnhanceFox",
    settings_lang: "Ngôn ngữ ứng dụng",
    settings_lang_desc: "Chuyển đổi giữa Tiếng Việt và Tiếng Anh",
    settings_export_quality: "Chất lượng ảnh xuất:",
    quality_ultra: "Cực cao (PNG không nén)",
    quality_high: "Cao (JPEG 95%)",
    settings_haptics: "Rung phản hồi khi vuốt chạm",
    settings_about: "Thông tin ứng dụng",
    settings_app_name: "EnhanceFox AI - Photo Enhancer Pro for Android",
    settings_version: "Phiên bản: 3.5.2 (Tương thích Android 14+)",
    settings_developer: "Phát triển theo mẫu app: EnhanceFox AI",
    btn_close: "Đóng",
    
    // Toasts
    toast_processing: "AI đang xử lý hình ảnh của bạn...",
    toast_success: "Xử lý thành công! Hãy kéo thanh trượt để so sánh.",
    toast_saved: "Đã lưu ảnh vào Thư viện của bạn!",
    toast_downloaded: "Đang tải ảnh chất lượng cao về máy...",
    toast_sample_loaded: "Đã tải ảnh mẫu thành công!",
    toast_deleted: "Đã xóa ảnh khỏi thư viện",
    toast_error: "Có lỗi khi xử lý ảnh. Vui lòng thử lại!"
  },
  
  en: {
    app_name: "EnhanceFox AI",
    app_tagline: "AI Photo Enhancer & Old Photo Restoration",
    package_name: "Package: com.enhancefox.photoenhancer",
    
    // Navigation
    nav_home: "Home",
    nav_enhance: "Enhance",
    nav_colorize: "Colorize",
    nav_restore: "Restore",
    nav_gallery: "Gallery",
    nav_settings: "Settings",
    
    // Status Bar & Controls
    toggle_view_phone: "Phone Mode",
    toggle_view_full: "Fullscreen",
    lang_btn: "English",
    
    // Home Tab
    hero_badge: "Next-Gen AI Engine",
    hero_title: "Unblur Photos & Restore Memories",
    hero_desc: "Transform blurry, pixelated, old photos into stunning crystal-clear HD & 4K portraits in seconds.",
    hero_btn: "Enhance Blurry Photo",
    hero_sample_btn: "Try Sample Photo",
    
    quick_tools_title: "AI Power Tools",
    tool_enhance_title: "Unblur & Sharpen",
    tool_enhance_desc: "Enhance facial details, fix out-of-focus blur",
    tool_colorize_title: "Colorize B&W Photos",
    tool_colorize_desc: "Breathe vivid, realistic colors into old photos",
    tool_restore_title: "Old Photo Restoration",
    tool_restore_desc: "Remove scratches, tears, and yellow stains",
    tool_denoise_title: "Denoise & Retouch",
    tool_denoise_desc: "Clean low-light grain, natural skin polish",
    
    sample_photos_title: "Quick Test Samples",
    sample_portrait_title: "Blurry Portrait",
    sample_portrait_desc: "Fix out-of-focus photo into sharp HD portrait",
    sample_vintage_title: "1950s Vintage B&W",
    sample_vintage_desc: "Remove scratches and add vivid colors",
    
    recent_title: "Recently Enhanced",
    no_recent: "No photos yet. Select or upload a photo to start!",
    view_all: "View all",
    
    // Enhance Tab
    enhance_upload_title: "Select Photo to Unblur & Enhance",
    enhance_upload_hint: "Tap to browse or drop photo here (JPG, PNG, WEBP)",
    btn_use_sample_portrait: "Use Sample Portrait",
    enhance_options_title: "AI Resolution Options",
    opt_clarity_hd: "HD Standard (Natural Sharpening)",
    opt_clarity_2x: "Ultra 2X (Double Sharpness)",
    opt_clarity_4x: "Studio 4K (Super Face Resolution)",
    opt_sharpness_level: "Enhancement & Sharpness Level:",
    strength_natural: "Natural",
    strength_high: "High Clarity (Recommended)",
    strength_ultra: "Ultra HD Deblur",
    btn_zoom_detail: "2.5X Detail Zoom",
    btn_zoom_reset: "1X Normal View",
    btn_hold_compare: "Hold to view ORIGINAL",
    opt_face_beautify: "Face & Eye Detail Refinement",
    opt_noise_reduction: "Background Denoising",
    btn_start_enhance: "Start Enhancing Now",
    btn_enhancing: "AI is analyzing & restoring pixels...",
    
    // Colorize Tab
    colorize_title: "AI Black & White Colorizer",
    colorize_hint: "Turn historical grayscale memories into vibrant realistic colors",
    btn_use_sample_vintage: "Use Sample Vintage Photo",
    colorize_palette_label: "Color Palette Mode:",
    palette_natural: "Warm Natural (Recommended)",
    palette_vintage: "Vintage 1970s Film",
    palette_vibrant: "Modern Vibrant",
    btn_start_colorize: "Start Colorizing",
    btn_colorizing: "AI is colorizing picture...",
    
    // Restore Tab
    restore_title: "Old Photo Restore & Scratch Removal",
    restore_hint: "Erase creases, yellow age tint, and restore contrast",
    restore_mode_scratch: "Remove Scratches & Creases",
    restore_mode_defade: "Defade & Balance Exposure",
    restore_mode_sharp: "Recover Base Sharpness",
    btn_start_restore: "Restore Old Photo Now",
    btn_restoring: "Restoring photo...",
    
    // Comparison Slider & Viewer
    compare_title: "Before & After Comparison",
    label_before: "BEFORE",
    label_after: "AFTER",
    btn_download: "Download HD Photo",
    btn_save_gallery: "Save to Gallery",
    btn_share: "Share",
    btn_re_edit: "Enhance Another",
    tip_slider: "Drag the center bar left / right to compare clarity",
    
    // Gallery Tab
    gallery_title: "Your Photo Gallery",
    tab_all: "All",
    tab_enhanced: "Enhanced",
    tab_colorized: "Colorized",
    tab_restored: "Restored",
    gallery_empty_title: "Gallery is Empty",
    gallery_empty_desc: "Photos you enhance or restore will automatically appear here",
    btn_clear_gallery: "Clear Gallery",
    confirm_clear_gallery: "Are you sure you want to delete all photos in gallery?",
    
    // Settings Modal
    settings_title: "EnhanceFox Settings",
    settings_lang: "App Language",
    settings_lang_desc: "Switch between Vietnamese and English",
    settings_export_quality: "Export Image Quality:",
    quality_ultra: "Ultra (Lossless PNG)",
    quality_high: "High (JPEG 95%)",
    settings_haptics: "Haptic Feedback (Vibration)",
    settings_about: "About EnhanceFox",
    settings_app_name: "EnhanceFox AI - Photo Enhancer Pro for Android",
    settings_version: "Version: 3.5.2 (Android 14+ Supported)",
    settings_developer: "Inspired by EnhanceFox AI Photo Enhancer",
    btn_close: "Close",
    
    // Toasts
    toast_processing: "AI is enhancing your photo...",
    toast_success: "Enhanced successfully! Slide to compare before & after.",
    toast_saved: "Saved to your Gallery!",
    toast_downloaded: "Downloading ultra HD photo...",
    toast_sample_loaded: "Sample photo loaded successfully!",
    toast_deleted: "Photo removed from gallery",
    toast_error: "Error processing photo. Please try again!"
  }
};

class I18nManager {
  constructor() {
    this.currentLang = localStorage.getItem("enhancefox_lang") || "vi"; // Default to Vietnamese
  }

  setLanguage(lang) {
    if (translations[lang]) {
      this.currentLang = lang;
      localStorage.setItem("enhancefox_lang", lang);
      this.applyTranslations();
      window.dispatchEvent(new CustomEvent("languageChanged", { detail: { lang } }));
    }
  }

  toggleLanguage() {
    const next = this.currentLang === "vi" ? "en" : "vi";
    this.setLanguage(next);
    return next;
  }

  t(key) {
    return translations[this.currentLang][key] || key;
  }

  applyTranslations() {
    const elements = document.querySelectorAll("[data-i18n]");
    elements.forEach(el => {
      const key = el.getAttribute("data-i18n");
      if (translations[this.currentLang][key]) {
        if (el.tagName === "INPUT" || el.tagName === "TEXTAREA") {
          el.placeholder = translations[this.currentLang][key];
        } else {
          el.textContent = translations[this.currentLang][key];
        }
      }
    });

    document.documentElement.lang = this.currentLang;
    document.title = this.t("app_name") + " - " + this.t("app_tagline");

    const langDisplays = document.querySelectorAll(".lang-display-text");
    langDisplays.forEach(el => {
      el.textContent = this.currentLang === "vi" ? "Tiếng Việt (VN)" : "English (EN)";
    });

    const langFlags = document.querySelectorAll(".lang-flag");
    langFlags.forEach(el => {
      el.textContent = this.currentLang === "vi" ? "🇻🇳" : "🇬🇧";
    });
  }
}

window.i18n = new I18nManager();
