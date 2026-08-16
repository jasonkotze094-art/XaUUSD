package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountPerformancePoint
import com.example.model.PerformanceTimeRange
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentHotPink
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBgElevated
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalSell

@Composable
fun AccountPerformanceChartSection(
    performancePoints: List<AccountPerformancePoint>,
    selectedRange: PerformanceTimeRange,
    isLoading: Boolean,
    currentBalance: Double,
    currentEquity: Double,
    peakEquity: Double,
    netGrowthPercent: Double,
    maxDrawdownPercent: Double,
    onSelectRange: (PerformanceTimeRange) -> Unit,
    onRefreshFromApi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current
    var activeTouchIndex by remember { mutableStateOf<Int?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val rotateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    val touchedPoint = activeTouchIndex?.let { idx ->
        if (idx in performancePoints.indices) performancePoints[idx] else null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("account_performance_chart_section")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(CyberCardBg)
                .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(22.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // -------------------------------------------------------------
                // 1. SECTION HEADER (Recharts Inspired Style)
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoGraph,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "EQUITY & BALANCE TRENDS",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.8.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AccentCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "RECHARTS",
                                        color = AccentCyan,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Text(
                                text = "MT5 REST API Performance Visualizer",
                                color = CyberTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // API Refresh Action Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isLoading) AccentCyan.copy(alpha = 0.2f) else Color(0xFF141828))
                            .border(1.dp, if (isLoading) AccentCyan else CyberCardBorder, RoundedCornerShape(10.dp))
                            .clickable(enabled = !isLoading) { onRefreshFromApi() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("fetch_equity_api_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh API",
                                tint = if (isLoading) AccentCyan else CyberTextSecondary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .then(if (isLoading) Modifier.rotate(rotateAnim) else Modifier)
                            )
                            Text(
                                text = if (isLoading) "SYNCING..." else "SYNC API",
                                color = if (isLoading) AccentCyan else CyberTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 2. RECHARTS LEGEND BAR
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F121E))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Balance Legend item
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(AccentCyan)
                            )
                            Text(
                                text = "Balance: $${String.format("%.2f", currentBalance)}",
                                color = AccentCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Equity Legend item
                        Row(
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
                                text = "Equity: $${String.format("%.2f", currentEquity)}",
                                color = SignalBuy,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Net floating badge
                    val floatingPnl = currentEquity - currentBalance
                    val isPositive = floatingPnl >= 0
                    Text(
                        text = "${if (isPositive) "+" else ""}$${String.format("%.2f", floatingPnl)} P/L",
                        color = if (isPositive) SignalBuy else SignalSell,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // -------------------------------------------------------------
                // 3. KEY PERFORMANCE TELEMETRY CARDS (KPIs)
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PerformanceKpiCard(
                        title = "PEAK EQUITY",
                        value = "$${String.format("%.0f", peakEquity)}",
                        subtext = "All-Time High",
                        valueColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                    PerformanceKpiCard(
                        title = "NET GROWTH",
                        value = "+${String.format("%.1f", netGrowthPercent)}%",
                        subtext = "Profit ROI",
                        valueColor = SignalBuy,
                        modifier = Modifier.weight(1f)
                    )
                    PerformanceKpiCard(
                        title = "MAX DRAWDOWN",
                        value = "${String.format("%.1f", maxDrawdownPercent)}%",
                        subtext = "Low Risk Score",
                        valueColor = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                // -------------------------------------------------------------
                // 4. TIMEFRAME SELECTOR CHIPS (1D, 1W, 1M, 3M, 1Y, ALL)
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PerformanceTimeRange.values().forEach { range ->
                        val isSelected = selectedRange == range
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentCyan else Color(0xFF141826))
                                .clickable { onSelectRange(range) }
                                .padding(vertical = 6.dp)
                                .testTag("time_range_${range.label}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = range.label,
                                color = if (isSelected) Color.Black else CyberTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 5. INTERACTIVE RECHARTS-EQUIVALENT LINE CHART CANVAS
                // -------------------------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0A0C16))
                        .border(1.dp, CyberCardBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                ) {
                    if (performancePoints.isNotEmpty()) {
                        val minVal = (performancePoints.minOf { minOf(it.balance, it.equity) } * 0.985).toFloat()
                        val maxVal = (performancePoints.maxOf { maxOf(it.balance, it.equity) } * 1.015).toFloat()
                        val valueRange = (maxVal - minVal).coerceAtLeast(10f)

                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(performancePoints) {
                                    detectTapGestures(
                                        onPress = { offset ->
                                            val step = size.width / (performancePoints.size - 1).coerceAtLeast(1)
                                            val idx = (offset.x / step).toInt().coerceIn(0, performancePoints.size - 1)
                                            activeTouchIndex = idx
                                        }
                                    )
                                }
                                .pointerInput(performancePoints) {
                                    detectDragGestures(
                                        onDrag = { change, _ ->
                                            val step = size.width / (performancePoints.size - 1).coerceAtLeast(1)
                                            val idx = (change.position.x / step).toInt().coerceIn(0, performancePoints.size - 1)
                                            activeTouchIndex = idx
                                        },
                                        onDragEnd = {
                                            // Keep selected point visible
                                        }
                                    )
                                }
                        ) {
                            val w = size.width
                            val h = size.height
                            val chartPaddingTop = 18.dp.toPx()
                            val chartPaddingBottom = 26.dp.toPx()
                            val availableH = h - chartPaddingTop - chartPaddingBottom

                            fun valToY(v: Float): Float {
                                val ratio = (v - minVal) / valueRange
                                return (chartPaddingTop + availableH) - (ratio * availableH)
                            }

                            // 1. Draw Horizontal Grid Lines (Recharts CartesianGrid)
                            val gridLines = 4
                            val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            for (i in 0..gridLines) {
                                val y = chartPaddingTop + (availableH / gridLines) * i
                                drawLine(
                                    color = Color(0xFF1E243A),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 0.8.dp.toPx(),
                                    pathEffect = dashedEffect
                                )
                            }

                            val count = performancePoints.size
                            val stepX = w / (count - 1).coerceAtLeast(1)

                            val balanceOffsets = mutableListOf<Offset>()
                            val equityOffsets = mutableListOf<Offset>()

                            performancePoints.forEachIndexed { i, p ->
                                val x = i * stepX
                                val yBalance = valToY(p.balance.toFloat())
                                val yEquity = valToY(p.equity.toFloat())
                                balanceOffsets.add(Offset(x, yBalance))
                                equityOffsets.add(Offset(x, yEquity))
                            }

                            // 2. Draw Gradient Area under Balance Curve (Recharts Area fill)
                            if (balanceOffsets.size > 1) {
                                val areaPath = Path().apply {
                                    moveTo(balanceOffsets.first().x, balanceOffsets.first().y)
                                    for (k in 1 until balanceOffsets.size) {
                                        lineTo(balanceOffsets[k].x, balanceOffsets[k].y)
                                    }
                                    lineTo(balanceOffsets.last().x, h - chartPaddingBottom)
                                    lineTo(balanceOffsets.first().x, h - chartPaddingBottom)
                                    close()
                                }

                                drawPath(
                                    path = areaPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            AccentCyan.copy(alpha = 0.30f),
                                            AccentCyan.copy(alpha = 0.05f),
                                            Color.Transparent
                                        ),
                                        startY = chartPaddingTop,
                                        endY = h - chartPaddingBottom
                                    )
                                )
                            }

                            // 3. Draw Balance Line (Cyan Stroke)
                            if (balanceOffsets.size > 1) {
                                val balancePath = Path().apply {
                                    moveTo(balanceOffsets.first().x, balanceOffsets.first().y)
                                    for (k in 1 until balanceOffsets.size) {
                                        lineTo(balanceOffsets[k].x, balanceOffsets[k].y)
                                    }
                                }
                                drawPath(
                                    path = balancePath,
                                    color = AccentCyan,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            // 4. Draw Floating Equity Line (Emerald Green Stroke)
                            if (equityOffsets.size > 1) {
                                val equityPath = Path().apply {
                                    moveTo(equityOffsets.first().x, equityOffsets.first().y)
                                    for (k in 1 until equityOffsets.size) {
                                        lineTo(equityOffsets[k].x, equityOffsets[k].y)
                                    }
                                }
                                drawPath(
                                    path = equityPath,
                                    color = SignalBuy,
                                    style = Stroke(width = 2.5.dp.toPx())
                                )
                            }

                            // 5. Draw Dot Nodes on Equity Line
                            equityOffsets.forEachIndexed { i, pt ->
                                val isHovered = activeTouchIndex == i
                                val dotRadius = if (isHovered) 5.dp.toPx() else 3.dp.toPx()
                                drawCircle(
                                    color = if (isHovered) Color.White else SignalBuy,
                                    radius = dotRadius,
                                    center = pt
                                )
                            }

                            // 6. Draw Interactive Touch Scrubber Line (Vertical Cursor)
                            activeTouchIndex?.let { idx ->
                                if (idx in equityOffsets.indices) {
                                    val scrubberX = equityOffsets[idx].x
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.75f),
                                        start = Offset(scrubberX, chartPaddingTop),
                                        end = Offset(scrubberX, h - chartPaddingBottom),
                                        strokeWidth = 1.2.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                                    )

                                    // Pulsing ring at touched equity point
                                    val eqPt = equityOffsets[idx]
                                    drawCircle(
                                        color = SignalBuy.copy(alpha = pulseGlow * 0.5f),
                                        radius = 10.dp.toPx(),
                                        center = eqPt
                                    )
                                    drawCircle(
                                        color = SignalBuy,
                                        radius = 5.dp.toPx(),
                                        center = eqPt
                                    )
                                }
                            }
                        }

                        // -------------------------------------------------------------
                        // Interactive Tooltip Overlay (Recharts Tooltip Popup)
                        // -------------------------------------------------------------
                        touchedPoint?.let { point ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF161C2E).copy(alpha = 0.95f))
                                    .border(1.dp, AccentCyan.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "TIME: ${point.timestamp}",
                                            color = CyberTextMuted,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "BAL: $${String.format("%.2f", point.balance)}",
                                            color = AccentCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(22.dp)
                                            .background(CyberCardBorder)
                                    )
                                    Column {
                                        Text(
                                            text = "EQ: $${String.format("%.2f", point.equity)}",
                                            color = SignalBuy,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        val pnl = point.floatingPnl
                                        Text(
                                            text = "${if (pnl >= 0) "+" else ""}$${String.format("%.2f", pnl)}",
                                            color = if (pnl >= 0) SignalBuy else SignalSell,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Loading performance history...",
                                color = CyberTextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 6. BOTTOM TELEMETRY FOOTER
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SignalBuy)
                        )
                        Text(
                            text = "MetaTrader 5 API Bridge • ICMarkets-Live05",
                            color = CyberTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Touch to inspect point",
                        color = CyberTextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceKpiCard(
    title: String,
    value: String,
    subtext: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0E111E))
            .border(1.dp, CyberCardBorder.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = title,
                color = CyberTextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = subtext,
                color = CyberTextSecondary,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
