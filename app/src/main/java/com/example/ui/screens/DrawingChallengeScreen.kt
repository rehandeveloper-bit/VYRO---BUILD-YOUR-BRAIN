package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChallengeEntity
import com.example.data.local.FriendEntity
import com.example.data.remote.DrawingEvaluationResult
import com.example.ui.components.ConfettiEffect
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import kotlinx.coroutines.delay

data class DrawingStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun DrawingChallengeScreen(
    challenge: ChallengeEntity,
    friends: List<FriendEntity>,
    isEvaluating: Boolean,
    evaluationResult: DrawingEvaluationResult?,
    onSubmitDrawing: (strokeCount: Int, totalPoints: Int, secondsSpent: Int) -> Unit,
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

    val context = LocalContext.current
    var secondsLeft by remember { mutableIntStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var currentStrokePoints = remember { mutableStateListOf<Offset>() }
    val strokes = remember { mutableStateListOf<DrawingStroke>() }
    var selectedColor by remember { mutableStateOf(Color.White) }
    var selectedStrokeWidth by remember { mutableStateOf(8f) }
    var showFriendDuelDialog by remember { mutableStateOf(false) }

    val paletteColors = listOf(
        Color.White,
        NeonPurple,
        NeonCyan,
        NeonAmber,
        NeonRose,
        NeonEmerald
    )

    // Countdown Timer Effect
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (secondsLeft > 0 && isTimerRunning) {
                delay(1000)
                secondsLeft -= 1
                if (secondsLeft == 5) {
                    triggerVibration(context)
                }
            }
            if (secondsLeft == 0 && isTimerRunning) {
                isTimerRunning = false
                val totalPointsCount = strokes.sumOf { it.points.size }
                onSubmitDrawing(strokes.size, totalPointsCount, 30)
            }
        }
    }

    val timerProgress by animateFloatAsState(
        targetValue = secondsLeft / 30f,
        label = "timerProgress"
    )

    val timerColor by animateColorAsState(
        targetValue = when {
            secondsLeft > 15 -> NeonEmerald
            secondsLeft > 6 -> NeonAmber
            else -> NeonRose
        },
        label = "timerColor"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar
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
                    modifier = Modifier.testTag("drawing_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "30-Sec AI Duel",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Timer Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = timerColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, timerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = timerColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${secondsLeft}s",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = timerColor
                        )
                    }
                }
            }

            // Prompt Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DRAW THIS NOW:",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = NeonPurple,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = challenge.targetAnswer,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        text = challenge.promptDetail,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Timer Progress Bar
            LinearProgressIndicator(
                progress = { timerProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = timerColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Drawing Canvas Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F0B1E))
                    .border(2.dp, timerColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .pointerInput(isTimerRunning) {
                        if (!isTimerRunning) return@pointerInput
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStrokePoints.clear()
                                currentStrokePoints.add(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentStrokePoints.add(change.position)
                            },
                            onDragEnd = {
                                if (currentStrokePoints.isNotEmpty()) {
                                    strokes.add(
                                        DrawingStroke(
                                            points = currentStrokePoints.toList(),
                                            color = selectedColor,
                                            strokeWidth = selectedStrokeWidth
                                        )
                                    )
                                    currentStrokePoints.clear()
                                }
                            }
                        )
                    }
                    .testTag("drawing_canvas_box")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw completed strokes
                    strokes.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                moveTo(stroke.points.first().x, stroke.points.first().y)
                                for (i in 1 until stroke.points.size) {
                                    lineTo(stroke.points[i].x, stroke.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = stroke.color,
                                style = Stroke(
                                    width = stroke.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        } else if (stroke.points.isNotEmpty()) {
                            drawCircle(
                                color = stroke.color,
                                radius = stroke.strokeWidth / 2,
                                center = stroke.points.first()
                            )
                        }
                    }

                    // Draw active drag stroke
                    if (currentStrokePoints.size > 1) {
                        val path = Path().apply {
                            moveTo(currentStrokePoints.first().x, currentStrokePoints.first().y)
                            for (i in 1 until currentStrokePoints.size) {
                                lineTo(currentStrokePoints[i].x, currentStrokePoints[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = selectedColor,
                            style = Stroke(
                                width = selectedStrokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // If user hasn't drawn anything yet, show subtle watermark
                if (strokes.isEmpty() && currentStrokePoints.isEmpty()) {
                    Text(
                        text = "Touch & drag to doodle $secondsLeft seconds remaining!",
                        color = Color.White.copy(alpha = 0.25f),
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Canvas Toolbar (Palette & Undo/Clear)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Color Palette
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    paletteColors.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(
                                    width = if (selectedColor == col) 2.5.dp else 0.dp,
                                    color = if (selectedColor == col) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = col }
                        )
                    }
                }

                // Undo & Clear buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex)
                        },
                        enabled = strokes.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (strokes.isNotEmpty()) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = {
                            strokes.clear()
                            currentStrokePoints.clear()
                        },
                        enabled = strokes.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Canvas",
                            tint = if (strokes.isNotEmpty()) NeonRose else Color.Gray
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (isTimerRunning) {
                        isTimerRunning = false
                        val totalPoints = strokes.sumOf { it.points.size }
                        val timeSpent = (30 - secondsLeft).coerceAtLeast(1)
                        onSubmitDrawing(strokes.size, totalPoints, timeSpent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_drawing_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                enabled = isTimerRunning && strokes.isNotEmpty()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Submit to AI Referee (${strokes.size} Strokes)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Loading Overlay while AI evaluates
        if (isEvaluating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = NeonPurple, modifier = Modifier.size(54.dp))
                    Text(
                        text = "AI Referee Is Scoring Your Doodle...",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Analyzing stroke dynamics, proportions & likeness",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // AI Score Result Dialog
        evaluationResult?.let { res ->
            ConfettiEffect()

            androidx.compose.material3.AlertDialog(
                onDismissRequest = { /* force action */ },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color(0xFF1E1738),
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.2f))
                                .border(2.dp, NeonPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${res.score}",
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                color = NeonPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = res.titleBadge,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = NeonAmber,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "\"${res.commentary}\"",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonCyan.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "AI Vision Guess: ${res.aiRecognitionGuess}",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showFriendDuelDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("challenge_friend_dialog_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.SportsKabaddi, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Challenge a Friend with this Score!")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onShareScore(res.score) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("share_score_dialog_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share")
                            }

                            Button(
                                onClick = {
                                    onClearResult()
                                    onBack()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text("Continue")
                            }
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
                title = { Text("Choose a Friend to Duel") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Send your score (${evaluationResult.score} pts) to beat:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                                    }
                                    .testTag("select_friend_${friend.id}"),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(friend.avatarColor)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = friend.avatarInitials,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = friend.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Challenge",
                                        tint = NeonPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    OutlinedButton(onClick = { showFriendDuelDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun triggerVibration(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(120)
        }
    } catch (_: Exception) {}
}
