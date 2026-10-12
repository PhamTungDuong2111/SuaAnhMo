package com.enhancefox.photoenhancer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enhancefox.photoenhancer.model.AppLanguage
import com.enhancefox.photoenhancer.model.EnhanceMode
import com.enhancefox.photoenhancer.model.Strings
import com.enhancefox.photoenhancer.theme.BorderDark
import com.enhancefox.photoenhancer.theme.PrimaryPurple
import com.enhancefox.photoenhancer.theme.SurfaceDark
import com.enhancefox.photoenhancer.theme.TextMuted
import com.enhancefox.photoenhancer.theme.TextPrimary

@Composable
fun BottomNavBar(
    currentMode: EnhanceMode,
    onModeSelected: (EnhanceMode) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .border(width = 1.dp, color = BorderDark)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavBarItem(
            icon = Icons.Default.Home,
            label = Strings.get("tab_home", language),
            isSelected = currentMode == EnhanceMode.HOME,
            onClick = { onModeSelected(EnhanceMode.HOME) }
        )
        NavBarItem(
            icon = Icons.Default.AutoAwesome,
            label = Strings.get("tab_enhance", language),
            isSelected = currentMode == EnhanceMode.ENHANCE,
            onClick = { onModeSelected(EnhanceMode.ENHANCE) }
        )
        NavBarItem(
            icon = Icons.Default.ColorLens,
            label = Strings.get("tab_colorize", language),
            isSelected = currentMode == EnhanceMode.COLORIZE,
            onClick = { onModeSelected(EnhanceMode.COLORIZE) }
        )
        NavBarItem(
            icon = Icons.Default.History,
            label = Strings.get("tab_restore", language),
            isSelected = currentMode == EnhanceMode.RESTORE,
            onClick = { onModeSelected(EnhanceMode.RESTORE) }
        )
        NavBarItem(
            icon = Icons.Default.PhotoLibrary,
            label = Strings.get("tab_gallery", language),
            isSelected = currentMode == EnhanceMode.GALLERY,
            onClick = { onModeSelected(EnhanceMode.GALLERY) }
        )
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PrimaryPurple else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) PrimaryPurple else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
