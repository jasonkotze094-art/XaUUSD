package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Gold Beast EA", appName)
  }

  @Test
  fun `test trading viewmodel manual buy and sell`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.viewmodel.TradingViewModel(application)

    val initialCount = viewModel.uiState.value.openTrades.size
    viewModel.executeManualBuy(symbol = "XAUUSD", volume = 0.10)

    val afterBuyCount = viewModel.uiState.value.openTrades.size
    assertEquals(initialCount + 1, afterBuyCount)
    assertEquals("XAUUSD", viewModel.uiState.value.openTrades.first().symbol)
    assertEquals(com.example.model.SignalType.BUY, viewModel.uiState.value.openTrades.first().type)
    assertEquals(0.10, viewModel.uiState.value.openTrades.first().lotSize, 0.001)

    viewModel.executeManualSell(symbol = "GBPUSD", volume = 0.05)
    val afterSellCount = viewModel.uiState.value.openTrades.size
    assertEquals(afterBuyCount + 1, afterSellCount)
    assertEquals(com.example.model.SignalType.SELL, viewModel.uiState.value.openTrades.first().type)

    val initialAuto = viewModel.uiState.value.isAutoTradingActive
    viewModel.toggleAutoTrading()
    assertEquals(!initialAuto, viewModel.uiState.value.isAutoTradingActive)
  }
}
