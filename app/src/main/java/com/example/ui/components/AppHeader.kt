package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.data.local.UserEntity
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose

@Composable
fun AppHeader(
    user: UserEntity?,
    unreadNotifsCount: Int,
    onNotificationsClick: () -> Unit,
    onStreakClick: () -> Unit,
    onCreateChallengeClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // User Avatar & Brand
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                        .testTag("user_avatar_header_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (!user?.avatarUri.isNullOrBlank()) {
                        AsyncImage(
                            model = user?.avatarUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeonPurple, NeonCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.avatarInitials ?: "AV",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VY",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "RO",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = NeonPurple,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Streak, Gems & Notif Bell
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Streak Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NeonAmber.copy(alpha = 0.15f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onStreakClick() }
                        .testTag("streak_chip_header")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = NeonAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.currentStreak ?: 0}d",
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber,
                            fontSize = 13.sp
                        )
                    }
                }

                // Gems Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NeonCyan.copy(alpha = 0.15f),
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Gems",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.gems ?: 0}",
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 13.sp
                        )
                    }
                }

                // Create AI Challenge Quick Button
                IconButton(
                    onClick = onCreateChallengeClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonPurple.copy(alpha = 0.2f))
                        .testTag("create_challenge_header_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Create Challenge with AI",
                        tint = NeonPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Notifications Bell
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("notifications_bell_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifsCount > 0) {
                                Badge(containerColor = NeonRose) {
                                    Text(text = "$unreadNotifsCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
