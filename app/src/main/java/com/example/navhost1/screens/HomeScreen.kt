package com.example.navhost1.screens

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.components.AnimatedTree
import com.example.navhost1.habits.HabitRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.random.Random
import java.util.Calendar

// Paleta NeuraBloom Dark Premium
private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF090D16)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFC084FC)
private val PurpleGlow = Color(0xFF6D28D9)

private val CardColor = Color(0xFF1E293B).copy(alpha = 0.85f)
private val CardSolid = Color(0xFF1E293B)
private val CardSecondary = Color(0xFF0F172A)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)
private val BorderColor = Color(0xFF334155)

data class HomeItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

data class HomeHabit(
    val id: String,
    val nombre: String,
    val categoria: String,
    val completado: Boolean,
    val xp: Long,
    val fechaXP: Long
)

private data class HomeParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

@Composable
fun HomeScreen(navController: NavController, username: String?) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val habitRepository = HabitRepository()

    var nombreUsuario by remember { mutableStateOf("Usuario") }
    var arbolNivel by remember { mutableStateOf(1) }
    var xp by remember { mutableStateOf(0L) }
    var habitosHome by remember { mutableStateOf<List<HomeHabit>>(emptyList()) }
    var racha by remember { mutableStateOf(0) }
    var emocionActual by remember { mutableStateOf("😌 Tranquilo") }
    var totalEntradas by remember { mutableStateOf(0) }

    var logroPrimerDiario by remember { mutableStateOf(false) }
    var logroRacha3 by remember { mutableStateOf(false) }
    var logroRacha7 by remember { mutableStateOf(false) }
    var logroArbol5 by remember { mutableStateOf(false) }

    // Partículas de luz de fondo
    val particles = remember {
        List(25) {
            HomeParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3.5f + 1.5f,
                alpha = Random.nextFloat() * 0.4f + 0.15f,
                speedY = Random.nextFloat() * 0.0008f + 0.0003f
            )
        }
    }

    val recomendacion = when (emocionActual) {
        "😟 Ansioso" -> "Te recomendamos realizar ejercicios de respiración consciente."
        "😔 Triste" -> "Practica gratitud para enfocarte en aspectos positivos."
        "😡 Enojado" -> "Realiza una meditación guiada para recuperar la calma."
        "😴 Cansado" -> "Realiza una sesión breve de respiración y descanso."
        "😊 Feliz" -> "Sigue fortaleciendo tus hábitos positivos."
        else -> "Mantén tu bienestar emocional con actividades diarias."
    }

    val textoBoton = when (emocionActual) {
        "😟 Ansioso" -> "Ir a Respiración"
        "😔 Triste" -> "Ir a Gratitud"
        "😡 Enojado" -> "Ir a Meditación"
        "😴 Cansado" -> "Ir a Respiración"
        else -> "Ver herramientas"
    }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("usuarios")
            .document(uid)
            .collection("habitos")
            .get()
            .addOnSuccessListener { result ->
                habitosHome = result.documents.mapNotNull { documento ->
                    val id = documento.getString("id") ?: documento.id
                    val nombre = documento.getString("nombre") ?: return@mapNotNull null
                    val categoria = documento.getString("categoria") ?: ""
                    val completadoFirebase =
                        documento.getBoolean("completado") ?: false

                    val xpHabit =
                        documento.getLong("xp") ?: 0L

                    val fechaXP =
                        documento.getLong("fechaXP") ?: 0L

                    val hoy = Calendar.getInstance()

                    val fechaCompletado = Calendar.getInstance().apply {
                        timeInMillis = fechaXP
                    }

                    val completadoHoy =
                        completadoFirebase &&
                                fechaXP > 0L &&
                                hoy.get(Calendar.YEAR) ==
                                fechaCompletado.get(Calendar.YEAR) &&
                                hoy.get(Calendar.DAY_OF_YEAR) ==
                                fechaCompletado.get(Calendar.DAY_OF_YEAR)

                    HomeHabit(
                        id = id,
                        nombre = nombre,
                        categoria = categoria,
                        completado = completadoHoy,
                        xp = xpHabit,
                        fechaXP = fechaXP
                    )
                }
            }

        db.collection("diarios")
            .document(uid)
            .collection("entradas")
            .get()
            .addOnSuccessListener { result ->
                totalEntradas = result.size()
            }

        db.collection("usuarios")
            .document(uid)
            .collection("logros")
            .document("estado")
            .get()
            .addOnSuccessListener { document ->
                logroPrimerDiario = document.getBoolean("primer_diario") ?: false
                logroRacha3 = document.getBoolean("racha_3") ?: false
                logroRacha7 = document.getBoolean("racha_7") ?: false
                logroArbol5 = document.getBoolean("arbol_nivel_5") ?: false
            }

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                nombreUsuario = document.getString("nombre") ?: "Usuario"
                racha = document.getLong("racha")?.toInt() ?: 0
                xp = document.getLong("xp") ?: 0

                arbolNivel = when {
                    xp >= 500 -> 5
                    xp >= 300 -> 4
                    xp >= 150 -> 3
                    xp >= 50 -> 2
                    else -> 1
                }
            }

        db.collection("diarios")
            .document(uid)
            .collection("entradas")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    emocionActual = result.documents[0].getString("emocion") ?: "😌 Tranquilo"
                }
            }
    }

    // Lista de Menú con el nuevo icono CheckCircle para Hábitos
    val menuItems = listOf(
        HomeItem("Chat IA", Icons.Default.Chat, "chat"),
        HomeItem("Herramientas", Icons.Default.Settings, "tools"),
        HomeItem("Diario", Icons.Default.Edit, "diary"),
        HomeItem("Contenido", Icons.Default.MenuBook, "content"),
        HomeItem("Estadísticas", Icons.Default.Psychology, "estadisticas"),
        HomeItem("Hábitos", Icons.Default.CheckCircle, "habitos")
    )

    val xpMinimoNivel = when (arbolNivel) {
        1 -> 0
        2 -> 50
        3 -> 150
        4 -> 300
        else -> 500
    }

    val xpSiguienteNivel = when (arbolNivel) {
        1 -> 50
        2 -> 150
        3 -> 300
        4 -> 500
        else -> 800
    }

    val progreso = ((xp - xpMinimoNivel).toFloat() / (xpSiguienteNivel - xpMinimoNivel).toFloat()).coerceIn(0f, 1f)

    val progresoAnimado by animateFloatAsState(
        targetValue = progreso,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "xpProgress"
    )

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

        // Resplandores Ambientales de Fondo
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
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header del Usuario
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = CardSolid,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Bienvenido de nuevo 👋",
                            color = PrimaryLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = nombreUsuario,
                            color = WhiteSoft,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "🌳 Nivel $arbolNivel   •   ⭐ $xp XP",
                            color = GrayText,
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(12.dp, CircleShape, spotColor = Primary)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Primary, PrimaryLight)
                                )
                            )
                            .clickable {
                                navController.navigate("profile/${username ?: "usuario"}")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner Interactivo Principal
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = Primary)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
                            ),
                            RoundedCornerShape(28.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(22.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Tu bienestar importa",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "La IA analiza tus emociones, fortalece tus hábitos y hace crecer tu árbol emocional.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }

                        Button(
                            onClick = { navController.navigate("chat") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Text(
                                "Comenzar ahora",
                                color = Color(0xFF6D28D9),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Árbol Emocional
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = CardSolid,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🌳 Árbol emocional",
                        color = WhiteSoft,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Tu bienestar crece contigo",
                        color = GrayText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nivel $arbolNivel",
                            color = PrimaryLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "$xp XP",
                            color = WhiteSoft,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progresoAnimado },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = PrimaryLight,
                        trackColor = CardSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedTree(
                            nivel = arbolNivel,
                            emocion = emocionActual
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Estado emocional",
                        color = GrayText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = emocionActual,
                        color = PrimaryLight,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🔥 Racha: $racha días",
                        color = WhiteSoft,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hábitos de hoy
            if (habitosHome.isNotEmpty()) {
                val completados = habitosHome.count { it.completado }
                val total = habitosHome.size

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = CardSolid,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌱", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hábitos de hoy",
                                    color = WhiteSoft,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$completados de $total completados",
                                    color = GrayText,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        habitosHome.take(3).forEach { habit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        val uid = auth.currentUser?.uid
                                        if (uid != null) {
                                            if (habit.completado) {
                                                habitRepository.descompletarHabit(
                                                    uid = uid,
                                                    habitId = habit.id,
                                                    onSuccess = {
                                                        habitosHome = habitosHome.map {
                                                            if (it.id == habit.id) it.copy(completado = false) else it
                                                        }
                                                    },
                                                    onError = { Log.e("HOME_HABITOS", "Error desmarcar hábito", it) }
                                                )
                                            } else {
                                                habitRepository.completarHabit(
                                                    uid = uid,
                                                    habitId = habit.id,
                                                    onSuccess = {
                                                        habitosHome = habitosHome.map {
                                                            if (it.id == habit.id) it.copy(completado = true) else it
                                                        }
                                                    },
                                                    onError = { Log.e("HOME_HABITOS", "Error completar hábito", it) }
                                                )
                                            }
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (habit.completado) "✓" else "○",
                                    color = if (habit.completado) Color(0xFF10B981) else PrimaryLight,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = habit.nombre,
                                    modifier = Modifier.weight(1f),
                                    color = WhiteSoft,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "+${habit.xp} XP",
                                    color = PrimaryLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        TextButton(
                            onClick = { navController.navigate("habitos") },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Ver hábitos →",
                                color = PrimaryLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Recomendación
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardSolid,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "💡 Recomendación para ti",
                        color = WhiteSoft,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = recomendacion,
                        color = GrayText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            when (emocionActual) {
                                "😟 Ansioso" -> navController.navigate("breathing")
                                "😔 Triste" -> navController.navigate("gratitude")
                                "😡 Enojado" -> navController.navigate("meditation")
                                "😴 Cansado" -> navController.navigate("breathing")
                                else -> navController.navigate("tools")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = textoBoton, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WhiteSoft)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Racha Emocional
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardSolid,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🔥 Racha emocional",
                        color = WhiteSoft,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "$racha días",
                        color = PrimaryLight,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Escribe diariamente para mantener tu progreso.",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Logros
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardSolid,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🏆 Tus Logros",
                        color = WhiteSoft,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AchievementItem(icon = "📖", title = "Primer diario", unlocked = logroPrimerDiario)
                    Spacer(modifier = Modifier.height(10.dp))
                    AchievementItem(icon = "🔥", title = "Racha de 3 días", unlocked = logroRacha3)
                    Spacer(modifier = Modifier.height(10.dp))
                    AchievementItem(icon = "🌳", title = "Árbol nivel 5", unlocked = logroArbol5)
                    Spacer(modifier = Modifier.height(10.dp))
                    AchievementItem(icon = "🏅", title = "Racha de 7 días", unlocked = logroRacha7)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Accesos rápidos",
                color = WhiteSoft,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Grid de Accesos Rápidos
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
            ) {
                items(menuItems) { item ->
                    ModernMenuCard(
                        title = item.title,
                        icon = item.icon
                    ) {
                        navController.navigate(item.route)
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(
    icon: String,
    title: String,
    unlocked: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (unlocked) Color(0xFF10B981).copy(alpha = 0.15f) else CardSecondary.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (unlocked) Color(0xFF10B981).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = WhiteSoft, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = if (unlocked) "Desbloqueado" else "Bloqueado", color = GrayText, fontSize = 11.sp)
            }
            Text(text = if (unlocked) "✅" else "🔒", fontSize = 18.sp)
        }
    }
}

@Composable
fun ModernMenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    Surface(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(scale),
        shape = RoundedCornerShape(24.dp),
        color = CardSolid,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Primary, PrimaryLight))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    color = WhiteSoft,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Toca para abrir",
                    color = GrayText,
                    fontSize = 11.sp,
                    modifier = Modifier.alpha(0.8f)
                )
            }
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(120)
            pressed = false
        }
    }
}