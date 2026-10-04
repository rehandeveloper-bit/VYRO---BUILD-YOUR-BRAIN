package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.AppNotificationEntity
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose

@Composable
fun NotificationsScreen(
    notifications: List<AppNotificationEntity>,
    onTriggerNotificationTest: (type: String) -> Unit,
    onMarkAsRead: (id: String) -> Unit,
    onMarkAllRead: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var dailyReminderEnabled by remember { mutableStateOf(true) }
    var friendDuelAlertsEnabled by remember { mutableStateOf(true) }
    var streakSafeguardEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("notif_back_button")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Notifications",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (notifications.any { !it.isRead }) {
                    OutlinedButton(
                        onClick = onMarkAllRead,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("mark_all_read_button")
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark all read", fontSize = 11.sp)
                    }
                }
            }
        }

        // Real System Notification Triggers (Interactive demo for the user!)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Test Custom Android Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap to test real Android push notification channels with custom vibration & priority:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onTriggerNotificationTest("DAILY") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("test_daily_notif_button")
                        ) {
                            Text("Daily Drop", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onTriggerNotificationTest("DUEL") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("test_duel_notif_button")
                        ) {
                            Text("Friend Duel", fontSize = 11.sp, color = Color.Black)
                        }

                        Button(
                            onClick = { onTriggerNotificationTest("STREAK") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("test_streak_notif_button")
                        ) {
                            Text("Streak Alert", fontSize = 11.sp, color = Color.Black)
                        }
                    }
                }
            }
        }

        // Notification Preferences
        item {
            Text(
                text = "⚙️ Scheduled Engagement Alerts",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Daily Challenge Drop", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "New 30-sec doodle or song challenge at 9:00 AM", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = dailyReminderEnabled,
                            onCheckedChange = { dailyReminderEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonPurple, checkedTrackColor = NeonPurple.copy(alpha = 0.5f))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Friend Duel Alerts", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Notify immediately when friends challenge you", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = friendDuelAlertsEnabled,
                            onCheckedChange = { friendDuelAlertsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Streak Safeguard Alert", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Warning at 8:00 PM if daily challenge is incomplete", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = streakSafeguardEnabled,
                            onCheckedChange = { streakSafeguardEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonAmber, checkedTrackColor = NeonAmber.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }

        // Notification History
        item {
            Text(
                text = "📬 Activity Center (${notifications.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (notifications.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No notifications yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(notifications) { notif ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMarkAsRead(notif.id) }
                        .testTag("notif_item_${notif.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (notif.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (!notif.isRead) androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    when (notif.type) {
                                        "DAILY_CHALLENGE" -> NeonPurple.copy(alpha = 0.2f)
                                        "FRIEND_DUEL" -> NeonCyan.copy(alpha = 0.2f)
                                        "STREAK_ALERT" -> NeonAmber.copy(alpha = 0.2f)
                                        else -> NeonEmerald.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (notif.type) {
                                    "DAILY_CHALLENGE" -> Icons.Default.NotificationsActive
                                    "FRIEND_DUEL" -> Icons.Default.SportsKabaddi
                                    "STREAK_ALERT" -> Icons.Default.LocalFireDepartment
                                    else -> Icons.Default.Celebration
                                },
                                contentDescription = null,
                                tint = when (notif.type) {
                                    "DAILY_CHALLENGE" -> NeonPurple
                                    "FRIEND_DUEL" -> NeonCyan
                                    "STREAK_ALERT" -> NeonAmber
                                    else -> NeonEmerald
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = notif.title,
                                fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notif.message,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!notif.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
