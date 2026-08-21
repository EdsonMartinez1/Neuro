package com.example.navhost1.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

// Paleta NeuraBloom Dark Premium
private val DarkBackgroundTop = Color(0xFF0F172A)
private val DarkBackgroundBottom = Color(0xFF090D16)

private val CyanPrimary = Color(0xFF06B6D4)
private val CyanLight = Color(0xFF22D3EE)
private val CyanAccent = Color(0xFFA5F3FC)
private val PurpleGlow = Color(0xFF8B5CF6)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)
private val CardBackground = Color(0xFF1E293B).copy(alpha = 0.65f)

// Configuración de Técnicas de Respiración
data class BreathingTechnique(
    val name: String,
    val description: String,
    val inhaleMs: Long,
    val holdMs: Long,
    val exhaleMs: Long,
    val holdAfterExhaleMs: Long = 0L
)

private val techniques = listOf(
    BreathingTechnique("Calma Profunda (4-7-8)", "Ideal para reducir la ansiedad e inducir al sueño", 4000, 7000, 8000),
    BreathingTechnique("Respiración en Caja (4-4-4-4)", "Mejora el enfoque mental y reduce el estrés", 4000, 4000, 4000, 4000),
    BreathingTechnique("Relajación Rápida (4-6)", "Restablece el ritmo cardíaco de forma ágil", 4000, 0, 6000)
)

private data class Particle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

@Composable
fun BreathingScreen(navController: NavController) {
    val context = LocalContext.current

    fun vibrar(duracion: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(
                VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION")
            vibrator.vibrate(duracion)
        }
    }

    var selectedTechnique by remember { mutableStateOf(techniques[0]) }
    var sesionIniciada by remember { mutableStateOf(false) }
    var sesionCompletada by remember { mutableStateOf(false) }
    var fase by remember { mutableStateOf("PREPARADO") }

    // Partículas de luz ambientales en movimiento
    val particles = remember {
        List(25) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 2f,
                alpha = Random.nextFloat() * 0.5f + 0.2f,
                speedY = Random.nextFloat() * 0.001f + 0.0005f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particlesAnimation")
    val particleAnimationStep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "particleStep"
    )

    // Animación de escala del núcleo según la fase
    val escalaObjetivo = when (fase) {
        "INHALA" -> 1.32f
        "MANTÉN" -> 1.32f
        "EXHALA" -> 0.88f
        "DESCANSA" -> 0.88f
        else -> 0.95f
    }

    val scale by animateFloatAsState(
        targetValue = escalaObjetivo,
        animationSpec = tween(
            durationMillis = when (fase) {
                "INHALA" -> selectedTechnique.inhaleMs.toInt()
                "EXHALA" -> selectedTechnique.exhaleMs.toInt()
                else -> 500
            },
            easing = FastOutSlowInEasing
        ),
        label = "breathingScale"
    )

    val activeColor by animateColorAsState(
        targetValue = when (fase) {
            "INHALA" -> CyanLight
            "MANTÉN" -> PurpleGlow
            "EXHALA" -> CyanPrimary
            "DESCANSA" -> Color(0xFF3B82F6)
            else -> CyanPrimary
        },
        animationSpec = tween(durationMillis = 1000),
        label = "phaseColor"
    )

    // Ciclo dinámico de Respiración
    LaunchedEffect(sesionIniciada, selectedTechnique) {
        if (sesionIniciada) {
            repeat(3) {
                fase = "INHALA"
                vibrar(120)
                delay(selectedTechnique.inhaleMs)

                if (selectedTechnique.holdMs > 0) {
                    fase = "MANTÉN"
                    vibrar(60)
                    delay(selectedTechnique.holdMs)
                }

                fase = "EXHALA"
                vibrar(120)
                delay(selectedTechnique.exhaleMs)

                if (selectedTechnique.holdAfterExhaleMs > 0) {
                    fase = "DESCANSA"
                    vibrar(60)
                    delay(selectedTechnique.holdAfterExhaleMs)
                }
            }

            fase = "COMPLETADO"
            sesionCompletada = true
            sesionIniciada = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackgroundTop, DarkBackgroundBottom)
                )
            )
    ) {
        // Fondo de Partículas Ambientales
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                p.y -= p.speedY
                if (p.y < 0f) p.y = 1f

                drawCircle(
                    color = CyanAccent.copy(alpha = p.alpha),
                    radius = p.radius.dp.toPx(),
                    center = Offset(p.x * width, p.y * height)
                )
            }
        }

        // Resplandores ambientales
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-80).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleGlow.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = WhiteSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Respiración Sensorial",
                color = WhiteSoft,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = selectedTechnique.description,
                color = GrayText,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de Técnicas (Chips)
            if (!sesionIniciada) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    techniques.forEach { technique ->
                        val isSelected = technique == selectedTechnique
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTechnique = technique },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) CyanPrimary.copy(alpha = 0.2f) else CardBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyanLight else Color.White.copy(alpha = 0.08f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = technique.name.substringBefore(" "),
                                    color = if (isSelected) CyanLight else GrayText,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Núcleo de Respiración Sensorial Animado
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.scale(scale)
            ) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .alpha(0.2f)
                        .clip(CircleShape)
                        .background(activeColor)
                )

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .alpha(0.4f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(activeColor, activeColor.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CyanAccent, activeColor)
                            )
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = null,
                        tint = DarkBackgroundTop,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Fase Actual
            Text(
                text = fase,
                color = WhiteSoft,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (fase) {
                    "INHALA" -> "Siente cómo te llenas de calma..."
                    "MANTÉN" -> "Sostén el aire suavemente..."
                    "EXHALA" -> "Libera la tensión lentamente..."
                    "DESCANSA" -> "Pausa un segundo antes de volver a inhalar..."
                    "COMPLETADO" -> "¡Excelente sesión! Has relajado tu cuerpo."
                    else -> "Presiona iniciar para comenzar la experiencia"
                },
                color = GrayText,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Estado al Completar
            if (sesionCompletada) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = CardBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyanLight,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sesión Finalizada",
                                color = WhiteSoft,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tu ritmo cardíaco y mente están en armonía.",
                                color = GrayText,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Botón Principal
            Button(
                onClick = {
                    if (sesionCompletada) {
                        sesionCompletada = false
                        sesionIniciada = true
                        fase = "INHALA"
                    } else if (!sesionIniciada) {
                        sesionIniciada = true
                    }
                },
                enabled = !sesionIniciada,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    disabledContainerColor = CyanPrimary.copy(alpha = 0.4f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = when {
                        sesionIniciada -> "En curso..."
                        sesionCompletada -> "Comenzar otra sesión"
                        else -> "Comenzar sesión"
                    },
                    color = if (sesionIniciada) WhiteSoft.copy(alpha = 0.7f) else DarkBackgroundTop,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}