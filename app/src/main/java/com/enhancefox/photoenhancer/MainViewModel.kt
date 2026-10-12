package com.enhancefox.photoenhancer

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.enhancefox.photoenhancer.engine.AIEngine
import com.enhancefox.photoenhancer.model.AppLanguage
import com.enhancefox.photoenhancer.model.ColorizePalette
import com.enhancefox.photoenhancer.model.EnhanceMode
import com.enhancefox.photoenhancer.model.EnhanceOptions
import com.enhancefox.photoenhancer.model.GalleryItem
import com.enhancefox.photoenhancer.model.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class MainUiState(
    val language: AppLanguage = AppLanguage.VI,
    val currentMode: EnhanceMode = EnhanceMode.HOME,

    val samplePortrait: Bitmap? = null,
    val sampleVintage: Bitmap? = null,

    val enhanceSource: Bitmap? = null,
    val enhanceResult: Bitmap? = null,

    val colorizeSource: Bitmap? = null,
    val colorizeResult: Bitmap? = null,

    val restoreSource: Bitmap? = null,
    val restoreResult: Bitmap? = null,

    val isProcessing: Boolean = false,
    val progress: Int = 0,
    val stageText: String = "",

    val galleryItems: List<GalleryItem> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val aiEngine = AIEngine()
    private val prefs = application.getSharedPreferences("enhancefox_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        loadSampleBitmaps()
        loadGalleryItems()
    }

    private fun loadSettings() {
        val langCode = prefs.getString("app_lang", "vi") ?: "vi"
        val lang = if (langCode == "en") AppLanguage.EN else AppLanguage.VI
        _uiState.update { it.copy(language = lang) }
    }

    fun toggleLanguage() {
        val nextLang = if (_uiState.value.language == AppLanguage.VI) AppLanguage.EN else AppLanguage.VI
        prefs.edit().putString("app_lang", nextLang.code).apply()
        _uiState.update { it.copy(language = nextLang) }
    }

    fun setMode(mode: EnhanceMode) {
        _uiState.update { it.copy(currentMode = mode) }
    }

    private fun loadSampleBitmaps() {
        viewModelScope.launch {
            val portrait = aiEngine.loadBitmapFromAssets(getApplication(), "sample_portrait.jpg")
            val vintage = aiEngine.loadBitmapFromAssets(getApplication(), "sample_vintage.jpg")
            _uiState.update {
                it.copy(
                    samplePortrait = portrait,
                    sampleVintage = vintage
                )
            }
        }
    }

    fun useSample(sampleName: String, targetMode: EnhanceMode? = null) {
        viewModelScope.launch {
            val bmp = aiEngine.loadBitmapFromAssets(getApplication(), sampleName) ?: return@launch
            val mode = targetMode ?: _uiState.value.currentMode
            _uiState.update { state ->
                when (mode) {
                    EnhanceMode.ENHANCE -> state.copy(
                        enhanceSource = bmp,
                        enhanceResult = null,
                        currentMode = EnhanceMode.ENHANCE
                    )
                    EnhanceMode.COLORIZE -> state.copy(
                        colorizeSource = bmp,
                        colorizeResult = null,
                        currentMode = EnhanceMode.COLORIZE
                    )
                    EnhanceMode.RESTORE -> state.copy(
                        restoreSource = bmp,
                        restoreResult = null,
                        currentMode = EnhanceMode.RESTORE
                    )
                    else -> state.copy(
                        enhanceSource = bmp,
                        enhanceResult = null,
                        currentMode = EnhanceMode.ENHANCE
                    )
                }
            }
        }
    }

    fun loadFromUri(uri: Uri) {
        viewModelScope.launch {
            val bmp = aiEngine.loadBitmapFromUri(getApplication(), uri) ?: return@launch
            _uiState.update { state ->
                when (state.currentMode) {
                    EnhanceMode.ENHANCE -> state.copy(enhanceSource = bmp, enhanceResult = null)
                    EnhanceMode.COLORIZE -> state.copy(colorizeSource = bmp, colorizeResult = null)
                    EnhanceMode.RESTORE -> state.copy(restoreSource = bmp, restoreResult = null)
                    else -> state.copy(enhanceSource = bmp, enhanceResult = null, currentMode = EnhanceMode.ENHANCE)
                }
            }
        }
    }

    fun processEnhance(options: EnhanceOptions) {
        val src = _uiState.value.enhanceSource ?: return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProcessing = true,
                    progress = 5,
                    stageText = Strings.get("stage_analyzing", it.language)
                )
            }

            try {
                val result = aiEngine.enhanceBitmap(src, options) { p ->
                    val stage = when {
                        p < 40 -> Strings.get("stage_analyzing", _uiState.value.language)
                        p < 80 -> Strings.get("stage_deblurring", _uiState.value.language)
                        else -> Strings.get("stage_polishing", _uiState.value.language)
                    }
                    _uiState.update { it.copy(progress = p, stageText = stage) }
                }

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        enhanceResult = result,
                        progress = 100
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isProcessing = false) }
                Toast.makeText(getApplication(), "Lỗi xử lý: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun processColorize(palette: ColorizePalette) {
        val src = _uiState.value.colorizeSource ?: return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProcessing = true,
                    progress = 10,
                    stageText = Strings.get("stage_analyzing", it.language)
                )
            }

            try {
                val result = aiEngine.colorizeBitmap(src, palette) { p ->
                    val stage = when {
                        p < 50 -> Strings.get("stage_analyzing", _uiState.value.language)
                        else -> Strings.get("stage_polishing", _uiState.value.language)
                    }
                    _uiState.update { it.copy(progress = p, stageText = stage) }
                }

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        colorizeResult = result,
                        progress = 100
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isProcessing = false) }
                Toast.makeText(getApplication(), "Lỗi xử lý: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun processRestore(removeScratches: Boolean, defade: Boolean) {
        val src = _uiState.value.restoreSource ?: return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isProcessing = true,
                    progress = 10,
                    stageText = Strings.get("stage_analyzing", it.language)
                )
            }

            try {
                val result = aiEngine.restoreOldPhoto(src, removeScratches, defade) { p ->
                    val stage = when {
                        p < 50 -> Strings.get("stage_analyzing", _uiState.value.language)
                        p < 80 -> Strings.get("stage_deblurring", _uiState.value.language)
                        else -> Strings.get("stage_polishing", _uiState.value.language)
                    }
                    _uiState.update { it.copy(progress = p, stageText = stage) }
                }

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        restoreResult = result,
                        progress = 100
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isProcessing = false) }
                Toast.makeText(getApplication(), "Lỗi xử lý: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveActiveResult() {
        val state = _uiState.value
        val (bitmap, title, mode) = when (state.currentMode) {
            EnhanceMode.ENHANCE -> Triple(state.enhanceResult, "Enhance HD", EnhanceMode.ENHANCE)
            EnhanceMode.COLORIZE -> Triple(state.colorizeResult, "Colorize AI", EnhanceMode.COLORIZE)
            EnhanceMode.RESTORE -> Triple(state.restoreResult, "Restore Old Photo", EnhanceMode.RESTORE)
            else -> Triple(null, "", EnhanceMode.HOME)
        }

        if (bitmap == null) return

        viewModelScope.launch {
            val uri = aiEngine.saveToGallery(getApplication(), bitmap, title)
            val internalPath = aiEngine.saveToInternalStorage(getApplication(), bitmap, title)

            if (uri != null) {
                Toast.makeText(
                    getApplication(),
                    Strings.get("saved_success", state.language),
                    Toast.LENGTH_SHORT
                ).show()

                // Add to internal gallery history
                val newItem = GalleryItem(
                    title = title,
                    mode = mode,
                    filePath = internalPath
                )
                val updatedList = listOf(newItem) + state.galleryItems
                _uiState.update { it.copy(galleryItems = updatedList) }
                saveGalleryItems(updatedList)
            }
        }
    }

    fun shareActiveResult() {
        val state = _uiState.value
        val bitmap = when (state.currentMode) {
            EnhanceMode.ENHANCE -> state.enhanceResult
            EnhanceMode.COLORIZE -> state.colorizeResult
            EnhanceMode.RESTORE -> state.restoreResult
            else -> null
        } ?: return

        viewModelScope.launch {
            val path = aiEngine.saveToInternalStorage(getApplication(), bitmap, "share")
            val file = File(path)
            shareFile(file)
        }
    }

    fun shareGalleryItem(item: GalleryItem) {
        val file = File(item.filePath)
        if (file.exists()) {
            shareFile(file)
        }
    }

    private fun shareFile(file: File) {
        val context = getApplication<Application>()
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Chia sẻ ảnh").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            // Fallback
            Toast.makeText(context, "Chia sẻ ảnh thành công!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteGalleryItem(item: GalleryItem) {
        val file = File(item.filePath)
        if (file.exists()) file.delete()
        val updatedList = _uiState.value.galleryItems.filter { it.id != item.id }
        _uiState.update { it.copy(galleryItems = updatedList) }
        saveGalleryItems(updatedList)
    }

    private fun loadGalleryItems() {
        val jsonStr = prefs.getString("gallery_items", "[]") ?: "[]"
        try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<GalleryItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    GalleryItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        mode = EnhanceMode.valueOf(obj.getString("mode")),
                        filePath = obj.getString("filePath"),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
            _uiState.update { it.copy(galleryItems = list) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveGalleryItems(list: List<GalleryItem>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("mode", item.mode.name)
                put("filePath", item.filePath)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("gallery_items", array.toString()).apply()
    }
}
