package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClosedTrade
import com.example.model.SignalType
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

enum class HistoryFilter {
    ALL,
    PROFIT,
    LOSS
}

@Composable
fun TradeHistorySection(
    trades: List<ClosedTrade>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current
    val haptic = LocalHapticFeedback.current

    var selectedFilter by remember { mutableStateOf(HistoryFilter.ALL) }
    var expandedTradeTicket by remember { mutableStateOf<Long?>(null) }

    val filteredTrades = remember(trades, selectedFilter) {
        when (selectedFilter) {
            HistoryFilter.ALL -> trades
            HistoryFilter.PROFIT -> trades.filter { it.profitLossUsd >= 0 }
            HistoryFilter.LOSS -> trades.filter { it.profitLossUsd < 0 }
        }
    }

    val totalPnl = trades.sumOf { it.profitLossUsd }
    val winCount = trades.count { it.profitLossUsd > 0 }
    val winRate = if (trades.isNotEmpty()) (winCount.toDouble() / trades.size) * 100.0 else 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("trade_history_section")
    ) {
        // Main Container Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(CyberCardBg)
                .border(BorderStroke(1.dp, CyberCardBorder), shape = RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // -------------------------------------------------------------
                // 1. SECTION HEADER: Title, Badge & Refresh Button
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "TRADE HISTORY",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF1E2235))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${trades.size} CLOSED",
                                        color = CyberTextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Text(
                                text = "RECENT ORDERS RETRIEVED FROM API",
                                color = CyberTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Refresh Button with haptic
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onRefresh()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CyberCardBgElevated)
                            .border(1.dp, CyberCardBorder, CircleShape)
                            .testTag("refresh_trade_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Trade History",
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // -------------------------------------------------------------
                // 2. REALIZED SUMMARY KPI CARDS
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Net Realized PnL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberCardBgElevated)
                            .border(1.dp, if (totalPnl >= 0) SignalBuy.copy(alpha = 0.3f) else SignalSell.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "REALIZED P/L",
                                color = CyberTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = (if (totalPnl >= 0) "+" else "") + "$" + String.format("%.2f", totalPnl),
                                color = if (totalPnl >= 0) SignalBuy else SignalSell,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Win Rate %
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberCardBgElevated)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "WIN RATE",
                                color = CyberTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${String.format("%.1f", winRate)}%",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Total Wins / Losses
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberCardBgElevated)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "W / L RATIO",
                                color = CyberTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$winCount W • ${trades.size - winCount} L",
                                color = accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 3. FILTER TABS (All / Profit / Loss)
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HistoryFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val filterLabel = when (filter) {
                            HistoryFilter.ALL -> "ALL (${trades.size})"
                            HistoryFilter.PROFIT -> "WINNING (${winCount})"
                            HistoryFilter.LOSS -> "LOSING (${trades.size - winCount})"
                        }
                        val filterColor = when (filter) {
                            HistoryFilter.ALL -> accentColor
                            HistoryFilter.PROFIT -> SignalBuy
                            HistoryFilter.LOSS -> SignalSell
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) filterColor.copy(alpha = 0.2f) else Color(0xFF141724))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) filterColor else CyberCardBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = filter
                                }
                                .padding(vertical = 7.dp)
                                .testTag("trade_history_filter_${filter.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filterLabel,
                                color = if (isSelected) filterColor else CyberTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                HorizontalDivider(color = CyberCardBorder.copy(alpha = 0.6f), thickness = 0.8.dp)

                // -------------------------------------------------------------
                // 4. CLOSED ORDERS LIST (Displaying individual items)
                // -------------------------------------------------------------
                if (filteredTrades.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = CyberTextMuted,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "No orders found in this category",
                                color = CyberTextMuted,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredTrades.forEach { trade ->
                            val isExpanded = expandedTradeTicket == trade.ticketId
                            ClosedTradeItem(
                                trade = trade,
                                isExpanded = isExpanded,
                                onToggleExpand = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    expandedTradeTicket = if (isExpanded) null else trade.ticketId
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClosedTradeItem(
    trade: ClosedTrade,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val isProfit = trade.profitLossUsd >= 0
    val pnlColor = if (isProfit) SignalBuy else SignalSell

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberCardBgElevated)
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = if (isProfit) SignalBuy.copy(alpha = 0.25f) else SignalSell.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onToggleExpand)
            .padding(12.dp)
            .testTag("trade_history_item_${trade.ticketId}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Main Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Symbol badge & type & lot size
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Type Icon Box
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (trade.type == SignalType.BUY) SignalBuy.copy(alpha = 0.2f) else SignalSell.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (trade.type == SignalType.BUY) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (trade.type == SignalType.BUY) SignalBuy else SignalSell,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = trade.symbol,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (trade.type == SignalType.BUY) SignalBuy.copy(alpha = 0.25f) else SignalSell.copy(alpha = 0.25f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${trade.type.name} ${trade.lotSize}",
                                    color = if (trade.type == SignalType.BUY) SignalBuy else SignalSell,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Open -> Close Price
                        Text(
                            text = "${trade.openPrice} → ${trade.closePrice}",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Right: Profit/Loss & Timestamp
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = (if (isProfit) "+" else "") + "$" + String.format("%.2f", trade.profitLossUsd),
                        color = pnlColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = trade.closeTime,
                            color = CyberTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CyberTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expanded Order Telemetry
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10121C))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Ticket #:", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "#${trade.ticketId}", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Opened at:", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = trade.openTime, color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Exit Reason:", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = trade.exitReason, color = if (isProfit) SignalBuy else SignalSell, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pips Gain/Loss:", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "${if (trade.pips >= 0) "+" else ""}${trade.pips} pips", color = pnlColor, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Commission / Swap:", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "$${trade.commissionUsd} / $${trade.swapUsd}", color = CyberTextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "EA Magic Number:", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "#${trade.magicNumber}", color = AccentGold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
