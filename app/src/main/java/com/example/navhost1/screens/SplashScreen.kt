package com.example.navhost1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// ── Paleta ────────────────────────────────────────────────────────────────────
private val PurpleStart = Color(0xFF7B2FBE)
private val PurpleEnd   = Color(0xFF9C6FD6)
private val IconBg      = Color(0xFFEDE7F6)
private val White       = Color.White

// ── Pantalla ──────────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(navController: NavController) {

    // Animación de escala del logo
    val scale = remember { Animatable(0.6f) }
    // Animación de opacidad del texto
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo aparece con rebote
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessLow
            )
        )
        // Texto aparece
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        // Espera 1.5 segundos y navega al login
        delay(1500)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PurpleStart, PurpleEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo con animación de escala ──────────────────────────────────
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(120.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(IconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧠", fontSize = 64.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Nombre de la app ──────────────────────────────────────────────
            Text(
                text      = "NeuraBloom",
                fontSize  = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color     = White,
                modifier  = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Subtítulo ─────────────────────────────────────────────────────
            Text(
                text      = "Tu bienestar emocional 💜",
                fontSize  = 14.sp,
                color     = White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // ── Indicador de carga ────────────────────────────────────────────
            val infiniteTransition = rememberInfiniteTransition(label = "dots")
            val dot1 = infiniteTransition.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(0)
                ), label = "d1"
            )
            val dot2 = infiniteTransition.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(200)
                ), label = "d2"
            )
            val dot3 = infiniteTransition.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(400)
                ), label = "d3"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(dot1.value, dot2.value, dot3.value).forEach { dotAlpha ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(dotAlpha)
                            .clip(RoundedCornerShape(50))
                            .background(White)
                    )
                }
            }
        }
    }
}