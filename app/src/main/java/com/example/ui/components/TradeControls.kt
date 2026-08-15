package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TradingSymbol
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBgElevated
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalSell
import kotlinx.coroutines.delay

/**
 * TradeControls component featuring:
 * - 'Buy' and 'Sell' buttons with tactile haptic feedback
 * - Dedicated toggle switch for 'Auto Trading' status
 * - Quick Lot Size selectors (0.01, 0.05, 0.10, 0.50, 1.00) & +/- Stepper
 * - Live Bid/Ask & Spread ticker
 * - MT5 EA Bridge execution feedback badge
 */
@Composable
fun TradeControls(
    isAutoTradingActive: Boolean,
    onToggleAutoTrading: () -> Unit,
    onExecuteBuy: (symbol: String, lot: Double) -> Unit,
    onExecuteSell: (symbol: String, lot: Double) -> Unit,
    symbols: List<TradingSymbol> = emptyList(),
    currentLivePrice: Double = 2642.80,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current
    val haptic = LocalHapticFeedback.current

    var selectedLot by remember { mutableDoubleStateOf(0.05) }
    var selectedSymbol by remember { mutableStateOf("XAUUSD") }
    var symbolMenuExpanded by remember { mutableStateOf(false) }

    // Instant execution confirmation banner state
    var lastExecutedAction by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lastExecutedAction) {
        if (lastExecutedAction != null) {
            delay(3500)
            lastExecutedAction = null
        }
    }

    // Bid & Ask calculations with live tick
    val spreadPips = 1.2
    val askPrice = currentLivePrice + (spreadPips * 0.1)
    val bidPrice = currentLivePrice

    // Pulse animation when Auto Trading is active
    val infiniteTransition = rememberInfiniteTransition(label = "AutoTradingPulse")
    val autoPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isAutoTradingActive) 1.0f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AutoPulseAlpha"
    )

    val autoGlowColor by animateColorAsState(
        targetValue = if (isAutoTradingActive) SignalBuy else CyberTextMuted,
        animationSpec = tween(300),
        label = "AutoGlowColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("trade_controls_component")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(CyberCardBg)
                .border(
                    BorderStroke(
                        width = 1.2.dp,
                        color = if (isAutoTradingActive) autoGlowColor.copy(alpha = 0.5f) else CyberCardBorder
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // -------------------------------------------------------------
                // 1. HEADER ROW: Title + Auto Trading Toggle Switch
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isAutoTradingActive) SignalBuy.copy(alpha = 0.2f) else Color(0xFF1E2235)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = if (isAutoTradingActive) SignalBuy else CyberTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "TRADE CONTROLS",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "MT5 DIRECT EXECUTION BRIDGE",
                                color = CyberTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Auto Trading Switch & Status Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF141724))
                            .border(1.dp, if (isAutoTradingActive) SignalBuy.copy(alpha = 0.4f) else CyberCardBorder, RoundedCornerShape(24.dp))
                            .padding(start = 10.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(autoGlowColor.copy(alpha = autoPulseAlpha))
                            )
                            Text(
                                text = if (isAutoTradingActive) "AUTO: ON" else "AUTO: OFF",
                                color = if (isAutoTradingActive) SignalBuy else CyberTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Switch(
                            checked = isAutoTradingActive,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onToggleAutoTrading()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = SignalBuy,
                                uncheckedThumbColor = CyberTextMuted,
                                uncheckedTrackColor = Color(0xFF222638),
                                uncheckedBorderColor = CyberCardBorder
                            ),
                            modifier = Modifier
                                .scale(0.8f)
                                .testTag("auto_trading_switch")
                        )
                    }
                }

                HorizontalDivider(color = CyberCardBorder.copy(alpha = 0.6f), thickness = 0.8.dp)

                // -------------------------------------------------------------
                // 2. SYMBOL & LOT SIZE CONFIGURATION ROW
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Symbol Selector Dropdown Chip
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCardBgElevated)
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                                .clickable { symbolMenuExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = selectedSymbol,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select Symbol",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = symbolMenuExpanded,
                            onDismissRequest = { symbolMenuExpanded = false },
                            modifier = Modifier.background(CyberCardBgElevated)
                        ) {
                            val availableSymbols = if (symbols.isNotEmpty()) symbols.map { it.symbol } else listOf("XAUUSD", "GOLDm", "GBPUSD", "EURUSD", "US30", "NAS100")
                            availableSymbols.forEach { sym ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = sym,
                                            color = if (sym == selectedSymbol) accentColor else Color.White,
                                            fontWeight = if (sym == selectedSymbol) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    onClick = {
                                        selectedSymbol = sym
                                        symbolMenuExpanded = false
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                )
                            }
                        }
                    }

                    // Lot Size Stepper Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Minus Button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCardBgElevated)
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (selectedLot > 0.01) {
                                        selectedLot = Math.round((selectedLot - 0.01) * 100.0) / 100.0
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }
                                .testTag("lot_size_stepper_minus"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease Lot Size",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Lot Size Value
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF121420))
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format("%.2f", selectedLot),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Plus Button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCardBgElevated)
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (selectedLot < 10.0) {
                                        selectedLot = Math.round((selectedLot + 0.01) * 100.0) / 100.0
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }
                                .testTag("lot_size_stepper_plus"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase Lot Size",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Quick Lot Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0.01, 0.02, 0.05, 0.10, 0.50, 1.00).forEach { preset ->
                        val isSelected = selectedLot == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) accentColor else Color(0xFF181B28))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) accentColor else CyberCardBorder,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    selectedLot = preset
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$preset",
                                color = if (isSelected) Color.Black else CyberTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 3. MAIN BUY & SELL BUTTONS WITH HAPTIC FEEDBACK
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SELL BUTTON (Red Cyber Button)
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onExecuteSell(selectedSymbol, selectedLot)
                            lastExecutedAction = "SELL $selectedLot lots $selectedSymbol @ ${String.format("%.2f", bidPrice)}"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        SignalSell.copy(alpha = 0.95f),
                                        SignalSell.copy(alpha = 0.75f)
                                    )
                                )
                            )
                            .border(1.dp, SignalSell, RoundedCornerShape(14.dp))
                            .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = SignalSell)
                            .testTag("sell_button")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "SELL",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = String.format("%.2f", bidPrice),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // BUY BUTTON (Green Cyber Button)
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onExecuteBuy(selectedSymbol, selectedLot)
                            lastExecutedAction = "BUY $selectedLot lots $selectedSymbol @ ${String.format("%.2f", askPrice)}"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        SignalBuy.copy(alpha = 0.95f),
                                        SignalBuy.copy(alpha = 0.75f)
                                    )
                                )
                            )
                            .border(1.dp, SignalBuy, RoundedCornerShape(14.dp))
                            .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = SignalBuy)
                            .testTag("buy_button")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "BUY",
                                    color = Color.Black,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = String.format("%.2f", askPrice),
                                color = Color.Black.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 4. SPREAD / EXECUTION FOOTER & RECENT FEEDBACK BANNER
                // -------------------------------------------------------------
                AnimatedVisibility(
                    visible = lastExecutedAction != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    lastExecutedAction?.let { feedback ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF141E28))
                                .border(1.dp, SignalBuy.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SignalBuy,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = feedback,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Real-time Spread & Execution Parameters info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPREAD: ${spreadPips} pips",
                        color = CyberTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "MAGIC: #20260815 • DEV: 50pts",
                        color = CyberTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
