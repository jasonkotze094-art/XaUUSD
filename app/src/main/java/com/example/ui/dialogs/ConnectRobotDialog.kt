package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AccentGold
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalAppAccentColor

@Composable
fun ConnectRobotDialog(
    onAddRobot: (name: String, author: String, version: String, desc: String) -> Unit,
    onDismiss: () -> Unit
) {
    val accentColor = LocalAppAccentColor.current
    var name by remember { mutableStateOf("Gold Sniper Beast") }
    var author by remember { mutableStateOf("GoldBeast FX") }
    var version by remember { mutableStateOf("v2.0 PRO") }
    var description by remember { mutableStateOf("Auto scalper with multi-timeframe confirmation") }
    var webhookUrl by remember { mutableStateOf("https://vps-ea.goldbeast.io/webhook/mt5") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CyberCardBg)
                .border(BorderStroke(1.5.dp, CyberCardBorder), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = accentColor
                        )
                        Text(
                            text = "Connect New Robot",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextMuted
                        )
                    }
                }

                Text(
                    text = "Link an Expert Advisor (EA) script running on your VPS MetaTrader 5 terminal.",
                    color = CyberTextSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Robot Name") },
                    colors = textFieldColors(accentColor),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author / Developer") },
                    colors = textFieldColors(accentColor),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = webhookUrl,
                    onValueChange = { webhookUrl = it },
                    label = { Text("MT5 VPS Webhook Bridge URL") },
                    colors = textFieldColors(accentColor),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { onAddRobot(name, author, version, description) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_connect_robot_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LINK ROBOT TO MT5",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun textFieldColors(accentColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = accentColor,
    unfocusedBorderColor = CyberCardBorder,
    focusedLabelColor = accentColor,
    unfocusedLabelColor = CyberTextMuted,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
