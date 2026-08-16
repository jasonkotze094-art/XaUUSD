package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.R

enum class ExecutionMode(val title: String, val description: String) {
    NORMAL("NORMAL", "Standard algorithmic execution with risk guard"),
    DYNAMIC("DYNAMIC", "High-frequency momentum scaling & multi-tick trailing")
}

enum class ActionType(val label: String) {
    BOTH("BOTH"),
    BUY_ONLY("BUY ONLY"),
    SELL_ONLY("SELL ONLY")
}

enum class SignalType {
    BUY,
    SELL,
    NEUTRAL
}

data class RobotProfile(
    val id: String,
    val name: String,
    val author: String,
    val version: String,
    val subtitle: String,
    val description: String,
    val avatarDrawableRes: Int = R.drawable.robot_cyber_avatar_1786825877723,
    val defaultMode: ExecutionMode = ExecutionMode.NORMAL,
    val winRatePercent: Double = 89.4,
    val totalTrades: Int = 1248,
    val profitTotalUsd: Double = 4280.50,
    val profitFactor: Double = 2.85,
    val statusText: String = "Algorithmic Engine Ready"
)

data class TradingSymbol(
    val symbol: String,
    val name: String,
    val lotSize: Double,
    val actionType: ActionType,
    val isAllowed: Boolean,
    val currentPrice: Double,
    val changePercent24h: Double,
    val spreadPips: Double,
    val digits: Int = 2,
    val pipValueMultiplier: Double = 100.0
)

data class TradeSignal(
    val id: String,
    val symbol: String,
    val type: SignalType,
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val trailingStopPips: Double,
    val confidencePercent: Double,
    val timeAgo: String,
    val reason: String
)

data class ActiveTrade(
    val ticketId: Long,
    val symbol: String,
    val type: SignalType,
    val lotSize: Double,
    val openPrice: Double,
    val currentPrice: Double,
    val stopLoss: Double,
    val takeProfit: Double,
    val profitUsd: Double,
    val trailingStopActive: Boolean,
    val openTime: String
)

data class ClosedTrade(
    val ticketId: Long,
    val symbol: String,
    val type: SignalType,
    val lotSize: Double,
    val openPrice: Double,
    val closePrice: Double,
    val profitLossUsd: Double,
    val pips: Double,
    val openTime: String,
    val closeTime: String,
    val exitReason: String = "Take Profit Hit",
    val magicNumber: Long = 20260815L,
    val commissionUsd: Double = -0.35,
    val swapUsd: Double = 0.00
)

data class AccountPerformancePoint(
    val id: String,
    val timestamp: String,
    val timeLabel: String,
    val balance: Double,
    val equity: Double,
    val floatingPnl: Double = equity - balance,
    val tradeCount: Int = 0
)

enum class PerformanceTimeRange(val label: String, val days: Int) {
    DAY_1("1D", 1),
    WEEK_1("1W", 7),
    MONTH_1("1M", 30),
    MONTH_3("3M", 90),
    YEAR_1("1Y", 365),
    ALL("ALL", 999)
}

data class MetaTraderAccount(
    val accountNumber: String = "8849201",
    val broker: String = "IC Markets Global MT5",
    val server: String = "ICMarkets-Live05",
    val currency: String = "USD",
    val balance: Double = 10540.00,
    val equity: Double = 11164.80,
    val margin: Double = 148.50,
    val freeMargin: Double = 11016.30,
    val leverage: String = "1:500",
    val isConnected: Boolean = true,
    val pingMs: Int = 12
)

data class DiagnosticStatus(
    val connectedAccount: Boolean = true,
    val accountLabel: String = "IC Markets #8849201",
    val internetLatency: Boolean = true,
    val latencyMs: Int = 12,
    val pairsSynced: Boolean = true,
    val activePairsSummary: String = "XAUUSD, GBPUSD",
    val botActive: Boolean = false,
    val botStateLabel: String = "BOT STANDBY"
)
