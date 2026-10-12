package com.enhancefox.photoenhancer.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enhancefox.photoenhancer.model.AppLanguage
import com.enhancefox.photoenhancer.model.Strings
import com.enhancefox.photoenhancer.theme.AccentIndigo
import com.enhancefox.photoenhancer.theme.BorderDark
import com.enhancefox.photoenhancer.theme.LaserBeamColor
import com.enhancefox.photoenhancer.theme.PrimaryPurple
import com.enhancefox.photoenhancer.theme.SurfaceCard
import com.enhancefox.photoenhancer.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun ComparisonSlider(
    beforeBitmap: Bitmap,
    afterBitmap: Bitmap?,
    isProcessing: Boolean = false,
    language: AppLanguage = AppLanguage.VI,
    modifier: Modifier = Modifier
) {
    var splitPosition by remember { mutableFloatStateOf(0.5f) }
    var savedSplitPosition by remember { mutableFloatStateOf(0.5f) }
    var isZoomed by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
        ) {
            val viewWidth = maxWidth
            val viewHeight = maxHeight

            // Pointer input for dragging the split line
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newX = (splitPosition * size.width + dragAmount.x).coerceIn(0f, size.width.toFloat())
                            splitPosition = newX / size.width
                        }
                    }
            ) {
                // If afterBitmap is null (e.g. before processing starts), show beforeBitmap fully
                if (afterBitmap == null) {
                    Image(
                        bitmap = beforeBitmap.asImageBitmap(),
                        contentDescription = "Original Photo",
                        contentScale = if (isZoomed) ContentScale.Crop else ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // BEFORE Layer (left side)
                    Image(
                        bitmap = beforeBitmap.asImageBitmap(),
                        contentDescription = "Before Photo",
                        contentScale = if (isZoomed) ContentScale.Crop else ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                clipRect(left = 0f, top = 0f, right = size.width * splitPosition, bottom = size.height) {
                                    this@drawWithContent.drawContent()
                                }
                            }
                    )

                    // AFTER Layer (right side)
                    Image(
                        bitmap = afterBitmap.asImageBitmap(),
                        contentDescription = "After Photo",
                        contentScale = if (isZoomed) ContentScale.Crop else ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                clipRect(left = size.width * splitPosition, top = 0f, right = size.width, bottom = size.height) {
                                    this@drawWithContent.drawContent()
                                }
                            }
                    )

                    // Vertical Divider Line & Knob Handle
                    val dividerX = viewWidth * splitPosition
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(dividerX.toPx().roundToInt() - 1.dp.roundToPx(), 0) }
                            .width(2.dp)
                            .height(viewHeight)
                            .background(Color.White)
                    )

                    // Handle Knob in middle
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    dividerX.toPx().roundToInt() - 18.dp.roundToPx(),
                                    (viewHeight.toPx() / 2 - 18.dp.toPx()).roundToInt()
                                )
                            }
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(PrimaryPurple, AccentIndigo)
                                )
                            )
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Drag Divider",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Before Tag (Top Left)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .background(Color(0x99000000), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = Strings.get("label_before", language),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // After Tag (Top Right)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .background(PrimaryPurple.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = Strings.get("label_after", language),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Scanning Laser Bar (During processing)
                if (isProcessing) {
                    val laserY = viewHeight * laserPosition
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, laserY.toPx().roundToInt()) }
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        LaserBeamColor,
                                        Color.White,
                                        LaserBeamColor,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }

        // Bottom quick toolbar (Zoom & Hold compare)
        if (afterBitmap != null && !isProcessing) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xCC0D111E))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Zoom toggle button
                FilledTonalButton(
                    onClick = { isZoomed = !isZoomed },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isZoomed) PrimaryPurple else SurfaceCard,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isZoomed) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isZoomed) Strings.get("btn_zoom_out", language) else Strings.get("btn_zoom", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Hold to view original button
                FilledTonalButton(
                    onClick = { /* Tap handled via pointerInput */ },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SurfaceCard,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    savedSplitPosition = splitPosition
                                    splitPosition = 1.0f // Reveal 100% original
                                    tryAwaitRelease()
                                    splitPosition = savedSplitPosition
                                }
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("btn_hold_compare", language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
