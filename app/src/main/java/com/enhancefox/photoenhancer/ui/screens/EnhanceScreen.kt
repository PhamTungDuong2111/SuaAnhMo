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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.enhancefox.photoenhancer.model.EnhanceOptions
import com.enhancefox.photoenhancer.model.SharpnessStrength
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
fun EnhanceScreen(
    currentBitmap: Bitmap?,
    resultBitmap: Bitmap?,
    isProcessing: Boolean,
    progress: Int,
    stageText: String,
    language: AppLanguage,
    onSelectImageUri: (android.net.Uri) -> Unit,
    onUseSample: (String) -> Unit,
    onStartEnhance: (EnhanceOptions) -> Unit,
    onSaveToGallery: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var selectedScale by remember { mutableIntStateOf(1) }
    var selectedStrength by remember { mutableStateOf(SharpnessStrength.HIGH) }
    var faceEnhance by remember { mutableStateOf(true) }
    var denoise by remember { mutableStateOf(true) }

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
        // Top Action: Change / Pick Photo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Strings.get("tool_enhance_title", language),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Row {
                FilledTonalButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = PrimaryPurple.copy(alpha = 0.2f),
                        contentColor = PrimaryPurpleLight
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Photo Display / Before & After Slider
        if (currentBitmap != null) {
            ComparisonSlider(
                beforeBitmap = currentBitmap,
                afterBitmap = resultBitmap,
                isProcessing = isProcessing,
                language = language
            )
        } else {
            // Empty State: Prompt to select or pick sample
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
                            .background(PrimaryPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = PrimaryPurple,
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
                        text = "PNG, JPG hoặc HEIC",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { onUseSample("sample_portrait.jpg") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = Strings.get("btn_try_sample", language),
                            fontSize = 12.sp,
                            color = PrimaryPurpleLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Card (Visible when processing)
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceCard)
                    .border(1.dp, PrimaryPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
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
                            color = PrimaryPurpleLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimaryPurple,
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

        // Enhancement Options Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard)
                .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                // Resolution scale options
                Text(
                    text = Strings.get("resolution_scale", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResolutionPill(
                        label = Strings.get("scale_1x", language),
                        selected = selectedScale == 1,
                        onClick = { selectedScale = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    ResolutionPill(
                        label = Strings.get("scale_2x", language),
                        selected = selectedScale == 2,
                        onClick = { selectedScale = 2 },
                        modifier = Modifier.weight(1f)
                    )
                    ResolutionPill(
                        label = Strings.get("scale_4x", language),
                        selected = selectedScale == 4,
                        onClick = { selectedScale = 4 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sharpness Strength options
                Text(
                    text = Strings.get("strength_level", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResolutionPill(
                        label = Strings.get("strength_natural", language),
                        selected = selectedStrength == SharpnessStrength.NATURAL,
                        onClick = { selectedStrength = SharpnessStrength.NATURAL },
                        modifier = Modifier.weight(1f)
                    )
                    ResolutionPill(
                        label = Strings.get("strength_high", language),
                        selected = selectedStrength == SharpnessStrength.HIGH,
                        onClick = { selectedStrength = SharpnessStrength.HIGH },
                        modifier = Modifier.weight(1f)
                    )
                    ResolutionPill(
                        label = Strings.get("strength_ultra", language),
                        selected = selectedStrength == SharpnessStrength.ULTRA,
                        onClick = { selectedStrength = SharpnessStrength.ULTRA },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Face enhance toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("face_enhance", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = Strings.get("face_enhance_desc", language),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = faceEnhance,
                        onCheckedChange = { faceEnhance = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryPurple
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Denoise toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("denoise", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = Strings.get("denoise_desc", language),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = denoise,
                        onCheckedChange = { denoise = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryPurple
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Start processing button
        Button(
            onClick = {
                onStartEnhance(
                    EnhanceOptions(
                        scale = selectedScale,
                        strength = selectedStrength,
                        faceEnhance = faceEnhance,
                        denoise = denoise
                    )
                )
            },
            enabled = currentBitmap != null && !isProcessing,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                disabledContainerColor = SurfaceCard,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Strings.get("btn_process_enhance", language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Result Save & Share buttons
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
                        containerColor = AccentIndigo,
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
fun ResolutionPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) PrimaryPurple else BorderDark.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (selected) PrimaryPurpleLight else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else TextSecondary
        )
    }
}
