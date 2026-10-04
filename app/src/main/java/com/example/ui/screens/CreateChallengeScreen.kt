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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose

@Composable
fun CreateChallengeScreen(
    isGenerating: Boolean,
    onGenerate: (category: String, topic: String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedCategory by remember { mutableStateOf("DRAWING_30S") }
    var customTopicInput by remember { mutableStateOf("") }

    val drawingIdeas = listOf("Cyberpunk Animal", "Alien Food Stall", "Futuristic Vehicle", "Neon Samurai", "Mythical Creature")
    val songIdeas = listOf("Bollywood 90s Hit", "Synthwave 80s", "Global Pop Banger", "Rock Anthem", "Hip-Hop Classic")

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
                IconButton(onClick = onBack, modifier = Modifier.testTag("create_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "AI Challenge Studio",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Craft infinite custom challenges with Gemini",
                        fontSize = 12.sp,
                        color = NeonPurple
                    )
                }
            }
        }

        // Select Category Mode
        item {
            Text(
                text = "1. Choose Challenge Mode",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Drawing Mode
                CategorySelectCard(
                    title = "30-Sec Draw",
                    subtitle = "Sketch & Referee",
                    icon = Icons.Default.Brush,
                    isSelected = selectedCategory == "DRAWING_30S",
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedCategory = "DRAWING_30S" },
                    testTag = "select_mode_draw"
                )

                // Song Guess Mode
                CategorySelectCard(
                    title = "Guess Song",
                    subtitle = "Riddles & Beats",
                    icon = Icons.Default.MusicNote,
                    isSelected = selectedCategory == "GUESS_SONG",
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedCategory = "GUESS_SONG" },
                    testTag = "select_mode_song"
                )

                // AI Trivia Mode
                CategorySelectCard(
                    title = "AI Sprint",
                    subtitle = "Logic Rush",
                    icon = Icons.Default.Psychology,
                    isSelected = selectedCategory == "AI_TRIVIA",
                    accentColor = NeonAmber,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedCategory = "AI_TRIVIA" },
                    testTag = "select_mode_trivia"
                )
            }
        }

        // Custom Topic Input
        item {
            Text(
                text = "2. Customize Topic or Theme (Optional)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customTopicInput,
                onValueChange = { customTopicInput = it },
                label = { Text("Theme or Vibe") },
                placeholder = {
                    Text(
                        if (selectedCategory == "DRAWING_30S") "e.g. Robot Chef making pancakes"
                        else "e.g. 90s Bollywood or Synthwave"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_topic_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Quick Suggestions
        item {
            Text(
                text = "💡 Quick Inspiration:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            val currentIdeas = if (selectedCategory == "DRAWING_30S") drawingIdeas else songIdeas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentIdeas.take(3).forEach { idea ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { customTopicInput = idea }
                    ) {
                        Text(
                            text = idea,
                            fontSize = 11.sp,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Generate Action Button
        item {
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onGenerate(
                        selectedCategory,
                        customTopicInput.ifBlank { null }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_with_ai_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                enabled = !isGenerating
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Gemini is Generating Challenge...")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Generate with Gemini AI",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // AI Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Powered by Google Gemini 3.5 Flash. Each challenge includes AI refereeing, scoring criteria, and friend duel links.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySelectCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, accentColor) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) accentColor else Color.DarkGray.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
