@file:JvmName("AnimatedTreeComponent")

package com.example.navhost1.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private val ColorFeliz = Color(0xFF10B981)
private val ColorTranquilo = Color(0xFF06B6D4)
private val ColorAnsioso = Color(0xFFF59E0B)
private val ColorCansado = Color(0xFF6366F1)
private val ColorTriste = Color(0xFF3B82F6)
private val ColorEnojado = Color(0xFFEF4444)
private val ColorTronco = Color(0xFF8D4925)
private val ColorTroncoOscuro = Color(0xFF5C2D13)

private data class TreeParticle(
    var xOffset: Float,
    var yOffset: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float,
    val speedX: Float
)

@Composable
fun AnimatedTree(
    nivel: Int,
    emocion: String,
    modifier: Modifier = Modifier
) {
    // 1. Color de la emoción con transición suave
    val targetLeafColor = when (emocion) {
        "😊 Feliz", "FELIZ" -> ColorFeliz
        "😌 Tranquilo", "TRANQUILO" -> ColorTranquilo
        "😟 Ansioso", "ANSIOSO" -> ColorAnsioso
        "😴 Cansado", "CANSADO" -> ColorCansado
        "😔 Triste", "TRISTE" -> ColorTriste
        "😡 Enojado", "ENOJADO" -> ColorEnojado
        else -> ColorTranquilo
    }

    val leafColor by animateColorAsState(
        targetValue = targetLeafColor,
        animationSpec = tween(durationMillis = 1000),
        label = "leafColor"
    )

    // 2. Escala según el nivel (animación elástica)
    val targetScale = (0.55f + (nivel.coerceIn(1, 5) * 0.10f)).coerceIn(0.55f, 1.0f)
    val scaleFactor by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "treeScale"
    )

    // 3. Animaciones en bucle
    val infiniteTransition = rememberInfiniteTransition(label = "treeAnimations")

    val windSway by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "windSway"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Partículas climáticas/emocionales
    val particles = remember {
        List(22) {
            TreeParticle(
                xOffset = Random.nextFloat() * 200f - 100f,
                yOffset = Random.nextFloat() * 160f - 80f,
                radius = Random.nextFloat() * 3.5f + 1.5f,
                alpha = Random.nextFloat() * 0.6f + 0.2f,
                speedY = Random.nextFloat() * 0.002f + 0.001f,
                speedX = Random.nextFloat() * 0.001f - 0.0005f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val baseY = height * 0.82f

        // Partículas en movimiento según clima emocional
        particles.forEach { p ->
            val isTriste = emocion.contains("Triste", ignoreCase = true)
            if (isTriste) {
                p.yOffset += p.speedY * height * 1.5f
                if (p.yOffset > 40f) p.yOffset = -120f
            } else {
                p.yOffset -= p.speedY * height
                p.xOffset += p.speedX * width
                if (p.yOffset < -140f) p.yOffset = 20f
            }

            drawCircle(
                color = if (isTriste) Color(0xFF60A5FA).copy(alpha = p.alpha) else leafColor.copy(alpha = p.alpha),
                radius = if (isTriste) 2.dp.toPx() else p.radius.dp.toPx(),
                center = Offset(centerX + p.xOffset + (windSway * 0.4f), baseY - 60f + p.yOffset)
            )
        }

        // Resplandor del fondo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(leafColor.copy(alpha = 0.3f), Color.Transparent),
                center = Offset(centerX + windSway, height * 0.4f),
                radius = width * 0.48f * scaleFactor
            ),
            center = Offset(centerX + windSway, height * 0.4f),
            radius = width * 0.48f * scaleFactor
        )

        // Suelo / Maceta
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF334155), Color(0xFF1E293B)),
                center = Offset(centerX, baseY + 12f),
                radius = width * 0.32f
            ),
            radius = width * 0.32f,
            center = Offset(centerX, baseY + 12f)
        )

        // Tronco con textura y ramas
        val trunkWidth = 20f * scaleFactor
        val trunkPath = Path().apply {
            moveTo(centerX - trunkWidth * 1.6f, baseY)
            quadraticTo(
                centerX - trunkWidth * 0.6f, baseY - 30f,
                centerX - trunkWidth + (windSway * 0.2f), height * 0.45f
            )
            lineTo(centerX + trunkWidth + (windSway * 0.2f), height * 0.45f)
            quadraticTo(
                centerX + trunkWidth * 0.6f, baseY - 30f,
                centerX + trunkWidth * 1.6f, baseY
            )
            close()
        }
        drawPath(path = trunkPath, color = ColorTronco)

        // Ramas laterales (Nivel 3+)
        if (nivel >= 3) {
            val leftBranch = Path().apply {
                moveTo(centerX - (trunkWidth * 0.5f), height * 0.55f)
                quadraticTo(
                    centerX - (60f * scaleFactor), height * 0.52f,
                    centerX - (80f * scaleFactor) + windSway, height * 0.46f
                )
            }
            drawPath(path = leftBranch, color = ColorTroncoOscuro, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f * scaleFactor))

            val rightBranch = Path().apply {
                moveTo(centerX + (trunkWidth * 0.5f), height * 0.52f)
                quadraticTo(
                    centerX + (60f * scaleFactor), height * 0.48f,
                    centerX + (75f * scaleFactor) + windSway, height * 0.42f
                )
            }
            drawPath(path = rightBranch, color = ColorTroncoOscuro, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f * scaleFactor))
        }

        // Si es Nivel 1 (Brote básico)
        if (nivel == 1) {
            drawCircle(
                color = leafColor,
                radius = 26f * scaleFactor * pulseScale,
                center = Offset(centerX + windSway, height * 0.45f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = 10f * scaleFactor,
                center = Offset(centerX + windSway - 5f, height * 0.45f - 5f)
            )
            return@Canvas
        }

        // Copa de Hojas Enriquecida con Nubes de Hojas Organizadas (Niveles 2+)
        val crownY = height * 0.4f
        val crownRadius = (50f + (nivel * 16f)) * scaleFactor * pulseScale

        // Hojas Fondo (Oscuras)
        drawCircle(
            color = leafColor.copy(alpha = 0.5f),
            radius = crownRadius * 0.75f,
            center = Offset(centerX - (40f * scaleFactor) + windSway, crownY + 20f)
        )
        drawCircle(
            color = leafColor.copy(alpha = 0.5f),
            radius = crownRadius * 0.75f,
            center = Offset(centerX + (40f * scaleFactor) + windSway, crownY + 20f)
        )

        // Hojas Centro
        drawCircle(
            color = leafColor.copy(alpha = 0.85f),
            radius = crownRadius * 0.88f,
            center = Offset(centerX - (22f * scaleFactor) + windSway, crownY - 10f)
        )
        drawCircle(
            color = leafColor.copy(alpha = 0.85f),
            radius = crownRadius * 0.88f,
            center = Offset(centerX + (22f * scaleFactor) + windSway, crownY - 10f)
        )

        // Copa Principal (Frente)
        drawCircle(
            color = leafColor,
            radius = crownRadius,
            center = Offset(centerX + windSway, crownY - (25f * scaleFactor))
        )

        // Frutos / Flores de XP en Niveles Altos (Nivel 4 y 5)
        if (nivel >= 4) {
            val fruitColor = if (emocion.contains("Feliz", ignoreCase = true)) Color(0xFFFBBF24) else Color(0xFFF43F5E)
            drawCircle(color = fruitColor, radius = 6f * scaleFactor, center = Offset(centerX + windSway - 30f, crownY - 20f))
            drawCircle(color = fruitColor, radius = 7f * scaleFactor, center = Offset(centerX + windSway + 25f, crownY - 35f))
            drawCircle(color = fruitColor, radius = 5f * scaleFactor, center = Offset(centerX + windSway - 10f, crownY - 50f))
            drawCircle(color = fruitColor, radius = 6f * scaleFactor, center = Offset(centerX + windSway + 35f, crownY + 10f))
        }

        // Destello de Luz Superior
        drawCircle(
            color = Color.White.copy(alpha = 0.35f),
            radius = crownRadius * 0.35f,
            center = Offset(centerX + windSway - (15f * scaleFactor), crownY - (40f * scaleFactor))
        )
    }
}