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
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChallengeEntity
import com.example.data.local.DuelEntity
import com.example.data.local.FriendEntity
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose

@Composable
fun FriendsDuelsScreen(
    friends: List<FriendEntity>,
    duels: List<DuelEntity>,
    challenges: List<ChallengeEntity>,
    onSendDuel: (FriendEntity, ChallengeEntity, Int) -> Unit,
    onOpenChat: (friendId: String, friendName: String) -> Unit,
    onShareAppInvite: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedFriendForDuel by remember { mutableStateOf<FriendEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("friends_back_button")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Friends & Duels",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onShareAppInvite,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("share_app_invite_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Invite", fontSize = 12.sp)
                }
            }
        }

        // Active Duels Section
        item {
            Text(
                text = "⚔️ Active Duels (${duels.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (duels.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No active duels right now. Pick a friend below to challenge!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(duels) { duel ->
                DuelDetailCard(duel = duel)
            }
        }

        // Friends List Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥 Squad Friends (${friends.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        items(friends) { friend ->
            FriendListItem(
                friend = friend,
                onChallengeClick = { selectedFriendForDuel = friend },
                onChatClick = { onOpenChat(friend.id, friend.name) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Challenge Dialog to select challenge to send
    selectedFriendForDuel?.let { friend ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { selectedFriendForDuel = null },
            title = {
                Text("Challenge ${friend.name}!")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose a challenge to duel on:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    challenges.take(4).forEach { chal ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    val scoreToBeat = chal.userScore ?: 85
                                    onSendDuel(friend, chal, scoreToBeat)
                                    selectedFriendForDuel = null
                                }
                                .testTag("duel_select_challenge_${chal.id}"),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = chal.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${chal.category} • Target Score: ${chal.userScore ?: 85} pts",
                                    fontSize = 12.sp,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { selectedFriendForDuel = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DuelDetailCard(duel: DuelEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = duel.challengeTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (duel.status) {
                        "WON" -> NeonEmerald.copy(alpha = 0.2f)
                        "LOST" -> NeonRose.copy(alpha = 0.2f)
                        else -> NeonAmber.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = when (duel.status) {
                            "WON" -> "VICTORY 🏆"
                            "LOST" -> "DEFEAT"
                            else -> "PENDING ⏳"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (duel.status) {
                            "WON" -> NeonEmerald
                            "LOST" -> NeonRose
                            else -> NeonAmber
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Your Score", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${duel.userScore} pts", fontSize = 16.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                }

                Text(
                    text = "VS",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = NeonPurple,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "${duel.friendName}'s Score", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (duel.friendScore != null) "${duel.friendScore} pts" else "Waiting...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = if (duel.friendScore != null) NeonAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendListItem(
    friend: FriendEntity,
    onChallengeClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(friend.avatarColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = friend.avatarInitials,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = friend.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${friend.currentStreak}d Streak • ${friend.totalScore} pts",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onChatClick,
                    modifier = Modifier.testTag("chat_friend_${friend.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Chat",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Button(
                    onClick = onChallengeClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    modifier = Modifier.testTag("challenge_friend_${friend.id}")
                ) {
                    Icon(Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Duel", fontSize = 12.sp)
                }
            }
        }
    }
}
