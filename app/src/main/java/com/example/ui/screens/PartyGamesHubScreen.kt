package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose

data class PartyRoomItem(
    val id: String,
    val name: String,
    val gameTitle: String,
    val playersCount: String,
    val status: String,
    val hostName: String,
    val iconEmoji: String
)

@Composable
fun PartyGamesHubScreen(
    onPlayUndercoverSpy: () -> Unit,
    onPlayWordBomb: () -> Unit,
    onPlayGrabTheMic: () -> Unit,
    onPlayDrawing: () -> Unit,
    onPlaySongGuess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val activeRooms = listOf(
        PartyRoomItem("r1", "Priya's Spy Lounge", "Who is the Spy? 🕵️", "3/4 Players", "Waiting", "Priya Sharma", "🕵️"),
        PartyRoomItem("r2", "Midnight Bomb Pass 💣", "Word Bomb", "2/4 Players", "Starting", "Rohan Mehta", "💣"),
        PartyRoomItem("r3", "Bollywood & Pop Karaoke", "Grab the Mic 🎤", "4/4 Players", "In Progress", "Marcus Chen", "🎤"),
        PartyRoomItem("r4", "30-Sec Doodle Arena", "30-Sec Draw Duel", "1/2 Players", "Waiting", "Zara Khan", "🎨")
    )

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("party_hub_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "WePlay Party Arcade 🎮",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Multiplayer Social Party Games & Voice Lounges",
                        fontSize = 12.sp,
                        color = NeonPurple
                    )
                }
            }
        }

        // Hero Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF381468), Color(0xFF140F2E)))
                        )
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NeonRose.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonRose)
                        ) {
                            Text(
                                text = "🔥 WEPLAY PARTY GAMES ARE LIVE!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonRose,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Play Iconic Party Games With Friends",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        Text(
                            text = "Jump into Who is the Spy?, Pass the Ticking Bomb, or Grab the Mic in real-time party rooms!",
                            fontSize = 13.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }

        // Section Title: Party Games
        item {
            Text(
                text = "🕹️ Featured Party Games",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 1. Who is the Spy (Undercover)
        item {
            PartyGameCard(
                title = "Who is the Spy? (Undercover)",
                subtitle = "Social deduction secret word duel. Find the impostor before they deduce your word!",
                badgeText = "POPULAR 🕵️",
                badgeColor = NeonCyan,
                iconEmoji = "🕵️",
                accentColor = NeonCyan,
                onClick = onPlayUndercoverSpy,
                testTag = "play_undercover_spy_button"
            )
        }

        // 2. Word Bomb (Pass the Bomb)
        item {
            PartyGameCard(
                title = "Word Bomb (Pass the Bomb)",
                subtitle = "Ticking dynamite countdown! Type matching words fast and pass the bomb before it blows up.",
                badgeText = "HOT 💣",
                badgeColor = NeonRose,
                iconEmoji = "💣",
                accentColor = NeonAmber,
                onClick = onPlayWordBomb,
                testTag = "play_word_bomb_button"
            )
        }

        // 3. Grab the Mic (Karaoke Rush)
        item {
            PartyGameCard(
                title = "Grab the Mic (Lyric Rush)",
                subtitle = "Buzz in first when the music cuts! Complete the missing line and claim the karaoke crown.",
                badgeText = "MUSIC 🎤",
                badgeColor = NeonPurple,
                iconEmoji = "🎤",
                accentColor = NeonPurple,
                onClick = onPlayGrabTheMic,
                testTag = "play_grab_mic_button"
            )
        }

        // 4. 30-Sec Draw Duel
        item {
            PartyGameCard(
                title = "30-Sec Draw Duel",
                subtitle = "Speed sketch against the 30-second buzzer with real-time AI referee scoring.",
                badgeText = "CREATIVE 🎨",
                badgeColor = NeonEmerald,
                iconEmoji = "🎨",
                accentColor = NeonEmerald,
                onClick = onPlayDrawing,
                testTag = "play_draw_party_button"
            )
        }

        // Section: Active Party Rooms
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛋️ Active Party Lounges",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "4 Rooms Online",
                    fontSize = 12.sp,
                    color = NeonEmerald
                )
            }
        }

        items(activeRooms) { room ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (room.iconEmoji) {
                            "🕵️" -> onPlayUndercoverSpy()
                            "💣" -> onPlayWordBomb()
                            "🎤" -> onPlayGrabTheMic()
                            else -> onPlayDrawing()
                        }
                    },
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
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2A1C48)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = room.iconEmoji, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = room.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${room.gameTitle} • Host: ${room.hostName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NeonPurple.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
                    ) {
                        Text(
                            text = "JOIN (${room.playersCount})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun PartyGameCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    iconEmoji: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .border(1.5.dp, accentColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Play",
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
