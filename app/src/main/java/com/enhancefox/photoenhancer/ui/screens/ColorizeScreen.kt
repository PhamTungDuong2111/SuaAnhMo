package com.enhancefox.photoenhancer.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enhancefox.photoenhancer.model.AppLanguage
import com.enhancefox.photoenhancer.model.ColorizePalette
import com.enhancefox.photoenhancer.model.Strings
import com.enhancefox.photoenhancer.theme.AccentIndigo
import com.enhancefox.photoenhancer.theme.BgDark
import com.enhancefox.photoenhancer.theme.BorderDark
import com.enhancefox.photoenhancer.theme.PrimaryPurple
import com.enhancefox.photoenhancer.theme.PrimaryPurpleLight
import com.enhancefox.photoenhancer.theme.SuccessGreen
import com.enhancefox.photoenhancer.theme.SurfaceCard
import com.enhancefox.photoenhancer.theme.TextMuted
import com.enhancefox.photoenhancer.theme.TextPrimary
import com.enhancefox.photoenhancer.theme.TextSecondary
import com.enhancefox.photoenhancer.ui.components.ComparisonSlider

@Composable
fun ColorizeScreen(
    currentBitmap: Bitmap?,
    resultBitmap: Bitmap?,
    isProcessing: Boolean,
    progress: Int,
    stageText: String,
    language: AppLanguage,
    onSelectImageUri: (android.net.Uri) -> Unit,
    onUseSample: (String) -> Unit,
    onStartColorize: (ColorizePalette) -> Unit,
    onSaveToGallery: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedPalette by remember { mutableStateOf(ColorizePalette.NATURAL_WARM) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onSelectImageUri(uri)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Strings.get("tool_colorize_title", language),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            FilledTonalButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = AccentIndigo.copy(alpha = 0.2f),
                    contentColor = AccentIndigo
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (currentBitmap == null) Strings.get("select_photo", language) else Strings.get("change_photo", language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Photo Slider or Empty state
        if (currentBitmap != null) {
            ComparisonSlider(
                beforeBitmap = currentBitmap,
                afterBitmap = resultBitmap,
                isProcessing = isProcessing,
                language = language
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(AccentIndigo.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null,
                            tint = AccentIndigo,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = Strings.get("select_photo", language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Chọn ảnh đen trắng thời xưa cần tô màu",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { onUseSample("sample_vintage.jpg") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = Strings.get("btn_try_sample", language),
                            fontSize = 12.sp,
                            color = AccentIndigo
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceCard)
                    .border(1.dp, AccentIndigo.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings.get("processing_title", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "$progress%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = AccentIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AccentIndigo,
                        trackColor = BorderDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stageText,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Palette Selector Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard)
                .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = Strings.get("palette_title", language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                PaletteOption(
                    title = Strings.get("palette_natural", language),
                    desc = "Tự động tái tạo màu da hồng hào, quần áo và cảnh quan chân thực",
                    selected = selectedPalette == ColorizePalette.NATURAL_WARM,
                    onClick = { selectedPalette = ColorizePalette.NATURAL_WARM }
                )

                Spacer(modifier = Modifier.height(10.dp))

                PaletteOption(
                    title = Strings.get("palette_vintage", language),
                    desc = "Tông màu vàng ấm hoài cổ đậm chất phim ảnh Kodachrome 1970s",
                    selected = selectedPalette == ColorizePalette.VINTAGE_1970S,
                    onClick = { selectedPalette = ColorizePalette.VINTAGE_1970S }
                )

                Spacer(modifier = Modifier.height(10.dp))

                PaletteOption(
                    title = Strings.get("palette_vibrant", language),
                    desc = "Tăng độ rực rỡ và bão hòa màu sắc sống động, sắc nét cao",
                    selected = selectedPalette == ColorizePalette.MODERN_VIBRANT,
                    onClick = { selectedPalette = ColorizePalette.MODERN_VIBRANT }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Start Colorize Button
        Button(
            onClick = { onStartColorize(selectedPalette) },
            enabled = currentBitmap != null && !isProcessing,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentIndigo,
                contentColor = Color.White,
                disabledContainerColor = SurfaceCard,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.ColorLens,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Strings.get("btn_process_colorize", language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Result Save & Share
        if (resultBitmap != null && !isProcessing) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSaveToGallery,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("btn_save_gallery", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                FilledTonalButton(
                    onClick = onShare,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = PrimaryPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("btn_share", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PaletteOption(
    title: String,
    desc: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) AccentIndigo.copy(alpha = 0.15f) else BorderDark.copy(alpha = 0.3f))
            .border(
                1.dp,
                if (selected) AccentIndigo else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 15.sp
            )
        }
    }
}
