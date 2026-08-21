package com.example.navhost1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

// Paleta NeuraBloom Dark Premium
private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF090D16)

private val Danger = Color(0xFFEF4444)
private val DangerLight = Color(0xFFF87171)
private val DangerGlow = Color(0xFF991B1B)

private val CardColor = Color(0xFF1E293B).copy(alpha = 0.85f)
private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)

private data class LogoutParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

@Composable
fun LogoutScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    // Partículas ambientales de fondo
    val particles = remember {
        List(18) {
            LogoutParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 1.5f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                speedY = Random.nextFloat() * 0.0008f + 0.0003f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundTop, BackgroundBottom)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Fondo de Partículas Ambientales
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                p.y -= p.speedY
                if (p.y < 0f) p.y = 1f

                drawCircle(
                    color = DangerLight.copy(alpha = p.alpha),
                    radius = p.radius.dp.toPx(),
                    center = Offset(p.x * width, p.y * height)
                )
            }
        }

        // Resplandor de Alerta Ambiental
        Box(
            modifier = Modifier
                .size(320.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(DangerGlow.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
        )

        // Tarjeta Diálogo Modal Glassmorphism
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            color = CardColor,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.1f)
            ),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de Salida con Resplandor
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .shadow(20.dp, CircleShape, spotColor = Danger)
                        .background(
                            Brush.linearGradient(
                                listOf(Danger, DangerLight)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Cerrar sesión",
                    color = WhiteSoft,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "¿Estás seguro que deseas salir de tu cuenta en NeuraBloom?",
                    color = GrayText,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.15f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = WhiteSoft
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = WhiteSoft
                        )
                    }

                    Button(
                        onClick = {
                            // Cierre de sesión real en Firebase Auth
                            auth.signOut()
                            navController.navigate("login") {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Danger
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            text = "Salir",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}