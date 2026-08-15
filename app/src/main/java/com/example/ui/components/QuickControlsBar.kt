package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.SignalBuy

@Composable
fun QuickControlsBar(
    isActive: Boolean,
    onToggleTrade: () -> Unit,
    onPairsClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current

    // Pulsing animation for the main Trade/Stop button
    val infiniteTransition = rememberInfiniteTransition(label = "ButtonPulseTransition")
    val buttonPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isActive) 1.08f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isActive) 800 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ButtonPulse"
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isActive) 0.85f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isActive) 800 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloAlpha"
    )

    val activeBtnColor by animateColorAsState(
        targetValue = if (isActive) AccentRed else accentColor,
        animationSpec = tween(400),
        label = "ActiveBtnColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Controls Container (matching video layout)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CyberCardBg)
                .border(
                    BorderStroke(1.dp, CyberCardBorder),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 14.dp, horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PAIRS Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .testTag("pairs_button")
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = accentColor),
                            onClick = onPairsClick
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Pairs",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PAIRS",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }

                // Center TRADE / STOP Big Glowing Action Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .testTag("trade_toggle_button")
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = activeBtnColor),
                            onClick = onToggleTrade
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .scale(buttonPulseScale),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer Halo Ring
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .border(
                                    BorderStroke(2.dp, activeBtnColor.copy(alpha = haloAlpha)),
                                    CircleShape
                                )
                        )

                        // Inner Solid Glowing Circle
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            activeBtnColor,
                                            activeBtnColor.copy(alpha = 0.85f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .shadow(8.dp, CircleShape, spotColor = activeBtnColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isActive) {
                                // Stop Square
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(Color.White, RoundedCornerShape(3.dp))
                                )
                            } else {
                                // Play Arrow / Upward Icon
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start Trade",
                                    tint = if (accentColor == com.example.ui.theme.AccentWhite) Color.Black else Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isActive) "STOP" else "TRADE",
                        color = if (isActive) AccentRed else accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // INFO Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .testTag("info_button")
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = accentColor),
                            onClick = onInfoClick
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "INFO",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Branding Subtitle
        Text(
            text = "Tradeport.EA",
            color = accentColor.copy(alpha = 0.85f),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Cursive
        )
    }
}
