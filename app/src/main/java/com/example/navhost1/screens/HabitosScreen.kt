package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.navhost1.habits.Habit
import com.example.navhost1.habits.HabitRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.ui.Alignment
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.draw.clip

import androidx.compose.material3.LinearProgressIndicator


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton

import androidx.compose.material3.FilterChip
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Box

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

data class HabitRecommendation(
    val nombre: String,
    val categoria: String,
    val descripcion: String
)

data class HabitAIResponse(
    val recommendations: List<HabitRecommendation>
)

suspend fun analizarHabitosConIA(
    objetivo: String,
    habitos: String,
    diario: String,
    chat: String
): HabitAIResponse? {

    return withContext(Dispatchers.IO) {

        try {

            val url = URL(
                "https://openai-api-worker.ed-ia-app.workers.dev"
            )

            val connection =
                url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.doOutput = true

            val body = JSONObject().apply {

                put(
                    "type",
                    "habit"
                )

                put(
                    "message",
                    objetivo
                )

                put(
                    "habitos",
                    habitos
                )

                put(
                    "diario",
                    diario
                )

                put(
                    "chat",
                    chat
                )
            }

            connection.outputStream.use { output ->

                output.write(
                    body
                        .toString()
                        .toByteArray(Charsets.UTF_8)
                )
            }

            val response =
                connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

            connection.disconnect()

            val json =
                JSONObject(response)

            val resultadoIA =
                json.getString("result")

            val resultadoJSON =
                JSONObject(resultadoIA)

            val recomendacionesJSON =
                resultadoJSON.getJSONArray(
                    "recommendations"
                )

            val recomendaciones =
                mutableListOf<HabitRecommendation>()

            for (i in 0 until recomendacionesJSON.length()) {

                val recomendacion =
                    recomendacionesJSON.getJSONObject(i)

                recomendaciones.add(
                    HabitRecommendation(
                        nombre =
                            recomendacion.getString(
                                "nombre"
                            ),

                        categoria =
                            recomendacion.getString(
                                "categoria"
                            ),

                        descripcion =
                            recomendacion.getString(
                                "descripcion"
                            )
                    )
                )
            }

            HabitAIResponse(
                recommendations =
                    recomendaciones
            )

        } catch (e: Exception) {

            Log.e(
                "IA_HABITOS",
                "Error al conectar con la IA",
                e
            )

            null
        }
    }
}

@Composable
fun HabitosScreen() {

    val auth = FirebaseAuth.getInstance()
    val repository = remember { HabitRepository() }
    val db = FirebaseFirestore.getInstance()

    val scope = rememberCoroutineScope()

    var habitos by remember {
        mutableStateOf<List<Habit>>(emptyList())
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var xpUsuario by remember {
        mutableStateOf(0L)
    }

    var rachaHabitos by remember {
        mutableStateOf(0)
    }

    var mostrarCrearHabit by remember {
        mutableStateOf(false)
    }

    var mostrarIA by remember {
        mutableStateOf(false)
    }

    var generandoIA by remember {
        mutableStateOf(false)
    }

    var objetivoIA by remember {
        mutableStateOf("")
    }

    var mostrarRecomendacionesIA by remember {
        mutableStateOf(false)
    }

    var recomendacionesIA by remember {
        mutableStateOf<List<HabitRecommendation>>(emptyList())
    }

    var seleccionadasIA by remember {
        mutableStateOf<Set<Int>>(emptySet())
    }

    var nombreNuevoHabit by remember {
        mutableStateOf("")
    }

    var categoriaNuevoHabit by remember {
        mutableStateOf("")
    }

    var descripcionNuevoHabit by remember {
        mutableStateOf("")
    }

    var creandoHabit by remember {
        mutableStateOf(false)
    }

    var errorCrearHabit by remember {
        mutableStateOf<String?>(null)
    }

    var habitSeleccionado by remember {
        mutableStateOf<Habit?>(null)
    }

    var editandoHabit by remember {
        mutableStateOf(false)
    }

    var mostrarMenuHabit by remember {
        mutableStateOf(false)
    }

    var mostrarConfirmarEliminar by remember {
        mutableStateOf(false)
    }

    var eliminandoHabit by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {

        val uid = auth.currentUser?.uid

        if (uid != null) {

            auth.currentUser?.uid?.let { usuarioId ->

                FirebaseFirestore
                    .getInstance()
                    .collection("usuarios")
                    .document(usuarioId)
                    .get()
                    .addOnSuccessListener { documento ->

                        xpUsuario =
                            documento.getLong("xp") ?: 0L

                        rachaHabitos =
                            documento.getLong("rachaHabitos")?.toInt() ?: 0
                    }
            }

            repository.obtenerHabitos(
                uid = uid,

                onSuccess = { lista ->

                    habitos = lista
                    cargando = false
                },

                onError = { error ->

                    cargando = false

                    println(
                        "Error al obtener hábitos: ${error.message}"
                    )
                }
            )

        } else {

            cargando = false
        }
    }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Tu crecimiento 🌱",
                        color = PrimaryLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Hábitos",
                        color = WhiteSoft,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Pequeños pasos, grandes cambios.",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Primary,
                                    PrimaryLight
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "🌱",
                        fontSize = 28.sp
                    )
                }
            }

            val habitosCompletados =
                habitos.count { it.completado }

            val totalHabitos =
                habitos.size

            val progresoHabitos =
                if (totalHabitos > 0) {
                    habitosCompletados.toFloat() / totalHabitos.toFloat()
                } else {
                    0f
                }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor.copy(alpha = 0.96f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        text = "TU PROGRESO",
                        color = PrimaryLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = when {
                                    totalHabitos == 0 ->
                                        "Comienza tu primer hábito"

                                    progresoHabitos >= 1f ->
                                        "¡Todo completado!"

                                    else ->
                                        "Sigue avanzando"
                                },
                                color = WhiteSoft,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                text = when {
                                    totalHabitos == 0 ->
                                        "Un pequeño hábito puede iniciar un gran cambio."

                                    else ->
                                        "$habitosCompletados de $totalHabitos hábitos completados"
                                },
                                color = GrayText,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(16.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Primary,
                                            PrimaryLight
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "${(progresoHabitos * 100).toInt()}%",
                                color = WhiteSoft,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    LinearProgressIndicator(
                        progress = {
                            progresoHabitos
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(RoundedCornerShape(50)),
                        color = PrimaryLight,
                        trackColor = Color(0xFF293548)
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = "🔥 Racha",
                                color = GrayText,
                                fontSize = 12.sp
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = if (rachaHabitos == 1) {
                                    "1 día"
                                } else {
                                    "$rachaHabitos días"
                                },
                                color = WhiteSoft,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column {

                            Text(
                                text = "⭐ XP",
                                color = GrayText,
                                fontSize = 12.sp
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "$xpUsuario XP",
                                color = WhiteSoft,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column {

                            Text(
                                text = "🌱 Estado",
                                color = GrayText,
                                fontSize = 12.sp
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = when {
                                    progresoHabitos >= 1f ->
                                        "¡Completado!"

                                    progresoHabitos > 0f ->
                                        "En progreso"

                                    else ->
                                        "Por comenzar"
                                },
                                color = PrimaryLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "Tus hábitos",
                    color = WhiteSoft,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Button(
                        onClick = {
                            if (habitos.size >= 7) {
                                errorCrearHabit =
                                    "Has alcanzado el máximo de 7 hábitos."
                            } else {
                                mostrarCrearHabit = true
                                errorCrearHabit = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        )
                    ) {
                        Text(
                            text = "＋ Nuevo",
                            color = WhiteSoft,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            objetivoIA = ""
                            mostrarIA = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardColor
                        )
                    ) {
                        Text(
                            text = "✨ Con IA",
                            color = PrimaryLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (cargando) {

                    Text(
                        text = "Cargando hábitos...",
                        color = GrayText,
                        fontSize = 14.sp
                    )

                } else if (habitos.isEmpty()) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardColor
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {

                            Text(
                                text = "🌱 Aún no tienes hábitos",
                                color = WhiteSoft,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                text = "NeuraBloom podrá recomendarte actividades personalizadas.",
                                color = GrayText,
                                fontSize = 13.sp
                            )
                        }

                    }

                } else {

                    habitos.forEach { habit ->

                        val categoriaIcono = when (habit.categoria.uppercase()) {
                            "LECTURA" -> "📚"
                            "EJERCICIO" -> "🏃"
                            "BIENESTAR" -> "🧘"
                            "DESCANSO" -> "🌙"
                            else -> "🌱"
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    if (habit.completado) {
                                        Color(0xFF172B26)
                                    } else {
                                        Color(0xFF182235)
                                    }
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            if (habit.completado) {
                                                Color(0xFF245C48)
                                            } else {
                                                Color(0xFF2A2545)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = if (habit.completado) {
                                            "✓"
                                        } else {
                                            categoriaIcono
                                        },
                                        fontSize = 25.sp
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.width(14.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = habit.nombre,
                                            color = WhiteSoft,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Box {
                                            IconButton(
                                                onClick = {
                                                    habitSeleccionado = habit
                                                    mostrarMenuHabit = true
                                                }
                                            ) {
                                                Text(
                                                    text = "⋮",
                                                    color = GrayText,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            DropdownMenu(
                                                expanded =
                                                    mostrarMenuHabit &&
                                                            habitSeleccionado?.id == habit.id,

                                                onDismissRequest = {
                                                    mostrarMenuHabit = false
                                                }
                                            ) {

                                                DropdownMenuItem(
                                                    text = {
                                                        Text("Editar")
                                                    },
                                                    onClick = {

                                                        mostrarMenuHabit = false

                                                        habitSeleccionado = habit

                                                        nombreNuevoHabit = habit.nombre
                                                        categoriaNuevoHabit = habit.categoria
                                                        descripcionNuevoHabit = habit.descripcion

                                                        errorCrearHabit = null

                                                        editandoHabit = true
                                                        mostrarCrearHabit = true
                                                    }
                                                )

                                                DropdownMenuItem(
                                                    text = {
                                                        Text("Eliminar")
                                                    },
                                                    onClick = {

                                                        mostrarMenuHabit = false

                                                        habitSeleccionado = habit
                                                        mostrarConfirmarEliminar = true
                                                    }
                                                )
                                            }
                                        }
                                    }



                                    Spacer(
                                        modifier = Modifier.height(5.dp)
                                    )

                                    Text(
                                        text = if (habit.completado) {
                                            "Hábito completado. ¡Buen trabajo! 🌱"
                                        } else {
                                            habit.descripcion
                                        },
                                        color = GrayText,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(
                                        modifier = Modifier.height(8.dp)
                                    )

                                    Text(
                                        text = "⭐ +${habit.xp} XP  •  ${habit.categoria}",
                                        color = PrimaryLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Checkbox(
                                    checked = habit.completado,

                                    onCheckedChange = { marcado ->

                                        val uid =
                                            auth.currentUser?.uid

                                        if (uid != null) {

                                            if (marcado) {

                                                repository.completarHabit(
                                                    uid = uid,
                                                    habitId = habit.id,

                                                    onSuccess = {

                                                        habitos =
                                                            habitos.map {

                                                                if (it.id == habit.id) {
                                                                    it.copy(
                                                                        completado = true
                                                                    )
                                                                } else {
                                                                    it
                                                                }
                                                            }
                                                    },

                                                    onError = { error ->

                                                        println(
                                                            "Error al completar hábito: ${error.message}"
                                                        )
                                                    }
                                                )

                                            } else {

                                                repository.descompletarHabit(
                                                    uid = uid,
                                                    habitId = habit.id,

                                                    onSuccess = {

                                                        habitos =
                                                            habitos.map {

                                                                if (it.id == habit.id) {
                                                                    it.copy(
                                                                        completado = false
                                                                    )
                                                                } else {
                                                                    it
                                                                }
                                                            }

                                                        FirebaseFirestore
                                                            .getInstance()
                                                            .collection("usuarios")
                                                            .document(uid)
                                                            .get()
                                                            .addOnSuccessListener { documento ->

                                                                xpUsuario =
                                                                    documento.getLong("xp")
                                                                        ?: xpUsuario

                                                                rachaHabitos =
                                                                    documento
                                                                        .getLong("rachaHabitos")
                                                                        ?.toInt()
                                                                        ?: rachaHabitos
                                                            }
                                                    },

                                                    onError = { error ->

                                                        println(
                                                            "Error al descompletar hábito: ${error.message}"
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                if (mostrarCrearHabit) {

                    AlertDialog(

                        onDismissRequest = {
                            if (!creandoHabit) {
                                mostrarCrearHabit = false
                            }
                        },

                        title = {
                            Text(
                                text = if (editandoHabit) {
                                    "Editar hábito ✏️"
                                } else {
                                    "Nuevo hábito 🌱"
                                },
                                color = WhiteSoft,
                                fontWeight = FontWeight.Bold
                            )
                        },

                        text = {

                            Column {

                                OutlinedTextField(
                                    value = nombreNuevoHabit,
                                    onValueChange = {
                                        nombreNuevoHabit = it
                                        errorCrearHabit = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),

                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = WhiteSoft,
                                        unfocusedTextColor = WhiteSoft,
                                        focusedLabelColor = PrimaryLight,
                                        unfocusedLabelColor = GrayText,
                                        focusedBorderColor = PrimaryLight,
                                        unfocusedBorderColor = GrayText,
                                        cursorColor = PrimaryLight
                                    ),

                                    label = {
                                        Text("Nombre")
                                    },
                                    placeholder = {
                                        Text("Ej. Leer 10 páginas")
                                    },
                                    singleLine = true
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                Text(
                                    text = "Categoría",
                                    color = GrayText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    FilterChip(
                                        selected = categoriaNuevoHabit == "LECTURA",
                                        onClick = {
                                            categoriaNuevoHabit = "LECTURA"
                                            errorCrearHabit = null
                                        },
                                        label = {
                                            Text("📚 Lectura")
                                        }
                                    )

                                    FilterChip(
                                        selected = categoriaNuevoHabit == "EJERCICIO",
                                        onClick = {
                                            categoriaNuevoHabit = "EJERCICIO"
                                            errorCrearHabit = null
                                        },
                                        label = {
                                            Text("🏃 Ejercicio")
                                        }
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    FilterChip(
                                        selected = categoriaNuevoHabit == "BIENESTAR",
                                        onClick = {
                                            categoriaNuevoHabit = "BIENESTAR"
                                            errorCrearHabit = null
                                        },
                                        label = {
                                            Text("🧘 Bienestar")
                                        }
                                    )

                                    FilterChip(
                                        selected = categoriaNuevoHabit == "DESCANSO",
                                        onClick = {
                                            categoriaNuevoHabit = "DESCANSO"
                                            errorCrearHabit = null
                                        },
                                        label = {
                                            Text("🌙 Descanso")
                                        }
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                OutlinedTextField(
                                    value = descripcionNuevoHabit,
                                    onValueChange = {
                                        descripcionNuevoHabit = it
                                        errorCrearHabit = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),

                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = WhiteSoft,
                                        unfocusedTextColor = WhiteSoft,
                                        focusedLabelColor = PrimaryLight,
                                        unfocusedLabelColor = GrayText,
                                        focusedBorderColor = PrimaryLight,
                                        unfocusedBorderColor = GrayText,
                                        cursorColor = PrimaryLight
                                    ),

                                    label = {
                                        Text("Descripción")
                                    },
                                    placeholder = {
                                        Text("Describe el hábito")
                                    },
                                    minLines = 3
                                )

                                if (errorCrearHabit != null) {

                                    Spacer(
                                        modifier = Modifier.height(10.dp)
                                    )

                                    Text(
                                        text = errorCrearHabit!!,
                                        color = Color(0xFFFF8A8A),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        },

                        confirmButton = {

                            Button(

                                onClick = {

                                    val uid = auth.currentUser?.uid

                                    if (uid == null) {

                                        errorCrearHabit =
                                            "No hay un usuario autenticado."

                                        return@Button
                                    }

                                    if (nombreNuevoHabit.isBlank()) {

                                        errorCrearHabit =
                                            "Escribe el nombre del hábito."

                                        return@Button
                                    }

                                    if (categoriaNuevoHabit.isBlank()) {

                                        errorCrearHabit =
                                            "Selecciona una categoría."

                                        return@Button
                                    }

                                    if (descripcionNuevoHabit.isBlank()) {

                                        errorCrearHabit =
                                            "Escribe una descripción."

                                        return@Button
                                    }

                                    creandoHabit = true
                                    errorCrearHabit = null

                                    if (editandoHabit && habitSeleccionado != null) {

                                        repository.editarHabit(

                                            uid = uid,

                                            habitId =
                                                habitSeleccionado!!.id,

                                            nombre =
                                                nombreNuevoHabit.trim(),

                                            categoria =
                                                categoriaNuevoHabit
                                                    .trim()
                                                    .uppercase(),

                                            descripcion =
                                                descripcionNuevoHabit.trim(),

                                            onSuccess = {

                                                repository.obtenerHabitos(

                                                    uid = uid,

                                                    onSuccess = { lista ->

                                                        habitos = lista

                                                        creandoHabit = false
                                                        mostrarCrearHabit = false
                                                        editandoHabit = false
                                                        habitSeleccionado = null

                                                        nombreNuevoHabit = ""
                                                        categoriaNuevoHabit = ""
                                                        descripcionNuevoHabit = ""
                                                    },

                                                    onError = { error ->

                                                        creandoHabit = false

                                                        errorCrearHabit =
                                                            error.message
                                                                ?: "No se pudieron actualizar los hábitos."
                                                    }
                                                )
                                            },

                                            onError = { error ->

                                                creandoHabit = false

                                                errorCrearHabit =
                                                    error.message
                                                        ?: "No se pudo editar el hábito."
                                            }
                                        )

                                    } else {

                                        if (habitos.size >= 7) {

                                            creandoHabit = false

                                            errorCrearHabit =
                                                "Has alcanzado el máximo de 7 hábitos."

                                            return@Button
                                        }

                                        repository.crearHabit(

                                            uid = uid,

                                            nombre =
                                                nombreNuevoHabit.trim(),

                                            categoria =
                                                categoriaNuevoHabit
                                                    .trim()
                                                    .uppercase(),

                                            descripcion =
                                                descripcionNuevoHabit.trim(),

                                            xp = 10,

                                            generadoPorIA = false,

                                            onSuccess = {

                                                repository.obtenerHabitos(

                                                    uid = uid,

                                                    onSuccess = { lista ->

                                                        habitos = lista

                                                        creandoHabit = false
                                                        mostrarCrearHabit = false

                                                        nombreNuevoHabit = ""
                                                        categoriaNuevoHabit = ""
                                                        descripcionNuevoHabit = ""
                                                    },

                                                    onError = { error ->

                                                        creandoHabit = false

                                                        errorCrearHabit =
                                                            error.message
                                                                ?: "No se pudieron actualizar los hábitos."
                                                    }
                                                )
                                            },

                                            onError = { error ->

                                                creandoHabit = false

                                                errorCrearHabit =
                                                    error.message
                                                        ?: "No se pudo crear el hábito."
                                            }
                                        )
                                    }
                                },

                                enabled = !creandoHabit,

                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary
                                )
                            ) {

                                Text(
                                    text = if (creandoHabit) {
                                        "Guardando..."
                                    } else {
                                        "Guardar"
                                    },
                                    color = WhiteSoft
                                )
                            }
                        },

                        dismissButton = {

                            TextButton(

                                onClick = {

                                    if (!creandoHabit) {
                                        mostrarCrearHabit = false
                                    }
                                }

                            ) {

                                Text(
                                    text = "Cancelar",
                                    color = PrimaryLight
                                )
                            }
                        },

                        containerColor = CardColor
                    )
                }

                if (mostrarIA) {

                    AlertDialog(

                        onDismissRequest = {
                            mostrarIA = false
                        },

                        title = {
                            Text(
                                text = "✨ Crear hábitos con IA",
                                color = WhiteSoft,
                                fontWeight = FontWeight.Bold
                            )
                        },

                        text = {

                            Column {

                                Text(
                                    text = "¿Qué quieres mejorar?",
                                    color = GrayText,
                                    fontSize = 14.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                OutlinedTextField(
                                    value = objetivoIA,

                                    onValueChange = {
                                        objetivoIA = it
                                    },

                                    modifier = Modifier.fillMaxWidth(),

                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = WhiteSoft,
                                        unfocusedTextColor = WhiteSoft,
                                        focusedLabelColor = PrimaryLight,
                                        unfocusedLabelColor = GrayText,
                                        focusedBorderColor = PrimaryLight,
                                        unfocusedBorderColor = GrayText,
                                        cursorColor = PrimaryLight
                                    ),

                                    label = {
                                        Text("Tu objetivo")
                                    },

                                    placeholder = {
                                        Text("Ej. Quiero mejorar mi sueño")
                                    },

                                    minLines = 3
                                )
                                if (errorCrearHabit != null) {

                                    Spacer(
                                        modifier = Modifier.height(10.dp)
                                    )

                                    Text(
                                        text = errorCrearHabit!!,
                                        color = Color(0xFFFF8A8A),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        },

                        confirmButton = {

                            Button(
                                onClick = {

                                    if (objetivoIA.isBlank()) {
                                        return@Button
                                    }

                                    generandoIA = true

                                    val contextoHabitos = habitos.joinToString("\n") { habit ->
                                        "- ${habit.nombre} (${habit.categoria}): ${habit.descripcion}"
                                    }

                                    scope.launch {

                                        val uid = auth.currentUser?.uid

                                        if (uid == null) {
                                            generandoIA = false
                                            return@launch
                                        }

                                        val documentoDiario =
                                            db.collection("diarios")
                                                .document(uid)
                                                .collection("entradas")
                                                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                                                .limit(1)
                                                .get()
                                                .await()

                                        val contextoDiario =
                                            if (!documentoDiario.isEmpty) {
                                                documentoDiario.documents[0]
                                                    .getString("texto")
                                                    ?: ""
                                            } else {
                                                ""
                                            }

                                        val documentosChat =
                                            db.collection("usuarios")
                                                .document(uid)
                                                .collection("chat")
                                                .orderBy(
                                                    "fecha",
                                                    com.google.firebase.firestore.Query.Direction.DESCENDING
                                                )
                                                .limit(10)
                                                .get()
                                                .await()

                                        val contextoChat =
                                            documentosChat.documents
                                                .reversed()
                                                .joinToString("\n") { documento ->

                                                    val texto =
                                                        documento.getString("texto") ?: ""

                                                    val esUsuario =
                                                        documento.getBoolean("isUser") ?: false

                                                    if (esUsuario) {
                                                        "Usuario: $texto"
                                                    } else {
                                                        "NeuraBloom: $texto"
                                                    }
                                                }

                                        val resultadoIA =
                                            analizarHabitosConIA(
                                                objetivo = objetivoIA,
                                                habitos = contextoHabitos,
                                                diario = contextoDiario,
                                                chat = contextoChat
                                            )



                                        if (resultadoIA == null) {

                                            Log.e(
                                                "IA_HABITOS",
                                                "La IA no devolvió recomendaciones."
                                            )

                                            errorCrearHabit =
                                                "No se pudieron generar hábitos con IA."

                                            mostrarIA = true

                                            return@launch
                                        }

                                        recomendacionesIA =
                                            resultadoIA.recommendations

                                        seleccionadasIA =
                                            emptySet()

                                        mostrarIA = false

                                        mostrarRecomendacionesIA = true
                                    }
                                },

                                enabled = objetivoIA.isNotBlank() && !generandoIA,

                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary
                                )
                            ) {

                                Text(
                                    text = if (generandoIA) {
                                        "🧠 Generando..."
                                    } else {
                                        "✨ Generar hábitos"
                                    },
                                    color = WhiteSoft
                                )
                            }
                        },

                        dismissButton = {

                            TextButton(
                                onClick = {
                                    mostrarIA = false
                                }
                            ) {

                                Text(
                                    text = "Cancelar",
                                    color = PrimaryLight
                                )
                            }
                        },

                        containerColor = CardColor
                    )
                }

                if (mostrarRecomendacionesIA) {

                    AlertDialog(

                        onDismissRequest = {
                            mostrarRecomendacionesIA = false
                        },

                        title = {
                            Text(
                                text = "✨ Recomendaciones",
                                color = WhiteSoft,
                                fontWeight = FontWeight.Bold
                            )
                        },

                        text = {

                            Column {

                                Text(
                                    text = "Selecciona los hábitos que quieras agregar.",
                                    color = GrayText,
                                    fontSize = 14.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                recomendacionesIA.forEachIndexed { index, recomendacion ->

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Checkbox(
                                            checked =
                                                seleccionadasIA.contains(index),

                                            onCheckedChange = { seleccionado ->

                                                seleccionadasIA =
                                                    if (seleccionado) {
                                                        seleccionadasIA + index
                                                    } else {
                                                        seleccionadasIA - index
                                                    }
                                            },

                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Primary,
                                                uncheckedColor = GrayText,
                                                checkmarkColor = WhiteSoft
                                            )
                                        )

                                        Text(
                                            text = recomendacion.nombre,
                                            color = WhiteSoft,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        },

                        confirmButton = {

                            Button(
                                onClick = {

                                    val uid = auth.currentUser?.uid

                                    if (uid == null) {
                                        return@Button
                                    }

                                    val espaciosDisponibles =
                                        7 - habitos.size

                                    val seleccionadas =
                                        seleccionadasIA
                                            .sorted()
                                            .take(espaciosDisponibles)

                                    if (seleccionadas.isEmpty()) {
                                        return@Button
                                    }

                                    var pendientes =
                                        seleccionadas.size

                                    seleccionadas.forEach { index ->

                                        val recomendacion =
                                            recomendacionesIA[index]

                                        repository.crearHabit(

                                            uid = uid,

                                            nombre = recomendacion.nombre,

                                            categoria = recomendacion.categoria,

                                            descripcion = recomendacion.descripcion,

                                            xp = 10,

                                            generadoPorIA = true,

                                            onSuccess = {

                                                pendientes--

                                                if (pendientes == 0) {

                                                    repository.obtenerHabitos(

                                                        uid = uid,

                                                        onSuccess = { lista ->

                                                            habitos = lista

                                                            mostrarRecomendacionesIA = false
                                                            seleccionadasIA = emptySet()
                                                            recomendacionesIA = emptyList()
                                                        },

                                                        onError = { error ->

                                                            println(
                                                                "Error al actualizar hábitos: ${error.message}"
                                                            )
                                                        }
                                                    )
                                                }
                                            },

                                            onError = { error ->

                                                pendientes--

                                                println(
                                                    "Error al crear hábito IA: ${error.message}"
                                                )

                                                if (pendientes == 0) {
                                                    mostrarRecomendacionesIA = false
                                                }
                                            }
                                        )
                                    }
                                },

                                enabled = seleccionadasIA.isNotEmpty(),

                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary
                                )
                            ) {

                                Text(
                                    text = "Agregar seleccionados",
                                    color = WhiteSoft
                                )
                            }
                        },

                        dismissButton = {

                            TextButton(
                                onClick = {
                                    mostrarRecomendacionesIA = false
                                }
                            ) {

                                Text(
                                    text = "Cancelar",
                                    color = PrimaryLight
                                )
                            }
                        },

                        containerColor = CardColor
                    )
                }

                if (mostrarConfirmarEliminar && habitSeleccionado != null) {

                    AlertDialog(

                        onDismissRequest = {
                            if (!eliminandoHabit) {
                                mostrarConfirmarEliminar = false
                            }
                        },

                        title = {
                            Text(
                                text = "¿Eliminar hábito?",
                                color = WhiteSoft,
                                fontWeight = FontWeight.Bold
                            )
                        },

                        text = {
                            Text(
                                text = "¿Seguro que quieres eliminar \"${habitSeleccionado?.nombre}\"? Esta acción no se puede deshacer.",
                                color = GrayText
                            )
                        },

                        confirmButton = {

                            Button(
                                onClick = {

                                    val uid =
                                        auth.currentUser?.uid

                                    val habit =
                                        habitSeleccionado

                                    if (uid == null || habit == null) {
                                        mostrarConfirmarEliminar = false
                                        return@Button
                                    }

                                    eliminandoHabit = true

                                    repository.eliminarHabit(

                                        uid = uid,

                                        habitId = habit.id,

                                        onSuccess = {

                                            habitos =
                                                habitos.filter {
                                                    it.id != habit.id
                                                }

                                            eliminandoHabit = false
                                            mostrarConfirmarEliminar = false
                                            habitSeleccionado = null
                                        },

                                        onError = { error ->

                                            eliminandoHabit = false

                                            println(
                                                "Error al eliminar hábito: ${error.message}"
                                            )
                                        }
                                    )

                                },

                                enabled = !eliminandoHabit,

                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFB91C1C)
                                )
                            ) {

                                Text(
                                    text = if (eliminandoHabit) {
                                        "Eliminando..."
                                    } else {
                                        "Eliminar"
                                    },
                                    color = WhiteSoft
                                )
                            }
                        },

                        dismissButton = {

                            TextButton(
                                onClick = {
                                    if (!eliminandoHabit) {
                                        mostrarConfirmarEliminar = false
                                    }
                                }
                            ) {

                                Text(
                                    text = "Cancelar",
                                    color = PrimaryLight
                                )
                            }
                        },

                        containerColor = CardColor
                    )
                }



                Text(
                    text = if (rachaHabitos == 1) {
                        "🔥 Racha de hábitos: 1 día"
                    } else {
                        "🔥 Racha de hábitos: $rachaHabitos días"
                    },
                    color = WhiteSoft,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "⭐ XP acumulado: $xpUsuario",
                    color = GrayText,
                    fontSize = 14.sp
                )
            }
        }
    }
}

