package com.enhancefox.photoenhancer.ui.screens

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enhancefox.photoenhancer.model.AppLanguage
import com.enhancefox.photoenhancer.model.GalleryItem
import com.enhancefox.photoenhancer.model.Strings
import com.enhancefox.photoenhancer.theme.BgDark
import com.enhancefox.photoenhancer.theme.BorderDark
import com.enhancefox.photoenhancer.theme.ErrorRed
import com.enhancefox.photoenhancer.theme.PrimaryPurple
import com.enhancefox.photoenhancer.theme.SurfaceCard
import com.enhancefox.photoenhancer.theme.TextMuted
import com.enhancefox.photoenhancer.theme.TextPrimary
import com.enhancefox.photoenhancer.theme.TextSecondary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GalleryScreen(
    items: List<GalleryItem>,
    language: AppLanguage,
    onDeleteItem: (GalleryItem) -> Unit,
    onShareItem: (GalleryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp)
    ) {
        Text(
            text = Strings.get("tab_gallery", language),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard)
                            .border(1.dp, BorderDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = Strings.get("gallery_empty", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = Strings.get("gallery_empty_desc", language),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { item ->
                    GalleryCard(
                        item = item,
                        language = language,
                        onDelete = { onDeleteItem(item) },
                        onShare = { onShareItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryCard(
    item: GalleryItem,
    language: AppLanguage,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val bitmap = remember(item.filePath) {
        try {
            val file = File(item.filePath)
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        } catch (e: Exception) {
            null
        }
    }

    val dateFormatted = remember(item.timestamp) {
        val sdf = SimpleDateFormat("HH:mm dd/MM", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color.Black)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = item.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = dateFormatted,
                fontSize = 10.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = ErrorRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
