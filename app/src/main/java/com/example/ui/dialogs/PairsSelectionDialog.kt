package com.example.ui.dialogs

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ActionType
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

@Composable
fun PairsSelectionDialog(
    robotName: String,
    symbols: List<TradingSymbol>,
    onToggleAllowed: (String) -> Unit,
    onUpdateLot: (String, Double, ActionType) -> Unit,
    onDismiss: () -> Unit
) {
    val accentColor = LocalAppAccentColor.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var editingSymbol by remember { mutableStateOf<TradingSymbol?>(null) }

    val allowedSymbols = symbols.filter { it.isAllowed }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                // Top App Bar: Back Arrow + Robot Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("back_pairs_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = robotName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Cursive
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Tab Row (Allowed Symbols | All Symbols)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = AccentRed,
                                height = 2.5.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Allowed Symbols",
                                color = if (selectedTab == 0) Color.White else CyberTextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Cursive
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "All Symbols",
                                color = if (selectedTab == 1) Color.White else CyberTextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Cursive
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle helper prompt (matching video)
                Text(
                    text = if (selectedTab == 0)
                        "These are Symbols you have selected for your EA to trade."
                    else
                        "Select symbols you want your EA to trade, you can select multiple.",
                    color = CyberTextSecondary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Cursive,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content List
                if (selectedTab == 0) {
                    // Allowed Symbols
                    if (allowedSymbols.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No symbols selected yet. Go to 'All Symbols' to add.",
                                color = CyberTextMuted,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(allowedSymbols) { item ->
                                AllowedSymbolCard(
                                    symbol = item,
                                    onEdit = { editingSymbol = item },
                                    onRemove = { onToggleAllowed(item.symbol) }
                                )
                            }
                        }
                    }
                } else {
                    // All Symbols
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(symbols) { item ->
                            AllSymbolCard(
                                symbol = item,
                                onToggle = { onToggleAllowed(item.symbol) }
                            )
                        }
                    }
                }
            }

            // Edit Lot Size Dialog Modal
            editingSymbol?.let { sym ->
                EditLotSizeSheet(
                    symbol = sym,
                    onSave = { newLot, newAction ->
                        onUpdateLot(sym.symbol, newLot, newAction)
                        editingSymbol = null
                    },
                    onDismiss = { editingSymbol = null }
                )
            }
        }
    }
}

@Composable
private fun AllowedSymbolCard(
    symbol: TradingSymbol,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberCardBg)
            .border(BorderStroke(1.2.dp, CyberCardBorder), RoundedCornerShape(16.dp))
            .clickable { onEdit() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Circular Arrow Button (matching video)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1C1E2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = symbol.symbol,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Lot Size : ${symbol.lotSize}",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "•",
                            color = CyberCardBorder,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Action : ${symbol.actionType.label}",
                            color = SignalBuy,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = CyberTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AllSymbolCard(
    symbol: TradingSymbol,
    onToggle: () -> Unit
) {
    val isSelected = symbol.isAllowed

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberCardBg)
            .border(
                BorderStroke(1.2.dp, if (isSelected) SignalBuy.copy(alpha = 0.6f) else CyberCardBorder),
                RoundedCornerShape(16.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Circle Arrow
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) SignalBuy.copy(alpha = 0.2f) else Color(0xFF1C1E2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = if (isSelected) SignalBuy else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = symbol.symbol,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = symbol.name,
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = "$${symbol.currentPrice}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun EditLotSizeSheet(
    symbol: TradingSymbol,
    onSave: (Double, ActionType) -> Unit,
    onDismiss: () -> Unit
) {
    var lot by remember { mutableStateOf(symbol.lotSize.toString()) }
    var action by remember { mutableStateOf(symbol.actionType) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CyberCardBg)
                .border(BorderStroke(1.dp, CyberCardBorder), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Configure ${symbol.symbol}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Lot Sizing selector buttons
                Text(text = "Lot Size", color = CyberTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.01, 0.02, 0.05, 0.10, 0.50).forEach { size ->
                        val isCurr = lot == size.toString()
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurr) AccentGold else CyberCardBgElevated)
                                .clickable { lot = size.toString() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$size",
                                color = if (isCurr) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Action selector
                Text(text = "Trade Direction", color = CyberTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionType.values().forEach { type ->
                        val isCurr = action == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurr) SignalBuy.copy(alpha = 0.25f) else CyberCardBgElevated)
                                .border(
                                    1.dp,
                                    if (isCurr) SignalBuy else CyberCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { action = type }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = type.label,
                                color = if (isCurr) SignalBuy else CyberTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentGold)
                            .clickable {
                                val parsed = lot.toDoubleOrNull() ?: 0.01
                                onSave(parsed, action)
                            }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "SAVE SETTINGS",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
