package com.example.navhost1.screens

import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Paleta NeuraBloom Dark Premium
private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF090D16)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFC084FC)
private val PurpleGlow = Color(0xFF6D28D9)

private val CardSolid = Color(0xFF1E293B)
private val FieldColor = Color(0xFF0F172A)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)
private val BorderColor = Color(0xFF334155)

data class MeditationStage(
    val startSecond: Int,
    val message: String
)

data class EmotionStateItem(
    val tag: String,
    val label: String,
    val icon: ImageVector
)

data class AudioTrackItem(
    val id: String,
    val nombre: String,
    val categoria: String,
    val icon: ImageVector,
    val rawResId: Int,
    var volumen: Float = 0.5f,
    var activo: Boolean = false
)

private data class ZenParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(navController: NavController) {

    var duracionSeleccionada by remember { mutableStateOf(1) }
    var sesionIniciada by remember { mutableStateOf(false) }
    var sesionCompletada by remember { mutableStateOf(false) }
    var segundosRestantes by remember { mutableStateOf(0) }

    var mensajeMeditacion by remember {
        mutableStateOf("Encuentra una postura cómoda y cierra suavemente los ojos.")
    }
    var vozGuiada by remember { mutableStateOf(true) }
    var modoAudio by remember { mutableStateOf("voz_ambiente") }
    var estadoEmocional by remember { mutableStateOf<String?>(null) }

    var recomendacionIA by remember { mutableStateOf<String?>(null) }
    var duracionRecomendadaIA by remember { mutableStateOf<Int?>(null) }
    var consultandoIA by remember { mutableStateOf(false) }

    var ambienteRecomendadoIA by remember { mutableStateOf<String?>(null) }
    var volumenAmbienteRecomendadoIA by remember { mutableStateOf<Int?>(null) }
    var vozRecomendadaIA by remember { mutableStateOf<Int?>(null) }

    var sonidoReproduciendo by remember { mutableStateOf(false) }

    // Control Maestro de Volumen y Mute Rápido
    var volumenMaestro by remember { mutableStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }

    // Catálogo Escalable de Audios (Bosque inicializado activo para pruebas directas)
    var catalog by remember {
        mutableStateOf(
            listOf(
                AudioTrackItem("mar", "Olas del mar", "Naturaleza", Icons.Default.Waves, R.raw.mar, 0.7f, true),
                AudioTrackItem("lluvia", "Lluvia serena", "Naturaleza", Icons.Default.WaterDrop, R.raw.lluvia, 0.4f, false),
                AudioTrackItem("viento", "Viento suave", "Naturaleza", Icons.Default.Air, R.raw.viento, 0.4f, false),
                AudioTrackItem("bosque", "Bosque vivaz", "Naturaleza", Icons.Default.Forest, R.raw.bosque, 0.6f, true)
            )
        )
    }

    var showAudioBottomSheet by remember { mutableStateOf(false) }

    // Map de Reproductores Activos
    val mediaPlayers = remember { mutableStateMapOf<String, MediaPlayer>() }

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val particles = remember {
        List(25) {
            ZenParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3.5f + 1.5f,
                alpha = Random.nextFloat() * 0.4f + 0.15f,
                speedY = Random.nextFloat() * 0.0006f + 0.0002f
            )
        }
    }

    val emocionesLista = listOf(
        EmotionStateItem("Ansioso", "Ansioso", Icons.Default.AutoAwesome),
        EmotionStateItem("Triste", "Triste", Icons.Default.SentimentDissatisfied),
        EmotionStateItem("Cansado", "Cansado", Icons.Default.Bedtime),
        EmotionStateItem("Neutral", "Neutral", Icons.Default.SelfImprovement),
        EmotionStateItem("Tranquilo", "Tranquilo", Icons.Default.Favorite)
    )

    val mensajesEmocionales = when {
        estadoEmocional?.contains("Ansioso") == true -> listOf(
            "Respira lentamente. No necesitas resolver todo en este momento.",
            "Inhala con calma y permite que tus hombros se relajen.",
            "Si aparece un pensamiento, déjalo pasar y vuelve suavemente a tu respiración.",
            "Continúa respirando. Este momento es para ti.",
            "Permanece tranquilo y lleva tu atención nuevamente a tu respiración."
        )
        estadoEmocional?.contains("Triste") == true -> listOf(
            "Permítete estar presente con lo que sientes, sin juzgarte.",
            "Respira lentamente y date permiso para descansar por unos momentos.",
            "No tienes que resolverlo todo ahora. Concéntrate únicamente en este momento.",
            "Reconoce tus emociones con amabilidad y continúa respirando.",
            "Recuerda que está bien tomarte un momento para ti."
        )
        estadoEmocional?.contains("Cansado") == true -> listOf(
            "Encuentra una posición cómoda y permite que tu cuerpo descanse.",
            "Respira lentamente y relaja poco a poco cada parte de tu cuerpo.",
            "Suelta la tensión de tus hombros y deja que tu respiración sea tranquila.",
            "Permite que tu cuerpo descanse mientras mantienes una respiración suave.",
            "Quédate unos momentos en calma y prepárate para continuar con más tranquilidad."
        )
        estadoEmocional?.contains("Neutral") == true -> listOf(
            "Encuentra una postura cómoda y lleva tu atención a tu respiración.",
            "Observa cómo entra y sale el aire sin intentar modificarlo.",
            "Si aparece un pensamiento, déjalo pasar y vuelve al momento presente.",
            "Permanece atento a las sensaciones de tu cuerpo.",
            "Continúa respirando y disfruta de este momento de calma."
        )
        estadoEmocional?.contains("Tranquilo") == true -> listOf(
            "Disfruta de este momento de calma y permite que tu respiración fluya naturalmente.",
            "Observa tu respiración y conserva esta sensación de tranquilidad.",
            "Lleva tu atención a las sensaciones agradables de este momento.",
            "Permite que la calma permanezca mientras respiras lentamente.",
            "Agradece este momento y continúa llevando contigo esta sensación de tranquilidad."
        )
        else -> listOf(
            "Encuentra una postura cómoda y cierra suavemente los ojos.",
            "Observa tu respiración sin intentar cambiarla.",
            "Permanece presente y permite que tu mente descanse.",
            "Relaja lentamente tu cuerpo y permanece en calma.",
            "Permanece tranquilo y disfruta de este momento."
        )
    }

    val etapasMeditacion = when (duracionSeleccionada) {
        1 -> listOf(
            MeditationStage(60, mensajesEmocionales[0]),
            MeditationStage(40, mensajesEmocionales[1]),
            MeditationStage(20, mensajesEmocionales[4])
        )
        3 -> listOf(
            MeditationStage(180, mensajesEmocionales[0]),
            MeditationStage(140, mensajesEmocionales[1]),
            MeditationStage(90, mensajesEmocionales[2]),
            MeditationStage(40, mensajesEmocionales[3]),
            MeditationStage(20, mensajesEmocionales[4])
        )
        else -> listOf(
            MeditationStage(300, mensajesEmocionales[0]),
            MeditationStage(240, mensajesEmocionales[1]),
            MeditationStage(180, mensajesEmocionales[2]),
            MeditationStage(120, mensajesEmocionales[3]),
            MeditationStage(60, mensajesEmocionales[4]),
            MeditationStage(20, mensajesEmocionales[4])
        )
    }

    val textToSpeech = remember { TextToSpeech(context, null) }

    DisposableEffect(Unit) {
        textToSpeech.language = Locale("es", "ES")
        textToSpeech.setSpeechRate(0.85f)

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
            mediaPlayers.values.forEach {
                try {
                    if (it.isPlaying) it.stop()
                    it.release()
                } catch (_: Exception) {}
            }
            mediaPlayers.clear()
        }
    }

    // Actualizador de volumen e inicializador dinámico de reproductores
    fun updatePlayerVolumes() {
        catalog.forEach { track ->
            if (track.activo) {
                var player = mediaPlayers[track.id]
                if (player == null) {
                    try {
                        player = MediaPlayer.create(context, track.rawResId)
                        player?.isLooping = true
                        if (player != null) {
                            mediaPlayers[track.id] = player
                        }
                    } catch (_: Exception) {}
                }

                player = mediaPlayers[track.id]
                if (player != null) {
                    val finalVol = if (isMuted) 0f else (track.volumen * volumenMaestro).coerceIn(0f, 1f)
                    player.setVolume(finalVol, finalVol)

                    if (sonidoReproduciendo && finalVol > 0f) {
                        try {
                            if (!player.isPlaying) player.start()
                        } catch (_: Exception) {}
                    } else if (finalVol == 0f || !sonidoReproduciendo) {
                        try {
                            if (player.isPlaying) player.pause()
                        } catch (_: Exception) {}
                    }
                }
            } else {
                val player = mediaPlayers[track.id]
                if (player != null) {
                    try {
                        if (player.isPlaying) player.pause()
                    } catch (_: Exception) {}
                }
            }
        }
    }

    // Cronómetro + Sleep Timer con Fade-Out automático
    LaunchedEffect(sesionIniciada) {
        if (sesionIniciada) {
            segundosRestantes = duracionSeleccionada * 60
            mensajeMeditacion = "Encuentra una postura cómoda y cierra suavemente los ojos."

            while (segundosRestantes > 0) {
                delay(1000)
                segundosRestantes--

                if (segundosRestantes <= 30 && segundosRestantes > 0) {
                    volumenMaestro = (segundosRestantes.toFloat() / 30f).coerceIn(0f, 1f)
                    updatePlayerVolumes()
                }

                val etapaActual = etapasMeditacion.lastOrNull { segundosRestantes <= it.startSecond }
                if (etapaActual != null) {
                    mensajeMeditacion = etapaActual.message
                }
            }

            mensajeMeditacion = "Has terminado tu meditación. Tómate un momento antes de continuar."
            sesionIniciada = false
            sesionCompletada = true
            volumenMaestro = 1.0f
            updatePlayerVolumes()
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(mensajeMeditacion, modoAudio) {
        vozGuiada = modoAudio != "solo_ambiente"

        if (vozGuiada && sesionIniciada) {
            textToSpeech.speak(
                mensajeMeditacion,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "meditation_message"
            )
        } else {
            textToSpeech.stop()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "breathOrb")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val totalSegundos = remember(duracionSeleccionada) { duracionSeleccionada * 60 }
    val progresoRadial = if (totalSegundos > 0 && sesionIniciada) {
        (segundosRestantes.toFloat() / totalSegundos.toFloat()).coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progresoRadial,
        animationSpec = tween(durationMillis = 800),
        label = "radialProgress"
    )

    val activeTracks = catalog.filter { it.activo }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundTop, BackgroundBottom)))
    ) {
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

        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-80).dp, y = (-50).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleGlow.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        navController.popBackStack()
                    },
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Orbe de Meditación con Visualizer Circular Animado
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(250.dp)
                    .scale(if (sesionIniciada) breathScale else 1f)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val diameter = size.minDimension
                    val strokeWidth = 8.dp.toPx()
                    val arcSize = diameter - strokeWidth

                    drawArc(
                        color = BorderColor.copy(alpha = 0.4f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth)
                    )

                    if (sonidoReproduciendo && !isMuted) {
                        val barsCount = 36
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = (diameter / 2) - 4.dp.toPx()

                        for (i in 0 until barsCount) {
                            val angleRad = Math.toRadians((i * (360f / barsCount)).toDouble())
                            val waveHeight = (sin(angleRad * 3 + Math.toRadians(wavePhase.toDouble())).toFloat() + 1f) * 10.dp.toPx() * volumenMaestro
                            val startX = center.x + radius * cos(angleRad).toFloat()
                            val startY = center.y + radius * sin(angleRad).toFloat()
                            val endX = center.x + (radius + waveHeight) * cos(angleRad).toFloat()
                            val endY = center.y + (radius + waveHeight) * sin(angleRad).toFloat()

                            drawLine(
                                color = PrimaryLight.copy(alpha = 0.65f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    if (sesionIniciada) {
                        drawArc(
                            brush = Brush.sweepGradient(listOf(Primary, PrimaryLight, Primary)),
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .alpha(auraAlpha * 0.4f)
                        .clip(CircleShape)
                        .background(Primary)
                )

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .alpha(auraAlpha * 0.6f)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                )

                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .shadow(28.dp, CircleShape, spotColor = Primary)
                        .background(
                            Brush.linearGradient(listOf(Primary, PrimaryLight)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.meditation_titulo),
                color = WhiteSoft,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.meditation_subtitulo),
                color = GrayText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            // Contador Animado
            AnimatedVisibility(
                visible = sesionIniciada,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = String.format(
                            "%02d:%02d",
                            segundosRestantes / 60,
                            segundosRestantes % 60
                        ),
                        color = WhiteSoft,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = CardSolid,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(PrimaryLight.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                    ) {
                        Text(
                            text = mensajeMeditacion,
                            color = PrimaryLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            vozGuiada = !vozGuiada
                        }
                    ) {
                        Icon(
                            imageVector = if (vozGuiada) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = null,
                            tint = WhiteSoft,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (vozGuiada) "Voz guiada: Activada" else "Voz guiada: Desactivada",
                            color = WhiteSoft,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Banner Sesión Completada
            AnimatedVisibility(
                visible = sesionCompletada,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = CardSolid,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryLight.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryLight,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Sesión completada",
                                color = WhiteSoft,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Has terminado tu meditación. Tómate un momento antes de continuar.",
                                color = GrayText,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de Duración
            Text(
                text = "Elige tu sesión",
                color = WhiteSoft,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(1, 3, 5).forEach { minutos ->
                    val selected = duracionSeleccionada == minutos
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    val scaleBtn by animateFloatAsState(
                        targetValue = if (isPressed) 0.94f else 1f,
                        animationSpec = tween(150),
                        label = "scaleDuracion"
                    )

                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            duracionSeleccionada = minutos
                        },
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .scale(scaleBtn),
                        shape = RoundedCornerShape(16.dp),
                        color = if (selected) Primary else CardSolid,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selected) PrimaryLight else BorderColor
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$minutos min",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhiteSoft
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selector Emocional con Íconos Vectoriales
            Text(
                text = "¿Cómo te sientes ahora?",
                color = WhiteSoft,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                emocionesLista.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fila.forEach { estadoItem ->
                            val selected = estadoEmocional == estadoItem.tag
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()

                            val scaleChip by animateFloatAsState(
                                targetValue = if (isPressed) 0.94f else 1f,
                                animationSpec = tween(150),
                                label = "scaleEmotion"
                            )

                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    estadoEmocional = estadoItem.tag
                                },
                                interactionSource = interactionSource,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .scale(scaleChip),
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) Primary else CardSolid,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selected) PrimaryLight else BorderColor
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = estadoItem.icon,
                                        contentDescription = null,
                                        tint = if (selected) WhiteSoft else PrimaryLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = estadoItem.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = WhiteSoft
                                    )
                                }
                            }
                        }

                        if (fila.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Botón Recomendación IA
            Button(
                onClick = {
                    val estado = estadoEmocional
                    if (estado != null && !consultandoIA) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        consultandoIA = true
                        scope.launch {
                            val respuestaIA = obtenerRecomendacionMeditacion(estado)
                            recomendacionIA = respuestaIA

                            val lineas = respuestaIA
                                ?.lineSequence()
                                ?.map { it.trim() }
                                ?.toList()
                                ?: emptyList()

                            duracionRecomendadaIA = lineas
                                .firstOrNull { it.startsWith("DURACION:") }
                                ?.substringAfter(":")
                                ?.trim()
                                ?.toIntOrNull()
                                ?.takeIf { d -> d == 1 || d == 3 || d == 5 }

                            ambienteRecomendadoIA = lineas
                                .firstOrNull { it.startsWith("AMBIENTE:") }
                                ?.substringAfter(":")
                                ?.trim()
                                ?.lowercase()
                                ?.takeIf { ambiente -> ambiente in listOf("lluvia", "mar", "viento", "bosque") }

                            volumenAmbienteRecomendadoIA = lineas
                                .firstOrNull { it.startsWith("VOLUMEN_AMBIENTE:") }
                                ?.substringAfter(":")
                                ?.trim()
                                ?.toIntOrNull()
                                ?.coerceIn(0, 100)

                            vozRecomendadaIA = lineas
                                .firstOrNull { it.startsWith("VOZ:") }
                                ?.substringAfter(":")
                                ?.trim()
                                ?.toIntOrNull()
                                ?.coerceIn(0, 100)

                            consultandoIA = false
                        }
                    }
                },
                enabled = estadoEmocional != null && !consultandoIA,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (consultandoIA) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consultando IA...", color = WhiteSoft)
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Recomendación IA", fontWeight = FontWeight.Bold, color = WhiteSoft)
                }
            }

            // Card Respuesta IA
            recomendacionIA?.let { recomendacion ->
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = CardSolid,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryLight.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = recomendacion,
                        color = WhiteSoft,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Modo de Audio Segmentado
            Text(
                text = "Modo de audio",
                color = WhiteSoft,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Elige cómo quieres escuchar tu sesión.",
                color = GrayText,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = CardSolid,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AudioModeTab(
                        text = "Solo guía",
                        icon = Icons.Default.Mic,
                        selected = modoAudio == "solo_voz",
                        modifier = Modifier.weight(1f)
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        modoAudio = "solo_voz"
                        mediaPlayers.values.forEach { if (it.isPlaying) it.pause() }
                    }

                    AudioModeTab(
                        text = "Voz + ambiente",
                        icon = Icons.Default.Tune,
                        selected = modoAudio == "voz_ambiente",
                        modifier = Modifier.weight(1f)
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        modoAudio = "voz_ambiente"
                    }

                    AudioModeTab(
                        text = "Solo ambiente",
                        icon = Icons.Default.VolumeUp,
                        selected = modoAudio == "solo_ambiente",
                        modifier = Modifier.weight(1f)
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        modoAudio = "solo_ambiente"
                        textToSpeech.stop()
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECCIÓN AMBIENTE DE MEDITACIÓN CON MEZCLADOR ACTIVO Y BOTÓN CATÁLOGO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ambiente de meditación",
                        color = WhiteSoft,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${activeTracks.size} sonidos activos",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showAudioBottomSheet = true
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryLight),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = PrimaryLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Explorar audios", fontSize = 12.sp, color = PrimaryLight, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CONTROL MAESTRO Y BOTÓN MUTE RÁPIDO
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = CardSolid,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    isMuted = !isMuted
                                    updatePlayerVolumes()
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isMuted) Color(0xFFEF4444).copy(alpha = 0.2f) else Primary.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                    contentDescription = "Mute",
                                    tint = if (isMuted) Color(0xFFEF4444) else PrimaryLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Volumen Maestro",
                                color = WhiteSoft,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (isMuted) "Silenciado" else "${(volumenMaestro * 100).toInt()}%",
                            color = GrayText,
                            fontSize = 12.sp
                        )
                    }

                    Slider(
                        value = volumenMaestro,
                        onValueChange = {
                            volumenMaestro = it
                            if (isMuted && it > 0f) isMuted = false
                            updatePlayerVolumes()
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryLight,
                            activeTrackColor = Primary,
                            inactiveTrackColor = FieldColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LISTA DE TARJETAS ACTIVAS EN PANTALLA
            if (activeTracks.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardSolid,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Text(
                        text = "No hay sonidos activos. Toca 'Explorar audios' para agregar ambiente.",
                        color = GrayText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                activeTracks.forEach { track ->
                    ActiveTrackCard(
                        track = track,
                        onVolumeChange = { newVol ->
                            catalog = catalog.map {
                                if (it.id == track.id) it.copy(volumen = newVol) else it
                            }
                            updatePlayerVolumes()
                        },
                        onRemove = {
                            catalog = catalog.map {
                                if (it.id == track.id) it.copy(activo = false, volumen = 0f) else it
                            }
                            updatePlayerVolumes()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        if (sonidoReproduciendo) {
                            sonidoReproduciendo = false
                            updatePlayerVolumes()
                        } else {
                            sonidoReproduciendo = true
                            updatePlayerVolumes()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(
                        imageVector = if (sonidoReproduciendo) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (sonidoReproduciendo) "Pausar" else "Reproducir",
                        fontWeight = FontWeight.Bold,
                        color = WhiteSoft
                    )
                }

                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        mediaPlayers.values.forEach {
                            try {
                                if (it.isPlaying) it.stop()
                                it.release()
                            } catch (_: Exception) {}
                        }
                        mediaPlayers.clear()

                        sonidoReproduciendo = false
                    },
                    modifier = Modifier
                        .weight(0.7f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WhiteSoft),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Detener")
                }
            }

            // Aplicar Recomendación IA Button
            if (duracionRecomendadaIA != null && ambienteRecomendadoIA != null) {
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        duracionSeleccionada = duracionRecomendadaIA ?: duracionSeleccionada
                        val volumenAmbiente = (volumenAmbienteRecomendadoIA ?: 0) / 100f

                        catalog = catalog.map { track ->
                            val isTarget = track.id == ambienteRecomendadoIA
                            track.copy(
                                activo = isTarget,
                                volumen = if (isTarget) volumenAmbiente else 0f
                            )
                        }
                        updatePlayerVolumes()

                        textToSpeech.setSpeechRate(
                            when {
                                (vozRecomendadaIA ?: 100) >= 90 -> 0.85f
                                (vozRecomendadaIA ?: 100) >= 75 -> 0.80f
                                else -> 0.75f
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryLight.copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Usar recomendación IA",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta de Consejo Sólida
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardSolid,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = PrimaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Consejo",
                            color = WhiteSoft,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Encuentra un lugar tranquilo, respira profundamente y permite que tu mente descanse.",
                        color = GrayText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Principal de Inicio de Meditación
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    sesionCompletada = false
                    segundosRestantes = duracionSeleccionada * 60
                    volumenMaestro = 1.0f

                    sonidoReproduciendo = true
                    updatePlayerVolumes()
                    sesionIniciada = true

                    registrarUsoHerramienta("meditacion")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (sesionIniciada)
                            "Meditando..."
                        else if (sesionCompletada)
                            "Comenzar otra sesión"
                        else
                            "Comenzar meditación",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhiteSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // BOTTOM SHEET DESPLEGABLE CON EL CATÁLOGO COMPLETO DE AUDIOS
        if (showAudioBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAudioBottomSheet = false },
                containerColor = CardSolid,
                dragHandle = { BottomSheetDefaults.DragHandle(color = GrayText) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Catálogo de sonidos ambientales",
                        color = WhiteSoft,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Selecciona los audios que deseas integrar a tu sesión.",
                        color = GrayText,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.heightIn(max = 350.dp)
                    ) {
                        items(catalog) { track ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = if (track.activo) Primary.copy(alpha = 0.2f) else FieldColor,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (track.activo) PrimaryLight else BorderColor
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = track.icon,
                                            contentDescription = null,
                                            tint = PrimaryLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = track.nombre,
                                                color = WhiteSoft,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = track.categoria,
                                                color = GrayText,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Checkbox(
                                        checked = track.activo,
                                        onCheckedChange = { isChecked ->
                                            catalog = catalog.map {
                                                if (it.id == track.id) {
                                                    it.copy(
                                                        activo = isChecked,
                                                        volumen = if (isChecked) 0.5f else 0f
                                                    )
                                                } else it
                                            }
                                            updatePlayerVolumes()
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Primary,
                                            checkmarkColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun AudioModeTab(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) Brush.linearGradient(listOf(Primary, PrimaryLight))
                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else GrayText,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = if (selected) Color.White else GrayText,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ActiveTrackCard(
    track: AudioTrackItem,
    onVolumeChange: (Float) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardSolid,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = track.icon,
                        contentDescription = null,
                        tint = PrimaryLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = track.nombre,
                        color = WhiteSoft,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${(track.volumen * 100).toInt()}%",
                        color = GrayText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quitar",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = track.volumen,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryLight,
                    activeTrackColor = Primary,
                    inactiveTrackColor = FieldColor
                )
            )
        }
    }
}