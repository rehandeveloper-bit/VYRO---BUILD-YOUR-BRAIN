package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoScale = remember { Animatable(0.4f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val kingAlpha = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0f) }

    // Pulsing neon halo in background
    val infiniteTransition = rememberInfiniteTransition(label = "halo_pulse")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloScale"
    )

    LaunchedEffect(Unit) {
        // 1. Logo fades in and zooms into view
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )
        // 2. Tagline and brand fade in
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
        // 3. Made by line reveals
        kingAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
        // 4. Progress bar completes
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = LinearEasing)
        )
        delay(400)
        onAnimationFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF26124A),
                        Color(0xFF0F0B1E),
                        Color(0xFF06040B)
                    )
                )
            )
            .clickable { onAnimationFinished() }
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Neon Background Ring
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(haloScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.35f),
                            NeonCyan.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // App Logo Image with Scale & Alpha animation
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF0D0B18))
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple, NeonRose)),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .testTag("splash_logo_image"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vyro_logo),
                    contentDescription = "VYRO App Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Subtitle & Brand Tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VY",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "RO",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonPurple,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "— BUILD YOUR BRAIN —",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 4.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Exact Requested Line: "made by :-KING" with glowing styling
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E1538).copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(listOf(NeonAmber, NeonPurple, NeonAmber))
                ),
                modifier = Modifier
                    .alpha(kingAlpha.value)
                    .testTag("made_by_king_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👑",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "made by :-KING",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonAmber,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Loading Progress Bar
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .alpha(textAlpha.value)
            ) {
                LinearProgressIndicator(
                    progress = { progressAnim.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = NeonPurple,
                    trackColor = Color(0xFF221C3E)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tap anywhere to skip",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
    }
}
