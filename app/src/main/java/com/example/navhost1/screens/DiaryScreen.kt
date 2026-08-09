package com.example.navhost1.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import androidx.compose.ui.res.stringResource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

import kotlinx.coroutines.launch



private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)
private val FieldColor = Color(0xFF1F2937)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)
private val BorderColor = Color(0xFF334155)

fun detectarEmocion(texto: String): String {

    val contenido = texto.lowercase()

    return when {

        contenido.contains("feliz") ||
                contenido.contains("alegre") ||
                contenido.contains("contento") ->
            "😊 Feliz"

        contenido.contains("triste") ||
                contenido.contains("deprimido") ||
                contenido.contains("llorar") ->
            "😔 Triste"

        contenido.contains("ansioso") ||
                contenido.contains("ansiedad") ||
                contenido.contains("nervioso") ->
            "😟 Ansioso"

        contenido.contains("cansado") ||
                contenido.contains("agotado") ||
                contenido.contains("fatigado") ||
                contenido.contains("sin energía") ->
            "😴 Cansado"

        contenido.contains("enojado") ||
                contenido.contains("molesto") ||
                contenido.contains("furioso") ->
            "😡 Enojado"

        else ->
            "😌 Tranquilo"
    }
}

data class EntradaDiario(
    val texto: String = "",
    val fecha: Long = 0,
    val emocion: String = "😌 Tranquilo"
)

suspend fun analizarDiarioConIA(texto: String): String? {

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
                put("type", "diary")
                put("message", texto)
            }

            connection.outputStream.use { output ->
                output.write(
                    body.toString().toByteArray(Charsets.UTF_8)
                )
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            connection.disconnect()

            val json = JSONObject(response)

            json.getString("result")

        } catch (e: Exception) {

            Log.e(
                "IA_DIARIO",
                "Error al conectar con la IA",
                e
            )

            null
        }
    }
}
@Composable
fun DiaryScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var entradas by remember {
        mutableStateOf<List<EntradaDiario>>(emptyList())
    }

    var text by remember {
        mutableStateOf(TextFieldValue(""))
    }

    var saved by remember {
        mutableStateOf(false)
    }

    var respuestaIA by remember {
        mutableStateOf("")
    }

    var analizandoIA by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        Log.d("FIREBASE", "Intentando guardar diario")

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
                        emocion = it.getString("emocion")
                            ?: "😌 Tranquilo"
                    )
                }.reversed()
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

        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-90).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Primary.copy(alpha = 0.12f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = WhiteSoft
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
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

                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = stringResource(R.string.diario_titulo),
                        color = WhiteSoft,
                        fontSize = 24.sp,
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

            Spacer(modifier = Modifier.height(34.dp))

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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = PrimaryLight
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Tu espacio seguro",
                            color = WhiteSoft,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Escribe cómo te sientes hoy, qué piensas o qué deseas mejorar.",
                        color = GrayText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            saved = false
                        },
                        placeholder = {

                            Text(
                                text = stringResource(R.string.diario_subtitulo),
                                color = GrayText
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldColor,
                            unfocusedContainerColor = FieldColor,

                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,

                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,

                            cursorColor = Primary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {

                            val uid = auth.currentUser?.uid

                            Log.d("FIREBASE", "UID: $uid")

                            if (uid != null && text.text.isNotBlank()) {

                                analizandoIA = true
                                respuestaIA = ""

                                val textoEntrada = text.text

                                scope.launch {

                                    val resultadoIA =
                                        analizarDiarioConIA(textoEntrada)

                                    respuestaIA = resultadoIA ?: ""
                                    analizandoIA = false
                                }

                                val emocionDetectada =
                                    detectarEmocion(textoEntrada)

                                val diario = hashMapOf(

                                    "texto" to text.text,
                                    "fecha" to System.currentTimeMillis(),
                                    "emocion" to emocionDetectada
                                )

                                db.collection("diarios")
                                    .document(uid)
                                    .collection("entradas")
                                    .add(diario)
                                    .addOnSuccessListener {

                                        val userRef =
                                            db.collection("usuarios")
                                                .document(uid)

                                        val totalEntradas = entradas.size + 1

                                        userRef.get()
                                            .addOnSuccessListener { document ->

                                                var xpActual =
                                                    document.getLong("xp") ?: 0

                                                val rachaActual =
                                                    document.getLong("racha")
                                                        ?.toInt() ?: 0

                                                val ultimaEntrada =
                                                    document.getLong("ultimaEntrada")
                                                        ?: 0L

                                                val ahora = System.currentTimeMillis()

                                                val unDia = 24 * 60 * 60 * 1000L

                                                val diasTranscurridos =
                                                    (ahora - ultimaEntrada) / unDia

                                                val nuevaRacha = when {

                                                    ultimaEntrada == 0L -> 1

                                                    diasTranscurridos == 0L -> rachaActual

                                                    diasTranscurridos == 1L -> rachaActual + 1

                                                    else -> 1
                                                }



                                                // XP base
                                                xpActual += 20

// XP por diario largo
                                                if (text.text.length >= 200) {
                                                    xpActual += 10
                                                }

// Bonus de racha
                                                if (nuevaRacha == 3 || nuevaRacha == 7) {
                                                    xpActual += 50
                                                }

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

                                                userRef.collection("logros")
                                                    .document("estado")
                                                    .set(logros)
                                            }




                                        entradas = listOf(
                                            EntradaDiario(
                                                texto = text.text,
                                                fecha = System.currentTimeMillis(),
                                                emocion = emocionDetectada
                                            )
                                        ) + entradas

                                        saved = true

                                        text = TextFieldValue("")
                                    }

                                    .addOnFailureListener { e ->
                                        Log.e("FIREBASE", "Error al guardar", e)
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        )
                    )  {

                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = stringResource(R.string.diario_guardar),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (analizandoIA) {

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "🧠 Analizando tu entrada...",
                            color = PrimaryLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (respuestaIA.isNotBlank()) {

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = CardColor
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {

                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {

                                Text(
                                    text = "🤖 NeuraBloom IA",
                                    color = PrimaryLight,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = respuestaIA,
                                    color = WhiteSoft,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    AnimatedVisibility(saved) {

                        Column {

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Entrada guardada correctamente ✨",
                                color = Color(0xFF4ADE80),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            if (entradas.isNotEmpty()) {

                Text(
                    text = "Mis entradas",
                    color = WhiteSoft,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                entradas.forEach { entrada ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = CardColor
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = entrada.emocion,
                                color = PrimaryLight,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = entrada.texto,
                                color = WhiteSoft,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}