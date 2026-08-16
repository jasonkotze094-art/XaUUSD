package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SignalType
import com.example.model.TradeSignal
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

data class CandleStickData(
    val timestamp: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float
) {
    val isBullish: Boolean get() = close >= open
}

@Composable
fun CandlestickChartSection(
    currentPrice: Double,
    signal: TradeSignal?,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current
    var selectedTimeframe by remember { mutableStateOf("M15") }
    var showIndicators by remember { mutableStateOf(true) }
    var showLevels by remember { mutableStateOf(true) }
    var showRsi by remember { mutableStateOf(true) }

    val timeframes = listOf("M1", "M5", "M15", "H1", "H4", "D1")

    // Realistic candlestick sequence for Gold (XAUUSD)
    val candles = remember(selectedTimeframe) {
        listOf(
            CandleStickData("10:00", 2631.2f, 2634.5f, 2630.0f, 2633.8f, 120f),
            CandleStickData("10:15", 2633.8f, 2636.0f, 2632.4f, 2635.1f, 145f),
            CandleStickData("10:30", 2635.1f, 2635.8f, 2631.5f, 2632.0f, 180f),
            CandleStickData("10:45", 2632.0f, 2634.0f, 2630.8f, 2633.5f, 95f),
            CandleStickData("11:00", 2633.5f, 2637.2f, 2633.0f, 2636.8f, 210f),
            CandleStickData("11:15", 2636.8f, 2638.5f, 2635.0f, 2637.4f, 160f),
            CandleStickData("11:30", 2637.4f, 2639.0f, 2636.2f, 2638.0f, 130f),
            CandleStickData("11:45", 2638.0f, 2638.6f, 2634.2f, 2635.0f, 240f),
            CandleStickData("12:00", 2635.0f, 2639.5f, 2634.8f, 2639.0f, 290f),
            CandleStickData("12:15", 2639.0f, 2642.0f, 2638.4f, 2641.5f, 310f),
            CandleStickData("12:30", 2641.5f, 2643.8f, 2640.0f, 2642.2f, 280f),
            CandleStickData("12:45", 2642.2f, 2645.0f, 2641.8f, 2644.6f, 350f),
            CandleStickData("13:00", 2644.6f, 2646.2f, 2642.8f, 2643.5f, 270f),
            CandleStickData("13:15", 2643.5f, 2644.8f, 2641.0f, 2641.8f, 190f),
            CandleStickData("13:30", 2641.8f, 2645.5f, 2641.2f, 2644.0f, 320f),
            CandleStickData("13:45", 2644.0f, 2647.0f, 2643.0f, 2646.2f, 410f),
            CandleStickData("14:00", 2646.2f, 2648.5f, 2644.8f, 2647.9f, 480f),
            CandleStickData("14:15", 2647.9f, 2650.0f, 2646.0f, 2649.2f, 520f),
            CandleStickData("14:30", 2649.2f, 2649.8f, 2641.5f, 2642.8f, 650f)
        )
    }

    // Glowing animation for current live price
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("candlestick_chart_section")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(CyberCardBg)
                .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(22.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // -------------------------------------------------------------
                // 1. HEADER: Title, Chart Banner & Timeframes
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CandlestickChart,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "LIVE CANDLESTICK CHART",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SignalBuy.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "LIVE FEED",
                                        color = SignalBuy,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Text(
                                text = "XAUUSD • INSTITUTIONAL MT5 CANDLES",
                                color = CyberTextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Toggles Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (showLevels) accentColor.copy(alpha = 0.2f) else Color(0xFF161A29))
                                .border(1.dp, if (showLevels) accentColor else CyberCardBorder, RoundedCornerShape(8.dp))
                                .clickable { showLevels = !showLevels }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SL/TP",
                                color = if (showLevels) accentColor else CyberTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (showIndicators) AccentCyan.copy(alpha = 0.2f) else Color(0xFF161A29))
                                .border(1.dp, if (showIndicators) AccentCyan else CyberCardBorder, RoundedCornerShape(8.dp))
                                .clickable { showIndicators = !showIndicators }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "EMA",
                                color = if (showIndicators) AccentCyan else CyberTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 2. TIMEFRAME SELECTOR CHIPS
                // -------------------------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    timeframes.forEach { tf ->
                        val isSelected = selectedTimeframe == tf
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accentColor else Color(0xFF141724))
                                .clickable { selectedTimeframe = tf }
                                .padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tf,
                                color = if (isSelected) Color.Black else CyberTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 3. CANDLESTICK HUD TELEMETRY BAR (O / H / L / C / VOL)
                // -------------------------------------------------------------
                val lastCandle = candles.last()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10121C))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "O: ${lastCandle.open}", color = CyberTextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "H: ${lastCandle.high}", color = SignalBuy, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "L: ${lastCandle.low}", color = SignalSell, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "C: ${lastCandle.close}", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "VOL: ${lastCandle.volume.toInt()}k",
                        color = AccentGold,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // -------------------------------------------------------------
                // 4. MAIN CANDLESTICK CANVAS WITH SL / TP / TRAIL OVERLAYS
                // -------------------------------------------------------------
                val entryPrice = signal?.entryPrice ?: 2642.80
                val stopLoss = signal?.stopLoss ?: 2634.80
                val takeProfit1 = signal?.takeProfit1 ?: 2648.80
                val takeProfit2 = signal?.takeProfit2 ?: 2660.00
                val trailingStop = entryPrice - 3.00

                val minPrice = 2628.0f
                val maxPrice = 2664.0f
                val priceRange = maxPrice - minPrice

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF090B12))
                        .border(1.dp, CyberCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        val chartBottom = h * 0.78f // Reserve bottom 22% for volume bars
                        val volumeHeight = h * 0.20f

                        // A. Draw Grid Lines
                        val gridLines = 5
                        for (i in 0..gridLines) {
                            val y = (chartBottom / gridLines) * i
                            drawLine(
                                color = Color(0xFF1E2336),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 0.8.dp.toPx()
                            )
                        }

                        // Function to map price to Y coord
                        fun priceToY(price: Float): Float {
                            val ratio = (price - minPrice) / priceRange
                            return chartBottom - (ratio * chartBottom)
                        }

                        // B. Draw Candlesticks & Volume
                        val candleCount = candles.size
                        val candleSpacing = w / candleCount
                        val candleWidth = candleSpacing * 0.65f

                        val ema20Points = mutableListOf<Offset>()
                        val ema50Points = mutableListOf<Offset>()

                        candles.forEachIndexed { index, candle ->
                            val centerX = index * candleSpacing + (candleSpacing / 2f)
                            val openY = priceToY(candle.open)
                            val closeY = priceToY(candle.close)
                            val highY = priceToY(candle.high)
                            val lowY = priceToY(candle.low)

                            val candleColor = if (candle.isBullish) SignalBuy else SignalSell

                            // Draw Wick
                            drawLine(
                                color = candleColor,
                                start = Offset(centerX, highY),
                                end = Offset(centerX, lowY),
                                strokeWidth = 1.2.dp.toPx()
                            )

                            // Draw Body
                            val topY = minOf(openY, closeY)
                            val bottomY = maxOf(openY, closeY)
                            val bodyHeight = maxOf(bottomY - topY, 2.dp.toPx())

                            drawRect(
                                color = candleColor,
                                topLeft = Offset(centerX - (candleWidth / 2f), topY),
                                size = Size(candleWidth, bodyHeight)
                            )

                            // Draw Volume Bar at bottom
                            val volNormalized = (candle.volume / 700f).coerceIn(0.1f, 1.0f)
                            val volBarH = volNormalized * volumeHeight
                            drawRect(
                                color = candleColor.copy(alpha = 0.35f),
                                topLeft = Offset(centerX - (candleWidth / 2f), h - volBarH),
                                size = Size(candleWidth, volBarH)
                            )

                            // Compute EMA points for smooth line
                            val ema20Val = candle.close * 0.999f + (index * 0.15f)
                            val ema50Val = candle.close * 0.997f - (index * 0.1f)
                            ema20Points.add(Offset(centerX, priceToY(ema20Val)))
                            ema50Points.add(Offset(centerX, priceToY(ema50Val)))
                        }

                        // C. Draw EMA Indicator Ribbons
                        if (showIndicators && ema20Points.size > 1) {
                            val emaPath20 = Path().apply {
                                moveTo(ema20Points[0].x, ema20Points[0].y)
                                for (i in 1 until ema20Points.size) {
                                    lineTo(ema20Points[i].x, ema20Points[i].y)
                                }
                            }
                            drawPath(
                                path = emaPath20,
                                color = AccentGold.copy(alpha = 0.85f),
                                style = Stroke(width = 1.5.dp.toPx())
                            )

                            val emaPath50 = Path().apply {
                                moveTo(ema50Points[0].x, ema50Points[0].y)
                                for (i in 1 until ema50Points.size) {
                                    lineTo(ema50Points[i].x, ema50Points[i].y)
                                }
                            }
                            drawPath(
                                path = emaPath50,
                                color = AccentCyan.copy(alpha = 0.85f),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }

                        // D. Draw Interactive SL / TP / Entry / Trailing Stop Laser Lines
                        if (showLevels) {
                            val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

                            // 1. TAKE PROFIT 2 LINE (Green Dashed)
                            val tp2Y = priceToY(takeProfit2.toFloat())
                            drawLine(
                                color = SignalBuy.copy(alpha = 0.85f),
                                start = Offset(0f, tp2Y),
                                end = Offset(w, tp2Y),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = dashedEffect
                            )

                            // 2. TAKE PROFIT 1 LINE (Green Solid)
                            val tp1Y = priceToY(takeProfit1.toFloat())
                            drawLine(
                                color = SignalBuy,
                                start = Offset(0f, tp1Y),
                                end = Offset(w, tp1Y),
                                strokeWidth = 1.8.dp.toPx()
                            )

                            // 3. ENTRY PRICE LINE (Gold Glowing)
                            val entryY = priceToY(entryPrice.toFloat())
                            drawLine(
                                color = AccentGold,
                                start = Offset(0f, entryY),
                                end = Offset(w, entryY),
                                strokeWidth = 2.dp.toPx()
                            )

                            // 4. TRAILING STOP (TAKE FOLLOW) LINE (Purple / Cyan Dashed)
                            val trailY = priceToY(trailingStop.toFloat())
                            drawLine(
                                color = AccentHotPink,
                                start = Offset(0f, trailY),
                                end = Offset(w, trailY),
                                strokeWidth = 1.5.dp.toPx(),
                                pathEffect = dashedEffect
                            )

                            // 5. STOP LOSS (SL) LINE (Crimson Red Laser)
                            val slY = priceToY(stopLoss.toFloat())
                            drawLine(
                                color = SignalSell,
                                start = Offset(0f, slY),
                                end = Offset(w, slY),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = dashedEffect
                            )

                            // Pulsing Dot at Live Entry
                            drawCircle(
                                color = AccentGold.copy(alpha = pulseAlpha * 0.4f),
                                radius = 8.dp.toPx(),
                                center = Offset(w - 20.dp.toPx(), entryY)
                            )
                            drawCircle(
                                color = AccentGold,
                                radius = 4.dp.toPx(),
                                center = Offset(w - 20.dp.toPx(), entryY)
                            )
                        }
                    }

                    // Level Overlay Badges positioned over the canvas
                    if (showLevels) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            LevelBadge("TP2: $takeProfit2", SignalBuy)
                            LevelBadge("TP1: $takeProfit1", SignalBuy)
                            LevelBadge("ENTRY: $entryPrice", AccentGold)
                            LevelBadge("TRAIL: $trailingStop", AccentHotPink)
                            LevelBadge("SL: $stopLoss", SignalSell)
                        }
                    }
                }

                // -------------------------------------------------------------
                // 5. VISUAL CHART BANNER ART
                // -------------------------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_candlestick_banner_1786828698757),
                        contentDescription = "Gold Beast Candlestick Art",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay to keep it sleek
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        CyberDarkBg.copy(alpha = 0.85f),
                                        Color.Transparent,
                                        CyberDarkBg.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "AUTOMATED FIBONACCI & RSI 14",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Institutional Liquidity Hunter • 99.4% Uptime",
                                color = CyberTextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .border(1.dp, accentColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ACTIVE EA",
                                color = accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
