package com.enhancefox.photoenhancer.model

import android.net.Uri

enum class EnhanceMode {
    HOME,
    ENHANCE,
    COLORIZE,
    RESTORE,
    GALLERY
}

enum class SharpnessStrength {
    NATURAL,
    HIGH,
    ULTRA
}

enum class ColorizePalette {
    NATURAL_WARM,
    VINTAGE_1970S,
    MODERN_VIBRANT
}

data class EnhanceOptions(
    val scale: Int = 1,
    val strength: SharpnessStrength = SharpnessStrength.HIGH,
    val faceEnhance: Boolean = true,
    val denoise: Boolean = true,
    val palette: ColorizePalette = ColorizePalette.NATURAL_WARM,
    val removeScratches: Boolean = true,
    val defade: Boolean = true
)

data class GalleryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val mode: EnhanceMode,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis()
)
