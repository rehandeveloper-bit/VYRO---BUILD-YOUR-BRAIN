package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    val xRatio: Float,
    val ySpeed: Float,
    val xDrift: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }

    val colors = listOf(
        Color(0xFF8B5CF6), // Purple
        Color(0xFF06B6D4), // Cyan
        Color(0xFFF59E0B), // Amber
        Color(0xFFF43F5E), // Rose
        Color(0xFF10B981), // Emerald
        Color(0xFFFFDD00)  // Gold
    )

    val particles = remember {
        List(60) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                ySpeed = Random.nextFloat() * 600f + 400f,
                xDrift = (Random.nextFloat() - 0.5f) * 150f,
                color = colors.random(),
                size = Random.nextFloat() * 16f + 8f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val currentP = progress.value
        particles.forEach { p ->
            val y = currentP * (size.height + 100f) * (p.ySpeed / 600f)
            val x = (p.xRatio * size.width) + (p.xDrift * currentP)
            val alpha = (1f - (currentP * 0.8f)).coerceIn(0f, 1f)

            rotate(degrees = p.rotationSpeed * currentP, pivot = Offset(x, y)) {
                if (p.isCircle) {
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.size / 2,
                        center = Offset(x, y)
                    )
                } else {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(x - p.size / 2, y - p.size / 4),
                        size = androidx.compose.ui.geometry.Size(p.size, p.size / 2)
                    )
                }
            }
        }
    }
}
