package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveTrade
import com.example.model.SignalType
import com.example.model.TradeSignal
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
import com.example.ui.theme.SignalSell

@Composable
fun GoldBeastHudPanel(
    goldPrice: Double,
    priceChange: Double,
    totalProfitLoss: Double,
    signal: TradeSignal?,
    openTrades: List<ActiveTrade>,
    onCloseTrade: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gold Live Beast Price & Signal Terminal Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CyberCardBg)
                .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                // Header Row
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "GOLD BEAST (XAUUSD)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Live Institutional MT5 Feed",
                                color = CyberTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Live Signal Badge (BUY / SELL)
                    val isBuy = signal?.type == SignalType.BUY
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isBuy) SignalBuy.copy(alpha = 0.15f) else SignalSell.copy(alpha = 0.15f))
                            .border(
                                1.dp,
                                if (isBuy) SignalBuy else SignalSell,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isBuy) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (isBuy) SignalBuy else SignalSell,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isBuy) "STRONG BUY" else "STRONG SELL",
                                color = if (isBuy) SignalBuy else SignalSell,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Row & Mini Chart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "$${String.format("%.2f", goldPrice)}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = SignalBuy,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "+$priceChange% (Today)",
                                color = SignalBuy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Mini Candlestick Sparkline Canvas
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(42.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val path = Path().apply {
                                moveTo(0f, h * 0.7f)
                                cubicTo(w * 0.25f, h * 0.8f, w * 0.4f, h * 0.3f, w * 0.65f, h * 0.45f)
                                cubicTo(w * 0.8f, h * 0.55f, w * 0.9f, h * 0.15f, w, h * 0.1f)
                            }
                            drawPath(
                                path = path,
                                color = SignalBuy,
                                style = Stroke(width = 2.5.dp.toPx())
                            )
                            drawCircle(
                                color = SignalBuy,
                                radius = 4.dp.toPx(),
                                center = Offset(w, h * 0.1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trade Execution Grid (Entry, SL, TP1, TP2, Trailing Stop)
                if (signal != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberCardBgElevated)
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SignalParamItem("ENTRY", "$${signal.entryPrice}", Color.White)
                                SignalParamItem("STOP LOSS", "$${signal.stopLoss}", SignalSell)
                                SignalParamItem("TAKE PROFIT 1", "$${signal.takeProfit1}", SignalBuy)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SignalParamItem("TAKE PROFIT 2", "$${signal.takeProfit2}", SignalBuy)
                                SignalParamItem("TRAILING STOP", "${signal.trailingStopPips} Pips", accentColor)
                                SignalParamItem("CONFIDENCE", "${signal.confidencePercent}%", AccentGold)
                            }
                        }
                    }
                }
            }
        }

        // Active Trades & P/L Section
        if (openTrades.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyberCardBg)
                    .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .animateContentSize()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "ACTIVE TRADES (${openTrades.size})",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Total Profit / Loss badge
                        val isProfitable = totalProfitLoss >= 0
                        Text(
                            text = "${if (isProfitable) "+" else ""}$${String.format("%.2f", totalProfitLoss)} USD",
                            color = if (isProfitable) SignalBuy else SignalSell,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Open trades list
                    openTrades.forEach { trade ->
                        OpenTradeRowItem(
                            trade = trade,
                            onClose = { onCloseTrade(trade.ticketId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SignalParamItem(label: String, value: String, color: Color) {
    Column {
        Text(
            text = label,
            color = CyberTextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun OpenTradeRowItem(
    trade: ActiveTrade,
    onClose: () -> Unit
) {
    val isBuy = trade.type == SignalType.BUY
    val isProfit = trade.profitUsd >= 0

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = trade.symbol,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isBuy) "BUY ${trade.lotSize}" else "SELL ${trade.lotSize}",
                        color = if (isBuy) SignalBuy else SignalSell,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Open: ${trade.openPrice}  •  Now: ${trade.currentPrice}",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${if (isProfit) "+" else ""}$${String.format("%.2f", trade.profitUsd)}",
                    color = if (isProfit) SignalBuy else SignalSell,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FF334B))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close trade",
                        tint = AccentRed,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
