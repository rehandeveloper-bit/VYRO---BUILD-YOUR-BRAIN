package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
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

data class BombPlayer(
    val id: String,
    val name: String,
    val initials: String,
    val color: Long,
    val isUser: Boolean,
    var lives: Int = 3,
    var lastWord: String = ""
)

@Composable
fun WordBombScreen(
    onGameVictory: (gameName: String, xp: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val promptConstraints = remember {
        listOf(
            "Contains 'EX'" to listOf("extra", "exam", "exit", "exact", "expert", "relax", "expand"),
            "Contains 'TH'" to listOf("thing", "think", "water", "other", "father", "mother", "path", "truth"),
            "Starts with 'TR'" to listOf("train", "track", "tree", "trip", "true", "trust", "traffic"),
            "Contains 'CH'" to listOf("chair", "chocolate", "check", "beach", "reach", "champion", "march"),
            "Ends with 'ING'" to listOf("sing", "ring", "wing", "king", "running", "playing", "jumping"),
            "Contains 'ST'" to listOf("star", "stop", "fast", "blast", "storm", "stone", "stand", "master"),
            "Contains 'AN'" to listOf("banana", "panda", "plant", "dance", "stand", "orange", "candy")
        )
    }

    var currentPromptPair by remember { mutableStateOf(promptConstraints.random()) }
    var currentPromptText by remember { mutableStateOf(currentPromptPair.first) }
    var validWordsDictionary by remember { mutableStateOf(currentPromptPair.second) }

    val usedWords = remember { mutableStateListOf<String>() }

    val players = remember {
        mutableStateListOf(
            BombPlayer("p1", "You (Alex)", "AV", 0xFF8B5CF6, isUser = true),
            BombPlayer("p2", "Priya", "PS", 0xFFEC4899, isUser = false),
            BombPlayer("p3", "Rohan", "RM", 0xFF06B6D4, isUser = false),
            BombPlayer("p4", "Zara", "ZK", 0xFFF59E0B, isUser = false)
        )
    }

    var activePlayerIndex by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(9) }
    var isTimerActive by remember { mutableStateOf(true) }
    var userInputWord by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("Pass the ticking bomb before it detonates!") }
    var showExplosionEffect by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    // Pulsing bomb animation
    val infiniteTransition = rememberInfiniteTransition(label = "bomb_pulse")
    val bombScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (secondsLeft <= 3) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (secondsLeft <= 3) 250 else 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    fun pickNewPrompt() {
        val newPair = promptConstraints.random()
        currentPromptPair = newPair
        currentPromptText = newPair.first
        validWordsDictionary = newPair.second
        usedWords.clear()
    }

    fun passToNextPlayer() {
        val alivePlayers = players.filter { it.lives > 0 }
        if (alivePlayers.size <= 1) {
            isGameOver = true
            isTimerActive = false
            val winner = alivePlayers.firstOrNull() ?: players[0]
            statusMessage = "👑 ${winner.name} SURVIVED AND WON THE MATCH!"
            if (winner.isUser) {
                showConfetti = true
                onGameVictory("Word Bomb Survivor", 300)
            }
            return
        }

        var nextIdx = (activePlayerIndex + 1) % players.size
        while (players[nextIdx].lives <= 0) {
            nextIdx = (nextIdx + 1) % players.size
        }
        activePlayerIndex = nextIdx
        secondsLeft = 9
    }

    fun explodeCurrentPlayer() {
        showExplosionEffect = true
        triggerVibration(context)
        val victim = players[activePlayerIndex]
        victim.lives = (victim.lives - 1).coerceAtLeast(0)
        statusMessage = "💥 BOOM! Bomb exploded in ${victim.name}'s hands!"

        val alivePlayers = players.filter { it.lives > 0 }
        if (alivePlayers.size <= 1 || players[0].lives <= 0) {
            isGameOver = true
            isTimerActive = false
            val winner = alivePlayers.firstOrNull()
            if (winner != null && winner.isUser) {
                showConfetti = true
                onGameVictory("Word Bomb Champion", 300)
                statusMessage = "👑 YOU SURVIVED THE WORD BOMB DETONATION!"
            } else {
                statusMessage = "Game Over! ${winner?.name ?: "Opponents"} won the match."
            }
        } else {
            pickNewPrompt()
            passToNextPlayer()
        }
    }

    // Timer and AI turn logic
    LaunchedEffect(activePlayerIndex, isTimerActive, isGameOver) {
        if (!isTimerActive || isGameOver) return@LaunchedEffect

        val active = players[activePlayerIndex]
        if (!active.isUser) {
            // AI Player Turn: Simulates typing a word after 2-4 seconds
            val aiDelay = Random.nextLong(2200, 4200)
            delay(aiDelay)
            if (isTimerActive && !isGameOver && activePlayerIndex == players.indexOf(active)) {
                val availableWords = validWordsDictionary.filter { !usedWords.contains(it) }
                val chosenWord = if (availableWords.isNotEmpty()) availableWords.random() else "expert"
                usedWords.add(chosenWord.lowercase())
                active.lastWord = chosenWord.uppercase()
                statusMessage = "${active.name} passed: '$chosenWord' 💣"
                passToNextPlayer()
            }
        } else {
            // User Turn: Countdown ticks down
            while (secondsLeft > 0 && isTimerActive && !isGameOver && activePlayerIndex == 0) {
                delay(1000)
                secondsLeft -= 1
                if (secondsLeft <= 3) {
                    triggerVibration(context)
                }
            }
            if (secondsLeft == 0 && activePlayerIndex == 0 && !isGameOver) {
                explodeCurrentPlayer()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("word_bomb_back_button")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Word Bomb 💣",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "WePlay Rapid Party Game",
                            fontSize = 12.sp,
                            color = NeonAmber
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonRose.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonRose)
                ) {
                    Text(
                        text = "LIVE DUEL",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = NeonRose,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Player Seats & Hearts List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                players.forEachIndexed { index, player ->
                    val isActive = index == activePlayerIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(player.color))
                                .border(
                                    width = if (isActive) 3.dp else 0.dp,
                                    color = if (isActive) NeonAmber else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player.initials,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = player.name.take(7),
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = if (isActive) NeonAmber else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        // Lives Hearts
                        Row {
                            repeat(3) { i ->
                                Icon(
                                    imageVector = if (i < player.lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (i < player.lives) NeonRose else Color.DarkGray,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Central Bomb Arena
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B122C)),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (activePlayerIndex == 0) NeonAmber else Color(0xFF3B285E)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Pulsing Ticking Bomb
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(bombScale),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (showExplosionEffect) "💥" else "💣",
                            fontSize = 72.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timer Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (secondsLeft <= 3) NeonRose.copy(alpha = 0.25f) else NeonAmber.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (secondsLeft <= 3) NeonRose else NeonAmber
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⏱️ ${secondsLeft}s",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = if (secondsLeft <= 3) NeonRose else NeonAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Constraint Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2A1C48),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "RULE FOR WORD:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentPromptText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = statusMessage,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // User Typing Box (Active when it's User's turn)
            if (activePlayerIndex == 0 && !isGameOver) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInputWord,
                        onValueChange = { userInputWord = it.filter { ch -> ch.isLetter() } },
                        placeholder = { Text("Type word matching prompt...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("word_bomb_input"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val clean = userInputWord.lowercase().trim()
                            if (clean.length >= 2 && !usedWords.contains(clean)) {
                                usedWords.add(clean)
                                players[0].lastWord = clean.uppercase()
                                statusMessage = "You passed: '${clean.uppercase()}'! 💣"
                                userInputWord = ""
                                passToNextPlayer()
                            }
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("pass_bomb_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                        enabled = userInputWord.isNotBlank()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PASS! 💣", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                // Quick smart word hints if user is stuck!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    validWordsDictionary.take(3).forEach { hintWord ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { userInputWord = hintWord }
                        ) {
                            Text(
                                text = hintWord,
                                fontSize = 11.sp,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Game Over Section
            if (isGameOver) {
                Button(
                    onClick = {
                        players.forEach { it.lives = 3 }
                        isGameOver = false
                        isTimerActive = true
                        activePlayerIndex = 0
                        secondsLeft = 9
                        pickNewPrompt()
                        statusMessage = "Pass the ticking bomb before it detonates!"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("restart_word_bomb_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play Next Round", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showConfetti) {
            ConfettiEffect(onFinished = { showConfetti = false })
        }
    }
}

private fun triggerVibration(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            v?.vibrate(100)
        }
    } catch (_: Exception) {}
}
