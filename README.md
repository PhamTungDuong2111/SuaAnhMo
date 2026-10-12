# EnhanceFox AI - Sửa Ảnh Mờ & Phục Chế Kỷ Niệm (Android Native App)

> **Dự án Android Native thuần (Kotlin + Jetpack Compose + Material 3)**  
> **Package Name / Application ID:** `com.enhancefox.photoenhancer`  
> **Mục tiêu bài làm:** Ứng dụng Android chuẩn Native phục hồi ảnh mờ, làm nét chân dung, tô màu ảnh đen trắng và phục chế ảnh cũ với chức năng song ngữ **Tiếng Việt 🇻🇳 & Tiếng Anh 🇬🇧**.  
> **File cài đặt nhanh:** Đã build sẵn file APK tại thư mục gốc: [EnhanceFoxAI-v1.0.apk](file:///c:/Users/Admin/Downloads/SuaAnhMo/EnhanceFoxAI-v1.0.apk)

---

## 🦊 Kiến trúc & Công nghệ (100% Android Native)

* **Ngôn ngữ:** Kotlin 1.9.23
* **Giao diện:** Jetpack Compose + Material 3 + Dark Modern Theme
* **Quản lý trạng thái:** Android ViewModel (`MainViewModel`) + Kotlin Coroutines (`StateFlow`)
* **Thuật toán xử lý ảnh:**
  * **AIEngine thuần Kotlin**: Chạy nền trên `Dispatchers.Default`, xử lý trực tiếp mảng pixel bitmap tốc độ cực nhanh.
  * **Làm nét & Khử mờ (Deblur & Unsharp Masking)**: Tách kênh Luminance ($Y$), phân tách tần số đa tầng (Fine Details: mắt, lông mi, tóc; Macro Contours: khôi phục viền out-of-focus), nâng độ tương phản vi mô khuôn mặt (Face Clarity S-curve), khử hạt nhiễu thích ứng (Adaptive Coring Denoise).
  * **Tô màu AI (Colorizer)**: Phân vùng sáng - tối tự động để hòa sắc độ da người chân thực (Natural Warm), tông màu phim Kodachrome 1970s hoài niệm (Vintage), và chế độ tươi tắn rực rỡ (Modern Vibrant).
  * **Phục chế ảnh cũ & Vết xước (Restore & Inpainting)**: Tự động kéo dải tương phản loại bỏ màu ố vàng loang lổ (Auto-levels Defade); thuật toán nội suy trung vị xóa vết nứt rách, nếp gấp giấy ảnh cũ.
  * **Thanh trượt so sánh (Comparison Slider)**: Kéo thả trực quan trước/sau, phóng to cận cảnh 2.5X, chạm giữ để xem tức thì ảnh gốc.
  * **Ảnh mẫu tích hợp sẵn**: Đã nhúng sẵn 2 ảnh mẫu (`sample_portrait.jpg` và `sample_vintage.jpg`) giúp người chấm/người dùng thử nghiệm ngay trong 1 chạm mà không cần tải ảnh ngoài.
  * **Song ngữ động**: Chuyển đổi mượt mà giữa Tiếng Việt 🇻🇳 và Tiếng Anh 🇬🇧 chỉ với 1 click ở góc trên cùng.
  * **Lưu & Chia sẻ**: Lưu trực tiếp vào thư viện máy (MediaStore Pictures) và chia sẻ qua FileProvider.

---

## 📱 Cách chạy và kiểm tra ứng dụng

### Cách 1: Cài đặt trực tiếp file APK vào điện thoại (Nhanh nhất)
File APK đã được biên dịch thành công sẵn sàng cài đặt:
👉 **[EnhanceFoxAI-v1.0.apk](file:///c:/Users/Admin/Downloads/SuaAnhMo/EnhanceFoxAI-v1.0.apk)** (hoặc tại `app/build/outputs/apk/debug/app-debug.apk`)
* Bạn chỉ cần chép file này sang điện thoại Android (qua Zalo, Google Drive, hoặc cắm cáp USB) rồi nhấn cài đặt.

### Cách 2: Chạy trực tiếp từ Android Studio
1. Mở **Android Studio**.
2. Chọn **Open** và trỏ đến thư mục: `C:\Users\Admin\Downloads\SuaAnhMo`
3. Đợi Android Studio sync Gradle (dự án dùng Gradle 8.4 đã cache sẵn trên máy nên sync rất nhanh trong vài giây).
4. Cắm điện thoại Android vào máy tính (hoặc mở máy ảo Emulator).
5. Nhấn nút tam giác màu xanh **Run 'app'** (hoặc phím tắt `Shift + F10`) để cài và chạy ứng dụng lên máy.

### Cách 3: Build lại APK bằng dòng lệnh
Mở Terminal / PowerShell tại thư mục dự án và chạy:
```powershell
.\gradlew.bat assembleDebug
```
File APK đầu ra sẽ nằm tại `app\build\outputs\apk\debug\app-debug.apk`.

---

## 📁 Cấu trúc thư mục mã nguồn Kotlin

```
app/src/main/
├── AndroidManifest.xml
├── assets/
│   ├── sample_portrait.jpg       # Ảnh mẫu chân dung mờ
│   └── sample_vintage.jpg        # Ảnh mẫu đen trắng cũ
├── java/com/enhancefox/photoenhancer/
│   ├── MainActivity.kt           # Màn hình chính điều hướng Compose
│   ├── MainViewModel.kt          # Quản lý luồng xử lý và trạng thái
│   ├── engine/
│   │   └── AIEngine.kt           # Thuật toán làm nét, tô màu & phục chế
│   ├── model/
│   │   ├── EnhanceOptions.kt     # Các tham số tùy chọn xử lý
│   │   └── Language.kt           # Từ điển song ngữ Việt - Anh
│   ├── theme/
│   │   ├── Color.kt              # Bảng màu Dark Material 3 cao cấp
│   │   └── Theme.kt              # Cấu hình theme ứng dụng
│   └── ui/
│       ├── components/
│       │   ├── BottomNavBar.kt   # Thanh điều hướng 5 tab
│       │   ├── ComparisonSlider.kt # Thanh trượt so sánh Trước & Sau
│       │   └── TopAppBar.kt      # Thanh tiêu đề & nút đổi ngôn ngữ
│       └── screens/
│           ├── HomeScreen.kt     # Trang chủ & thẻ công cụ nhanh
│           ├── EnhanceScreen.kt  # Màn hình làm nét & khử mờ
│           ├── ColorizeScreen.kt # Màn hình tô màu AI
│           ├── RestoreScreen.kt  # Màn hình phục chế ảnh cũ
│           └── GalleryScreen.kt  # Màn hình quản lý ảnh đã lưu
└── res/
    ├── drawable/app_icon.png
    ├── values/themes.xml
    └── xml/file_paths.xml
```
