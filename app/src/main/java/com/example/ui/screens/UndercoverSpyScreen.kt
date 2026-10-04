package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.components.ConfettiEffect
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import kotlinx.coroutines.delay
import kotlin.random.Random

data class SpyPlayer(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Long,
    val isUser: Boolean,
    val isSpy: Boolean,
    var isEliminated: Boolean = false,
    var clue: String = "",
    var votesReceived: Int = 0
)

data class WordPair(
    val citizenWord: String,
    val spyWord: String,
    val citizenClues: List<String>,
    val spyClues: List<String>
)

@Composable
fun UndercoverSpyScreen(
    onGameVictory: (gameName: String, xp: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val wordPairs = remember {
        listOf(
            WordPair("Coffee", "Tea", listOf("I drink this hot in the morning to wake up.", "Often ordered at a cafe with milk.", "Rich roasted dark aroma."), listOf("Steeped with leaves in hot water.", "Very popular with biscuits and ginger.", "Great for relaxing in the evening.")),
            WordPair("Pizza", "Burger", listOf("Cut into triangular slices.", "Lots of melted mozzarella cheese on dough.", "Baked inside an oven."), listOf("Grilled patty between two round buns.", "Often served with french fries.", "You hold it with two hands to bite.")),
            WordPair("Bicycle", "Motorcycle", listOf("Powered by pedaling with legs.", "Environment friendly with two wheels.", "Has a bell and chain."), listOf("Powered by an engine and fuel.", "Wears a helmet and drives fast on highway.", "Has an exhaust pipe.")),
            WordPair("Instagram", "TikTok", listOf("Known for photo grids and story filters.", "Follow friends and double-tap to like.", "Owned by Meta."), listOf("Famous for viral short video trends.", "Infinite scrolling vertical feed.", "Known for dance routines and audio clips.")),
            WordPair("Cinema", "Theater", listOf("Giant projection screen with surround sound.", "Popcorn and 3D glasses.", "Hollywood blockbuster releases."), listOf("Live actors performing on stage.", "Curtains open and close between acts.", "Live drama and musical performance."))
        )
    }

    var selectedPair by remember { mutableStateOf(wordPairs.random()) }
    var spyPlayerIndex by remember { mutableIntStateOf(Random.nextInt(4)) }

    // Game Phases: 0 = Reveal Word, 1 = Clue Giving, 2 = Voting, 3 = Game Over
    var gamePhase by remember { mutableIntStateOf(0) }
    var isWordRevealed by remember { mutableStateOf(false) }
    var userClueInput by remember { mutableStateOf("") }
    var selectedVotePlayerId by remember { mutableStateOf<String?>(null) }
    var winnerAnnouncement by remember { mutableStateOf("") }
    var showConfetti by remember { mutableStateOf(false) }

    val players = remember(selectedPair, spyPlayerIndex) {
        mutableStateListOf(
            SpyPlayer("p1", "You (Alex)", "AV", 0xFF8B5CF6, isUser = true, isSpy = spyPlayerIndex == 0),
            SpyPlayer("p2", "Priya Sharma", "PS", 0xFFEC4899, isUser = false, isSpy = spyPlayerIndex == 1),
            SpyPlayer("p3", "Rohan Mehta", "RM", 0xFF06B6D4, isUser = false, isSpy = spyPlayerIndex == 2),
            SpyPlayer("p4", "Marcus Chen", "MC", 0xFF10B981, isUser = false, isSpy = spyPlayerIndex == 3)
        )
    }

    val userPlayer = players[0]
    val userWord = if (userPlayer.isSpy) selectedPair.spyWord else selectedPair.citizenWord

    fun resetGame() {
        selectedPair = wordPairs.random()
        spyPlayerIndex = Random.nextInt(4)
        gamePhase = 0
        isWordRevealed = false
        userClueInput = ""
        selectedVotePlayerId = null
        winnerAnnouncement = ""
        showConfetti = false

        players.clear()
        players.addAll(
            listOf(
                SpyPlayer("p1", "You (Alex)", "AV", 0xFF8B5CF6, isUser = true, isSpy = spyPlayerIndex == 0),
                SpyPlayer("p2", "Priya Sharma", "PS", 0xFFEC4899, isUser = false, isSpy = spyPlayerIndex == 1),
                SpyPlayer("p3", "Rohan Mehta", "RM", 0xFF06B6D4, isUser = false, isSpy = spyPlayerIndex == 2),
                SpyPlayer("p4", "Marcus Chen", "MC", 0xFF10B981, isUser = false, isSpy = spyPlayerIndex == 3)
            )
        )
    }

    // AI Clue Generation during Phase 1
    fun submitUserClueAndGenerateOthers() {
        if (userClueInput.isBlank()) return
        userPlayer.clue = userClueInput

        // Generate AI clues
        for (i in 1..3) {
            val p = players[i]
            p.clue = if (p.isSpy) {
                selectedPair.spyClues.random()
            } else {
                selectedPair.citizenClues[i - 1]
            }
        }
        gamePhase = 2 // Move to Voting
    }

    // Voting Resolution
    fun resolveVoting() {
        if (selectedVotePlayerId == null) return

        // Distribute votes: AI players vote with slight logic
        players.forEach { it.votesReceived = 0 }

        // User vote
        players.firstOrNull { it.id == selectedVotePlayerId }?.let { it.votesReceived += 1 }

        // AI votes
        val spy = players.first { it.isSpy }
        for (i in 1..3) {
            val voter = players[i]
            // If voter is citizen, they have 65% chance to vote for the spy, 35% random citizen
            val target = if (!voter.isSpy && Random.nextFloat() < 0.65f) {
                spy
            } else {
                players.filter { it.id != voter.id }.random()
            }
            target.votesReceived += 1
        }

        // Most voted player is eliminated
        val eliminated = players.maxByOrNull { it.votesReceived } ?: players.last()
        eliminated.isEliminated = true

        gamePhase = 3 // Game Over / Result
        if (eliminated.isSpy) {
            winnerAnnouncement = "🎉 Citizens Win! The Undercover Spy (${eliminated.name}) was caught! Secret words: '${selectedPair.citizenWord}' vs '${selectedPair.spyWord}'."
            showConfetti = true
            onGameVictory("Who is the Spy?", 250)
        } else {
            val spyName = spy.name
            winnerAnnouncement = if (userPlayer.isSpy) {
                showConfetti = true
                onGameVictory("Who is the Spy? (As Spy!)", 300)
                "🏆 SPY VICTORY! You fooled the entire room! Citizens voted out ${eliminated.name}."
            } else {
                "💀 Defeat! Citizens voted out an innocent (${eliminated.name})! The Undercover Spy was $spyName."
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
                        IconButton(onClick = onBack, modifier = Modifier.testTag("spy_back_button")) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Who is the Spy? 🕵️",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "WePlay Party Social Deduction",
                                fontSize = 12.sp,
                                color = NeonPurple
                            )
                        }
                    }

                    IconButton(onClick = { resetGame() }, modifier = Modifier.testTag("reset_spy_game")) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "New Match", tint = NeonCyan)
                    }
                }
            }

            // Phase Progress Indicator
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (gamePhase) {
                                0 -> "Step 1: Check Secret Word"
                                1 -> "Step 2: Give Your Clue"
                                2 -> "Step 3: Vote Out The Spy"
                                else -> "Game Over & Results"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeonAmber
                        )
                        Text(
                            text = "4 Players in Room",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 1. Secret Word Card (Phase 0)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isWordRevealed = !isWordRevealed }
                        .testTag("secret_word_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1738)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isWordRevealed) NeonPurple else NeonCyan)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isWordRevealed) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = NeonCyan
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isWordRevealed) "YOUR SECRET WORD (Tap to hide)" else "TAP TO REVEAL SECRET WORD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = NeonCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isWordRevealed) {
                            Text(
                                text = userWord.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                color = if (userPlayer.isSpy) NeonRose else Color.White,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (userPlayer.isSpy) "⚠️ You are the UNDERCOVER SPY! Blend in and don't get caught!" else "🛡️ You are an ORDINARY CITIZEN! Give subtle clues and find the spy.",
                                fontSize = 12.sp,
                                color = if (userPlayer.isSpy) NeonRose else NeonEmerald,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = "••••••••",
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                color = Color.Gray,
                                letterSpacing = 4.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Keep your screen private from friends!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (gamePhase == 0) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { gamePhase = 1 },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                enabled = isWordRevealed
                            ) {
                                Text("Ready! Start Clue Round", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Clue Input Box (Phase 1)
            if (gamePhase == 1) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🎙️ Your Turn: Describe your word in 1 sentence",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Rule: Do NOT say the exact word '$userWord'. Be subtle so other citizens understand!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = userClueInput,
                                onValueChange = { userClueInput = it },
                                placeholder = { Text("e.g. Something warm I enjoy in the morning...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("user_spy_clue_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick clue suggestion chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Very popular with friends", "Usually enjoyed warm", "Part of my daily routine").forEach { suggestion ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF151025),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { userClueInput = suggestion }
                                    ) {
                                        Text(
                                            text = suggestion,
                                            fontSize = 10.sp,
                                            color = NeonCyan,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { submitUserClueAndGenerateOthers() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_clue_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                enabled = userClueInput.isNotBlank()
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Speak Clue & Hear Others", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3. Player Clues & Voting List (Phases 2 & 3)
            if (gamePhase >= 2) {
                item {
                    Text(
                        text = if (gamePhase == 2) "🗳️ Room Discussion & Voting: Who is the Spy?" else "🏁 Final Room Elimination",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(players) { player ->
                    val isSelectedToVote = selectedVotePlayerId == player.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = gamePhase == 2 && !player.isUser) {
                                selectedVotePlayerId = player.id
                            }
                            .testTag("player_card_${player.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelectedToVote) NeonPurple.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelectedToVote) androidx.compose.foundation.BorderStroke(1.5.dp, NeonPurple) else null
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
                                        .clip(CircleShape)
                                        .background(Color(player.avatarColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = player.initials,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = player.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        if (gamePhase == 3) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (player.isSpy) NeonRose.copy(alpha = 0.2f) else NeonEmerald.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = if (player.isSpy) "SPY 🕵️" else "CITIZEN 🛡️",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (player.isSpy) NeonRose else NeonEmerald,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = if (player.clue.isNotBlank()) "\"${player.clue}\"" else "No clue given",
                                        fontSize = 12.sp,
                                        color = Color(0xFFCBD5E1),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }

                            if (gamePhase == 2 && !player.isUser) {
                                Button(
                                    onClick = { selectedVotePlayerId = player.id },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelectedToVote) NeonRose else Color(0xFF2A2248)
                                    ),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(if (isSelectedToVote) "Voted" else "Vote", fontSize = 11.sp)
                                }
                            }

                            if (gamePhase == 3) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E1738)
                                ) {
                                    Text(
                                        text = "${player.votesReceived} votes",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonAmber,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (gamePhase == 2) {
                    item {
                        Button(
                            onClick = { resolveVoting() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("cast_vote_confirm_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonRose),
                            enabled = selectedVotePlayerId != null
                        ) {
                            Text("Confirm Vote & Reveal Impostor 🕵️", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Game Result Box (Phase 3)
            if (gamePhase == 3) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF251945)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonAmber)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = winnerAnnouncement,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { resetGame() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Play Again")
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
