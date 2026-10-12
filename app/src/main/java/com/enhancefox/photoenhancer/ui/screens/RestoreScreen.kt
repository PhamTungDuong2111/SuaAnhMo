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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.enhancefox.photoenhancer.model.Strings
import com.enhancefox.photoenhancer.theme.BgDark
import com.enhancefox.photoenhancer.theme.BorderDark
import com.enhancefox.photoenhancer.theme.PrimaryPurple
import com.enhancefox.photoenhancer.theme.SuccessGreen
import com.enhancefox.photoenhancer.theme.SurfaceCard
import com.enhancefox.photoenhancer.theme.TextMuted
import com.enhancefox.photoenhancer.theme.TextPrimary
import com.enhancefox.photoenhancer.theme.TextSecondary
import com.enhancefox.photoenhancer.ui.components.ComparisonSlider

@Composable
fun RestoreScreen(
    currentBitmap: Bitmap?,
    resultBitmap: Bitmap?,
    isProcessing: Boolean,
    progress: Int,
    stageText: String,
    language: AppLanguage,
    onSelectImageUri: (android.net.Uri) -> Unit,
    onUseSample: (String) -> Unit,
    onStartRestore: (removeScratches: Boolean, defade: Boolean) -> Unit,
    onSaveToGallery: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var removeScratches by remember { mutableStateOf(true) }
    var defade by remember { mutableStateOf(true) }

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
                text = Strings.get("tool_restore_title", language),
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
                    containerColor = SuccessGreen.copy(alpha = 0.2f),
                    contentColor = SuccessGreen
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

        // Comparison Slider or Empty State
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
                            .background(SuccessGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = SuccessGreen,
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
                        text = "Chọn ảnh cũ bị xước, nứt rách hoặc ố vàng",
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
                            color = SuccessGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Card
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
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
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SuccessGreen,
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

        // Restore Options Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard)
                .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                // Scratch removal switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("option_scratches", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = Strings.get("option_scratches_desc", language),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = removeScratches,
                        onCheckedChange = { removeScratches = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SuccessGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Defade switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("option_defade", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = Strings.get("option_defade_desc", language),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = defade,
                        onCheckedChange = { defade = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SuccessGreen
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Start Restore Button
        Button(
            onClick = { onStartRestore(removeScratches, defade) },
            enabled = currentBitmap != null && !isProcessing,
            colors = ButtonDefaults.buttonColors(
                containerColor = SuccessGreen,
                contentColor = Color.White,
                disabledContainerColor = SurfaceCard,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Strings.get("btn_process_restore", language),
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
