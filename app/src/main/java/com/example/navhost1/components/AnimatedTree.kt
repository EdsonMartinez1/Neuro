@file:JvmName("AnimatedTreeComponent")

package com.example.navhost1.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Paleta de Colores de Emociones NeuraBloom Premium
private val ColorFeliz = Color(0xFF10B981)
private val ColorFelizHighlight = Color(0xFFA3E635)
private val ColorFelizFruto = Color(0xFFFBBF24)

private val ColorTranquilo = Color(0xFF06B6D4)
private val ColorTranquiloHighlight = Color(0xFF38BDF8)
private val ColorTranquiloFruto = Color(0xFFE0F2FE)

private val ColorAnsioso = Color(0xFFF59E0B)
private val ColorAnsiosoHighlight = Color(0xFFFDBA74)
private val ColorAnsiosoFruto = Color(0xFFFEF3C7)

private val ColorCansado = Color(0xFF6366F1)
private val ColorCansadoHighlight = Color(0xFFA855F7)
private val ColorCansadoFruto = Color(0xFFC7D2FE)

private val ColorTriste = Color(0xFF3B82F6)
private val ColorTristeHighlight = Color(0xFF93C5FD)
private val ColorTristeFruto = Color(0xFFBFDBFE)

private val ColorEnojado = Color(0xFFEF4444)
private val ColorEnojadoHighlight = Color(0xFFF87171)
private val ColorEnojadoFruto = Color(0xFFFEE2E2)

// Paleta del Tronco y Tierra
private val ColorTroncoBase = Color(0xFF7C3F1D)
private val ColorTroncoOscuro = Color(0xFF4A230D)
private val ColorTroncoLuz = Color(0xFFA65928)
private val ColorSemilla = Color(0xFFF59E0B)
private val ColorSemillaGlow = Color(0xFFFCD34D)

private data class PremiumParticle(
    var xOffset: Float,
    var yOffset: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float,
    val speedX: Float,
    val angleSeed: Float = Random.nextFloat() * 360f
)

@Composable
fun AnimatedTree(
    nivel: Int,
    emocion: String,
    modifier: Modifier = Modifier
) {
    // 1. Configuración dinámica según emoción
    val (targetLeafColor, targetHighlightColor, targetFruitColor) = when {
        emocion.contains("Feliz", ignoreCase = true) -> Triple(ColorFeliz, ColorFelizHighlight, ColorFelizFruto)
        emocion.contains("Tranquilo", ignoreCase = true) -> Triple(ColorTranquilo, ColorTranquiloHighlight, ColorTranquiloFruto)
        emocion.contains("Ansioso", ignoreCase = true) -> Triple(ColorAnsioso, ColorAnsiosoHighlight, ColorAnsiosoFruto)
        emocion.contains("Cansado", ignoreCase = true) -> Triple(ColorCansado, ColorCansadoHighlight, ColorCansadoFruto)
        emocion.contains("Triste", ignoreCase = true) -> Triple(ColorTriste, ColorTristeHighlight, ColorTristeFruto)
        emocion.contains("Enojado", ignoreCase = true) -> Triple(ColorEnojado, ColorEnojadoHighlight, ColorEnojadoFruto)
        else -> Triple(ColorTranquilo, ColorTranquiloHighlight, ColorTranquiloFruto)
    }

    val leafColor by animateColorAsState(targetValue = targetLeafColor, animationSpec = tween(1000), label = "leafColor")
    val highlightColor by animateColorAsState(targetValue = targetHighlightColor, animationSpec = tween(1000), label = "highlightColor")
    val fruitColor by animateColorAsState(targetValue = targetFruitColor, animationSpec = tween(1000), label = "fruitColor")

    // Escala progresiva por nivel
    val targetScale = when (nivel) {
        1 -> 0.65f
        2 -> 0.78f
        3 -> 0.88f
        4 -> 0.96f
        else -> 1.08f // Nivel 5: Majestuoso y brillante
    }

    val scaleFactor by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "treeScale"
    )

    // Animaciones en bucle para físicas de viento y vida
    val infiniteTransition = rememberInfiniteTransition(label = "treeAnimations")

    // Movimiento orgánico del viento
    val windSway by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "windSway"
    )

    // Pulsación de respiración vital
    val lifeBreathing by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lifeBreathing"
    )

    // Rotación suave para la corona mística de Nivel 5
    val crownRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "crownRotation"
    )

    // Caída rítmica de la gota de agua para Nivel 1
    val waterDropProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waterDropProgress"
    )

    // Partículas ambientales de alta calidad
    val particles = remember {
        List(30) {
            PremiumParticle(
                xOffset = Random.nextFloat() * 260f - 130f,
                yOffset = Random.nextFloat() * 220f - 110f,
                radius = Random.nextFloat() * 3.5f + 1.8f,
                alpha = Random.nextFloat() * 0.7f + 0.3f,
                speedY = Random.nextFloat() * 0.002f + 0.0008f,
                speedX = Random.nextFloat() * 0.0015f - 0.0007f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val baseY = height * 0.82f

        // --- 1. RESPLANDOR EMOCIONAL MULTICAPA (AURA DE FONDO) ---
        val auraRadius = (width * 0.45f) * scaleFactor * (if (nivel == 5) 1.25f else 1.0f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    leafColor.copy(alpha = if (nivel == 5) 0.38f else 0.25f),
                    highlightColor.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = Offset(centerX, height * 0.42f),
                radius = auraRadius
            ),
            center = Offset(centerX, height * 0.42f),
            radius = auraRadius
        )

        // --- 2. BASE ORGANICA Y MACETA ELEGANTE ---
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF334155), Color(0xFF1E293B)),
                center = Offset(centerX, baseY + 14f),
                radius = width * 0.35f
            ),
            topLeft = Offset(centerX - (width * 0.32f), baseY - 5f),
            size = Size(width * 0.64f, 32.dp.toPx())
        )

        val isTriste = emocion.contains("Triste", ignoreCase = true)
        val isFelizOrNivel5 = emocion.contains("Feliz", ignoreCase = true) || nivel == 5

        // ===================================================================
        // NIVEL 1: SEMILLA MÍSTICA Y ONDAS DE AGUA
        // ===================================================================
        if (nivel == 1) {
            val seedY = baseY - 16f
            val dropStartY = seedY - 140f
            val currentDropY = dropStartY + (seedY - dropStartY) * waterDropProgress

            // Gota de vida brillante
            if (waterDropProgress < 0.85f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFE0F2FE), Color(0xFF38BDF8)),
                        center = Offset(centerX, currentDropY),
                        radius = 8.dp.toPx()
                    ),
                    radius = 6.dp.toPx(),
                    center = Offset(centerX, currentDropY)
                )
            } else {
                // Ondas concéntricas de nutrición al impactar
                val rippleRadius = (waterDropProgress - 0.85f) * 140f
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 1f - waterDropProgress),
                    radius = rippleRadius,
                    center = Offset(centerX, seedY),
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }

            // Cuerpo de la Semilla Latiendo
            val seedRadius = 14f * lifeBreathing
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ColorSemillaGlow, ColorSemilla, ColorTroncoOscuro),
                    center = Offset(centerX - 3f, seedY - 3f),
                    radius = seedRadius * 1.2f
                ),
                radius = seedRadius,
                center = Offset(centerX, seedY)
            )

            // Grieta mística de energía emocional
            drawCircle(
                color = highlightColor.copy(alpha = 0.85f),
                radius = seedRadius * 0.45f,
                center = Offset(centerX - 2f, seedY - 2f)
            )

            // Raíces orgánicas vivas
            val rootPath = Path().apply {
                moveTo(centerX, seedY + seedRadius)
                quadraticTo(centerX - 4f, seedY + seedRadius + 8f, centerX - 10f, seedY + seedRadius + 14f)
                moveTo(centerX, seedY + seedRadius)
                quadraticTo(centerX + 5f, seedY + seedRadius + 10f, centerX + 12f, seedY + seedRadius + 16f)
            }
            drawPath(path = rootPath, color = ColorTroncoBase, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            return@Canvas
        }

        // ===================================================================
        // NIVEL 2: BROTE VITAL CON HOJAS BRILLANTES
        // ===================================================================
        if (nivel == 2) {
            val stemBaseY = baseY - 4f
            val stemHeight = 65f * scaleFactor

            // Tallo con curva orgânica
            val stemPath = Path().apply {
                moveTo(centerX, stemBaseY)
                quadraticTo(
                    centerX + (windSway * 0.25f), stemBaseY - (stemHeight * 0.5f),
                    centerX + (windSway * 0.15f), stemBaseY - stemHeight
                )
            }
            drawPath(path = stemPath, color = ColorTroncoBase, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))

            // Hoja Izquierda
            val leafLeftPos = Offset(centerX - 24f + (windSway * 0.3f), stemBaseY - stemHeight - 20f)
            drawCircle(
                brush = Brush.radialGradient(listOf(highlightColor, leafColor), center = leafLeftPos, radius = 16f),
                radius = 14f * lifeBreathing,
                center = leafLeftPos
            )

            // Hoja Derecha
            val leafRightPos = Offset(centerX + 24f + (windSway * 0.3f), stemBaseY - stemHeight - 14f)
            drawCircle(
                brush = Brush.radialGradient(listOf(highlightColor, leafColor), center = leafRightPos, radius = 14f),
                radius = 12f * lifeBreathing,
                center = leafRightPos
            )
            return@Canvas
        }

        // ===================================================================
        // NIVEL 3 EN ADELANTE: TRONCO CON TEXTURA Y COPA MULTICAPA
        // ===================================================================
        val trunkWidth = (if (nivel == 3) 14f else 22f) * scaleFactor
        val trunkTopY = height * (if (nivel >= 5) 0.40f else 0.44f)

        // Tronco vector con sombras y luces
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
        drawPath(
            path = trunkPath,
            brush = Brush.horizontalGradient(
                colors = listOf(ColorTroncoOscuro, ColorTroncoBase, ColorTroncoLuz),
                startX = centerX - trunkWidth * 1.5f,
                endX = centerX + trunkWidth * 1.5f
            )
        )

        // Ramas Principales
        val leftBranch = Path().apply {
            moveTo(centerX - (trunkWidth * 0.4f), height * 0.54f)
            quadraticTo(
                centerX - (55f * scaleFactor), height * 0.50f,
                centerX - (75f * scaleFactor) + (windSway * 0.3f), height * 0.43f
            )
        }
        drawPath(path = leftBranch, color = ColorTroncoOscuro, style = Stroke(width = 5.5f * scaleFactor, cap = StrokeCap.Round))

        val rightBranch = Path().apply {
            moveTo(centerX + (trunkWidth * 0.4f), height * 0.52f)
            quadraticTo(
                centerX + (55f * scaleFactor), height * 0.48f,
                centerX + (70f * scaleFactor) + (windSway * 0.3f), height * 0.40f
            )
        }
        drawPath(path = rightBranch, color = ColorTroncoOscuro, style = Stroke(width = 5f * scaleFactor, cap = StrokeCap.Round))

        // --- COPA DE HOJAS MULTICAPA (3D DEPTH) ---
        val crownY = height * (if (nivel >= 5) 0.34f else 0.38f)
        val crownRadius = (45f + (nivel * 16f)) * scaleFactor * lifeBreathing

        // Capa 1: Sombra posterior del follaje
        drawCircle(
            color = ColorTroncoOscuro.copy(alpha = 0.5f),
            radius = crownRadius * 0.85f,
            center = Offset(centerX - (30f * scaleFactor) + (windSway * 0.4f), crownY + 22f)
        )
        drawCircle(
            color = ColorTroncoOscuro.copy(alpha = 0.5f),
            radius = crownRadius * 0.85f,
            center = Offset(centerX + (30f * scaleFactor) + (windSway * 0.4f), crownY + 22f)
        )

        // Capa 2: Follaje Principal
        drawCircle(
            brush = Brush.radialGradient(listOf(highlightColor, leafColor), center = Offset(centerX - 35f, crownY + 10f), radius = crownRadius),
            radius = crownRadius * 0.88f,
            center = Offset(centerX - (35f * scaleFactor) + (windSway * 0.4f), crownY + 10f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(highlightColor, leafColor), center = Offset(centerX + 35f, crownY + 10f), radius = crownRadius),
            radius = crownRadius * 0.88f,
            center = Offset(centerX + (35f * scaleFactor) + (windSway * 0.4f), crownY + 10f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(highlightColor, leafColor), center = Offset(centerX, crownY - 15f), radius = crownRadius * 1.1f),
            radius = crownRadius,
            center = Offset(centerX + (windSway * 0.2f), crownY - (15f * scaleFactor))
        )

        // Capa 3: Brillo de copa superior
        drawCircle(
            color = highlightColor.copy(alpha = 0.45f),
            radius = crownRadius * 0.45f,
            center = Offset(centerX + (windSway * 0.2f) - (15f * scaleFactor), crownY - (35f * scaleFactor))
        )

        // ===================================================================
        // NIVEL 4 Y 5: FRUTOS, FLORES Y PARTÍCULAS MÁGICAS
        // ===================================================================
        if (nivel >= 4) {
            // Frutos y flores resplandecientes
            val fRadius = if (nivel == 5) 8f * scaleFactor else 6.5f * scaleFactor

            drawCircle(color = fruitColor, radius = fRadius, center = Offset(centerX + (windSway * 0.2f) - 35f, crownY - 20f))
            drawCircle(color = fruitColor, radius = fRadius * 1.1f, center = Offset(centerX + (windSway * 0.2f) + 30f, crownY - 35f))
            drawCircle(color = fruitColor, radius = fRadius * 0.9f, center = Offset(centerX + (windSway * 0.2f) - 12f, crownY - 55f))
            drawCircle(color = fruitColor, radius = fRadius, center = Offset(centerX + (windSway * 0.2f) + 40f, crownY + 12f))

            if (nivel == 5) {
                drawCircle(color = fruitColor, radius = fRadius * 1.2f, center = Offset(centerX + (windSway * 0.2f) - 50f, crownY + 5f))
                drawCircle(color = fruitColor, radius = fRadius, center = Offset(centerX + (windSway * 0.2f) + 10f, crownY - 70f))
            }
        }

        // ===================================================================
        // NIVEL 5 EXCLUSIVO: CORONA CÓSMICA Y POLVO ESTELAR
        // ===================================================================
        if (nivel == 5) {
            // Anillo brillante giratorio alrededor de la copa
            val ringRadius = crownRadius * 1.25f
            val ringCenter = Offset(centerX + (windSway * 0.2f), crownY - 20f)

            for (i in 0 until 8) {
                val angle = Math.toRadians((crownRotation + (i * 45)).toDouble())
                val sparkX = ringCenter.x + (ringRadius * cos(angle)).toFloat()
                val sparkY = ringCenter.y + (ringRadius * 0.45f * sin(angle)).toFloat()

                drawCircle(
                    color = highlightColor.copy(alpha = 0.85f),
                    radius = 3.5.dp.toPx(),
                    center = Offset(sparkX, sparkY)
                )
            }
        }

        // --- SISTEMA DE PARTÍCULAS CLIMÁTICAS Y DE ENERGÍA ---
        if (nivel >= 3) {
            particles.forEach { p ->
                if (isTriste) {
                    // Caída suave tipo lluvia serena
                    p.yOffset += p.speedY * height * 1.6f
                    if (p.yOffset > 90f) p.yOffset = -130f
                } else if (isFelizOrNivel5) {
                    // Partículas ascendentes mágicas
                    p.yOffset -= p.speedY * height * 1.2f
                    p.xOffset += (sin((p.yOffset + p.angleSeed) * 0.04f) * 1.8f) + (windSway * 0.08f)
                    if (p.yOffset < -150f) p.yOffset = 80f
                } else {
                    // Viento flotante estándar
                    p.yOffset += p.speedY * height * 1.1f
                    p.xOffset += (sin((p.yOffset + p.angleSeed) * 0.05f) * 2f) + (windSway * 0.1f)
                    if (p.yOffset > 100f) p.yOffset = -120f
                }

                val particleColor = if (isTriste) ColorTristeHighlight.copy(alpha = p.alpha) else highlightColor.copy(alpha = p.alpha)

                drawCircle(
                    color = particleColor,
                    radius = p.radius.dp.toPx(),
                    center = Offset(centerX + p.xOffset, crownY + p.yOffset)
                )
            }
        }
    }
}