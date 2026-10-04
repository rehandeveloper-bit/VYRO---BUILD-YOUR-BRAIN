package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChallengeEntity
import com.example.data.local.FriendEntity
import com.example.data.remote.SongEvaluationResult
import com.example.ui.components.ConfettiEffect
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import kotlinx.coroutines.delay

@Composable
fun SongGuessChallengeScreen(
    challenge: ChallengeEntity,
    friends: List<FriendEntity>,
    evaluationResult: SongEvaluationResult?,
    onSubmitGuess: (guess: String, hintsUsed: Int, secondsSpent: Int) -> Unit,
    onSendDuelToFriend: (FriendEntity, Int) -> Unit,
    onShareScore: (Int) -> Unit,
    onBack: () -> Unit,
    onClearResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onClearResult()
        onBack()
    }

    var guessInput by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableIntStateOf(45) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var hintsRevealed by remember { mutableIntStateOf(1) }
    var showFriendDuelDialog by remember { mutableStateOf(false) }

    val rawHints = remember(challenge) {
        if (challenge.hintsJson.isNotBlank()) {
            challenge.hintsJson.split("||")
        } else {
            listOf(
                "Listen to the rhythmic heartbeat and tempo",
                "Chart-topping hit played across the world",
                "Sing the chorus melody in your mind"
            )
        }
    }

    // Countdown Timer
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (secondsLeft > 0 && isTimerRunning) {
                delay(1000)
                secondsLeft -= 1
            }
            if (secondsLeft == 0 && isTimerRunning) {
                isTimerRunning = false
                onSubmitGuess(guessInput, hintsRevealed, 45)
            }
        }
    }

    // Music waveform animation
    val infiniteTransition = rememberInfiniteTransition(label = "music_waves")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w4"
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            onClearResult()
                            onBack()
                        },
                        modifier = Modifier.testTag("song_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Guess the Song",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NeonCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${secondsLeft}s",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }

            // Waveform Hero Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Visual Waveform Bars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(48.dp)
                        ) {
                            listOf(wave1, wave2, wave3, wave4, wave2, wave1, wave3).forEach { waveScale ->
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height((48 * waveScale).coerceAtLeast(8f).dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            Brush.verticalGradient(listOf(NeonCyan, NeonPurple))
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = challenge.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = challenge.promptDetail,
                            fontSize = 13.sp,
                            color = NeonAmber,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Clues Cards Section
            item {
                Text(
                    text = "🔍 Musical Clues ($hintsRevealed / ${rawHints.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(rawHints.size) { index ->
                val isRevealed = index < hintsRevealed
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isRevealed) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF151025)
                    ),
                    border = if (isRevealed) androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f)) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isRevealed) NeonPurple.copy(alpha = 0.2f) else Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRevealed) Icons.Default.MusicNote else Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = if (isRevealed) NeonPurple else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = if (isRevealed) rawHints[index] else "Clue ${index + 1} locked • Tap below to reveal",
                            fontSize = 13.sp,
                            color = if (isRevealed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isRevealed) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }

            // Unlock next hint button
            if (hintsRevealed < rawHints.size) {
                item {
                    OutlinedButton(
                        onClick = {
                            if (hintsRevealed < rawHints.size) hintsRevealed += 1
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reveal_hint_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reveal Next Clue (-15 XP multiplier)", color = NeonAmber, fontSize = 13.sp)
                    }
                }
            }

            // Guess Input Field
            item {
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = guessInput,
                    onValueChange = { guessInput = it },
                    label = { Text("Enter Song Title...") },
                    placeholder = { Text("e.g. Blinding Lights, Shape of You") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("song_guess_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Quick suggestion chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Blinding Lights", "Shape of You", "Chhaiya Chhaiya").forEach { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { guessInput = suggestion }
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Submit Guess Button
            item {
                Button(
                    onClick = {
                        if (guessInput.isNotBlank()) {
                            isTimerRunning = false
                            val timeSpent = (45 - secondsLeft).coerceAtLeast(1)
                            onSubmitGuess(guessInput, hintsRevealed, timeSpent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_song_guess_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    enabled = guessInput.isNotBlank()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lock In Song Guess",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }

        // Result Dialog
        evaluationResult?.let { res ->
            if (res.isCorrect) {
                ConfettiEffect()
            }

            androidx.compose.material3.AlertDialog(
                onDismissRequest = { /* force action */ },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color(0xFF1E1738),
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (res.isCorrect) "🎉 Correct Song!" else "❌ Close, but not quite!",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = if (res.isCorrect) NeonEmerald else NeonRose
                        )
                        if (res.isCorrect) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "+${res.score} XP Earned",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = NeonAmber
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = res.feedback,
                            fontSize = 14.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Correct Answer: ${challenge.targetAnswer}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = res.trivia,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (res.isCorrect) {
                            Button(
                                onClick = { showFriendDuelDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("challenge_friend_song_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.SportsKabaddi, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Challenge Friends to Beat Your Time!")
                            }

                            OutlinedButton(
                                onClick = { onShareScore(res.score) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Guess Win")
                            }
                        }

                        Button(
                            onClick = {
                                onClearResult()
                                onBack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("Done")
                        }
                    }
                },
                dismissButton = null
            )
        }

        // Friend Duel Picker Dialog
        if (showFriendDuelDialog && evaluationResult != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showFriendDuelDialog = false },
                title = { Text("Send Song Duel") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        friends.forEach { friend ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        onSendDuelToFriend(friend, evaluationResult.score)
                                        showFriendDuelDialog = false
                                        onClearResult()
                                        onBack()
                                    },
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = friend.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Icon(Icons.Default.Send, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    OutlinedButton(onClick = { showFriendDuelDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
