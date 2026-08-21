package com.example.navhost1.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs

// Paleta NeuraBloom Premium Dark
private val DarkBackgroundTop = Color(0xFF0F172A)
private val DarkBackgroundBottom = Color(0xFF090D16)

private val PrimaryViolet = Color(0xFF8B5CF6)
private val PrimaryVioletLight = Color(0xFFC084FC)
private val PurpleGlow = Color(0xFF6D28D9)

private val CardColor = Color(0xFF1E293B).copy(alpha = 0.75f)
private val CardSecondary = Color(0xFF334155).copy(alpha = 0.5f)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)

// Colores específicos para emociones
private val ColorFeliz = Color(0xFF10B981)
private val ColorTranquilo = Color(0xFF06B6D4)
private val ColorAnsioso = Color(0xFFF59E0B)
private val ColorCansado = Color(0xFF6366F1)
private val ColorTriste = Color(0xFF3B82F6)
private val ColorEnojado = Color(0xFFEF4444)

private data class EmocionData(
    val nombre: String,
    val cantidad: Int,
    val porcentaje: Int,
    val color: Color
)

@Composable
fun EstadisticasScreen() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Filtro temporal seleccionado: 0 = Semana, 1 = Mes, 2 = Total
    var filtroSeleccionado by remember { mutableStateOf(0) }

    var feliz by remember { mutableStateOf(0) }
    var triste by remember { mutableStateOf(0) }
    var ansioso by remember { mutableStateOf(0) }
    var enojado by remember { mutableStateOf(0) }
    var cansado by remember { mutableStateOf(0) }
    var tranquilo by remember { mutableStateOf(0) }

    var xp by remember { mutableStateOf(0L) }
    var racha by remember { mutableStateOf(0L) }
    var rachaHabitos by remember { mutableStateOf(0L) }
    var arbolNivel by remember { mutableStateOf(1L) }

    var registrosSemana by remember { mutableStateOf(0) }
    var intensidadPromedio by remember { mutableStateOf(0.0) }
    var habitosCompletadosSemana by remember { mutableStateOf(0) }

    var registrosSemanaAnterior by remember { mutableStateOf(0) }
    var intensidadPromedioSemanaAnterior by remember { mutableStateOf(0.0) }

    // Puntos mock simulados para el gráfico de líneas dinámico
    val puntosGrafico = remember(filtroSeleccionado, intensidadPromedio) {
        when (filtroSeleccionado) {
            0 -> listOf(4f, 6f, 5f, 7f, 6f, 8f, (intensidadPromedio.toFloat().takeIf { it > 0 } ?: 6.5f))
            1 -> listOf(5f, 6f, 7f, 5f, 8f, 6f, 7f, 9f, 6f, 7f)
            else -> listOf(3f, 5f, 6f, 8f, 7f, 9f, 8f)
        }
    }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                xp = document.getLong("xp") ?: 0
                racha = document.getLong("racha") ?: 0
                rachaHabitos = document.getLong("rachaHabitos") ?: 0
                arbolNivel = document.getLong("arbolNivel") ?: 1
            }

        db.collection("diarios")
            .document(uid)
            .collection("entradas")
            .get()
            .addOnSuccessListener { documentos ->
                val ahora = System.currentTimeMillis()
                val haceSieteDias = ahora - (7L * 24L * 60L * 60L * 1000L)
                val haceCatorceDias = ahora - (14L * 24L * 60L * 60L * 1000L)

                var sumaIntensidad = 0
                var cantidadIntensidades = 0
                var cantidadSemana = 0

                var sumaIntensidadSemanaAnterior = 0
                var cantidadIntensidadesSemanaAnterior = 0
                var cantidadSemanaAnterior = 0

                feliz = 0
                triste = 0
                ansioso = 0
                enojado = 0
                cansado = 0
                tranquilo = 0

                for (doc in documentos) {
                    val fecha = doc.getLong("fecha") ?: 0L
                    val intensidad = doc.getLong("intensidad")

                    if (fecha >= haceSieteDias) {
                        cantidadSemana++
                        if (intensidad != null) {
                            sumaIntensidad += intensidad.toInt()
                            cantidadIntensidades++
                        }
                    } else if (fecha >= haceCatorceDias) {
                        cantidadSemanaAnterior++
                        if (intensidad != null) {
                            sumaIntensidadSemanaAnterior += intensidad.toInt()
                            cantidadIntensidadesSemanaAnterior++
                        }
                    }

                    when (doc.getString("emocion")) {
                        "😊 Feliz", "FELIZ" -> feliz++
                        "😔 Triste", "TRISTE" -> triste++
                        "😟 Ansioso", "ANSIOSO" -> ansioso++
                        "😡 Enojado", "ENOJADO" -> enojado++
                        "😴 Cansado", "CANSADO" -> cansado++
                        "😌 Tranquilo", "TRANQUILO" -> tranquilo++
                    }
                }

                registrosSemana = cantidadSemana
                intensidadPromedio = if (cantidadIntensidades > 0) {
                    sumaIntensidad.toDouble() / cantidadIntensidades
                } else {
                    0.0
                }

                registrosSemanaAnterior = cantidadSemanaAnterior
                intensidadPromedioSemanaAnterior = if (cantidadIntensidadesSemanaAnterior > 0) {
                    sumaIntensidadSemanaAnterior.toDouble() / cantidadIntensidadesSemanaAnterior
                } else {
                    0.0
                }

                db.collection("usuarios")
                    .document(uid)
                    .collection("habitos")
                    .get()
                    .addOnSuccessListener { documentosHabitos ->
                        var completados = 0
                        for (doc in documentosHabitos) {
                            val completado = doc.getBoolean("completado") ?: false
                            val fechaXP = doc.getLong("fechaXP") ?: 0L
                            if (completado && fechaXP >= haceSieteDias) {
                                completados++
                            }
                        }
                        habitosCompletadosSemana = completados
                    }
            }
    }

    val total = feliz + triste + ansioso + enojado + cansado + tranquilo

    val porcentajeFeliz = if (total > 0) (feliz * 100) / total else 0
    val porcentajeTriste = if (total > 0) (triste * 100) / total else 0
    val porcentajeAnsioso = if (total > 0) (ansioso * 100) / total else 0
    val porcentajeEnojado = if (total > 0) (enojado * 100) / total else 0
    val porcentajeCansado = if (total > 0) (cansado * 100) / total else 0
    val porcentajeTranquilo = if (total > 0) (tranquilo * 100) / total else 0

    val bienestar = porcentajeFeliz + porcentajeTranquilo

    val estadoBienestar = when {
        bienestar >= 70 -> "🌟 Excelente"
        bienestar >= 50 -> "😊 Bueno"
        bienestar >= 30 -> "😐 Regular"
        else -> "⚠️ Necesita atención"
    }

    val emocionDominante = listOf(
        "😊 Feliz" to feliz,
        "😔 Triste" to triste,
        "😟 Ansioso" to ansioso,
        "😡 Enojado" to enojado,
        "😴 Cansado" to cansado,
        "😌 Tranquilo" to tranquilo
    ).maxByOrNull { it.second }?.first ?: "Sin datos"

    val porcentajeDominante = if (total > 0) {
        val cantidad = when (emocionDominante) {
            "😊 Feliz" -> feliz
            "😔 Triste" -> triste
            "😟 Ansioso" -> ansioso
            "😡 Enojado" -> enojado
            "😴 Cansado" -> cansado
            "😌 Tranquilo" -> tranquilo
            else -> 0
        }
        (cantidad * 100) / total
    } else {
        0
    }

    val recomendacion = when (emocionDominante) {
        "😟 Ansioso", "ANSIOSO" -> "Te recomendamos realizar ejercicios de respiración consciente."
        "😔 Triste", "TRISTE" -> "Practica gratitud y dedica unos minutos a actividades que disfrutes."
        "😡 Enojado", "ENOJADO" -> "Realiza una meditación guiada para recuperar la calma."
        "😴 Cansado", "CANSADO" -> "Descansa y realiza una sesión breve de respiración."
        "😊 Feliz", "FELIZ" -> "Sigue fortaleciendo los hábitos que contribuyen a tu bienestar."
        else -> "Mantén tu bienestar emocional con pequeñas actividades diarias."
    }

    val emocionesOrdenadas = listOf(
        EmocionData("😊 Feliz", feliz, porcentajeFeliz, ColorFeliz),
        EmocionData("😌 Tranquilo", tranquilo, porcentajeTranquilo, ColorTranquilo),
        EmocionData("😟 Ansioso", ansioso, porcentajeAnsioso, ColorAnsioso),
        EmocionData("😴 Cansado", cansado, porcentajeCansado, ColorCansado),
        EmocionData("😔 Triste", triste, porcentajeTriste, ColorTriste),
        EmocionData("😡 Enojado", enojado, porcentajeEnojado, ColorEnojado)
    ).sortedByDescending { it.cantidad }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBackgroundTop, DarkBackgroundBottom)
                )
            )
    ) {
        // Luces de fondo ambientales (Glow Effects)
        Box(
            modifier = Modifier
                .size(300.dp)
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
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Text(
                text = "Tu bienestar",
                color = WhiteSoft,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Conoce cómo ha evolucionado tu mundo emocional.",
                color = GrayText,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de Tiempo (Chips Interactivos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Esta Semana", "Este Mes", "Histórico").forEachIndexed { index, title ->
                    val selected = filtroSeleccionado == index
                    val chipBg by animateColorAsState(
                        targetValue = if (selected) PrimaryViolet.copy(alpha = 0.25f) else CardColor,
                        label = "chipBg"
                    )
                    val chipBorder by animateColorAsState(
                        targetValue = if (selected) PrimaryVioletLight else Color.White.copy(alpha = 0.08f),
                        label = "chipBorder"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { filtroSeleccionado = index },
                        shape = RoundedCornerShape(14.dp),
                        color = chipBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, chipBorder)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (selected) PrimaryVioletLight else GrayText,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TARJETA PRINCIPAL: RESUMEN DE BIENESTAR
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Estado emocional",
                            color = PrimaryVioletLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = PrimaryVioletLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = estadoBienestar,
                        color = WhiteSoft,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tu bienestar positivo representa $bienestar% de tus registros.",
                        color = GrayText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val animatedProgress by animateFloatAsState(
                        targetValue = (bienestar / 100f).coerceIn(0f, 1f),
                        animationSpec = tween(durationMillis = 1000),
                        label = "progress"
                    )

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = PrimaryVioletLight,
                        trackColor = CardSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GRÁFICO DINÁMICO DE TENDENCIA INTENSIDAD
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📈 Tendencia de Intensidad",
                            color = PrimaryVioletLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Prom. ${String.format("%.1f", intensidadPromedio)}",
                            color = WhiteSoft,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gráfico Custom Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        if (puntosGrafico.size < 2) return@Canvas

                        val width = size.width
                        val height = size.height
                        val stepX = width / (puntosGrafico.size - 1)
                        val maxY = 10f

                        val path = Path()
                        val points = puntosGrafico.mapIndexed { i, valY ->
                            val x = i * stepX
                            val y = height - ((valY / maxY) * height)
                            Offset(x, y)
                        }

                        path.moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val cx = (p1.x + p2.x) / 2
                            path.cubicTo(cx, p1.y, cx, p2.y, p2.x, p2.y)
                        }

                        // Línea principal del gráfico
                        drawPath(
                            path = path,
                            color = PrimaryVioletLight,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Puntos en cada vértice
                        points.forEach { point ->
                            drawCircle(
                                color = PrimaryViolet,
                                radius = 5.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = WhiteSoft,
                                radius = 2.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // XP Y RACHAS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = "🔥",
                    title = "Racha",
                    value = "$racha",
                    subtitle = "días diarios"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = "⭐",
                    title = "XP Total",
                    value = "$xp",
                    subtitle = "puntos ganados"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            StatCard(
                modifier = Modifier.fillMaxWidth(),
                icon = "🌱",
                title = "Racha de hábitos",
                value = "$rachaHabitos",
                subtitle = "días consecutivos cumpliendo metas"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Crecimiento / Árbol
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌳 Tu crecimiento",
                        color = PrimaryVioletLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(PrimaryViolet.copy(alpha = 0.15f))
                    ) {
                        Text(text = "🌳", fontSize = 48.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Nivel $arbolNivel",
                        color = WhiteSoft,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Cada pequeño hábito ayuda a que tu árbol crezca.",
                        color = GrayText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // EMOCIÓN DOMINANTE
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💭 Emoción más frecuente",
                        color = PrimaryVioletLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = emocionDominante,
                        color = WhiteSoft,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$porcentajeDominante% de tus registros totales",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DISTRIBUCIÓN EMOCIONAL CON COLORES DEDICADOS
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = PrimaryVioletLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Distribución emocional",
                            color = PrimaryVioletLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    emocionesOrdenadas.forEach { item ->
                        EmotionBar(
                            nombre = item.nombre,
                            cantidad = item.cantidad,
                            porcentaje = item.porcentaje,
                            barColor = item.color
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RECOMENDACIÓN / CONSEJO
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = PrimaryViolet.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryVioletLight.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryVioletLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Consejo de NeuraBloom",
                            color = PrimaryVioletLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = recomendacion,
                        color = WhiteSoft,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Total de registros analizados: $total",
                color = GrayText,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String,
    subtitle: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = CardColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = GrayText,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = WhiteSoft,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = GrayText,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun EmotionBar(
    nombre: String,
    cantidad: Int,
    porcentaje: Int,
    barColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = nombre,
                color = WhiteSoft,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$porcentaje% ($cantidad)",
                color = GrayText,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        val animatedProgress by animateFloatAsState(
            targetValue = (porcentaje / 100f).coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 800),
            label = "barProgress"
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = CardSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))
    }
}