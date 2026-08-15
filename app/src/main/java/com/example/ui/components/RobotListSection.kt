package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.RobotProfile
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.SignalBuy

@Composable
fun RobotListSection(
    robots: List<RobotProfile>,
    activeRobotId: String,
    onSelectRobot: (String) -> Unit,
    onConnectNewRobotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Robot List",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )

        // List of Robots
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            robots.forEach { robot ->
                val isSelected = robot.id == activeRobotId
                RobotCardItem(
                    robot = robot,
                    isSelected = isSelected,
                    onClick = { onSelectRobot(robot.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // + CONNECT NEW ROBOT Button (matching video)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("connect_new_robot_button")
                .clip(RoundedCornerShape(50))
                .background(Color.Black)
                .border(
                    BorderStroke(1.5.dp, accentColor),
                    RoundedCornerShape(50)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = accentColor),
                    onClick = onConnectNewRobotClick
                )
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "CONNECT NEW ROBOT",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun RobotCardItem(
    robot: RobotProfile,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = LocalAppAccentColor.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberCardBg)
            .border(
                BorderStroke(
                    width = if (isSelected) 1.5.dp else 0.8.dp,
                    color = if (isSelected) accentColor else CyberCardBorder
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                onClick = onClick
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Robot Avatar Thumbnail
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .border(
                            BorderStroke(1.dp, if (isSelected) accentColor else Color(0x33FFFFFF)),
                            CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(id = robot.avatarDrawableRes),
                        contentDescription = robot.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = robot.name,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SignalBuy.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = SignalBuy,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${robot.author}  •  Win: ${robot.winRatePercent}%  •  ${robot.version}",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isSelected) accentColor else CyberTextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
