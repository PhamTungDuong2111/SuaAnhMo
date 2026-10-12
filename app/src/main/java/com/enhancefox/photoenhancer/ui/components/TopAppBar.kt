package com.enhancefox.photoenhancer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enhancefox.photoenhancer.R
import com.enhancefox.photoenhancer.model.AppLanguage
import com.enhancefox.photoenhancer.model.Strings
import com.enhancefox.photoenhancer.theme.BorderDark
import com.enhancefox.photoenhancer.theme.PrimaryPurple
import com.enhancefox.photoenhancer.theme.SurfaceDark
import com.enhancefox.photoenhancer.theme.TextMuted
import com.enhancefox.photoenhancer.theme.TextPrimary

@Composable
fun TopAppBar(
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "EnhanceFox Logo",
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = Strings.get("app_name", currentLanguage),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = Strings.get("app_tagline", currentLanguage),
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1
                )
            }
        }

        // Quick Language Switcher Button (🇻🇳 / 🇬🇧)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(PrimaryPurple.copy(alpha = 0.15f))
                .border(1.dp, PrimaryPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .clickable { onToggleLanguage() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = currentLanguage.flag, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = currentLanguage.code.uppercase(),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
