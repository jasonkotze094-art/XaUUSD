package com.example.ui.drawer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentHotPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AccentSlate
import com.example.ui.theme.AccentWhite
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalAppAccentColor

@Composable
fun AppDrawerContent(
    selectedAccent: Color,
    isVoiceEnabled: Boolean,
    onSelectAccent: (Color) -> Unit,
    onToggleVoice: () -> Unit,
    onNavigateHome: () -> Unit,
    onOpenMetaTraderConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColorsList = listOf(
        AccentRed,
        AccentGreen,
        AccentCyan,
        AccentPurple,
        AccentHotPink,
        AccentGold,
        AccentWhite,
        AccentSlate
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color(0xF510121C))
            .border(BorderStroke(1.dp, CyberCardBorder))
            .padding(20.dp)
    ) {
        // App Logo & Title (matching video drawer)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color(0x33000000)), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_launcher_icon_1786825887606),
                    contentDescription = "TradePort EA Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(42.dp)
                )
            }

            Column {
                Text(
                    text = "Trade Port EA",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Cursive
                )
                Text(
                    text = "Tradeport.EA",
                    color = selectedAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Cursive
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accent Color Picker Header & Palette (matching video)
        Text(
            text = "Accent",
            color = CyberTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2 Rows of 4 Colors
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                accentColorsList.take(4).forEach { color ->
                    AccentColorCircle(
                        color = color,
                        isSelected = color == selectedAccent,
                        onSelect = { onSelectAccent(color) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                accentColorsList.drop(4).take(4).forEach { color ->
                    AccentColorCircle(
                        color = color,
                        isSelected = color == selectedAccent,
                        onSelect = { onSelectAccent(color) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = CyberCardBorder, thickness = 0.8.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Items
        DrawerNavItem(
            icon = Icons.Default.Home,
            title = "Home",
            isSelected = true,
            onClick = onNavigateHome
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerNavItem(
            icon = Icons.Default.AccountCircle,
            title = "Metatrader",
            isSelected = false,
            onClick = onOpenMetaTraderConfig
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerNavItem(
            icon = Icons.Default.Hub,
            title = "VPS Webhook Bridge",
            isSelected = false,
            onClick = onOpenMetaTraderConfig
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Voice Telemetry Switch Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onToggleVoice() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = if (isVoiceEnabled) selectedAccent else CyberTextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "Voice Announcements",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isVoiceEnabled) "Active ('Money engine on')" else "Muted",
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = isVoiceEnabled,
                onCheckedChange = { onToggleVoice() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = selectedAccent,
                    uncheckedThumbColor = CyberTextMuted,
                    uncheckedTrackColor = CyberCardBorder
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Platform Version
        Text(
            text = "TradePort EA • Gold Beast v3.8\nMetaTrader 5 Mobile Bridge",
            color = CyberTextMuted,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun AccentColorCircle(
    color: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                BorderStroke(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) Color.White else Color(0x33FFFFFF)
                ),
                CircleShape
            )
            .clickable { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color == AccentWhite) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun DrawerNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = LocalAppAccentColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) accentColor else CyberTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = title,
            color = if (isSelected) Color.White else CyberTextSecondary,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
