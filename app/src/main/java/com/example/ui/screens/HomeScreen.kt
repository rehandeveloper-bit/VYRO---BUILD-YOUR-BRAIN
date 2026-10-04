package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChallengeEntity
import com.example.data.local.DuelEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.CardGradientEnd
import com.example.ui.theme.CardGradientStart
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.SurfaceBorder

@Composable
fun HomeScreen(
    user: UserEntity?,
    dailyChallenge: ChallengeEntity?,
    challenges: List<ChallengeEntity>,
    duels: List<DuelEntity>,
    onPlayChallenge: (ChallengeEntity) -> Unit,
    onFriendsDuelsClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onCreateChallengeClick: () -> Unit,
    onShareChallenge: (ChallengeEntity, Int?) -> Unit,
    onUseStreakFreeze: () -> Unit,
    onPlayUndercoverSpy: () -> Unit,
    onPlayWordBomb: () -> Unit,
    onPlayGrabTheMic: () -> Unit,
    onPartyGamesHubClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showStreakDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // 1. Hero Daily Challenge Card
        item {
            dailyChallenge?.let { challenge ->
                DailyChallengeHeroCard(
                    challenge = challenge,
                    onPlay = { onPlayChallenge(challenge) },
                    onShare = { onShareChallenge(challenge, challenge.userScore) }
                )
            }
        }

        // 2. Streak & Flame Status Banner
        item {
            user?.let { u ->
                StreakStatusCard(
                    user = u,
                    onStreakClick = { showStreakDialog = true }
                )
            }
        }

        // 3. Quick Play Arcade (30-Sec Drawing, Guess Song, AI Trivia)
        item {
            Text(
                text = "⚡ Challenge Modes",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 30-Sec Drawing Card
                ChallengeModeCard(
                    title = "30-Sec Draw",
                    subtitle = "AI Referee",
                    icon = Icons.Default.Brush,
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val drawChal = challenges.firstOrNull { it.category == "DRAWING_30S" && !it.isCompleted }
                            ?: challenges.firstOrNull { it.category == "DRAWING_30S" }
                            ?: dailyChallenge
                        drawChal?.let { onPlayChallenge(it) }
                    },
                    testTag = "mode_drawing_card"
                )

                // Guess The Song Card
                ChallengeModeCard(
                    title = "Guess Song",
                    subtitle = "Rhythm & Lyrics",
                    icon = Icons.Default.MusicNote,
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val songChal = challenges.firstOrNull { it.category == "GUESS_SONG" && !it.isCompleted }
                            ?: challenges.firstOrNull { it.category == "GUESS_SONG" }
                            ?: dailyChallenge
                        songChal?.let { onPlayChallenge(it) }
                    },
                    testTag = "mode_song_card"
                )

                // Create AI Challenge Card
                ChallengeModeCard(
                    title = "AI Prompt",
                    subtitle = "Custom Duel",
                    icon = Icons.Default.AutoAwesome,
                    accentColor = NeonAmber,
                    modifier = Modifier.weight(1f),
                    onClick = onCreateChallengeClick,
                    testTag = "mode_ai_generate_card"
                )
            }
        }

        // WePlay Party Arcade Games Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🎮 WePlay Party Games",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NeonRose.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "NEW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonRose,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Party Lounge →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier
                        .clickable { onPartyGamesHubClick() }
                        .testTag("open_party_lounge_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Who is the Spy?
                item {
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onPlayUndercoverSpy() }
                            .testTag("card_undercover_spy"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1738)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🕵️", fontSize = 28.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonCyan.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "4 PLAYERS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Who is the Spy?",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Social deduction word duel",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2. Word Bomb
                item {
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onPlayWordBomb() }
                            .testTag("card_word_bomb"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF241432)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💣", fontSize = 28.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonAmber.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "RAPID FIRE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Word Bomb",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Pass the ticking bomb!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 3. Grab the Mic
                item {
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onPlayGrabTheMic() }
                            .testTag("card_grab_mic"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF19153A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎤", fontSize = 28.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonPurple.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "KARAOKE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonPurple,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Grab the Mic",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Sing-along buzzer sprint",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 4. Active Friends Duels
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚔️ Friend Duels",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "See All (${duels.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonPurple,
                    modifier = Modifier
                        .clickable { onFriendsDuelsClick() }
                        .testTag("see_all_duels_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (duels.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No active duels yet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onFriendsDuelsClick,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text("Challenge a Friend Now")
                        }
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(duels) { duel ->
                        DuelMiniCard(
                            duel = duel,
                            onDuelClick = onFriendsDuelsClick
                        )
                    }
                }
            }
        }

        // 5. All Active Challenges List
        item {
            Text(
                text = "🎯 Daily Challenge Playlist",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(challenges) { challenge ->
            ChallengeListItem(
                challenge = challenge,
                onPlay = { onPlayChallenge(challenge) },
                onShare = { onShareChallenge(challenge, challenge.userScore) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Streak Freeze Info Dialog
    if (showStreakDialog && user != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showStreakDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = NeonAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Streak Shield & Stats")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Current Streak: ${user.currentStreak} Days 🔥",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "All-Time Best: ${user.highestStreak} Days 🏆",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "You have ${user.streakFreezeCount} Streak Freezes available. Streak Freezes auto-protect your streak if you miss a day!",
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (user.streakFreezeCount > 0) {
                            onUseStreakFreeze()
                        }
                        showStreakDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber)
                ) {
                    Icon(Icons.Default.AcUnit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Activate Freeze Shield")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showStreakDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun DailyChallengeHeroCard(
    challenge: ChallengeEntity,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_challenge_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B1D7A),
                            Color(0xFF1E153A)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                // Header tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonRose.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonRose.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = NeonRose,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "TODAY'S AI DROP",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = NeonRose
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonAmber.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "+${challenge.xpReward} XP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = NeonAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title & Description
                Text(
                    text = challenge.title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = challenge.description,
                    fontSize = 14.sp,
                    color = Color(0xFFCBD5E1),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPlay,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("play_daily_challenge_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (challenge.isCompleted) NeonEmerald else NeonPurple
                        )
                    ) {
                        Icon(
                            imageVector = if (challenge.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (challenge.isCompleted) "Completed (${challenge.userScore} pts)" else "Start Challenge",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("share_daily_challenge_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakStatusCard(
    user: UserEntity,
    onStreakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onStreakClick() }
            .testTag("streak_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonAmber.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "${user.currentStreak}-Day Streak Active! 🔥",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Score: ${user.totalScore} pts • Best: ${user.highestStreak}d",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NeonAmber.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AcUnit, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${user.streakFreezeCount} Shields",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeonCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeModeCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DuelMiniCard(
    duel: DuelEntity,
    onDuelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .clickable { onDuelClick() }
            .testTag("duel_card_${duel.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "vs ${duel.friendName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                        text = duel.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (duel.status) {
                            "WON" -> NeonEmerald
                            "LOST" -> NeonRose
                            else -> NeonAmber
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = duel.challengeTitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You: ${duel.userScore} pts",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = NeonCyan
                )

                Text(
                    text = if (duel.friendScore != null) "${duel.friendScore} pts" else "Waiting...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChallengeListItem(
    challenge: ChallengeEntity,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .testTag("challenge_item_${challenge.id}"),
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
                        .background(
                            when (challenge.category) {
                                "DRAWING_30S" -> NeonPurple.copy(alpha = 0.2f)
                                "GUESS_SONG" -> NeonCyan.copy(alpha = 0.2f)
                                else -> NeonAmber.copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (challenge.category) {
                            "DRAWING_30S" -> Icons.Default.Brush
                            "GUESS_SONG" -> Icons.Default.MusicNote
                            else -> Icons.Default.Psychology
                        },
                        contentDescription = null,
                        tint = when (challenge.category) {
                            "DRAWING_30S" -> NeonPurple
                            "GUESS_SONG" -> NeonCyan
                            else -> NeonAmber
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = challenge.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${challenge.difficulty} • +${challenge.xpReward} XP",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (challenge.isCompleted) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonEmerald.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "${challenge.userScore} pts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = NeonEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Play",
                        tint = NeonPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
