package com.example.navhost1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import kotlinx.coroutines.delay
import kotlin.random.Random

// ── Paleta NeuraBloom Premium ───────────────────────────────────────────────
private val DeepSpace      = Color(0xFF070A14)
private val MidnightPurple = Color(0xFF130924)
private val ElectricPurple = Color(0xFF7C3AED)
private val NeonMagenta    = Color(0xFFC084FC)
private val IconBg          = Color(0xFFF3E8FF)
private val White           = Color.White

private data class SplashParticle(
    val initialX: Float,
    val initialY: Float,
    val radius: Float,
    val speedY: Float,
    val alpha: Float
)

@Composable
fun SplashScreen(
    navController: NavController,
    destinationRoute: String = "login"
) {
    var startAnimation by remember { mutableStateOf(false) }
    var isExiting by remember { mutableStateOf(false) } // Controla el Fade-Out final

    // ── 1. Fondo vivo ────────────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "livingBackground")

    val bgPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgPhase"
    )

    // ── 2. Transición global de salida (Fade-Out hacia el Login) ────────────
    val exitAlpha by animateFloatAsState(
        targetValue = if (isExiting) 0f else 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "exitAlpha"
    )

    // ── 3. Ondas de energía ──────────────────────────────────────────────────
    val wave1Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wave1Scale"
    )
    val wave1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wave1Alpha"
    )

    // ── 4. Partículas flotantes ───────────────────────────────────────────────
    val particles = remember {
        List(25) {
            SplashParticle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat(),
                radius = Random.nextFloat() * 4f + 2f,
                speedY = Random.nextFloat() * 0.3f + 0.1f,
                alpha = Random.nextFloat() * 0.6f + 0.2f
            )
        }
    }

    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "particleTime"
    )

    // ── 5. Logo & Entradas ───────────────────────────────────────────────────
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "logoAlpha"
    )

    // Temporizador con secuencia de salida fluida
    // Temporizador con secuencia de salida fluida
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200) // Tiempo que dura la animación y texto visibles
        isExiting = true // Inicia el Fade-Out (se va desvaneciendo a transparente)
        delay(500) // Tiempo para permitir que se complete la opacidad a 0
        navController.navigate(destinationRoute) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(exitAlpha), // Aplica la opacidad de salida a toda la pantalla
        contentAlignment = Alignment.Center
    ) {
        val currentTopColor = Color(
            red = DeepSpace.red + (MidnightPurple.red - DeepSpace.red) * bgPhase,
            green = DeepSpace.green + (MidnightPurple.green - DeepSpace.green) * bgPhase,
            blue = DeepSpace.blue + (MidnightPurple.blue - DeepSpace.blue) * bgPhase
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(currentTopColor, DeepSpace)))
        )

        // Partículas Flotantes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                val currentY = (p.initialY * height - (particleTime * p.speedY * 50)) % height
                val actualY = if (currentY < 0) height + currentY else currentY
                val actualX = p.initialX * width

                drawCircle(
                    color = NeonMagenta.copy(alpha = p.alpha * logoAlpha),
                    radius = p.radius.dp.toPx(),
                    center = Offset(actualX, actualY)
                )
            }
        }

        // Ondas de luz
        Box(
            modifier = Modifier
                .scale(wave1Scale)
                .alpha(wave1Alpha * logoAlpha)
                .size(160.dp)
                .clip(CircleShape)
                .background(ElectricPurple)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo NBI Animado
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .size(125.dp)
                    .shadow(32.dp, RoundedCornerShape(32.dp), spotColor = ElectricPurple)
                    .clip(RoundedCornerShape(32.dp))
                    .background(IconBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nbi),
                    contentDescription = "NeuraBloom Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Texto animado estilo intro
            val appName = "NeuraBloom"
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                appName.forEachIndexed { index, char ->
                    val charOffset by animateFloatAsState(
                        targetValue = if (startAnimation) 0f else 45f,
                        animationSpec = tween(
                            durationMillis = 550,
                            delayMillis = 350 + (index * 65),
                            easing = FastOutSlowInEasing
                        ), label = "charOffset_$index"
                    )

                    val charAlpha by animateFloatAsState(
                        targetValue = if (startAnimation) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 350 + (index * 65)
                        ), label = "charAlpha_$index"
                    )

                    Text(
                        text = char.toString(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = White,
                        modifier = Modifier
                            .graphicsLayer { translationY = charOffset }
                            .alpha(charAlpha)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val subtitleAlpha by animateFloatAsState(
                targetValue = if (startAnimation) 0.85f else 0f,
                animationSpec = tween(durationMillis = 700, delayMillis = 1100),
                label = "subtitleAlpha"
            )

            Text(
                text = "Tu bienestar emocional 💜",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = NeonMagenta,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha)
            )
        }
    }
}