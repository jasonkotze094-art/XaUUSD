package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CandlestickChartSection
import com.example.ui.components.GoldBeastHeroBanner
import com.example.ui.components.GoldBeastHudPanel
import com.example.ui.components.QuickControlsBar
import com.example.ui.components.RobotCharacter
import com.example.ui.components.RobotListSection
import com.example.ui.components.TradeControls
import com.example.ui.components.TradeHistorySection
import com.example.ui.dialogs.ConnectRobotDialog
import com.example.ui.dialogs.InfoStatusDialog
import com.example.ui.dialogs.MetaTraderConfigDialog
import com.example.ui.dialogs.PairsSelectionDialog
import com.example.ui.drawer.AppDrawerContent
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.GoldBeastTheme
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.SignalBuy
import com.example.viewmodel.TradingViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: TradingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            GoldBeastTheme(accentColor = uiState.selectedAccentColor) {
                MainScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: TradingViewModel,
    uiState: com.example.viewmodel.TradingUiState
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val accentColor = LocalAppAccentColor.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.testTriggerSignalPushNotification()
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.Transparent,
                drawerContentColor = Color.White
            ) {
                AppDrawerContent(
                    selectedAccent = uiState.selectedAccentColor,
                    isVoiceEnabled = uiState.isVoiceEnabled,
                    isPushNotificationsEnabled = uiState.isPushNotificationEnabled,
                    onSelectAccent = { color ->
                        viewModel.selectAccentColor(color)
                    },
                    onToggleVoice = {
                        viewModel.toggleVoiceEnabled()
                    },
                    onTogglePushNotifications = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !uiState.isPushNotificationEnabled) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        viewModel.togglePushNotifications()
                    },
                    onNavigateHome = {
                        scope.launch { drawerState.close() }
                    },
                    onOpenMetaTraderConfig = {
                        scope.launch { drawerState.close() }
                        viewModel.setAccountConfigDialogVisible(true)
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDarkBg),
            containerColor = CyberDarkBg,
            topBar = {
                TopCyberBar(
                    isVoiceEnabled = uiState.isVoiceEnabled,
                    latencyMs = uiState.diagnostics.latencyMs,
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    onToggleVoice = {
                        viewModel.toggleVoiceEnabled()
                    },
                    onOpenAccount = {
                        viewModel.setAccountConfigDialogVisible(true)
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Futuristic Gold Beast AI Hero Banner
                item {
                    GoldBeastHeroBanner(
                        goldPrice = uiState.goldLivePrice,
                        winRate = 94.2,
                        activeRobotsCount = uiState.robots.size
                    )
                }

                // 2. Animated Robot Character Component
                item {
                    RobotCharacter(
                        robot = uiState.activeRobot,
                        isActive = uiState.isAutoTradingActive,
                        executionMode = uiState.executionMode,
                        onTap = {
                            viewModel.triggerRobotSpeech()
                        }
                    )
                }

                // 3. Quick Controls Bar (PAIRS, big TRADE/STOP, INFO)
                item {
                    QuickControlsBar(
                        isActive = uiState.isAutoTradingActive,
                        onToggleTrade = {
                            viewModel.toggleAutoTrading()
                        },
                        onPairsClick = {
                            viewModel.setPairsDialogVisible(true)
                        },
                        onInfoClick = {
                            viewModel.setInfoDialogVisible(true)
                        }
                    )
                }

                // 4. Direct MT5 TradeControls (Buy, Sell with Haptics, and Auto Trading Switch)
                item {
                    TradeControls(
                        isAutoTradingActive = uiState.isAutoTradingActive,
                        onToggleAutoTrading = {
                            viewModel.toggleAutoTrading()
                        },
                        onExecuteBuy = { symbol, lot ->
                            viewModel.executeManualBuy(symbol = symbol, volume = lot)
                        },
                        onExecuteSell = { symbol, lot ->
                            viewModel.executeManualSell(symbol = symbol, volume = lot)
                        },
                        symbols = uiState.symbols,
                        currentLivePrice = uiState.goldLivePrice
                    )
                }

                // 5. Gold Beast Real-Time HUD Panel (Price, Signal, Entry/SL/TP, Active Trades)
                item {
                    GoldBeastHudPanel(
                        goldPrice = uiState.goldLivePrice,
                        priceChange = uiState.goldPriceChange,
                        totalProfitLoss = uiState.totalProfitLoss,
                        signal = uiState.signals.firstOrNull(),
                        openTrades = uiState.openTrades,
                        onCloseTrade = { ticketId ->
                            viewModel.closeTrade(ticketId)
                        },
                        onTestAlertClick = {
                            viewModel.testTriggerSignalPushNotification()
                        }
                    )
                }

                // 6. Interactive Candlestick Chart with Stop Loss, Take Profit & Trailing Stop
                item {
                    CandlestickChartSection(
                        currentPrice = uiState.goldLivePrice,
                        signal = uiState.signals.firstOrNull()
                    )
                }

                // 7. Recent Trade History (Closed Orders from API)
                item {
                    TradeHistorySection(
                        trades = uiState.tradeHistory,
                        onRefresh = {
                            viewModel.refreshTradeHistoryFromApi()
                        }
                    )
                }

                // 8. Robot List Section & + CONNECT NEW ROBOT
                item {
                    RobotListSection(
                        robots = uiState.robots,
                        activeRobotId = uiState.activeRobotId,
                        onSelectRobot = { robotId ->
                            viewModel.selectRobot(robotId)
                        },
                        onConnectNewRobotClick = {
                            viewModel.setConnectRobotDialogVisible(true)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Modal Dialogs
    if (uiState.isInfoDialogOpen) {
        InfoStatusDialog(
            robot = uiState.activeRobot,
            account = uiState.account,
            diagnostics = uiState.diagnostics,
            executionMode = uiState.executionMode,
            onModeChange = { newMode ->
                viewModel.setExecutionMode(newMode)
            },
            onCloseAllTrades = {
                viewModel.closeAllTrades()
            },
            onDismiss = {
                viewModel.setInfoDialogVisible(false)
            }
        )
    }

    if (uiState.isPairsDialogOpen) {
        PairsSelectionDialog(
            robotName = uiState.activeRobot?.name ?: "Goat Empire EA",
            symbols = uiState.symbols,
            onToggleAllowed = { symbolCode ->
                viewModel.toggleSymbolAllowed(symbolCode)
            },
            onUpdateLot = { symbolCode, lot, action ->
                viewModel.updateSymbolSettings(symbolCode, lot, action)
            },
            onDismiss = {
                viewModel.setPairsDialogVisible(false)
            }
        )
    }

    if (uiState.isConnectRobotDialogOpen) {
        ConnectRobotDialog(
            onAddRobot = { name, author, version, desc ->
                viewModel.addNewRobot(name, author, version, desc)
            },
            onDismiss = {
                viewModel.setConnectRobotDialogVisible(false)
            }
        )
    }

    if (uiState.isAccountConfigDialogOpen) {
        MetaTraderConfigDialog(
            account = uiState.account,
            onSave = { acc, broker, server ->
                viewModel.updateAccountCredentials(acc, broker, server)
            },
            onDismiss = {
                viewModel.setAccountConfigDialogVisible(false)
            }
        )
    }
}

@Composable
private fun TopCyberBar(
    isVoiceEnabled: Boolean,
    latencyMs: Int,
    onOpenDrawer: () -> Unit,
    onToggleVoice: () -> Unit,
    onOpenAccount: () -> Unit
) {
    val accentColor = LocalAppAccentColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Drawer Menu Button
        IconButton(
            onClick = onOpenDrawer,
            modifier = Modifier.testTag("menu_drawer_button")
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Open Drawer Menu",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        // Title Branding
        Text(
            text = "GOLD BEAST EA",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )

        // Right Action Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Live Latency Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x33000000))
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(SignalBuy)
                    )
                    Text(
                        text = "${latencyMs}ms",
                        color = SignalBuy,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Voice sound toggle
            IconButton(
                onClick = onToggleVoice,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Toggle Voice",
                    tint = if (isVoiceEnabled) accentColor else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Account modal trigger
            IconButton(
                onClick = onOpenAccount,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Account Settings",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
