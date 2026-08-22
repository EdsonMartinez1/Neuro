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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

private val ColorFeliz = Color(0xFF10B981)
private val ColorTranquilo = Color(0xFF06B6D4)
private val ColorAnsioso = Color(0xFFF59E0B)
private val ColorCansado = Color(0xFF6366F1)
private val ColorTriste = Color(0xFF3B82F6)
private val ColorEnojado = Color(0xFFEF4444)

private val ColorTronco = Color(0xFF8D4925)
private val ColorTroncoOscuro = Color(0xFF5C2D13)
private val ColorSemilla = Color(0xFFD97706)

private data class TreeParticle(
    var xOffset: Float,
    var yOffset: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float,
    val speedX: Float,
    val rotation: Float = Random.nextFloat() * 360f
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

    // Escala según el nivel con animación de muelle
    val targetScale = (0.60f + (nivel.coerceIn(1, 5) * 0.09f)).coerceIn(0.60f, 1.0f)
    val scaleFactor by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "treeScale"
    )

    // Animaciones en bucle para físicas vivas
    val infiniteTransition = rememberInfiniteTransition(label = "treeAnimations")

    // Movimiento orgánico del viento
    val windSway by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "windSway"
    )

    // Respiración vital (movimiento continuo para simular vida)
    val lifeBreathing by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lifeBreathing"
    )

    // Caída rítmica de la gota de agua para Nivel 1
    val waterDropY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waterDropY"
    )

    // Partículas climáticas o pétalos flotantes
    val particles = remember {
        List(25) {
            TreeParticle(
                xOffset = Random.nextFloat() * 240f - 120f,
                yOffset = Random.nextFloat() * 200f - 100f,
                radius = Random.nextFloat() * 3.5f + 2f,
                alpha = Random.nextFloat() * 0.7f + 0.3f,
                speedY = Random.nextFloat() * 0.0025f + 0.001f,
                speedX = Random.nextFloat() * 0.0015f - 0.0007f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val baseY = height * 0.82f

        // --- RESPLANDOR EMOCIONAL DE FONDO ---
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(leafColor.copy(alpha = 0.28f), Color.Transparent),
                center = Offset(centerX, height * 0.45f),
                radius = width * 0.5f * scaleFactor
            ),
            center = Offset(centerX, height * 0.45f),
            radius = width * 0.5f * scaleFactor
        )

        // --- SUELO Y MACETA ORGANICA ---
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF334155), Color(0xFF1E293B)),
                center = Offset(centerX, baseY + 12f),
                radius = width * 0.34f
            ),
            radius = width * 0.34f,
            center = Offset(centerX, baseY + 12f)
        )

        val isTriste = emocion.contains("Triste", ignoreCase = true)

        // ===================================================================
        // NIVEL 1: SEMILLA VIVA RECIBIENDO AGUA
        // ===================================================================
        if (nivel == 1) {
            val seedY = baseY - 18f

            // Gota de agua nutriente
            val dropStartY = seedY - 140f
            val currentDropY = dropStartY + (seedY - dropStartY) * waterDropY

            if (waterDropY < 0.85f) {
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.85f),
                    radius = 5.dp.toPx(),
                    center = Offset(centerX, currentDropY)
                )
            } else {
                // Onda de nutrición al impactar
                val rippleRadius = (waterDropY - 0.85f) * 120f
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 1f - waterDropY),
                    radius = rippleRadius,
                    center = Offset(centerX, seedY),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Cuerpo de la Semilla Latiendo / VIVA
            val seedRadius = 14f * lifeBreathing
            drawCircle(
                color = ColorSemilla,
                radius = seedRadius,
                center = Offset(centerX, seedY)
            )

            // Brillo de energía emocional sobre la semilla
            drawCircle(
                color = leafColor.copy(alpha = 0.7f),
                radius = seedRadius * 0.6f,
                center = Offset(centerX - 3f, seedY - 3f)
            )

            // Raíces diminutas naciendo hacia la tierra
            val rootPath = Path().apply {
                moveTo(centerX, seedY + seedRadius)
                lineTo(centerX - 6f, seedY + seedRadius + 10f)
                moveTo(centerX, seedY + seedRadius)
                lineTo(centerX + 8f, seedY + seedRadius + 12f)
            }
            drawPath(path = rootPath, color = ColorTronco, style = Stroke(width = 2.5f))
            return@Canvas
        }

        // ===================================================================
        // NIVEL 2: BROTE CON 2 TALLOS Y PEQUEÑAS HOJAS
        // ===================================================================
        if (nivel == 2) {
            val stemBaseY = baseY - 5f
            val stemHeight = 60f * scaleFactor

            // Tallo principal naciente
            val stemPath = Path().apply {
                moveTo(centerX, stemBaseY)
                quadraticTo(
                    centerX + (windSway * 0.2f), stemBaseY - (stemHeight * 0.5f),
                    centerX, stemBaseY - stemHeight
                )
            }
            drawPath(path = stemPath, color = ColorTronco, style = Stroke(width = 5.dp.toPx()))

            // Brote 1 (Izquierdo) con Hoja
            val leftBranch = Path().apply {
                moveTo(centerX, stemBaseY - stemHeight)
                quadraticTo(
                    centerX - 18f, stemBaseY - stemHeight - 15f,
                    centerX - 28f + (windSway * 0.3f), stemBaseY - stemHeight - 28f
                )
            }
            drawPath(path = leftBranch, color = ColorTroncoOscuro, style = Stroke(width = 3.dp.toPx()))

            drawCircle(
                color = leafColor,
                radius = 12f * lifeBreathing,
                center = Offset(centerX - 28f + (windSway * 0.3f), stemBaseY - stemHeight - 28f)
            )

            // Brote 2 (Derecho) con Hoja
            val rightBranch = Path().apply {
                moveTo(centerX, stemBaseY - stemHeight)
                quadraticTo(
                    centerX + 18f, stemBaseY - stemHeight - 10f,
                    centerX + 26f + (windSway * 0.3f), stemBaseY - stemHeight - 22f
                )
            }
            drawPath(path = rightBranch, color = ColorTroncoOscuro, style = Stroke(width = 3.dp.toPx()))

            drawCircle(
                color = leafColor.copy(alpha = 0.9f),
                radius = 10f * lifeBreathing,
                center = Offset(centerX + 26f + (windSway * 0.3f), stemBaseY - stemHeight - 22f)
            )
            return@Canvas
        }

        // ===================================================================
        // NIVEL 3 EN ADELANTE: TRONCO CON TEXTURA Y FISICA ORGANICA
        // ===================================================================
        val trunkWidth = (if (nivel == 3) 14f else 20f) * scaleFactor
        val trunkTopY = height * 0.44f

        // Tronco realista estilizado
        val trunkPath = Path().apply {
            moveTo(centerX - trunkWidth * 1.5f, baseY)
            quadraticTo(
                centerX - trunkWidth * 0.7f, baseY - 40f,
                centerX - trunkWidth + (windSway * 0.15f), trunkTopY
            )
            lineTo(centerX + trunkWidth + (windSway * 0.15f), trunkTopY)
            quadraticTo(
                centerX + trunkWidth * 0.7f, baseY - 40f,
                centerX + trunkWidth * 1.5f, baseY
            )
            close()
        }
        drawPath(path = trunkPath, color = ColorTronco)

        // Detalle de vetas de madera en el tronco
        val barkLine = Path().apply {
            moveTo(centerX - 4f, baseY - 10f)
            quadraticTo(centerX - 2f, baseY - 35f, centerX - 6f + (windSway * 0.1f), trunkTopY + 20f)
        }
        drawPath(path = barkLine, color = ColorTroncoOscuro, style = Stroke(width = 2.dp.toPx()))

        // Ramificaciones animadas
        val leftBranch = Path().apply {
            moveTo(centerX - (trunkWidth * 0.5f), height * 0.56f)
            quadraticTo(
                centerX - (50f * scaleFactor), height * 0.51f,
                centerX - (70f * scaleFactor) + (windSway * 0.4f), height * 0.45f
            )
        }
        drawPath(path = leftBranch, color = ColorTroncoOscuro, style = Stroke(width = 4.5f * scaleFactor))

        val rightBranch = Path().apply {
            moveTo(centerX + (trunkWidth * 0.5f), height * 0.53f)
            quadraticTo(
                centerX + (50f * scaleFactor), height * 0.49f,
                centerX + (65f * scaleFactor) + (windSway * 0.4f), height * 0.42f
            )
        }
        drawPath(path = rightBranch, color = ColorTroncoOscuro, style = Stroke(width = 4f * scaleFactor))

        // Copa de Hojas en Nivel 3 (Física Discontinua con Senos)
        val crownY = height * 0.38f
        val crownRadius = (45f + (nivel * 14f)) * scaleFactor * lifeBreathing

        // Follaje multicapa
        drawCircle(
            color = leafColor.copy(alpha = 0.45f),
            radius = crownRadius * 0.8f,
            center = Offset(centerX - (36f * scaleFactor) + (windSway * 0.5f), crownY + 15f)
        )
        drawCircle(
            color = leafColor.copy(alpha = 0.45f),
            radius = crownRadius * 0.8f,
            center = Offset(centerX + (36f * scaleFactor) + (windSway * 0.5f), crownY + 15f)
        )
        drawCircle(
            color = leafColor.copy(alpha = 0.85f),
            radius = crownRadius * 0.9f,
            center = Offset(centerX - (20f * scaleFactor) + (windSway * 0.3f), crownY - 10f)
        )
        drawCircle(
            color = leafColor,
            radius = crownRadius,
            center = Offset(centerX + (windSway * 0.2f), crownY - (20f * scaleFactor))
        )

        // ===================================================================
        // NIVEL 4 Y 5: ÁRBOLES FRUTALES Y CAÍDA DE PÉTALOS/FLORES
        // ===================================================================
        if (nivel >= 4) {
            // Frutos y flores brillando
            val fruitColor = if (emocion.contains("Feliz", ignoreCase = true)) Color(0xFFFBBF24) else Color(0xFFF43F5E)
            drawCircle(color = fruitColor, radius = 6.5f * scaleFactor, center = Offset(centerX + (windSway * 0.2f) - 30f, crownY - 20f))
            drawCircle(color = fruitColor, radius = 7.5f * scaleFactor, center = Offset(centerX + (windSway * 0.2f) + 25f, crownY - 35f))
            drawCircle(color = fruitColor, radius = 5.5f * scaleFactor, center = Offset(centerX + (windSway * 0.2f) - 10f, crownY - 50f))
            drawCircle(color = fruitColor, radius = 6.5f * scaleFactor, center = Offset(centerX + (windSway * 0.2f) + 35f, crownY + 10f))

            // Partículas cayendo continuamente (Pétalos de cerezo / Hojas vivas)
            particles.forEach { p ->
                if (isTriste) {
                    p.yOffset += p.speedY * height * 1.8f
                    if (p.yOffset > 80f) p.yOffset = -140f
                } else {
                    p.yOffset += p.speedY * height * 1.2f
                    p.xOffset += (sin(p.yOffset * 0.05f) * 2f) + (windSway * 0.1f)
                    if (p.yOffset > 100f) p.yOffset = -120f
                }

                val petalColor = if (isTriste) Color(0xFF60A5FA).copy(alpha = p.alpha) else leafColor.copy(alpha = p.alpha)
                drawCircle(
                    color = petalColor,
                    radius = if (isTriste) 2.dp.toPx() else p.radius.dp.toPx(),
                    center = Offset(centerX + p.xOffset, crownY + p.yOffset)
                )
            }
        }

        // Destello de luz ambiental superior
        drawCircle(
            color = Color.White.copy(alpha = 0.32f),
            radius = crownRadius * 0.3f,
            center = Offset(centerX + (windSway * 0.2f) - (12f * scaleFactor), crownY - (35f * scaleFactor))
        )
    }
}

