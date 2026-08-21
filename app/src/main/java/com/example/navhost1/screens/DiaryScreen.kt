package com.example.navhost1.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// Paleta NeuraBloom Dark Premium
private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF090D16)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFC084FC)
private val PurpleGlow = Color(0xFF6D28D9)

private val CardColor = Color(0xFF1E293B).copy(alpha = 0.75f)
private val FieldColor = Color(0xFF0F172A).copy(alpha = 0.6f)
private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)
private val BorderColor = Color(0xFF334155)

fun detectarEmocion(texto: String): String {
    val contenido = texto.lowercase()
    return when {
        contenido.contains("feliz") || contenido.contains("alegre") || contenido.contains("contento") -> "😊 Feliz"
        contenido.contains("triste") || contenido.contains("deprimido") || contenido.contains("llorar") -> "😔 Triste"
        contenido.contains("ansioso") || contenido.contains("ansiedad") || contenido.contains("nervioso") -> "😟 Ansioso"
        contenido.contains("cansado") || contenido.contains("agotado") || contenido.contains("sin energía") -> "😴 Cansado"
        contenido.contains("enojado") || contenido.contains("molesto") || contenido.contains("furioso") -> "😡 Enojado"
        else -> "😌 Tranquilo"
    }
}

data class EntradaDiario(
    val texto: String = "",
    val fecha: Long = 0,
    val emocion: String = "😌 Tranquilo"
)

data class DiaryAIResponse(
    val emotion: String,
    val intensity: Int,
    val response: String,
    val recommendation: String
)

private data class DiaryParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

suspend fun analizarDiarioConIA(texto: String): DiaryAIResponse? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://openai-api-worker.ed-ia-app.workers.dev")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = JSONObject().apply {
                put("type", "diary")
                put("message", texto)
            }

            connection.outputStream.use { output ->
                output.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = JSONObject(response)

            DiaryAIResponse(
                emotion = json.getString("emotion"),
                intensity = json.getInt("intensity"),
                response = json.getString("response"),
                recommendation = json.getString("recommendation")
            )
        } catch (e: Exception) {
            Log.e("IA_DIARIO", "Error al conectar con la IA", e)
            null
        }
    }
}

@Composable
fun DiaryScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var entradas by remember { mutableStateOf<List<EntradaDiario>>(emptyList()) }
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var saved by remember { mutableStateOf(false) }

    var respuestaIA by remember { mutableStateOf("") }
    var emocionIA by remember { mutableStateOf("") }
    var intensidadIA by remember { mutableStateOf(0) }
    var recomendacionIA by remember { mutableStateOf("") }
    var analizandoIA by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Partículas ambientales de fondo
    val particles = remember {
        List(20) {
            DiaryParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 1.5f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                speedY = Random.nextFloat() * 0.0008f + 0.0003f
            )
        }
    }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("diarios")
            .document(uid)
            .collection("entradas")
            .orderBy("fecha")
            .get()
            .addOnSuccessListener { result ->
                entradas = result.documents.map {
                    EntradaDiario(
                        texto = it.getString("texto") ?: "",
                        fecha = it.getLong("fecha") ?: 0,
                        emocion = it.getString("emocion") ?: "😌 Tranquilo"
                    )
                }.reversed()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundTop, BackgroundBottom)
                )
            )
    ) {
        // Fondo de Partículas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                p.y -= p.speedY
                if (p.y < 0f) p.y = 1f

                drawCircle(
                    color = PrimaryLight.copy(alpha = p.alpha),
                    radius = p.radius.dp.toPx(),
                    center = Offset(p.x * width, p.y * height)
                )
            }
        }

        // Resplandores Ambientales
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-90).dp, y = (-60).dp)
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
            // Header Top
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(12.dp, CircleShape, spotColor = Primary)
                        .background(
                            Brush.linearGradient(listOf(Primary, PrimaryLight)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = stringResource(R.string.diario_titulo),
                        color = WhiteSoft,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Expresa tus emociones libremente",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de Entrada de Diario
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = PrimaryLight,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Tu espacio seguro",
                            color = WhiteSoft,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Escribe cómo te sientes hoy, qué piensas o qué deseas mejorar.",
                        color = GrayText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            saved = false
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.diario_subtitulo),
                                color = GrayText.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldColor,
                            unfocusedContainerColor = FieldColor,
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor.copy(alpha = 0.8f),
                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,
                            cursorColor = PrimaryLight
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val uid = auth.currentUser?.uid
                            if (uid != null && text.text.isNotBlank()) {
                                analizandoIA = true
                                respuestaIA = ""
                                val textoEntrada = text.text

                                scope.launch {
                                    val resultadoIA = analizarDiarioConIA(textoEntrada)

                                    if (resultadoIA == null) {
                                        analizandoIA = false
                                        return@launch
                                    }

                                    respuestaIA = resultadoIA.response
                                    emocionIA = resultadoIA.emotion
                                    intensidadIA = resultadoIA.intensity
                                    recomendacionIA = resultadoIA.recommendation
                                    analizandoIA = false

                                    val ahora = System.currentTimeMillis()

                                    val diario = hashMapOf(
                                        "texto" to textoEntrada,
                                        "fecha" to ahora,
                                        "emocion" to resultadoIA.emotion,
                                        "intensidad" to resultadoIA.intensity,
                                        "respuestaIA" to resultadoIA.response,
                                        "recomendacionIA" to resultadoIA.recommendation
                                    )

                                    db.collection("diarios")
                                        .document(uid)
                                        .collection("entradas")
                                        .add(diario)
                                        .addOnSuccessListener {
                                            val userRef = db.collection("usuarios").document(uid)

                                            userRef.get().addOnSuccessListener { document ->
                                                var xpActual = document.getLong("xp") ?: 0
                                                val rachaActual = document.getLong("racha")?.toInt() ?: 0
                                                val ultimaEntrada = document.getLong("ultimaEntrada") ?: 0L

                                                val hoy = Calendar.getInstance().apply { timeInMillis = ahora }
                                                val ultimoDia = Calendar.getInstance().apply { timeInMillis = ultimaEntrada }

                                                val mismoDia = ultimaEntrada != 0L &&
                                                        hoy.get(Calendar.YEAR) == ultimoDia.get(Calendar.YEAR) &&
                                                        hoy.get(Calendar.DAY_OF_YEAR) == ultimoDia.get(Calendar.DAY_OF_YEAR)

                                                val ayer = Calendar.getInstance().apply {
                                                    timeInMillis = ahora
                                                    add(Calendar.DAY_OF_YEAR, -1)
                                                }

                                                val fueAyer = ultimaEntrada != 0L &&
                                                        ayer.get(Calendar.YEAR) == ultimoDia.get(Calendar.YEAR) &&
                                                        ayer.get(Calendar.DAY_OF_YEAR) == ultimoDia.get(Calendar.DAY_OF_YEAR)

                                                val nuevaRacha = when {
                                                    ultimaEntrada == 0L -> 1
                                                    mismoDia -> rachaActual
                                                    fueAyer -> rachaActual + 1
                                                    else -> 1
                                                }

                                                xpActual += 20
                                                if (textoEntrada.length >= 200) xpActual += 10
                                                if (nuevaRacha == 3 || nuevaRacha == 7) xpActual += 50

                                                val nuevoNivel = when {
                                                    xpActual >= 500 -> 5
                                                    xpActual >= 300 -> 4
                                                    xpActual >= 200 -> 3
                                                    xpActual >= 100 -> 2
                                                    else -> 1
                                                }

                                                val logros = hashMapOf(
                                                    "primer_diario" to true,
                                                    "racha_3" to (nuevaRacha >= 3),
                                                    "racha_7" to (nuevaRacha >= 7),
                                                    "arbol_nivel_5" to (nuevoNivel >= 5)
                                                )

                                                userRef.update(
                                                    mapOf(
                                                        "xp" to xpActual,
                                                        "arbolNivel" to nuevoNivel,
                                                        "racha" to nuevaRacha,
                                                        "ultimaEntrada" to ahora
                                                    )
                                                )

                                                userRef.collection("logros").document("estado").set(logros)
                                            }

                                            entradas = listOf(
                                                EntradaDiario(
                                                    texto = textoEntrada,
                                                    fecha = ahora,
                                                    emocion = resultadoIA.emotion
                                                )
                                            ) + entradas

                                            saved = true
                                            text = TextFieldValue("")
                                        }
                                }
                            }
                        },
                        enabled = !analizandoIA,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        if (analizandoIA) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Analizando...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.diario_guardar),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Confirmación de Guardado
                    AnimatedVisibility(saved) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Entrada guardada correctamente ✨",
                                    color = Color(0xFF10B981),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Resultado del análisis IA
            if (respuestaIA.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = Primary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryLight.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryLight,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Análisis de NeuraBloom IA",
                                color = PrimaryLight,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Emoción: $emocionIA",
                                color = WhiteSoft,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Intensidad: $intensidadIA/10",
                                color = GrayText,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val animatedProgress by animateFloatAsState(
                            targetValue = (intensidadIA / 10f).coerceIn(0f, 1f),
                            animationSpec = tween(durationMillis = 800),
                            label = "intensityProgress"
                        )

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = PrimaryLight,
                            trackColor = BorderColor
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = respuestaIA,
                            color = WhiteSoft,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "💡 Recomendación",
                            color = PrimaryLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = recomendacionIA,
                            color = WhiteSoft,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de Entradas Anteriores
            if (entradas.isNotEmpty()) {
                Text(
                    text = "Mis entradas anteriores",
                    color = WhiteSoft,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                entradas.forEach { entrada ->
                    val fechaFormateada = remember(entrada.fecha) {
                        if (entrada.fecha > 0) {
                            val sdf = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
                            sdf.format(Date(entrada.fecha))
                        } else ""
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = CardColor,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entrada.emocion,
                                    color = PrimaryLight,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                if (fechaFormateada.isNotEmpty()) {
                                    Text(
                                        text = fechaFormateada,
                                        color = GrayText,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = entrada.texto,
                                color = WhiteSoft,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}