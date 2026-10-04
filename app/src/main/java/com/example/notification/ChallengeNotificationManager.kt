package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

class ChallengeNotificationManager(private val context: Context) {
    companion object {
        const val CHANNEL_DAILY = "daily_challenges_channel"
        const val CHANNEL_DUELS = "friend_duels_channel"
        const val CHANNEL_STREAKS = "streak_alerts_channel"

        const val NOTIF_ID_DAILY = 1001
        const val NOTIF_ID_DUEL = 1002
        const val NOTIF_ID_STREAK = 1003
        const val NOTIF_ID_CELEBRATION = 1004
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY,
                "Daily AI Challenges",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily 30-sec drawing, song guesses, and AI duel prompts"
                enableVibration(true)
            }

            val duelsChannel = NotificationChannel(
                CHANNEL_DUELS,
                "Friend Duels & Invites",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming challenges and duel score updates from friends"
                enableVibration(true)
            }

            val streaksChannel = NotificationChannel(
                CHANNEL_STREAKS,
                "Streak & Leaderboard Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to maintain your daily streak and ranking"
            }

            notificationManager.createNotificationChannel(dailyChannel)
            notificationManager.createNotificationChannel(duelsChannel)
            notificationManager.createNotificationChannel(streaksChannel)
        }
    }

    fun canPostNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showDailyChallengeNotification(title: String, description: String) {
        if (!canPostNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("🔥 Daily AI Challenge Ready!")
            .setContentText("$title — $description")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$title: $description. Tap to beat the clock!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIF_ID_DAILY, notification)
    }

    fun showFriendDuelNotification(friendName: String, challengeTitle: String) {
        if (!canPostNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DUELS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚔️ $friendName challenged you!")
            .setContentText("Duel on: '$challengeTitle'. Can you beat their score?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIF_ID_DUEL, notification)
    }

    fun showStreakAlertNotification(currentStreak: Int) {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_STREAKS)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("⏳ Keep Your $currentStreak-Day Streak Alive!")
            .setContentText("You haven't played today's challenge yet. Don't let your fire go out!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIF_ID_STREAK, notification)
    }
}
