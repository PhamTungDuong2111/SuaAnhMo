package com.enhancefox.photoenhancer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.enhancefox.photoenhancer.model.EnhanceMode
import com.enhancefox.photoenhancer.theme.BgDark
import com.enhancefox.photoenhancer.theme.EnhanceFoxAITheme
import com.enhancefox.photoenhancer.ui.components.BottomNavBar
import com.enhancefox.photoenhancer.ui.components.TopAppBar
import com.enhancefox.photoenhancer.ui.screens.ColorizeScreen
import com.enhancefox.photoenhancer.ui.screens.EnhanceScreen
import com.enhancefox.photoenhancer.ui.screens.GalleryScreen
import com.enhancefox.photoenhancer.ui.screens.HomeScreen
import com.enhancefox.photoenhancer.ui.screens.RestoreScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EnhanceFoxAITheme {
                val state by viewModel.uiState.collectAsState()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            currentLanguage = state.language,
                            onToggleLanguage = { viewModel.toggleLanguage() }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentMode = state.currentMode,
                            onModeSelected = { viewModel.setMode(it) },
                            language = state.language
                        )
                    },
                    containerColor = BgDark
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (state.currentMode) {
                            EnhanceMode.HOME -> {
                                HomeScreen(
                                    language = state.language,
                                    onNavigateMode = { viewModel.setMode(it) },
                                    onSelectSample = { sampleName, mode ->
                                        viewModel.useSample(sampleName, mode)
                                    },
                                    samplePortraitBitmap = state.samplePortrait,
                                    sampleVintageBitmap = state.sampleVintage
                                )
                            }
                            EnhanceMode.ENHANCE -> {
                                EnhanceScreen(
                                    currentBitmap = state.enhanceSource,
                                    resultBitmap = state.enhanceResult,
                                    isProcessing = state.isProcessing,
                                    progress = state.progress,
                                    stageText = state.stageText,
                                    language = state.language,
                                    onSelectImageUri = { uri -> viewModel.loadFromUri(uri) },
                                    onUseSample = { sampleName -> viewModel.useSample(sampleName, EnhanceMode.ENHANCE) },
                                    onStartEnhance = { opts -> viewModel.processEnhance(opts) },
                                    onSaveToGallery = { viewModel.saveActiveResult() },
                                    onShare = { viewModel.shareActiveResult() }
                                )
                            }
                            EnhanceMode.COLORIZE -> {
                                ColorizeScreen(
                                    currentBitmap = state.colorizeSource,
                                    resultBitmap = state.colorizeResult,
                                    isProcessing = state.isProcessing,
                                    progress = state.progress,
                                    stageText = state.stageText,
                                    language = state.language,
                                    onSelectImageUri = { uri -> viewModel.loadFromUri(uri) },
                                    onUseSample = { sampleName -> viewModel.useSample(sampleName, EnhanceMode.COLORIZE) },
                                    onStartColorize = { palette -> viewModel.processColorize(palette) },
                                    onSaveToGallery = { viewModel.saveActiveResult() },
                                    onShare = { viewModel.shareActiveResult() }
                                )
                            }
                            EnhanceMode.RESTORE -> {
                                RestoreScreen(
                                    currentBitmap = state.restoreSource,
                                    resultBitmap = state.restoreResult,
                                    isProcessing = state.isProcessing,
                                    progress = state.progress,
                                    stageText = state.stageText,
                                    language = state.language,
                                    onSelectImageUri = { uri -> viewModel.loadFromUri(uri) },
                                    onUseSample = { sampleName -> viewModel.useSample(sampleName, EnhanceMode.RESTORE) },
                                    onStartRestore = { scratches, defade ->
                                        viewModel.processRestore(scratches, defade)
                                    },
                                    onSaveToGallery = { viewModel.saveActiveResult() },
                                    onShare = { viewModel.shareActiveResult() }
                                )
                            }
                            EnhanceMode.GALLERY -> {
                                GalleryScreen(
                                    items = state.galleryItems,
                                    language = state.language,
                                    onDeleteItem = { item -> viewModel.deleteGalleryItem(item) },
                                    onShareItem = { item -> viewModel.shareGalleryItem(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
