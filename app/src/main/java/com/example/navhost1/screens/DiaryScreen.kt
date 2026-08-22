package com.example.navhost1.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
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
private val NotebookPaperColor = Color(0xFF131C2E)
private val NotebookLineColor = Color(0xFF334155).copy(alpha = 0.5f)
private val NotebookMarginColor = Color(0xFFEF4444).copy(alpha = 0.35f)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var entradas by remember { mutableStateOf<List<EntradaDiario>>(emptyList()) }
    var textValue by remember { mutableStateOf(TextFieldValue("")) }
    var saved by remember { mutableStateOf(false) }

    // Scroll state principal de la pantalla
    val mainScrollState = rememberScrollState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    // Estados de Formato de Texto
    var fontSizeSp by remember { mutableStateOf(16) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    var isStrikethrough by remember { mutableStateOf(false) }

    var respuestaIA by remember { mutableStateOf("") }
    var emocionIA by remember { mutableStateOf("") }
    var intensidadIA by remember { mutableStateOf(0) }
    var recomendacionIA by remember { mutableStateOf("") }
    var analizandoIA by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Mantiene visible la línea exacta del cursor sin generar espacios extra fofos
    LaunchedEffect(textValue.selection, textValue.text) {
        textLayoutResult?.let { layout ->
            val cursorOffset = textValue.selection.start
            if (cursorOffset <= layout.layoutInput.text.length) {
                val line = layout.getLineForOffset(cursorOffset)
                val lineBottom = layout.getLineBottom(line)
                val lineTop = layout.getLineTop(line)

                bringIntoViewRequester.bringIntoView(
                    Rect(
                        left = 0f,
                        top = lineTop,
                        right = layout.size.width.toFloat(),
                        bottom = lineBottom // Ajustado al límite exacto de la línea
                    )
                )
            }
        }
    }

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

    fun toggleStyle(bold: Boolean = false, italic: Boolean = false, underline: Boolean = false, strikethrough: Boolean = false) {
        val selection = textValue.selection
        if (selection.collapsed) {
            if (bold) isBold = !isBold
            if (italic) isItalic = !isItalic
            if (underline) isUnderline = !isUnderline
            if (strikethrough) isStrikethrough = !isStrikethrough
            return
        }

        val annotated = buildAnnotatedString {
            append(textValue.text)
            val styles = mutableListOf<TextDecoration>()
            if (underline || isUnderline) styles.add(TextDecoration.Underline)
            if (strikethrough || isStrikethrough) styles.add(TextDecoration.LineThrough)

            val combinedDecoration = when {
                styles.contains(TextDecoration.Underline) && styles.contains(TextDecoration.LineThrough) ->
                    TextDecoration.combine(styles)
                styles.contains(TextDecoration.Underline) -> TextDecoration.Underline
                styles.contains(TextDecoration.LineThrough) -> TextDecoration.LineThrough
                else -> null
            }

            addStyle(
                style = SpanStyle(
                    fontWeight = if (bold != isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (italic != isItalic) FontStyle.Italic else FontStyle.Normal,
                    textDecoration = combinedDecoration
                ),
                start = selection.min,
                end = selection.max
            )
        }

        textValue = textValue.copy(annotatedString = annotated)
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
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(mainScrollState)
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

            // Tarjeta Estilo Cuaderno de Notas
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = PrimaryLight,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Mi Bloc de Notas",
                            color = WhiteSoft,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Barra de Formato de Texto
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Bold
                            IconButton(
                                onClick = { toggleStyle(bold = true) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isBold) Primary.copy(alpha = 0.4f) else Color.Transparent)
                            ) {
                                Icon(Icons.Default.FormatBold, contentDescription = "Negrita", tint = WhiteSoft, modifier = Modifier.size(18.dp))
                            }

                            // Italic
                            IconButton(
                                onClick = { toggleStyle(italic = true) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isItalic) Primary.copy(alpha = 0.4f) else Color.Transparent)
                            ) {
                                Icon(Icons.Default.FormatItalic, contentDescription = "Cursiva", tint = WhiteSoft, modifier = Modifier.size(18.dp))
                            }

                            // Underline
                            IconButton(
                                onClick = { toggleStyle(underline = true) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isUnderline) Primary.copy(alpha = 0.4f) else Color.Transparent)
                            ) {
                                Icon(Icons.Default.FormatUnderlined, contentDescription = "Subrayado", tint = WhiteSoft, modifier = Modifier.size(18.dp))
                            }

                            // Strikethrough
                            IconButton(
                                onClick = { toggleStyle(strikethrough = true) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isStrikethrough) Primary.copy(alpha = 0.4f) else Color.Transparent)
                            ) {
                                Icon(Icons.Default.FormatStrikethrough, contentDescription = "Tachado", tint = WhiteSoft, modifier = Modifier.size(18.dp))
                            }
                        }

                        // Selector de tamaño de letra
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.FormatSize, contentDescription = "Tamaño", tint = GrayText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            listOf(14, 16, 18).forEach { size ->
                                Text(
                                    text = "${size}sp",
                                    fontSize = 11.sp,
                                    fontWeight = if (fontSizeSp == size) FontWeight.Bold else FontWeight.Normal,
                                    color = if (fontSizeSp == size) PrimaryLight else GrayText,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .clickable { fontSizeSp = size }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hoja de Cuaderno Pautada
                    val lineHeightPx = with(LocalDensity.current) { (fontSizeSp * 1.85f).sp.toPx() }
                    val marginPaddingPx = with(LocalDensity.current) { 36.dp.toPx() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NotebookPaperColor)
                            .border(1.dp, BorderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    ) {
                        // Canvas dinámico para renglones
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height

                            drawLine(
                                color = NotebookMarginColor,
                                start = Offset(marginPaddingPx, 0f),
                                end = Offset(marginPaddingPx, canvasHeight),
                                strokeWidth = 1.5f
                            )

                            var currentY = lineHeightPx
                            while (currentY < canvasHeight) {
                                drawLine(
                                    color = NotebookLineColor,
                                    start = Offset(0f, currentY),
                                    end = Offset(canvasWidth, currentY),
                                    strokeWidth = 1f
                                )
                                currentY += lineHeightPx
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 44.dp, end = 16.dp, top = 10.dp, bottom = 8.dp)
                        ) {
                            val decorations = mutableListOf<TextDecoration>()
                            if (isUnderline) decorations.add(TextDecoration.Underline)
                            if (isStrikethrough) decorations.add(TextDecoration.LineThrough)

                            val activeDecoration = when {
                                decorations.contains(TextDecoration.Underline) && decorations.contains(TextDecoration.LineThrough) ->
                                    TextDecoration.combine(decorations)
                                decorations.contains(TextDecoration.Underline) -> TextDecoration.Underline
                                decorations.contains(TextDecoration.LineThrough) -> TextDecoration.LineThrough
                                else -> TextDecoration.None
                            }

                            BasicTextField(
                                value = textValue,
                                onValueChange = {
                                    textValue = it
                                    saved = false
                                },
                                onTextLayout = { textLayoutResult = it },
                                textStyle = TextStyle(
                                    color = WhiteSoft,
                                    fontSize = fontSizeSp.sp,
                                    lineHeight = (fontSizeSp * 1.85f).sp,
                                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                    textDecoration = activeDecoration
                                ),
                                cursorBrush = SolidColor(PrimaryLight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 180.dp)
                                    .bringIntoViewRequester(bringIntoViewRequester),
                                decorationBox = { innerTextField ->
                                    if (textValue.text.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.diario_subtitulo),
                                            color = GrayText.copy(alpha = 0.5f),
                                            fontSize = fontSizeSp.sp,
                                            lineHeight = (fontSizeSp * 1.85f).sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val uid = auth.currentUser?.uid
                            val textoCompleto = textValue.text
                            if (uid != null && textoCompleto.isNotBlank()) {
                                analizandoIA = true
                                respuestaIA = ""

                                scope.launch {
                                    val resultadoIA = analizarDiarioConIA(textoCompleto)

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
                                        "texto" to textoCompleto,
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
                                                if (textoCompleto.length >= 200) xpActual += 10
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
                                                    texto = textoCompleto,
                                                    fecha = ahora,
                                                    emocion = resultadoIA.emotion
                                                )
                                            ) + entradas

                                            saved = true
                                            textValue = TextFieldValue("")
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
                                color = WhiteSoft.copy(alpha = 0.9f),
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