package com.example.navhost1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import com.google.firebase.firestore.Query

import androidx.compose.material3.LinearProgressIndicator

import com.example.navhost1.components.AnimatedTree


private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)
private val CardSecondary = Color(0xFF1F2937)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

data class HomeItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun HomeScreen(navController: NavController, username: String?) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var nombreUsuario by remember {
        mutableStateOf("Usuario")
    }
    var arbolNivel by remember {
        mutableStateOf(1)
    }
    var xp by remember {
        mutableStateOf(0L)
    }
    var racha by remember {
        mutableStateOf(0)
    }
    var emocionActual by remember {
        mutableStateOf("😌 Tranquilo")

    }
    var totalEntradas by remember {
        mutableStateOf(0)
    }
    var logroPrimerDiario by remember {
        mutableStateOf(false)
    }

    var logroRacha3 by remember {
        mutableStateOf(false)
    }

    var logroRacha7 by remember {
        mutableStateOf(false)
    }

    var logroArbol5 by remember {
        mutableStateOf(false)
    }

    val recomendacion = when (emocionActual) {

        "😟 Ansioso" ->
            "Te recomendamos realizar ejercicios de respiración consciente."

        "😔 Triste" ->
            "Practica gratitud para enfocarte en aspectos positivos."

        "😡 Enojado" ->
            "Realiza una meditación guiada para recuperar la calma."

        "😴 Cansado" ->
            "Realiza una sesión breve de respiración y descanso."

        "😊 Feliz" ->
            "Sigue fortaleciendo tus hábitos positivos."

        else ->
            "Mantén tu bienestar emocional con actividades diarias."
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

                logroPrimerDiario =
                    document.getBoolean("primer_diario") ?: false

                logroRacha3 =
                    document.getBoolean("racha_3") ?: false

                logroRacha7 =
                    document.getBoolean("racha_7") ?: false

                logroArbol5 =
                    document.getBoolean("arbol_nivel_5") ?: false
            }

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                println("UID: $uid")
                println("Documento existe: ${document.exists()}")
                println("Datos: ${document.data}")

                nombreUsuario =
                    document.getString("nombre")
                        ?: "Usuario"

                arbolNivel =
                    document.getLong("arbolNivel")
                        ?.toInt()
                        ?: 1

                racha =
                    document.getLong("racha")
                        ?.toInt()
                        ?: 0

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

                    emocionActual =
                        result.documents[0]
                            .getString("emocion")
                            ?: "😌 Tranquilo"
                }
            }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val menuItems = listOf(
        HomeItem("Chat IA", Icons.Default.Chat, "chat"),
        HomeItem("Herramientas", Icons.Default.Settings, "tools"),
        HomeItem("Diario", Icons.Default.Edit, "diary"),
        HomeItem("Contenido", Icons.Default.MenuBook, "content"),
        HomeItem("Estadísticas", Icons.Default.Psychology, "estadisticas")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
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
                    Primary.copy(alpha = alphaAnim)
                )
        )

        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .clip(CircleShape)
                .background(
                    PrimaryLight.copy(alpha = alphaAnim)
                )
        )

        val scrollState = rememberScrollState()



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

        val progreso =
            ((xp - xpMinimoNivel).toFloat() /
                    (xpSiguienteNivel - xpMinimoNivel).toFloat())
                .coerceIn(0f, 1f)

        val progresoAnimado by animateFloatAsState(
            targetValue = progreso,
            animationSpec = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            label = "xpProgress"
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 22.dp, vertical = 28.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor.copy(alpha = 0.92f)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            text = "Bienvenido de nuevo 👋",
                            color = PrimaryLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = nombreUsuario,
                            color = WhiteSoft,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "🌳 Nivel $arbolNivel   •   ⭐ $xp XP",
                            color = GrayText,
                            fontSize = 14.sp
                        )
                    }

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
                            )
                            .clickable {
                                navController.navigate(
                                    "profile/${username ?: "usuario"}"
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF7C3AED),
                                    Color(0xFF4F46E5)
                                )
                            )
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                text = "Tu bienestar importa",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "La IA analiza tus emociones, fortalece tus hábitos y hace crecer tu árbol emocional.",
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 22.sp
                            )
                        }

                        Button(
                            onClick = {
                                navController.navigate("chat")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(18.dp)
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




            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF182235)
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "🌳 Árbol emocional",
                        color = WhiteSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Tu bienestar crece contigo",
                        color = GrayText,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Spacer(modifier = Modifier.height(20.dp))

                    val estadoArbol = when (arbolNivel) {

                        1 -> when (emocionActual) {
                            "😊 Feliz" -> "☀️🌱 Semilla feliz"
                            "😔 Triste" -> "🌧️🌱 Semilla triste"
                            "😟 Ansioso" -> "🌬️🌱 Semilla inquieta"
                            "😡 Enojado" -> "⚡🌱 Semilla alterada"
                            "😴 Cansado" -> "🌙🌱 Semilla descansando"
                            else -> "🌱 Semilla tranquila"
                        }

                        2 -> when (emocionActual) {
                            "😊 Feliz" -> "☀️🌿 Brote feliz"
                            "😔 Triste" -> "🌧️🌿 Brote triste"
                            "😟 Ansioso" -> "🌬️🌿 Brote inquieto"
                            "😡 Enojado" -> "⚡🌿 Brote alterado"
                            "😴 Cansado" -> "🌙🌿 Brote descansando"
                            else -> "🌿 Brote tranquilo"
                        }

                        3 -> when (emocionActual) {
                            "😊 Feliz" -> "☀️🌳 Árbol joven feliz"
                            "😔 Triste" -> "🌧️🌳 Árbol joven triste"
                            "😟 Ansioso" -> "🌬️🌳 Árbol joven inquieto"
                            "😡 Enojado" -> "⚡🌳 Árbol joven alterado"
                            "😴 Cansado" -> "🌙🌳 Árbol joven descansando"
                            else -> "🌳 Árbol joven tranquilo"
                        }

                        4 -> when (emocionActual) {
                            "😊 Feliz" -> "☀️🍃 Árbol fuerte feliz"
                            "😔 Triste" -> "🌧️🍃 Árbol fuerte triste"
                            "😟 Ansioso" -> "🌬️🍃 Árbol fuerte inquieto"
                            "😡 Enojado" -> "⚡🍃 Árbol fuerte alterado"
                            "😴 Cansado" -> "🌙🍃 Árbol fuerte descansando"
                            else -> "🍃 Árbol fuerte tranquilo"
                        }

                        else -> when (emocionActual) {
                            "😊 Feliz" -> "☀️🌲 Árbol completo feliz"
                            "😔 Triste" -> "🌧️🌲 Árbol completo triste"
                            "😟 Ansioso" -> "🌬️🌲 Árbol completo inquieto"
                            "😡 Enojado" -> "⚡🌲 Árbol completo alterado"
                            "😴 Cansado" -> "🌙🌲 Árbol completo descansando"
                            else -> "🌲 Árbol completo tranquilo"
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Nivel $arbolNivel",
                            color = PrimaryLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "$xp XP",
                            color = WhiteSoft,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progresoAnimado },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = PrimaryLight,
                        trackColor = CardColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Progreso hacia el siguiente nivel",
                        color = GrayText,
                        fontSize = 12.sp
                    )



                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        AnimatedTree(
                            nivel = arbolNivel,
                            emocion = emocionActual
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))


                    Text(
                        text = "Estado emocional",
                        color = GrayText,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = emocionActual,
                        color = PrimaryLight,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🔥 Racha: $racha días",
                        color = WhiteSoft,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Accesos rápidos",
                color = WhiteSoft,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardSecondary
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "💡 Recomendación para ti",
                        color = WhiteSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = recomendacion,
                        color = GrayText,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {

                            when (emocionActual) {

                                "😟 Ansioso" ->
                                    navController.navigate("breathing")

                                "😔 Triste" ->
                                    navController.navigate("gratitude")

                                "😡 Enojado" ->
                                    navController.navigate("meditation")

                                "😴 Cansado" ->
                                    navController.navigate("breathing")
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = textoBoton)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardSecondary
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "🏆 Tu progreso",
                        color = WhiteSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Nivel $arbolNivel",
                        color = PrimaryLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$xp XP",
                        color = GrayText,
                        fontSize = 14.sp
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    LinearProgressIndicator(
                        progress = { progresoAnimado },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Siguiente nivel: $xpSiguienteNivel XP",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))




            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardSecondary
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "🔥 Racha emocional",
                        color = WhiteSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "$racha días",
                        color = PrimaryLight,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Escribe diariamente para mantener tu progreso.",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardSecondary
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    AchievementItem(
                        icon = "📖",
                        title = "Primer diario",
                        unlocked = logroPrimerDiario
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AchievementItem(
                        icon = "🔥",
                        title = "Racha de 3 días",
                        unlocked = logroRacha3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AchievementItem(
                        icon = "🌳",
                        title = "Árbol nivel 5",
                        unlocked = logroArbol5
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AchievementItem(
                        icon = "🏅",
                        title = "Racha de 7 días",
                        unlocked = logroRacha7
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp)
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

    Card(

        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(

            containerColor =
                if (unlocked)
                    Color(0xFF1E3A2F)
                else
                    Color(0xFF232A36)

        ),

        shape = RoundedCornerShape(18.dp)

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Text(

                text = icon,

                fontSize = 28.sp

            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = title,

                    color = WhiteSoft,

                    fontWeight = FontWeight.Bold,

                    fontSize = 16.sp

                )

                Text(

                    text =
                        if (unlocked)
                            "Desbloqueado"
                        else
                            "Bloqueado",

                    color = GrayText,

                    fontSize = 12.sp

                )
            }

            Text(

                text =
                    if (unlocked)
                        "✅"
                    else
                        "🔒",

                fontSize = 24.sp

            )
        }
    }
}
@Composable
fun ModernMenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    var pressed by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = tween(220),
        label = "scale"
    )

    Card(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .scale(scale),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardSecondary.copy(alpha = 0.96f)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryLight,
                                Primary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ){

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = title,
                    color = WhiteSoft,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Toca para Abrir",
                    color = GrayText,
                    fontSize = 13.sp,
                    modifier = Modifier.alpha(0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))


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

