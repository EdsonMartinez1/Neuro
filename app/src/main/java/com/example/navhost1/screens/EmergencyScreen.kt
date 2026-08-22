package com.example.navhost1.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import kotlin.random.Random

private val BackgroundTop = Color(0xFF2D0707)
private val BackgroundBottom = Color(0xFF5B1111)

private val EmergencyRed = Color(0xFFEF4444)
private val EmergencyLight = Color(0xFFF87171)

private val WhiteSoft = Color(0xFFF8FAFC)
private val CardColor = Color(0xFFFFFFFF)

private class EmergencySpark(
    var x: Float = Random.nextFloat(),
    var y: Float = Random.nextFloat(),
    val size: Float = Random.nextFloat() * 2.5f + 1f,
    val speedY: Float = Random.nextFloat() * 0.0006f + 0.0002f,
    val isRed: Boolean = Random.nextBoolean()
) {
    fun update() {
        y -= speedY
        if (y < -0.05f) {
            y = 1.05f
            x = Random.nextFloat()
        }
    }
}

@Composable
fun EmergencyScreen(navController: NavController) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Animación infinita para el pulso de las ondas de emergencia
    val infiniteTransition = rememberInfiniteTransition(label = "emergencyBeacon")

    // Onda 1
    val wave1Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    // Onda 2 (Desfasada)
    val wave2Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, delayMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    // Respira del centro
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beaconPulse"
    )

    // Lista de chispas/puntos de luz flotantes
    val sparks = remember { List(18) { EmergencySpark() } }

    // Loop para refrescar la subida de los destellos
    LaunchedEffect(wave1Progress) {
        sparks.forEach { it.update() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BackgroundTop, BackgroundBottom)
                )
            )
    ) {
        // Canvas de Animación de Ondas y Destellos de Emergencia
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height * 0.28f // Alineado visualmente cerca del icono central de alerta
            val maxRadius = size.width * 0.85f

            // 1. Dibujar Ondas Expansivas de Alerta
            listOf(wave1Progress, wave2Progress).forEach { progress ->
                val radius = maxRadius * progress
                val alpha = (1f - progress).coerceIn(0f, 1f) * 0.45f

                drawCircle(
                    color = EmergencyLight.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = (3.dp * (1f - progress * 0.5f)).toPx())
                )
            }

            // 2. Halo / Resplandor de baliza central
            drawCircle(
                color = EmergencyRed.copy(alpha = 0.18f),
                radius = (140.dp * beaconPulse).toPx(),
                center = Offset(centerX, centerY)
            )

            // 3. Dibujar Chispas / Destellos flotantes
            sparks.forEach { spark ->
                val px = spark.x * size.width
                val py = spark.y * size.height
                val color = if (spark.isRed) EmergencyLight.copy(alpha = 0.4f) else WhiteSoft.copy(alpha = 0.3f)

                drawCircle(
                    color = color,
                    radius = spark.size.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = WhiteSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Icono con resplandor central de la baliza
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                EmergencyLight,
                                EmergencyRed
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.emergency_titulo),
                color = WhiteSoft,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.emergency_subtitulo),
                color = WhiteSoft.copy(alpha = 0.88f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            EmergencyButton(
                label = stringResource(R.string.emergency_linea_crisis),
                icon = Icons.Default.Call,
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:5552598121")
                    }
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmergencyButton(
                label = stringResource(R.string.emergency_emergencia),
                icon = Icons.Default.LocalHospital,
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:911")
                    }
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmergencyButton(
                label = stringResource(R.string.emergency_ayuda_profesional),
                icon = Icons.Default.WarningAmber,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(
                            "https://simisae.com.mx/psicologos-en-linea"
                        )
                    }
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No estás solo",
                        color = WhiteSoft,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Buscar ayuda es un acto de valentía. Hay personas listas para escucharte y ayudarte.",
                        color = WhiteSoft.copy(alpha = 0.82f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EmergencyButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CardColor,
            contentColor = EmergencyRed
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        EmergencyRed.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmergencyRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ">",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}