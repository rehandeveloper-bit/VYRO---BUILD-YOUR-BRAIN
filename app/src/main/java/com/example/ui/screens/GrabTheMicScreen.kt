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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConfettiEffect
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import kotlinx.coroutines.delay
import kotlin.random.Random

data class KaraokeLyricRound(
    val songTitle: String,
    val artist: String,
    val lyricsBefore: String,
    val missingWord: String,
    val lyricsAfter: String,
    val options: List<String>
)

data class MicPlayer(
    val id: String,
    val name: String,
    val initials: String,
    val color: Long,
    val isUser: Boolean,
    var score: Int = 0
)

@Composable
fun GrabTheMicScreen(
    onGameVictory: (gameName: String, xp: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val rounds = remember {
        listOf(
            KaraokeLyricRound("Shape of You", "Ed Sheeran", "I'm in love with the shape of you, we push and pull like a", "magnet", "do", listOf("magnet", "rocket", "river", "candle")),
            KaraokeLyricRound("Blinding Lights", "The Weeknd", "Sin city's cold and empty, no one's around to", "judge", "me", listOf("judge", "hold", "save", "wake")),
            KaraokeLyricRound("Bohemian Rhapsody", "Queen", "Is this the real life? Is this just", "fantasy", "?", listOf("fantasy", "dream", "memory", "illusion")),
            KaraokeLyricRound("Chhaiya Chhaiya", "A.R. Rahman", "Chal chhaiya chhaiya chhaiya, sar ishq ki chaaon mein", "chal chhaiya", "!", listOf("chal chhaiya", "udd jaave", "ruk jaana", "nach baliye")),
            KaraokeLyricRound("Despacito", "Luis Fonsi", "Despacito, quiero respirar tu cuello", "despacito", "...", listOf("despacito", "suavemente", "despacio", "calladito"))
        )
    }

    val players = remember {
        mutableStateListOf(
            MicPlayer("p1", "You (Alex)", "AV", 0xFF8B5CF6, isUser = true),
            MicPlayer("p2", "Priya", "PS", 0xFFEC4899, isUser = false),
            MicPlayer("p3", "Rohan", "RM", 0xFF06B6D4, isUser = false),
            MicPlayer("p4", "Marcus", "MC", 0xFF10B981, isUser = false)
        )
    }

    var currentRoundIndex by remember { mutableIntStateOf(0) }
    val currentRound = rounds[currentRoundIndex]

    // States: 0 = Rolling Lyrics / Countdown, 1 = Buzzer Active ("GRAB MIC"), 2 = Answering (5s), 3 = Round Result, 4 = Final Game Over
    var roundState by remember { mutableIntStateOf(0) }
    var micHolderPlayerId by remember { mutableStateOf<String?>(null) }
    var answerTimeLeft by remember { mutableIntStateOf(5) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var roundFeedback by remember { mutableStateOf("") }
    var showConfetti by remember { mutableStateOf(false) }

    // Waveform animation
    val infiniteTransition = rememberInfiniteTransition(label = "mic_waves")
    val waveHeight by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w"
    )

    fun startRoundCountdown() {
        roundState = 0
        micHolderPlayerId = null
        selectedAnswer = null
        roundFeedback = ""
    }

    // Auto countdown to open Buzzer
    LaunchedEffect(currentRoundIndex, roundState) {
        if (roundState == 0) {
            delay(2500)
            roundState = 1 // Buzzer is now OPEN!
        } else if (roundState == 1) {
            // AI buzzer simulation: AI tries to buzz in within 1200 - 2400 ms
            val aiBuzzDelay = Random.nextLong(1200, 2400)
            delay(aiBuzzDelay)
            if (roundState == 1 && micHolderPlayerId == null) {
                // An AI player grabbed the mic!
                val aiVoter = players.filter { !it.isUser }.random()
                micHolderPlayerId = aiVoter.id
                roundState = 2
            }
        } else if (roundState == 2) {
            // Answering timer (5 seconds)
            answerTimeLeft = 5
            val isUserHolder = micHolderPlayerId == players[0].id
            if (!isUserHolder) {
                // AI automatically picks answer after 2 seconds
                delay(2000)
                val aiVoter = players.first { it.id == micHolderPlayerId }
                val isCorrect = Random.nextFloat() < 0.75f
                if (isCorrect) {
                    aiVoter.score += 100
                    roundFeedback = "🎤 ${aiVoter.name} nailed the lyric! (+100 pts)"
                } else {
                    roundFeedback = "❌ ${aiVoter.name} sang the wrong lyric!"
                }
                roundState = 3
            } else {
                while (answerTimeLeft > 0 && roundState == 2 && selectedAnswer == null) {
                    delay(1000)
                    answerTimeLeft -= 1
                }
                if (roundState == 2 && selectedAnswer == null) {
                    roundFeedback = "⏱️ Time expired! You didn't sing the lyric in time."
                    roundState = 3
                }
            }
        }
    }

    fun submitUserAnswer(choice: String) {
        selectedAnswer = choice
        val isCorrect = choice.equals(currentRound.missingWord, ignoreCase = true)
        if (isCorrect) {
            val bonus = 100 + (answerTimeLeft * 10)
            players[0].score += bonus
            roundFeedback = "🎯 SPOT ON! You belted out the perfect lyric! (+$bonus pts)"
            showConfetti = true
        } else {
            roundFeedback = "❌ Wrong lyric! The actual line was '${currentRound.missingWord}'."
        }
        roundState = 3
    }

    fun nextRoundOrFinish() {
        if (currentRoundIndex < rounds.size - 1) {
            currentRoundIndex += 1
            startRoundCountdown()
        } else {
            roundState = 4 // Final game over
            val topPlayer = players.maxByOrNull { it.score }
            if (topPlayer?.isUser == true) {
                showConfetti = true
                onGameVictory("Grab the Mic Karaoke", 250)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
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
                        IconButton(onClick = onBack, modifier = Modifier.testTag("mic_back_button")) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Grab the Mic! 🎤",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Round ${currentRoundIndex + 1} of ${rounds.size} • WePlay Karaoke",
                                fontSize = 12.sp,
                                color = NeonPurple
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonAmber.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber)
                    ) {
                        Text(
                            text = "Score: ${players[0].score}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = NeonAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Player Podium Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    players.forEach { p ->
                        val hasMic = micHolderPlayerId == p.id
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(p.color))
                                    .border(
                                        width = if (hasMic) 3.dp else 0.dp,
                                        color = if (hasMic) NeonAmber else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = p.initials,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = p.name.take(6),
                                fontSize = 11.sp,
                                fontWeight = if (hasMic) FontWeight.Bold else FontWeight.Normal,
                                color = if (hasMic) NeonAmber else MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "${p.score} pts",
                                fontSize = 10.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Karaoke Center Stage
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E153A)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonPurple)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Equalizer bars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(30.dp)
                        ) {
                            repeat(7) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height((30 * waveHeight).coerceAtLeast(6f).dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Brush.verticalGradient(listOf(NeonCyan, NeonPurple)))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${currentRound.songTitle} • ${currentRound.artist}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeonAmber
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rolling Karaoke Lyrics
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF100B22),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382662))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "\"${currentRound.lyricsBefore} ...",
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonAmber.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber)
                                ) {
                                    Text(
                                        text = if (roundState >= 3) currentRound.missingWord.uppercase() else "[ MISSING LYRIC ]",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = NeonAmber,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                if (currentRound.lyricsAfter.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "... ${currentRound.lyricsAfter}\"",
                                        fontSize = 15.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Giant "GRAB MIC" Buzzer Button (Phase 1)
            if (roundState == 1) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚡ TAP FAST TO GRAB THE MIC!",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = NeonAmber
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(listOf(NeonRose, Color(0xFF9F1239)))
                                )
                                .border(4.dp, Color.White, CircleShape)
                                .clickable {
                                    micHolderPlayerId = players[0].id
                                    roundState = 2 // User grabbed it!
                                }
                                .testTag("grab_mic_buzzer_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Grab Mic",
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = "GRAB!",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Lyric Options when User Grabs Mic (Phase 2)
            if (roundState == 2 && micHolderPlayerId == players[0].id) {
                item {
                    Text(
                        text = "🎤 You have the mic! Complete the lyric in ${answerTimeLeft}s:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonCyan
                    )
                }

                items(currentRound.options) { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { submitUserAnswer(option) }
                            .testTag("lyric_choice_$option"),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = option,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Round Result (Phase 3)
            if (roundState == 3) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = roundFeedback,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { nextRoundOrFinish() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("next_mic_round_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                            ) {
                                Text(
                                    if (currentRoundIndex < rounds.size - 1) "Next Lyric Sprint" else "View Final Podium 🏆",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Final Game Over (Phase 4)
            if (roundState == 4) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF22173E)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonAmber)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Karaoke Battle Complete!",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Final Score: ${players[0].score} pts",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        currentRoundIndex = 0
                                        players.forEach { it.score = 0 }
                                        startRoundCountdown()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Replay")
                                }

                                OutlinedButton(
                                    onClick = onBack,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Exit Room")
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        if (showConfetti) {
            ConfettiEffect(onFinished = { showConfetti = false })
        }
    }
}
