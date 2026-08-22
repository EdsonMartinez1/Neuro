package com.example.navhost1.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.sin
import kotlin.random.Random

// Paleta NeuraBloom Dark Premium
private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF090D16)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFC084FC)
private val PurpleGlow = Color(0xFF6D28D9)

private val UserBubble = Color(0xFF7C3AED)
private val BotBubble = Color(0xFF1E293B).copy(alpha = 0.9f)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)
private val BorderColor = Color(0xFF334155)

data class Message(
    val text: String,
    val isUser: Boolean
)

// Clase para controlar la animación individual de cada burbuja
private class AnimatedBubble(
    var x: Float = Random.nextFloat(),
    var y: Float = Random.nextFloat(),
    var baseRadius: Float = Random.nextFloat() * 18f + 8f,
    val speedY: Float = Random.nextFloat() * 0.0012f + 0.0004f,
    val phase: Float = Random.nextFloat() * 6.28f,
    val popThreshold: Float = Random.nextFloat() * 0.35f + 0.65f
) {
    var isPopping by mutableStateOf(false)
    var popProgress by mutableFloatStateOf(0f)

    fun update() {
        if (isPopping) {
            popProgress += 0.08f
            if (popProgress >= 1f) {
                // Reset de la burbuja al fondo cuando termina la explosión
                isPopping = false
                popProgress = 0f
                y = 1.05f
                x = Random.nextFloat()
                baseRadius = Random.nextFloat() * 18f + 8f
            }
        } else {
            y -= speedY
            // Activa la explosión de forma estocástica o al llegar arriba
            if (y < 0.1f && Random.nextFloat() < 0.015f || y < -0.05f) {
                isPopping = true
            }
        }
    }
}

// Sugerencias rápidas predeterminadas
private val quickSuggestions = listOf(
    "😟 Me siento ansioso",
    "😴 Tengo problemas para dormir",
    "💜 Necesito un consejo rápido",
    "🗣️ Solo quiero desahogarme"
)

suspend fun enviarMensajeAlChat(mensaje: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://openai-api-worker.ed-ia-app.workers.dev")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = JSONObject().apply {
                put("type", "chat")
                put("message", mensaje)
            }

            connection.outputStream.use { output ->
                output.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val response = if (connection.responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }

            Log.e("CHAT_IA", "HTTP ${connection.responseCode}: $response")
            connection.disconnect()

            val json = JSONObject(response)
            json.getString("result")

        } catch (e: Exception) {
            Log.e("CHAT_IA", "Error al conectar con la IA", e)
            null
        }
    }
}

suspend fun cargarHistorialChat(uid: String): List<Message> {
    val db = FirebaseFirestore.getInstance()

    val snapshot = db.collection("usuarios")
        .document(uid)
        .collection("chat")
        .orderBy("fecha")
        .get()
        .await()

    return snapshot.documents.mapNotNull { documento ->
        val texto = documento.getString("texto")
        val isUser = documento.getBoolean("isUser")

        if (texto != null && isUser != null) {
            Message(text = texto, isUser = isUser)
        } else {
            null
        }
    }
}

@Composable
fun ChatScreen(navController: NavController) {
    var message by remember { mutableStateOf(TextFieldValue("")) }
    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val initialGreeting = stringResource(R.string.chat_ia_saludo)

    var messages by remember {
        mutableStateOf(listOf(Message(initialGreeting, false)))
    }

    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Estado del motor de burbujas dinámicas
    val bubbles = remember { List(16) { AnimatedBubble() } }

    // Loop de animación fluido
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleLoop")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animTime"
    )

    // Actualizar lógica de burbujas en cada frame
    LaunchedEffect(animTime) {
        bubbles.forEach { it.update() }
    }

    // Carga de historial
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                val historial = cargarHistorialChat(uid)
                if (historial.isNotEmpty()) {
                    messages = historial
                }
            } catch (e: Exception) {
                Log.e("CHAT_FIREBASE", "Error al cargar historial", e)
            }
        }
    }

    // Función enviadora centralizada
    fun enviarMensaje(textoEnvio: String) {
        if (textoEnvio.isBlank() || isTyping) return

        messages = messages + Message(textoEnvio, true)
        message = TextFieldValue("")
        isTyping = true

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios")
                .document(uid)
                .collection("chat")
                .add(
                    mapOf(
                        "texto" to textoEnvio,
                        "isUser" to true,
                        "fecha" to System.currentTimeMillis()
                    )
                )
        }

        scope.launch {
            val respuestaIA = enviarMensajeAlChat(textoEnvio)
            isTyping = false

            if (respuestaIA != null) {
                messages = messages + Message(respuestaIA, false)

                if (uid != null) {
                    db.collection("usuarios")
                        .document(uid)
                        .collection("chat")
                        .add(
                            mapOf(
                                "texto" to respuestaIA,
                                "isUser" to false,
                                "fecha" to System.currentTimeMillis()
                            )
                        )
                }
            } else {
                messages = messages + Message(
                    "No pude conectarme con NeuraBloom en este momento. Inténtalo nuevamente. 💜",
                    false
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(BackgroundTop, BackgroundBottom))
            )
    ) {
        // Fondo Animado de Burbujas Interactivas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            bubbles.forEach { b ->
                val centerX = b.x * canvasWidth
                val centerY = b.y * canvasHeight

                if (b.isPopping) {
                    // Animación de Explosión (Onda expansiva que desaparece)
                    val burstRadius = (b.baseRadius * (1f + b.popProgress * 1.8f)).dp.toPx()
                    val alpha = (1f - b.popProgress).coerceIn(0f, 1f)

                    drawCircle(
                        color = PrimaryLight.copy(alpha = alpha * 0.6f),
                        radius = burstRadius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = (2f * (1f - b.popProgress)).dp.toPx())
                    )
                } else {
                    // Pulsación suave de tamaño
                    val currentScale = 1f + 0.18f * sin(animTime + b.phase)
                    val currentRadius = (b.baseRadius * currentScale).dp.toPx()

                    // Relleno translúcido
                    drawCircle(
                        color = PrimaryLight.copy(alpha = 0.12f),
                        radius = currentRadius,
                        center = Offset(centerX, centerY)
                    )

                    // Borde de la burbuja
                    drawCircle(
                        color = PrimaryLight.copy(alpha = 0.35f),
                        radius = currentRadius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }
            }
        }

        // Resplandor ambiental de fondo
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleGlow.copy(alpha = 0.22f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Header del Chat
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F172A).copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = WhiteSoft,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(8.dp, CircleShape, spotColor = Primary)
                            .background(
                                Brush.linearGradient(listOf(Primary, PrimaryLight)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.chat_ia_titulo),
                            color = WhiteSoft,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "En línea",
                                color = GrayText,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Lista de Mensajes
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = true,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (isTyping) {
                    item {
                        TypingIndicator()
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                items(messages.reversed()) { msg ->
                    MessageBubble(msg)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Sugerencias Rápidas
            if (!isTyping) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickSuggestions) { suggestion ->
                        Surface(
                            modifier = Modifier.clickable { enviarMensaje(suggestion) },
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                PrimaryLight.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = suggestion,
                                color = WhiteSoft,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Área de Entrada de Mensaje
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F172A).copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.chat_ia_msg),
                                color = GrayText.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(),
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor.copy(alpha = 0.6f),
                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,
                            cursorColor = PrimaryLight
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = { enviarMensaje(message.text) },
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, CircleShape, spotColor = Primary)
                            .background(
                                Brush.linearGradient(listOf(Primary, PrimaryLight)),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Primary, PrimaryLight))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isUser) 18.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 18.dp
            ),
            color = if (message.isUser) UserBubble else BotBubble,
            border = if (!message.isUser) androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.08f)
            ) else null,
            shadowElevation = 3.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = parseMarkdown(message.text),
                    color = WhiteSoft,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "dot1"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), RepeatMode.Reverse),
        label = "dot2"
    )
    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), RepeatMode.Reverse),
        label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Primary, PrimaryLight))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 4.dp,
                bottomEnd = 18.dp
            ),
            color = BotBubble,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .alpha(alpha1)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .alpha(alpha2)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .alpha(alpha3)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                )
            }
        }
    }
}

// Parser simple para detectar negritas (**texto**) en el chat
private fun parseMarkdown(text: String) = buildAnnotatedString {
    val parts = text.split("**")
    parts.forEachIndexed { index, part ->
        if (index % 2 == 1) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = PrimaryLight)) {
                append(part)
            }
        } else {
            append(part)
        }
    }
}