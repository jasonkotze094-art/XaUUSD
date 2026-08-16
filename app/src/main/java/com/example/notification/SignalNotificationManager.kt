package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.SignalType
import com.example.model.TradeSignal

class SignalNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "gold_beast_signals_channel"
        const val CHANNEL_NAME = "Gold Beast Trade Signals"
        const val CHANNEL_DESC = "Real-time AI Trade Signals and Order Alerts from Gold Beast EA"
        const val NOTIFICATION_ID_BASE = 1001
        private const val TAG = "SignalNotificationMgr"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = Color.YELLOW
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 100, 250, 100, 400)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun notifyNewSignal(signal: TradeSignal, livePrice: Double) {
        if (!hasNotificationPermission()) {
            Log.w(TAG, "Notification permission not granted. Skipping notification.")
            return
        }

        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("SIGNAL_SYMBOL", signal.symbol)
                putExtra("SIGNAL_TYPE", signal.type.name)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                signal.symbol.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val directionEmoji = if (signal.type == SignalType.BUY) "🟢 BUY" else "🔴 SELL"
            val title = "⚡ GOLD BEAST ALERT: $directionEmoji ${signal.symbol} @ $livePrice"
            val shortBody = "Confidence: ${signal.confidencePercent}% | SL: ${signal.stopLoss} | TP: ${signal.takeProfit1}"

            val bigText = """
                ⚡ NEW TRADING SIGNAL DETECTED
                • Asset: ${signal.symbol}
                • Action: ${signal.type.name} @ $livePrice
                • AI Confidence: ${signal.confidencePercent}%
                • Target SL: ${signal.stopLoss}
                • Take Profit 1: ${signal.takeProfit1}
                • Take Profit 2: ${signal.takeProfit2}
                • Trailing Stop: ${signal.trailingStopPips} Pips
                • Strategy: ${signal.reason}
            """.trimIndent()

            val notificationColor = if (signal.type == SignalType.BUY) {
                0xFF00E676.toInt() // Green
            } else {
                0xFFFF1744.toInt() // Red
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(shortBody)
                .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                .setColor(notificationColor)
                .setColorized(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVibrate(longArrayOf(0, 250, 100, 250, 100, 400))
                .build()

            val notificationManager = NotificationManagerCompat.from(context)
            val notificationId = NOTIFICATION_ID_BASE + (signal.symbol.hashCode() % 1000)
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException sending notification", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to dispatch signal notification", e)
        }
    }
}
