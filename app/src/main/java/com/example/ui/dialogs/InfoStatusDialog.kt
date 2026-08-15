package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.DiagnosticStatus
import com.example.model.ExecutionMode
import com.example.model.MetaTraderAccount
import com.example.model.RobotProfile
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentHotPink
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBgElevated
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.SignalBuy

@Composable
fun InfoStatusDialog(
    robot: RobotProfile?,
    account: MetaTraderAccount,
    diagnostics: DiagnosticStatus,
    executionMode: ExecutionMode,
    onModeChange: (ExecutionMode) -> Unit,
    onCloseAllTrades: () -> Unit,
    onDismiss: () -> Unit
) {
    val accentColor = LocalAppAccentColor.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC000000))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xF212131F))
                    .border(BorderStroke(1.5.dp, CyberCardBorder), RoundedCornerShape(24.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // consume click
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Robot Avatar & Info
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(BorderStroke(2.dp, accentColor), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = robot?.avatarDrawableRes ?: R.drawable.robot_cyber_avatar_1786825877723),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = robot?.name ?: "Goat Empire EA",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = robot?.author ?: "Fxgoat",
                        color = accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // STATUS and CLOSE Action buttons (matching video dialog)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // STATUS pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x2600E676))
                                .border(1.dp, SignalBuy, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SignalBuy)
                            )
                            Text(
                                text = "STATUS",
                                color = SignalBuy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // CLOSE button
                        Row(
                            modifier = Modifier
                                .testTag("close_info_dialog_button")
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x26FF334B))
                                .border(1.dp, AccentRed, RoundedCornerShape(8.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(color = AccentRed),
                                    onClick = onDismiss
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = AccentRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "CLOSE",
                                color = AccentRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // NORMAL vs DYNAMIC Mode Selector (as shown in video)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberCardBgElevated)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val isNormal = executionMode == ExecutionMode.NORMAL
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isNormal) SignalBuy.copy(alpha = 0.25f) else Color.Transparent)
                                .clickable { onModeChange(ExecutionMode.NORMAL) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NORMAL",
                                color = if (isNormal) SignalBuy else CyberTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        val isDynamic = executionMode == ExecutionMode.DYNAMIC
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDynamic) AccentHotPink.copy(alpha = 0.25f) else Color.Transparent)
                                .clickable { onModeChange(ExecutionMode.DYNAMIC) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "DYNAMIC",
                                color = if (isDynamic) AccentHotPink else CyberTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Diagnostics Checklist (green check circles)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DiagnosticItem(
                            isPositive = diagnostics.connectedAccount,
                            title = "CONNECTED ACCOUNT",
                            subtitle = diagnostics.accountLabel
                        )
                        DiagnosticItem(
                            isPositive = diagnostics.internetLatency,
                            title = "INTERNET / VPS",
                            subtitle = "Ping: ${diagnostics.latencyMs}ms  •  Ultra Low Latency"
                        )
                        DiagnosticItem(
                            isPositive = diagnostics.pairsSynced,
                            title = diagnostics.activePairsSummary,
                            subtitle = "Pairs Synchronized & Ready"
                        )
                        DiagnosticItem(
                            isPositive = diagnostics.botActive,
                            title = if (diagnostics.botActive) "BOT ACTIVE" else "BOT STANDBY",
                            subtitle = if (diagnostics.botActive) "Algorithmic orders execution enabled" else "Waiting for manual / schedule start"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Emergency Close All Trades
                    OutlinedButton(
                        onClick = {
                            onCloseAllTrades()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AccentRed
                        ),
                        border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AccentRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EMERGENCY CLOSE ALL TRADES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticItem(
    isPositive: Boolean,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCardBgElevated)
            .border(BorderStroke(0.8.dp, CyberCardBorder), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Green Checkmark Icon Circle (matching video)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isPositive) SignalBuy.copy(alpha = 0.2f) else Color(0x26FF334B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isPositive) SignalBuy else AccentRed,
                    modifier = Modifier.size(14.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = if (isPositive) SignalBuy else CyberTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    color = CyberTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
