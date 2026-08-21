package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



import java.util.Calendar

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)
private val CardSecondary = Color(0xFF1F2937)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

@Composable
fun EstadisticasScreen() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

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

    var habitosCompletadosSemana by remember {
        mutableStateOf(0)
    }

    var registrosSemanaAnterior by remember { mutableStateOf(0) }
    var intensidadPromedioSemanaAnterior by remember { mutableStateOf(0.0) }





    LaunchedEffect(Unit) {

        val uid =
            auth.currentUser?.uid
                ?: return@LaunchedEffect

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                xp =
                    document.getLong("xp") ?: 0

                racha =
                    document.getLong("racha") ?: 0

                rachaHabitos =
                    document.getLong("rachaHabitos") ?: 0

                arbolNivel =
                    document.getLong("arbolNivel") ?: 1
            }

        db.collection("diarios")
            .document(uid)
            .collection("entradas")
            .get()
            .addOnSuccessListener { documentos ->

                val ahora = System.currentTimeMillis()

                val haceSieteDias =
                    ahora - (7L * 24L * 60L * 60L * 1000L)

                var sumaIntensidad = 0
                var cantidadIntensidades = 0
                var cantidadSemana = 0

                var sumaIntensidadSemanaAnterior = 0
                var cantidadIntensidadesSemanaAnterior = 0
                var cantidadSemanaAnterior = 0

                val haceCatorceDias =
                    ahora - (14L * 24L * 60L * 60L * 1000L)

                feliz = 0
                triste = 0
                ansioso = 0
                enojado = 0
                cansado = 0
                tranquilo = 0

                for (doc in documentos) {

                    val fecha =
                        doc.getLong("fecha") ?: 0L

                    val intensidad =
                        doc.getLong("intensidad")

                    if (fecha >= haceSieteDias) {

                        // Últimos 7 días

                        cantidadSemana++

                        if (intensidad != null) {

                            sumaIntensidad += intensidad.toInt()

                            cantidadIntensidades++
                        }

                    } else if (fecha >= haceCatorceDias) {

                        // Semana anterior

                        cantidadSemanaAnterior++

                        if (intensidad != null) {

                            sumaIntensidadSemanaAnterior +=
                                intensidad.toInt()

                            cantidadIntensidadesSemanaAnterior++
                        }
                    }

                    when (doc.getString("emocion")) {

                        "😊 Feliz",
                        "FELIZ" ->
                            feliz++

                        "😔 Triste",
                        "TRISTE" ->
                            triste++

                        "😟 Ansioso",
                        "ANSIOSO" ->
                            ansioso++

                        "😡 Enojado",
                        "ENOJADO" ->
                            enojado++

                        "😴 Cansado",
                        "CANSADO" ->
                            cansado++

                        "😌 Tranquilo",
                        "TRANQUILO" ->
                            tranquilo++
                    }

                }
                registrosSemana = cantidadSemana

                intensidadPromedio =
                    if (cantidadIntensidades > 0) {
                        sumaIntensidad.toDouble() /
                                cantidadIntensidades
                    } else {
                        0.0
                    }

                registrosSemanaAnterior = cantidadSemanaAnterior

                intensidadPromedioSemanaAnterior =
                    if (cantidadIntensidadesSemanaAnterior > 0) {
                        sumaIntensidadSemanaAnterior.toDouble() /
                                cantidadIntensidadesSemanaAnterior
                    } else {
                        0.0
                    }
                db.collection("usuarios")
                    .document(uid)
                    .collection("habitos")
                    .get()
                    .addOnSuccessListener { documentosHabitos ->

                        val ahora = System.currentTimeMillis()

                        val haceSieteDias =
                            ahora - (7L * 24L * 60L * 60L * 1000L)

                        var completados = 0

                        for (doc in documentosHabitos) {

                            val completado =
                                doc.getBoolean("completado") ?: false

                            val fechaXP =
                                doc.getLong("fechaXP") ?: 0L

                            if (
                                completado &&
                                fechaXP >= haceSieteDias
                            ) {
                                completados++
                            }
                        }

                        habitosCompletadosSemana = completados
                    }
            }
    }

    val total =
        feliz +
                triste +
                ansioso +
                enojado +
                cansado +
                tranquilo

    val porcentajeFeliz =
        if (total > 0)
            (feliz * 100) / total
        else 0

    val porcentajeTriste =
        if (total > 0)
            (triste * 100) / total
        else 0

    val porcentajeAnsioso =
        if (total > 0)
            (ansioso * 100) / total
        else 0

    val porcentajeEnojado =
        if (total > 0)
            (enojado * 100) / total
        else 0

    val porcentajeCansado =
        if (total > 0)
            (cansado * 100) / total
        else 0

    val porcentajeTranquilo =
        if (total > 0)
            (tranquilo * 100) / total
        else 0

    val bienestar =
        porcentajeFeliz + porcentajeTranquilo

    val estadoBienestar =
        when {

            bienestar >= 70 ->
                "🌟 Excelente"

            bienestar >= 50 ->
                "😊 Bueno"

            bienestar >= 30 ->
                "😐 Regular"

            else ->
                "⚠️ Necesita atención"
        }

    val emocionDominante =
        listOf(
            "😊 Feliz" to feliz,
            "😔 Triste" to triste,
            "😟 Ansioso" to ansioso,
            "😡 Enojado" to enojado,
            "😴 Cansado" to cansado,
            "😌 Tranquilo" to tranquilo
        )
            .maxByOrNull { it.second }
            ?.first
            ?: "Sin datos"

    val porcentajeDominante =
        if (total > 0) {

            val cantidad =
                when (emocionDominante) {

                    "😊 Feliz" ->
                        feliz

                    "😔 Triste" ->
                        triste

                    "😟 Ansioso" ->
                        ansioso

                    "😡 Enojado" ->
                        enojado

                    "😴 Cansado" ->
                        cansado

                    "😌 Tranquilo" ->
                        tranquilo

                    else ->
                        0
                }

            (cantidad * 100) / total

        } else {
            0
        }

    val recomendacion =
        when (emocionDominante) {

            "😟 Ansioso",
            "ANSIOSO" ->
                "Te recomendamos realizar ejercicios de respiración consciente."

            "😔 Triste",
            "TRISTE" ->
                "Practica gratitud y dedica unos minutos a actividades que disfrutes."

            "😡 Enojado",
            "ENOJADO" ->
                "Realiza una meditación guiada para recuperar la calma."

            "😴 Cansado",
            "CANSADO" ->
                "Descansa y realiza una sesión breve de respiración."

            "😊 Feliz",
            "FELIZ" ->
                "Sigue fortaleciendo los hábitos que contribuyen a tu bienestar."

            else ->
                "Mantén tu bienestar emocional con pequeñas actividades diarias."
        }

    val emocionesOrdenadas = listOf(
        "😊 Feliz" to feliz,
        "😌 Tranquilo" to tranquilo,
        "😟 Ansioso" to ansioso,
        "😴 Cansado" to cansado,
        "😔 Triste" to triste,
        "😡 Enojado" to enojado
    ).sortedByDescending { it.second }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BackgroundTop,
                        BackgroundBottom
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                )
        ) {

            Text(
                text = "Tu bienestar",
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Conoce cómo ha evolucionado tu mundo emocional.",
                color = GrayText,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // RESUMEN DE BIENESTAR

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        text = "🧠 Estado emocional",
                        color = PrimaryLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = estadoBienestar,
                        color = WhiteSoft,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Tu bienestar positivo representa $bienestar% de tus registros.",
                        color = GrayText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    LinearProgressIndicator(
                        progress = {
                            (bienestar / 100f)
                                .coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = PrimaryLight,
                        trackColor = CardSecondary
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // XP Y RACHA

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = "🔥",
                    title = "Racha",
                    value = "$racha",
                    subtitle = "días"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = "⭐",
                    title = "XP",
                    value = "$xp",
                    subtitle = "experiencia"
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            StatCard(
                modifier = Modifier.fillMaxWidth(),
                icon = "🌱",
                title = "Racha de hábitos",
                value = "$rachaHabitos",
                subtitle = "días consecutivos"
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

// ESTA SEMANA

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        text = "📅 Esta semana",
                        color = PrimaryLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = "📝 Registros",
                                color = GrayText,
                                fontSize = 13.sp
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "$registrosSemana",
                                color = WhiteSoft,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(
                            horizontalAlignment =
                                Alignment.End
                        ) {

                            Text(
                                text = "🧠 Intensidad promedio",
                                color = GrayText,
                                fontSize = 13.sp
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    if (registrosSemana > 0)
                                        String.format(
                                            "%.1f/10",
                                            intensidadPromedio
                                        )
                                    else
                                        "Sin datos",
                                color = WhiteSoft,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {

                            Text(
                                text = "🌱 Hábitos",
                                color = GrayText,
                                fontSize = 13.sp
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "$habitosCompletadosSemana",
                                color = WhiteSoft,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "completados",
                                color = GrayText,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = "Basado en tus registros de los últimos 7 días.",
                        color = GrayText,
                        fontSize = 13.sp
                    )

                    if (registrosSemanaAnterior > 0) {

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        val diferencia =
                            intensidadPromedio -
                                    intensidadPromedioSemanaAnterior

                        val diferenciaTexto =
                            String.format(
                                "%.1f",
                                kotlin.math.abs(diferencia)
                            )

                        val mensaje =
                            when {
                                diferencia < 0 ->
                                    "↓ Tu intensidad emocional ha disminuido."

                                diferencia > 0 ->
                                    "↑ Tu intensidad emocional ha aumentado."

                                else ->
                                    "→ Tu intensidad emocional se mantiene igual."
                            }

                        Text(
                            text = "📈 Comparación con la semana anterior",
                            color = PrimaryLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Semana anterior: " +
                                        String.format(
                                            "%.1f/10",
                                            intensidadPromedioSemanaAnterior
                                        ),
                            color = GrayText,
                            fontSize = 13.sp
                        )

                        Text(
                            text =
                                "$mensaje ($diferenciaTexto puntos)",
                            color = WhiteSoft,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ÁRBOL

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🌳 Tu crecimiento",
                        color = PrimaryLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "🌳",
                        fontSize = 64.sp
                    )

                    Text(
                        text = "Nivel $arbolNivel",
                        color = WhiteSoft,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Cada pequeño hábito ayuda a que tu árbol crezca.",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // EMOCIÓN DOMINANTE

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "💭 Tu emoción más frecuente",
                        color = PrimaryLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = emocionDominante,
                        color = WhiteSoft,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "$porcentajeDominante% de tus registros",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // DISTRIBUCIÓN

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        text = "📈 Distribución emocional",
                        color = PrimaryLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    emocionesOrdenadas.forEach { (emocion, cantidad) ->

                        val porcentaje =
                            if (total > 0)
                                (cantidad * 100) / total
                            else
                                0

                        EmotionBar(
                            nombre = emocion,
                            cantidad = cantidad,
                            porcentaje = porcentaje
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // RECOMENDACIÓN

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Primary.copy(
                        alpha = 0.18f
                    )
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        text = "💡 Consejo de NeuraBloom",
                        color = PrimaryLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = recomendacion,
                        color = WhiteSoft,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Total de registros: $total",
                color = GrayText,
                fontSize = 14.sp,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )
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

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = icon,
                fontSize = 24.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                color = GrayText,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                color = WhiteSoft,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = GrayText,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun EmotionBar(
    nombre: String,
    cantidad: Int,
    porcentaje: Int
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = nombre,
                color = WhiteSoft,
                fontSize = 14.sp
            )

            Text(
                text = "$porcentaje%  ($cantidad)",
                color = GrayText,
                fontSize = 12.sp
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        LinearProgressIndicator(
            progress = {
                (porcentaje / 100f)
                    .coerceIn(0f, 1f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp),
            color = PrimaryLight,
            trackColor = CardSecondary
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )
    }
}