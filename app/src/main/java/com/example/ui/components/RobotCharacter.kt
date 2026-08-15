package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ExecutionMode
import com.example.model.RobotProfile
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentHotPink
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.SignalBuy
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RobotCharacter(
    robot: RobotProfile?,
    isActive: Boolean,
    executionMode: ExecutionMode,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current

    // Infinite animation transitions for RobotCharacter
    val infiniteTransition = rememberInfiniteTransition(label = "RobotAnimationTransition")

    // 1. Aura & Reactor Core Pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = if (isActive) 1.05f else 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isActive) 1200 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // 2. HUD Ring Continuous Rotation
    val hudRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isActive) 6000 else 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HudRotation"
    )

    // 3. Counter Rotation for Secondary Reticle
    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isActive) 8000 else 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CounterRotation"
    )

    // 4. Laser Visor Scanning line animation (vertical sweep)
    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScanLineProgress"
    )

    // 5. Dynamic Aura Glow Color
    val targetGlowColor = if (isActive) accentColor else accentColor.copy(alpha = 0.4f)
    val animatedGlowColor by animateColorAsState(
        targetValue = targetGlowColor,
        animationSpec = tween(600),
        label = "AuraColor"
    )

    // 6. Holographic Wave Expansion
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveScale"
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Robot Container Card with Glowing Border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CyberCardBg)
                .border(
                    BorderStroke(
                        width = if (isActive) 2.dp else 1.2.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                animatedGlowColor,
                                CyberCardBorder,
                                animatedGlowColor.copy(alpha = 0.3f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = accentColor),
                    onClick = onTap
                ),
            contentAlignment = Alignment.Center
        ) {
            // Background Cyber Grid Drawing & Glowing Ambient Light
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Radial Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            animatedGlowColor.copy(alpha = if (isActive) 0.30f else 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth / 2, canvasHeight / 2),
                        radius = canvasWidth * 0.65f
                    )
                )

                // Cyber Grid Lines
                val step = 32.dp.toPx()
                var x = 0f
                while (x < canvasWidth) {
                    drawLine(
                        color = Color(0x0CFFFFFF),
                        start = Offset(x, 0f),
                        end = Offset(x, canvasHeight),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = 0f
                while (y < canvasHeight) {
                    drawLine(
                        color = Color(0x0CFFFFFF),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            }

            // Robot Avatar & Animated HUD Layer
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Expanding sonic sonar wave if active
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .scale(waveScale)
                            .border(
                                width = 1.5.dp,
                                color = animatedGlowColor.copy(alpha = waveAlpha),
                                shape = CircleShape
                            )
                    )
                }

                // Rotating HUD Rings & Target Reticles Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val outerRadius = size.width * 0.46f
                    val innerRadius = size.width * 0.40f

                    // Outer segmented ring
                    rotate(hudRotation, pivot = center) {
                        drawCircle(
                            color = animatedGlowColor.copy(alpha = 0.35f),
                            radius = outerRadius,
                            style = Stroke(
                                width = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 15f, 10f, 15f), 0f)
                            )
                        )

                        // 4 Orbiting Neon Ticks
                        for (i in 0 until 4) {
                            val angle = (i * 90.0) * (Math.PI / 180.0)
                            val px = center.x + outerRadius * cos(angle).toFloat()
                            val py = center.y + outerRadius * sin(angle).toFloat()
                            drawCircle(
                                color = animatedGlowColor,
                                radius = 4f,
                                center = Offset(px, py)
                            )
                        }
                    }

                    // Inner reverse rotating dashed ring
                    rotate(counterRotation, pivot = center) {
                        drawCircle(
                            color = if (isActive) AccentHotPink.copy(alpha = 0.45f) else Color(0x33FFFFFF),
                            radius = innerRadius,
                            style = Stroke(
                                width = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 25f), 0f)
                            )
                        )
                    }
                }

                // Cyber Robot Image with Circular Mask & Neon Frame
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .border(
                            BorderStroke(2.dp, animatedGlowColor),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = robot?.avatarDrawableRes ?: R.drawable.robot_cyber_avatar_1786825877723),
                        contentDescription = "Robot Character Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay Laser Visor Scanline Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val scanY = size.height * scanLineProgress
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    animatedGlowColor.copy(alpha = 0.8f),
                                    Color.White,
                                    animatedGlowColor.copy(alpha = 0.8f),
                                    Color.Transparent
                                )
                            ),
                            start = Offset(0f, scanY),
                            end = Offset(size.width, scanY),
                            strokeWidth = 2.5f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Top Floating Status Pill (e.g. "Goat Empire EA" / "Fxgoat")
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xE610111A))
                        .border(BorderStroke(1.dp, animatedGlowColor.copy(alpha = 0.6f)), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FiberManualRecord,
                            contentDescription = null,
                            tint = if (isActive) SignalBuy else animatedGlowColor,
                            modifier = Modifier.size(8.dp)
                        )
                        Text(
                            text = if (isActive) "EA RUNNING • 12ms" else "EA STANDBY",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Bottom Name and Author Overlay (matching video layout)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xCC090A10),
                                Color(0xF2090A10)
                            )
                        )
                    )
                    .padding(bottom = 12.dp, top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = robot?.name ?: "Goat Empire EA",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = robot?.author ?: "Fxgoat",
                        color = accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "•",
                        color = CyberCardBorder,
                        fontSize = 12.sp
                    )
                    Text(
                        text = executionMode.title,
                        color = if (executionMode == ExecutionMode.DYNAMIC) AccentHotPink else AccentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
