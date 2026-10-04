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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.ui.components.ConfettiEffect
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose

@Composable
fun ChatScreen(
    conversationId: String,
    conversationTitle: String,
    messages: List<ChatMessageEntity>,
    onSendMessage: (text: String, celebrationType: String, score: Int?) -> Unit,
    onPlayChallengeFromChat: (challengeId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var inputText by remember { mutableStateOf("") }
    var showConfetti by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = conversationTitle,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (conversationId == "GLOBAL_SQUAD") "Squad Chat • Discuss & Celebrate" else "Direct Duel Chat",
                            fontSize = 12.sp,
                            color = NeonPurple
                        )
                    }
                }

                // Win Celebration Trigger Button
                Button(
                    onClick = {
                        showConfetti = true
                        onSendMessage("🎉 Celebrating an epic win on today's AI challenge! Let's GOOO!", "WIN_TROPHY", 95)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                    modifier = Modifier.testTag("celebrate_win_chat_button")
                ) {
                    Icon(Icons.Default.Celebration, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Celebrate", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(
                        message = msg,
                        onPlayChallenge = {
                            msg.challengeRefId?.let { id -> onPlayChallengeFromChat(id) }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Reaction Emojis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("🔥 Beast Mode!", "🏆 GG WP!", "👏 Insane score!", "⚡ Rematch?").forEach { phrase ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSendMessage(phrase, "NONE", null)
                            }
                    ) {
                        Text(
                            text = phrase,
                            fontSize = 11.sp,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Text Input Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Message squad / friend...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText, "NONE", null)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonPurple)
                        .testTag("send_chat_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (showConfetti) {
            ConfettiEffect(
                onFinished = { showConfetti = false }
            )
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessageEntity,
    onPlayChallenge: () -> Unit
) {
    val isUser = message.isFromUser

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Text(
                text = message.senderName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }

        when (message.celebrationType) {
            "WIN_TROPHY" -> {
                // Trophy Celebration Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.widthIn(max = 290.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF381E72), Color(0xFF633806)))
                            )
                            .border(1.5.dp, NeonAmber, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "VICTORY CELEBRATION 🏆",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = NeonAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = message.text,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            if (message.challengeScore != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Score: ${message.challengeScore} pts",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonEmerald
                                )
                            }
                        }
                    }
                }
            }
            "STREAK_FIRE" -> {
                // Streak Flame Banner Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1705)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = message.text,
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            "CHALLENGE_INVITE" -> {
                // Challenge Invite Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1838)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SportsKabaddi, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CHALLENGE DUEL ⚔️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = NeonPurple
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = message.text,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onPlayChallenge,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Play Duel Now", fontSize = 12.sp)
                        }
                    }
                }
            }
            else -> {
                // Standard text bubble
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    color = if (isUser) NeonPurple else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
