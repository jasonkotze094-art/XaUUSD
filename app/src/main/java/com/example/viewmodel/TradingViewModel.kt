package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.model.AccountPerformancePoint
import com.example.model.ActionType
import com.example.model.ActiveTrade
import com.example.model.ClosedTrade
import com.example.model.DiagnosticStatus
import com.example.model.ExecutionMode
import com.example.model.MetaTraderAccount
import com.example.model.PerformanceTimeRange
import com.example.model.RobotProfile
import com.example.model.SignalType
import com.example.model.TradeSignal
import com.example.model.TradingSymbol
import com.example.notification.SignalNotificationManager
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
    val tradeHistory: List<ClosedTrade> = emptyList(),
    val performancePoints: List<AccountPerformancePoint> = emptyList(),
    val selectedPerformanceRange: PerformanceTimeRange = PerformanceTimeRange.MONTH_1,
    val isPerformanceApiLoading: Boolean = false,
    val account: MetaTraderAccount = MetaTraderAccount(),
    val diagnostics: DiagnosticStatus = DiagnosticStatus(),
    val isVoiceEnabled: Boolean = true,
    val isPushNotificationEnabled: Boolean = true,
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

    val totalRealizedPnl: Double
        get() = tradeHistory.sumOf { it.profitLossUsd }

    val winRatePercent: Double
        get() {
            if (tradeHistory.isEmpty()) return 0.0
            val wins = tradeHistory.count { it.profitLossUsd > 0 }
            return (wins.toDouble() / tradeHistory.size) * 100.0
        }

    val startingBalance: Double
        get() = performancePoints.firstOrNull()?.balance ?: 10000.00

    val peakEquity: Double
        get() = performancePoints.maxOfOrNull { it.equity } ?: account.equity

    val maxDrawdownPercent: Double
        get() {
            if (performancePoints.isEmpty()) return 2.4
            var peak = performancePoints.first().equity
            var maxDd = 0.0
            for (p in performancePoints) {
                if (p.equity > peak) {
                    peak = p.equity
                } else {
                    val dd = (peak - p.equity) / peak * 100.0
                    if (dd > maxDd) maxDd = dd
                }
            }
            return Math.round(maxDd * 10.0) / 10.0
        }

    val netGrowthPercent: Double
        get() {
            val start = startingBalance
            if (start <= 0.0) return 0.0
            val growth = ((account.equity - start) / start) * 100.0
            return Math.round(growth * 100.0) / 100.0
        }
}

class TradingViewModel(application: Application) : AndroidViewModel(application) {

    private val voiceEngine = VoiceEngine(application)
    private val signalNotificationManager = SignalNotificationManager(application)

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

    private val defaultClosedTrades = listOf(
        ClosedTrade(
            ticketId = 8491950,
            symbol = "XAUUSD",
            type = SignalType.BUY,
            lotSize = 0.10,
            openPrice = 2628.50,
            closePrice = 2641.80,
            profitLossUsd = 133.00,
            pips = 133.0,
            openTime = "Today 10:15:22",
            closeTime = "Today 12:44:09",
            exitReason = "Take Profit Hit",
            commissionUsd = -0.70,
            swapUsd = 0.00
        ),
        ClosedTrade(
            ticketId = 8491892,
            symbol = "XAUUSD",
            type = SignalType.BUY,
            lotSize = 0.05,
            openPrice = 2632.10,
            closePrice = 2640.60,
            profitLossUsd = 42.50,
            pips = 85.0,
            openTime = "Today 08:30:11",
            closeTime = "Today 09:55:40",
            exitReason = "Dynamic Trailing Stop",
            commissionUsd = -0.35,
            swapUsd = 0.00
        ),
        ClosedTrade(
            ticketId = 8491744,
            symbol = "GBPUSD",
            type = SignalType.BUY,
            lotSize = 0.05,
            openPrice = 1.2810,
            closePrice = 1.2858,
            profitLossUsd = 24.00,
            pips = 48.0,
            openTime = "Today 07:12:05",
            closeTime = "Today 08:20:18",
            exitReason = "Take Profit Hit",
            commissionUsd = -0.35,
            swapUsd = 0.00
        ),
        ClosedTrade(
            ticketId = 8491620,
            symbol = "US30",
            type = SignalType.BUY,
            lotSize = 0.02,
            openPrice = 43820.0,
            closePrice = 43960.0,
            profitLossUsd = 28.00,
            pips = 140.0,
            openTime = "Yesterday 20:45:00",
            closeTime = "Yesterday 22:15:30",
            exitReason = "Take Profit Hit",
            commissionUsd = -0.40,
            swapUsd = -0.15
        ),
        ClosedTrade(
            ticketId = 8491510,
            symbol = "EURUSD",
            type = SignalType.SELL,
            lotSize = 0.05,
            openPrice = 1.0855,
            closePrice = 1.0868,
            profitLossUsd = -6.50,
            pips = -13.0,
            openTime = "Yesterday 16:20:14",
            closeTime = "Yesterday 17:05:52",
            exitReason = "Stop Loss Hit",
            commissionUsd = -0.35,
            swapUsd = 0.00
        ),
        ClosedTrade(
            ticketId = 8491402,
            symbol = "XAUUSD",
            type = SignalType.BUY,
            lotSize = 0.08,
            openPrice = 2618.00,
            closePrice = 2634.50,
            profitLossUsd = 132.00,
            pips = 165.0,
            openTime = "Yesterday 11:00:30",
            closeTime = "Yesterday 14:10:45",
            exitReason = "Take Profit Hit",
            commissionUsd = -0.56,
            swapUsd = 0.00
        )
    )

    private fun generatePerformancePoints(range: PerformanceTimeRange): List<AccountPerformancePoint> {
        return when (range) {
            PerformanceTimeRange.DAY_1 -> listOf(
                AccountPerformancePoint("d1_1", "00:00", "00:00", 10850.00, 10850.00, 0.00, 0),
                AccountPerformancePoint("d1_2", "02:00", "02:00", 10850.00, 10882.50, 32.50, 1),
                AccountPerformancePoint("d1_3", "04:00", "04:00", 10882.50, 10910.00, 27.50, 2),
                AccountPerformancePoint("d1_4", "06:00", "06:00", 10910.00, 10895.00, -15.00, 3),
                AccountPerformancePoint("d1_5", "08:00", "08:00", 10910.00, 10980.20, 70.20, 5),
                AccountPerformancePoint("d1_6", "10:00", "10:00", 10980.20, 11065.40, 85.20, 7),
                AccountPerformancePoint("d1_7", "12:00", "12:00", 11065.40, 11130.00, 64.60, 9),
                AccountPerformancePoint("d1_8", "14:00", "14:00", 11065.40, 11164.80, 99.40, 12)
            )
            PerformanceTimeRange.WEEK_1 -> listOf(
                AccountPerformancePoint("w1_1", "Mon", "Mon", 10350.00, 10380.00, 30.00, 4),
                AccountPerformancePoint("w1_2", "Tue", "Tue", 10480.00, 10520.00, 40.00, 11),
                AccountPerformancePoint("w1_3", "Wed", "Wed", 10620.00, 10675.00, 55.00, 18),
                AccountPerformancePoint("w1_4", "Thu", "Thu", 10790.00, 10840.00, 50.00, 26),
                AccountPerformancePoint("w1_5", "Fri", "Fri", 10950.00, 10985.00, 35.00, 35),
                AccountPerformancePoint("w1_6", "Sat", "Sat", 10985.00, 11020.00, 35.00, 35),
                AccountPerformancePoint("w1_7", "Sun", "Today", 11065.40, 11164.80, 99.40, 42)
            )
            PerformanceTimeRange.MONTH_1 -> listOf(
                AccountPerformancePoint("m1_1", "Jul 15", "W1", 10000.00, 10000.00, 0.00, 0),
                AccountPerformancePoint("m1_2", "Jul 20", "W2", 10180.00, 10240.00, 60.00, 14),
                AccountPerformancePoint("m1_3", "Jul 25", "W3", 10320.00, 10310.00, -10.00, 28),
                AccountPerformancePoint("m1_4", "Jul 30", "W4", 10490.00, 10560.00, 70.00, 45),
                AccountPerformancePoint("m1_5", "Aug 05", "W5", 10710.00, 10805.00, 95.00, 68),
                AccountPerformancePoint("m1_6", "Aug 10", "W6", 10890.00, 10960.00, 70.00, 92),
                AccountPerformancePoint("m1_7", "Aug 15", "Now", 11065.40, 11164.80, 99.40, 118)
            )
            PerformanceTimeRange.MONTH_3 -> listOf(
                AccountPerformancePoint("m3_1", "May 15", "May", 8500.00, 8500.00, 0.00, 0),
                AccountPerformancePoint("m3_2", "Jun 01", "Jun 1", 9050.00, 9120.00, 70.00, 48),
                AccountPerformancePoint("m3_3", "Jun 15", "Jun 15", 9480.00, 9420.00, -60.00, 95),
                AccountPerformancePoint("m3_4", "Jul 01", "Jul 1", 9950.00, 10050.00, 100.00, 150),
                AccountPerformancePoint("m3_5", "Jul 15", "Jul 15", 10300.00, 10380.00, 80.00, 210),
                AccountPerformancePoint("m3_6", "Aug 01", "Aug 1", 10750.00, 10890.00, 140.00, 275),
                AccountPerformancePoint("m3_7", "Aug 15", "Now", 11065.40, 11164.80, 99.40, 340)
            )
            PerformanceTimeRange.YEAR_1 -> listOf(
                AccountPerformancePoint("y1_1", "Aug '25", "Aug", 5000.00, 5000.00, 0.00, 0),
                AccountPerformancePoint("y1_2", "Oct '25", "Oct", 6100.00, 6220.00, 120.00, 180),
                AccountPerformancePoint("y1_3", "Dec '25", "Dec", 7350.00, 7480.00, 130.00, 390),
                AccountPerformancePoint("y1_4", "Feb '26", "Feb", 8200.00, 8150.00, -50.00, 560),
                AccountPerformancePoint("y1_5", "Apr '26", "Apr", 9400.00, 9580.00, 180.00, 780),
                AccountPerformancePoint("y1_6", "Jun '26", "Jun", 10250.00, 10390.00, 140.00, 990),
                AccountPerformancePoint("y1_7", "Aug '26", "Now", 11065.40, 11164.80, 99.40, 1250)
            )
            PerformanceTimeRange.ALL -> listOf(
                AccountPerformancePoint("all_1", "2024", "2024", 3000.00, 3000.00, 0.00, 0),
                AccountPerformancePoint("all_2", "Q2 24", "Q2 '24", 4200.00, 4350.00, 150.00, 320),
                AccountPerformancePoint("all_3", "Q4 24", "Q4 '24", 5800.00, 5920.00, 120.00, 710),
                AccountPerformancePoint("all_4", "Q2 25", "Q2 '25", 7900.00, 8050.00, 150.00, 1150),
                AccountPerformancePoint("all_5", "Q4 25", "Q4 '25", 9600.00, 9780.00, 180.00, 1680),
                AccountPerformancePoint("all_6", "Q2 26", "Q2 '26", 10650.00, 10800.00, 150.00, 2190),
                AccountPerformancePoint("all_7", "NOW", "Now", 11065.40, 11164.80, 99.40, 2640)
            )
        }
    }

    private val _uiState = MutableStateFlow(
        TradingUiState(
            robots = defaultRobots,
            symbols = defaultSymbols,
            signals = defaultSignals,
            openTrades = defaultTrades,
            tradeHistory = defaultClosedTrades,
            performancePoints = generatePerformancePoints(PerformanceTimeRange.MONTH_1)
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
            val targetTrade = current.openTrades.find { it.ticketId == ticketId }
            val updatedOpen = current.openTrades.filter { it.ticketId != ticketId }
            val updatedHistory = if (targetTrade != null) {
                val pipsDiff = (targetTrade.currentPrice - targetTrade.openPrice) * (if (targetTrade.type == SignalType.BUY) 1.0 else -1.0)
                val roundedPips = Math.round(pipsDiff * 10.0) / 10.0
                val closedItem = ClosedTrade(
                    ticketId = targetTrade.ticketId,
                    symbol = targetTrade.symbol,
                    type = targetTrade.type,
                    lotSize = targetTrade.lotSize,
                    openPrice = targetTrade.openPrice,
                    closePrice = targetTrade.currentPrice,
                    profitLossUsd = targetTrade.profitUsd,
                    pips = roundedPips,
                    openTime = targetTrade.openTime,
                    closeTime = "Just now",
                    exitReason = if (targetTrade.profitUsd >= 0) "Manual Profit Lock" else "Manual Cut",
                    commissionUsd = -0.35,
                    swapUsd = 0.00
                )
                listOf(closedItem) + current.tradeHistory
            } else {
                current.tradeHistory
            }
            current.copy(
                openTrades = updatedOpen,
                tradeHistory = updatedHistory,
                recentTelemetryLog = "Closed trade #$ticketId on MT5 with P/L: $${targetTrade?.profitUsd ?: 0.0}"
            )
        }
    }

    fun closeAllTrades() {
        _uiState.update { current ->
            val closedItems = current.openTrades.map { targetTrade ->
                val pipsDiff = (targetTrade.currentPrice - targetTrade.openPrice) * (if (targetTrade.type == SignalType.BUY) 1.0 else -1.0)
                val roundedPips = Math.round(pipsDiff * 10.0) / 10.0
                ClosedTrade(
                    ticketId = targetTrade.ticketId,
                    symbol = targetTrade.symbol,
                    type = targetTrade.type,
                    lotSize = targetTrade.lotSize,
                    openPrice = targetTrade.openPrice,
                    closePrice = targetTrade.currentPrice,
                    profitLossUsd = targetTrade.profitUsd,
                    pips = roundedPips,
                    openTime = targetTrade.openTime,
                    closeTime = "Just now",
                    exitReason = "Bulk Close (Emergency)",
                    commissionUsd = -0.35,
                    swapUsd = 0.00
                )
            }
            current.copy(
                openTrades = emptyList(),
                tradeHistory = closedItems + current.tradeHistory,
                recentTelemetryLog = "Closed all ${closedItems.size} positions via MT5 command"
            )
        }
        voiceEngine.speak("All active trades closed on MetaTrader.")
    }

    fun refreshTradeHistoryFromApi() {
        viewModelScope.launch {
            _uiState.update { it.copy(recentTelemetryLog = "Polling /api/mt5/history for closed orders...") }
            delay(500)
            _uiState.update { it.copy(recentTelemetryLog = "Trade history synchronized with MT5 Bridge.") }
            voiceEngine.speak("Trade history synchronized.")
        }
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

    fun togglePushNotifications() {
        val newVal = !_uiState.value.isPushNotificationEnabled
        _uiState.update { it.copy(isPushNotificationEnabled = newVal) }
        val msg = if (newVal) "Signal push alerts enabled." else "Signal push alerts disabled."
        voiceEngine.speak(msg)
    }

    fun dispatchSignalAlertNotification(signal: TradeSignal) {
        if (_uiState.value.isPushNotificationEnabled) {
            signalNotificationManager.notifyNewSignal(signal, _uiState.value.goldLivePrice)
            _uiState.update {
                it.copy(
                    recentTelemetryLog = "⚡ Signal alert notification dispatched: ${signal.type.name} ${signal.symbol}"
                )
            }
        }
    }

    fun testTriggerSignalPushNotification() {
        val activeSignal = _uiState.value.signals.firstOrNull() ?: TradeSignal(
            id = "sig_live_test",
            symbol = "XAUUSD",
            type = SignalType.BUY,
            entryPrice = _uiState.value.goldLivePrice,
            stopLoss = _uiState.value.goldLivePrice - 8.0,
            takeProfit1 = _uiState.value.goldLivePrice + 6.0,
            takeProfit2 = _uiState.value.goldLivePrice + 12.0,
            trailingStopPips = 30.0,
            confidencePercent = 96.0,
            timeAgo = "Just now",
            reason = "Institutional liquidity sweep + Golden pocket reversal on M15"
        )
        dispatchSignalAlertNotification(activeSignal)
        voiceEngine.speak("Incoming ${activeSignal.type.name} signal alert on ${activeSignal.symbol}.")
    }

    fun selectPerformanceTimeRange(range: PerformanceTimeRange) {
        _uiState.update {
            it.copy(
                selectedPerformanceRange = range,
                performancePoints = generatePerformancePoints(range),
                recentTelemetryLog = "📊 Performance timeline updated: ${range.label} range selected"
            )
        }
    }

    fun fetchPerformanceHistoryFromApi() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPerformanceApiLoading = true,
                    recentTelemetryLog = "🔄 Syncing account balance & equity performance from MT5 Broker API..."
                )
            }
            delay(800) // Simulated ultra-fast REST API response
            val currentRange = _uiState.value.selectedPerformanceRange
            val refreshedPoints = generatePerformancePoints(currentRange)
            _uiState.update {
                it.copy(
                    isPerformanceApiLoading = false,
                    performancePoints = refreshedPoints,
                    recentTelemetryLog = "✓ MT5 Equity history synced: Peak $${String.format("%.2f", it.peakEquity)}, Growth +${String.format("%.1f", it.netGrowthPercent)}%"
                )
            }
            voiceEngine.speak("Account balance and equity trends refreshed successfully.")
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
