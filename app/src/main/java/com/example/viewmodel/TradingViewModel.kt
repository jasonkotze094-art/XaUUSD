package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.model.ActionType
import com.example.model.ActiveTrade
import com.example.model.DiagnosticStatus
import com.example.model.ExecutionMode
import com.example.model.MetaTraderAccount
import com.example.model.RobotProfile
import com.example.model.SignalType
import com.example.model.TradeSignal
import com.example.model.TradingSymbol
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentHotPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AccentSlate
import com.example.ui.theme.AccentWhite
import com.example.util.VoiceEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TradingUiState(
    val robots: List<RobotProfile> = emptyList(),
    val activeRobotId: String = "goat_empire",
    val isAutoTradingActive: Boolean = false,
    val executionMode: ExecutionMode = ExecutionMode.NORMAL,
    val selectedAccentColor: Color = AccentGold,
    val symbols: List<TradingSymbol> = emptyList(),
    val signals: List<TradeSignal> = emptyList(),
    val openTrades: List<ActiveTrade> = emptyList(),
    val account: MetaTraderAccount = MetaTraderAccount(),
    val diagnostics: DiagnosticStatus = DiagnosticStatus(),
    val isVoiceEnabled: Boolean = true,
    val goldLivePrice: Double = 2642.80,
    val goldPriceChange: Double = 0.84,
    val totalProfitLoss: Double = 624.80,
    val isInfoDialogOpen: Boolean = false,
    val isPairsDialogOpen: Boolean = false,
    val isConnectRobotDialogOpen: Boolean = false,
    val isAccountConfigDialogOpen: Boolean = false,
    val isDrawerOpen: Boolean = false,
    val recentTelemetryLog: String = "Ready for MT5 commands"
) {
    val activeRobot: RobotProfile?
        get() = robots.find { it.id == activeRobotId } ?: robots.firstOrNull()
}

class TradingViewModel(application: Application) : AndroidViewModel(application) {

    private val voiceEngine = VoiceEngine(application)

    private val defaultRobots = listOf(
        RobotProfile(
            id = "goat_empire",
            name = "Goat Empire EA",
            author = "Fxgoat",
            version = "v4.2 PRO",
            subtitle = "Tradeport.EA",
            description = "Specialized institutional Gold & Forex momentum breakout EA with smart trailing stop algorithms.",
            avatarDrawableRes = R.drawable.robot_cyber_avatar_1786825877723,
            defaultMode = ExecutionMode.NORMAL,
            winRatePercent = 91.2,
            totalTrades = 1840,
            profitTotalUsd = 6420.50,
            profitFactor = 3.12,
            statusText = "Algorithmic Money Engine Active"
        ),
        RobotProfile(
            id = "gold_beast_pro",
            name = "Gold Beast EA",
            author = "GoldBeast Team",
            version = "v3.8 VIP",
            subtitle = "XAUUSD Specialist",
            description = "High-precision scalping robot optimized for Gold (XAUUSD) liquidity sweeps, order blocks, and dynamic TP scaling.",
            avatarDrawableRes = R.drawable.robot_cyber_avatar_1786825877723,
            defaultMode = ExecutionMode.DYNAMIC,
            winRatePercent = 88.6,
            totalTrades = 942,
            profitTotalUsd = 3890.00,
            profitFactor = 2.74,
            statusText = "Liquidity Sniper Engaged"
        ),
        RobotProfile(
            id = "scalper_matrix",
            name = "Scalper Matrix EA",
            author = "AlphaQuant",
            version = "v2.5",
            subtitle = "Multi-Pair VPS",
            description = "Micro-trend arbitrage and mean reversion robot for US30, NAS100, and GBPUSD with ultra-low latency.",
            avatarDrawableRes = R.drawable.robot_cyber_avatar_1786825877723,
            defaultMode = ExecutionMode.NORMAL,
            winRatePercent = 84.1,
            totalTrades = 620,
            profitTotalUsd = 2150.20,
            profitFactor = 2.15,
            statusText = "Multi-Currency Matrix Standby"
        )
    )

    private val defaultSymbols = listOf(
        TradingSymbol("GBPUSD", "British Pound / US Dollar", 0.01, ActionType.BOTH, true, 1.2845, 0.32, 0.8, digits = 4),
        TradingSymbol("XAUUSD", "Gold Spot / US Dollar", 0.05, ActionType.BOTH, true, 2642.80, 0.84, 1.2, digits = 2),
        TradingSymbol("GOLDm", "Gold Micro / US Dollar", 0.10, ActionType.BOTH, false, 2642.80, 0.84, 1.0, digits = 2),
        TradingSymbol("USDCHF", "US Dollar / Swiss Franc", 0.02, ActionType.BOTH, false, 0.8912, -0.15, 0.9, digits = 4),
        TradingSymbol("XAUUSDm", "Gold Micro Standard", 0.05, ActionType.BOTH, false, 2642.80, 0.84, 1.1, digits = 2),
        TradingSymbol("US30", "Wall Street 30 Index", 0.02, ActionType.BUY_ONLY, false, 43980.0, 1.12, 2.0, digits = 1),
        TradingSymbol("EURUSD", "Euro / US Dollar", 0.05, ActionType.BOTH, false, 1.0824, 0.08, 0.6, digits = 4),
        TradingSymbol("NAS100", "US Tech 100 Index", 0.01, ActionType.BOTH, false, 20850.0, 1.45, 1.8, digits = 1)
    )

    private val defaultSignals = listOf(
        TradeSignal(
            id = "sig_gold_1",
            symbol = "XAUUSD",
            type = SignalType.BUY,
            entryPrice = 2640.20,
            stopLoss = 2634.00,
            takeProfit1 = 2648.50,
            takeProfit2 = 2656.00,
            trailingStopPips = 25.0,
            confidencePercent = 94.8,
            timeAgo = "1m ago",
            reason = "H1 Order Block Rejection + Bullish EMA Crossover"
        ),
        TradeSignal(
            id = "sig_gbp_1",
            symbol = "GBPUSD",
            type = SignalType.BUY,
            entryPrice = 1.2835,
            stopLoss = 1.2805,
            takeProfit1 = 1.2880,
            takeProfit2 = 1.2920,
            trailingStopPips = 15.0,
            confidencePercent = 89.2,
            timeAgo = "3m ago",
            reason = "Liquidity Sweep at London Session Open"
        )
    )

    private val defaultTrades = listOf(
        ActiveTrade(
            ticketId = 8492019,
            symbol = "XAUUSD",
            type = SignalType.BUY,
            lotSize = 0.05,
            openPrice = 2638.40,
            currentPrice = 2642.80,
            stopLoss = 2634.00,
            takeProfit = 2656.00,
            profitUsd = 220.00,
            trailingStopActive = true,
            openTime = "14:22:08"
        ),
        ActiveTrade(
            ticketId = 8492024,
            symbol = "GBPUSD",
            type = SignalType.BUY,
            lotSize = 0.01,
            openPrice = 1.2838,
            currentPrice = 1.2845,
            stopLoss = 1.2805,
            takeProfit = 1.2880,
            profitUsd = 7.00,
            trailingStopActive = true,
            openTime = "14:28:15"
        )
    )

    private val _uiState = MutableStateFlow(
        TradingUiState(
            robots = defaultRobots,
            symbols = defaultSymbols,
            signals = defaultSignals,
            openTrades = defaultTrades
        )
    )
    val uiState: StateFlow<TradingUiState> = _uiState.asStateFlow()

    init {
        startLivePriceSimulation()
    }

    private fun startLivePriceSimulation() {
        viewModelScope.launch {
            while (isActive) {
                delay(1200)
                updateSimulatedMarketTicks()
            }
        }
    }

    private fun updateSimulatedMarketTicks() {
        _uiState.update { current ->
            val delta = (Random.nextDouble(-0.35, 0.45))
            val newGoldPrice = (current.goldLivePrice + delta).coerceIn(2620.0, 2690.0)
            val roundedGold = Math.round(newGoldPrice * 100.0) / 100.0

            // Update open trades PnL
            val updatedTrades = current.openTrades.map { trade ->
                if (trade.symbol.contains("XAU") || trade.symbol.contains("GOLD")) {
                    val pnlDiff = (roundedGold - trade.openPrice) * (trade.lotSize * 100.0)
                    val pnl = Math.round(pnlDiff * 100.0) / 100.0
                    trade.copy(currentPrice = roundedGold, profitUsd = pnl)
                } else {
                    trade
                }
            }

            val totalPnl = updatedTrades.sumOf { it.profitUsd }
            val roundedTotalPnl = Math.round(totalPnl * 100.0) / 100.0

            val updatedAccount = current.account.copy(
                equity = current.account.balance + roundedTotalPnl
            )

            current.copy(
                goldLivePrice = roundedGold,
                openTrades = updatedTrades,
                totalProfitLoss = roundedTotalPnl,
                account = updatedAccount
            )
        }
    }

    fun toggleAutoTrading() {
        val newState = !_uiState.value.isAutoTradingActive
        _uiState.update { current ->
            current.copy(
                isAutoTradingActive = newState,
                diagnostics = current.diagnostics.copy(
                    botActive = newState,
                    botStateLabel = if (newState) "BOT ACTIVE" else "BOT STANDBY"
                ),
                recentTelemetryLog = if (newState) "Auto-execution engine running via MT5 EA Bridge" else "Trading halted. Standby mode."
            )
        }

        if (newState) {
            voiceEngine.speakTradingActivated()
            // Auto open a simulation trade if none active
            if (_uiState.value.openTrades.isEmpty()) {
                executeAutoSignalTrade()
            }
        } else {
            voiceEngine.speakTradingDeactivated()
        }
    }

    fun setExecutionMode(mode: ExecutionMode) {
        _uiState.update { it.copy(executionMode = mode) }
        voiceEngine.speakModeChanged(mode.title)
    }

    fun selectAccentColor(color: Color) {
        _uiState.update { it.copy(selectedAccentColor = color) }
    }

    fun selectRobot(robotId: String) {
        _uiState.update { current ->
            val robot = current.robots.find { it.id == robotId }
            current.copy(
                activeRobotId = robotId,
                executionMode = robot?.defaultMode ?: current.executionMode
            )
        }
        val name = _uiState.value.activeRobot?.name ?: "Robot"
        voiceEngine.speak("$name connected and ready.")
    }

    fun toggleSymbolAllowed(symbolCode: String) {
        _uiState.update { current ->
            val updatedSymbols = current.symbols.map {
                if (it.symbol == symbolCode) it.copy(isAllowed = !it.isAllowed) else it
            }
            val allowedSummary = updatedSymbols.filter { it.isAllowed }.joinToString(", ") { it.symbol }
            current.copy(
                symbols = updatedSymbols,
                diagnostics = current.diagnostics.copy(activePairsSummary = allowedSummary.ifEmpty { "None" })
            )
        }
    }

    fun updateSymbolSettings(symbolCode: String, lotSize: Double, actionType: ActionType) {
        _uiState.update { current ->
            val updatedSymbols = current.symbols.map {
                if (it.symbol == symbolCode) it.copy(lotSize = lotSize, actionType = actionType, isAllowed = true) else it
            }
            current.copy(symbols = updatedSymbols)
        }
    }

    fun executeManualBuy(
        symbol: String = "XAUUSD",
        volume: Double = 0.05,
        sl: Double? = null,
        tp: Double? = null
    ) {
        val currentPrice = if (symbol == "XAUUSD" || symbol == "GOLDm" || symbol == "XAUUSDm") {
            _uiState.value.goldLivePrice
        } else {
            _uiState.value.symbols.find { it.symbol == symbol }?.currentPrice ?: 1.2845
        }
        val calculatedSl = sl ?: (currentPrice - (if (currentPrice > 100) 6.0 else 0.0030))
        val calculatedTp = tp ?: (currentPrice + (if (currentPrice > 100) 12.0 else 0.0060))
        val roundedPrice = Math.round(currentPrice * 100.0) / 100.0
        val roundedSl = Math.round(calculatedSl * 100.0) / 100.0
        val roundedTp = Math.round(calculatedTp * 100.0) / 100.0

        val newTrade = ActiveTrade(
            ticketId = 8492000L + Random.nextLong(100, 999),
            symbol = symbol,
            type = SignalType.BUY,
            lotSize = volume,
            openPrice = roundedPrice,
            currentPrice = roundedPrice,
            stopLoss = roundedSl,
            takeProfit = roundedTp,
            profitUsd = 0.0,
            trailingStopActive = true,
            openTime = "Just now"
        )

        _uiState.update { current ->
            current.copy(
                openTrades = listOf(newTrade) + current.openTrades,
                recentTelemetryLog = "MT5 Command Sent: BUY $volume lots $symbol @ $roundedPrice"
            )
        }
        voiceEngine.speak("Buy order executed for $symbol at $roundedPrice.")
    }

    fun executeManualSell(
        symbol: String = "XAUUSD",
        volume: Double = 0.05,
        sl: Double? = null,
        tp: Double? = null
    ) {
        val currentPrice = if (symbol == "XAUUSD" || symbol == "GOLDm" || symbol == "XAUUSDm") {
            _uiState.value.goldLivePrice
        } else {
            _uiState.value.symbols.find { it.symbol == symbol }?.currentPrice ?: 1.2845
        }
        val calculatedSl = sl ?: (currentPrice + (if (currentPrice > 100) 6.0 else 0.0030))
        val calculatedTp = tp ?: (currentPrice - (if (currentPrice > 100) 12.0 else 0.0060))
        val roundedPrice = Math.round(currentPrice * 100.0) / 100.0
        val roundedSl = Math.round(calculatedSl * 100.0) / 100.0
        val roundedTp = Math.round(calculatedTp * 100.0) / 100.0

        val newTrade = ActiveTrade(
            ticketId = 8492000L + Random.nextLong(100, 999),
            symbol = symbol,
            type = SignalType.SELL,
            lotSize = volume,
            openPrice = roundedPrice,
            currentPrice = roundedPrice,
            stopLoss = roundedSl,
            takeProfit = roundedTp,
            profitUsd = 0.0,
            trailingStopActive = true,
            openTime = "Just now"
        )

        _uiState.update { current ->
            current.copy(
                openTrades = listOf(newTrade) + current.openTrades,
                recentTelemetryLog = "MT5 Command Sent: SELL $volume lots $symbol @ $roundedPrice"
            )
        }
        voiceEngine.speak("Sell order executed for $symbol at $roundedPrice.")
    }

    fun executeAutoSignalTrade() {
        val signal = _uiState.value.signals.firstOrNull() ?: return
        val newTrade = ActiveTrade(
            ticketId = 8492000L + Random.nextLong(100, 999),
            symbol = signal.symbol,
            type = signal.type,
            lotSize = 0.05,
            openPrice = _uiState.value.goldLivePrice,
            currentPrice = _uiState.value.goldLivePrice,
            stopLoss = signal.stopLoss,
            takeProfit = signal.takeProfit1,
            profitUsd = 0.0,
            trailingStopActive = true,
            openTime = "Now"
        )
        _uiState.update { current ->
            current.copy(openTrades = listOf(newTrade) + current.openTrades)
        }
    }

    fun closeTrade(ticketId: Long) {
        _uiState.update { current ->
            current.copy(openTrades = current.openTrades.filter { it.ticketId != ticketId })
        }
    }

    fun closeAllTrades() {
        _uiState.update { current ->
            current.copy(openTrades = emptyList())
        }
        voiceEngine.speak("All active trades closed on MetaTrader.")
    }

    fun addNewRobot(name: String, author: String, version: String, description: String) {
        val id = "robot_${System.currentTimeMillis()}"
        val newRobot = RobotProfile(
            id = id,
            name = name.ifBlank { "Custom EA Robot" },
            author = author.ifBlank { "Trader" },
            version = version.ifBlank { "v1.0" },
            subtitle = "Custom EA",
            description = description.ifBlank { "Custom automated trading strategy" },
            avatarDrawableRes = R.drawable.robot_cyber_avatar_1786825877723,
            defaultMode = ExecutionMode.NORMAL,
            winRatePercent = 82.5,
            totalTrades = 120,
            profitTotalUsd = 1250.0,
            profitFactor = 2.1
        )
        _uiState.update { current ->
            current.copy(
                robots = current.robots + newRobot,
                activeRobotId = id,
                isConnectRobotDialogOpen = false
            )
        }
        voiceEngine.speak("New robot $name connected.")
    }

    fun updateAccountCredentials(accountNumber: String, broker: String, server: String) {
        _uiState.update { current ->
            current.copy(
                account = current.account.copy(
                    accountNumber = accountNumber,
                    broker = broker,
                    server = server
                ),
                diagnostics = current.diagnostics.copy(
                    accountLabel = "$broker #$accountNumber"
                ),
                isAccountConfigDialogOpen = false
            )
        }
        voiceEngine.speak("MetaTrader 5 connection credentials updated.")
    }

    fun toggleVoiceEnabled() {
        val currentVal = _uiState.value.isVoiceEnabled
        val newVal = !currentVal
        voiceEngine.setVoiceEnabled(newVal)
        _uiState.update { it.copy(isVoiceEnabled = newVal) }
        if (newVal) {
            voiceEngine.speak("Voice telemetry enabled.")
        }
    }

    fun triggerRobotSpeech() {
        val activeRobot = _uiState.value.activeRobot
        val name = activeRobot?.name ?: "Gold Beast EA"
        val status = if (_uiState.value.isAutoTradingActive) "online and executing trades" else "standing by on MetaTrader 5"
        voiceEngine.speak("$name is $status. Gold live price is ${_uiState.value.goldLivePrice} US dollars.")
    }

    fun setInfoDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isInfoDialogOpen = visible) }
    }

    fun setPairsDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isPairsDialogOpen = visible) }
    }

    fun setConnectRobotDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isConnectRobotDialogOpen = visible) }
    }

    fun setAccountConfigDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isAccountConfigDialogOpen = visible) }
    }

    fun setDrawerVisible(visible: Boolean) {
        _uiState.update { it.copy(isDrawerOpen = visible) }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.cleanup()
    }
}
