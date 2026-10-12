package com.enhancefox.photoenhancer.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enhancefox.photoenhancer.model.AppLanguage
import com.enhancefox.photoenhancer.model.EnhanceMode
import com.enhancefox.photoenhancer.model.Strings
import com.enhancefox.photoenhancer.theme.AccentCyan
import com.enhancefox.photoenhancer.theme.AccentIndigo
import com.enhancefox.photoenhancer.theme.AccentPink
import com.enhancefox.photoenhancer.theme.BgDark
import com.enhancefox.photoenhancer.theme.BorderDark
import com.enhancefox.photoenhancer.theme.PrimaryPurple
import com.enhancefox.photoenhancer.theme.PrimaryPurpleLight
import com.enhancefox.photoenhancer.theme.SuccessGreen
import com.enhancefox.photoenhancer.theme.SurfaceCard
import com.enhancefox.photoenhancer.theme.TextMuted
import com.enhancefox.photoenhancer.theme.TextPrimary
import com.enhancefox.photoenhancer.theme.TextSecondary

@Composable
fun HomeScreen(
    language: AppLanguage,
    onNavigateMode: (EnhanceMode) -> Unit,
    onSelectSample: (String, EnhanceMode) -> Unit,
    samplePortraitBitmap: Bitmap?,
    sampleVintageBitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PrimaryPurple.copy(alpha = 0.25f),
                            SurfaceCard
                        )
                    )
                )
                .border(1.dp, PrimaryPurple.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                // Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryPurple.copy(alpha = 0.3f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PrimaryPurpleLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("hero_badge", language),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = Strings.get("hero_title", language),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = Strings.get("hero_desc", language),
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onNavigateMode(EnhanceMode.ENHANCE) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        contentColor = Color.White
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
                        text = Strings.get("btn_start_now", language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick AI Tools Section
        Text(
            text = Strings.get("quick_tools_title", language),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolCard(
            title = Strings.get("tool_enhance_title", language),
            desc = Strings.get("tool_enhance_desc", language),
            icon = Icons.Default.AutoAwesome,
            accentColor = PrimaryPurple,
            onClick = { onNavigateMode(EnhanceMode.ENHANCE) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ToolCard(
            title = Strings.get("tool_colorize_title", language),
            desc = Strings.get("tool_colorize_desc", language),
            icon = Icons.Default.ColorLens,
            accentColor = AccentIndigo,
            onClick = { onNavigateMode(EnhanceMode.COLORIZE) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ToolCard(
            title = Strings.get("tool_restore_title", language),
            desc = Strings.get("tool_restore_desc", language),
            icon = Icons.Default.History,
            accentColor = SuccessGreen,
            onClick = { onNavigateMode(EnhanceMode.RESTORE) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sample Photos Section
        Text(
            text = Strings.get("btn_try_sample", language),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SampleCard(
                title = Strings.get("sample_portrait_title", language),
                tag = Strings.get("tool_enhance_title", language),
                bitmap = samplePortraitBitmap,
                onClick = { onSelectSample("sample_portrait.jpg", EnhanceMode.ENHANCE) },
                modifier = Modifier.weight(1f)
            )

            SampleCard(
                title = Strings.get("sample_vintage_title", language),
                tag = Strings.get("tool_restore_title", language),
                bitmap = sampleVintageBitmap,
                onClick = { onSelectSample("sample_vintage.jpg", EnhanceMode.RESTORE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ToolCard(
    title: String,
    desc: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.2f))
                .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SampleCard(
    title: String,
    tag: String,
    bitmap: Bitmap?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color.Black)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Test Sample",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tag,
                fontSize = 11.sp,
                color = PrimaryPurpleLight,
                maxLines = 1
            )
        }
    }
}
