package com.example.navhost1.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
private val Danger = Color(0xFFEF4444)

private data class ProfileParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

@Composable
fun ProfileScreen(
    navController: NavController,
    username: String?
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var userEmail by remember { mutableStateOf(auth.currentUser?.email ?: "correo@ejemplo.com") }
    var userPhone by remember { mutableStateOf("+52 000 000 0000") }
    var userBirthdate by remember { mutableStateOf("01 / 01 / 2000") }
    var nombreCompleto by remember { mutableStateOf(username ?: "Usuario") }

    // Cargar datos reales desde Firestore
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        db.collection("usuarios").document(uid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                nombreCompleto = document.getString("nombre") ?: (username ?: "Usuario")
                userEmail = document.getString("email") ?: (auth.currentUser?.email ?: "correo@ejemplo.com")
                userPhone = document.getString("telefono") ?: "+52 000 000 0000"
                userBirthdate = document.getString("fechaNacimiento") ?: "01 / 01 / 2000"
            }
        }
    }

    // Partículas ambientales de fondo
    val particles = remember {
        List(20) {
            ProfileParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 1.5f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                speedY = Random.nextFloat() * 0.0008f + 0.0003f
            )
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Text(
                        text = "←",
                        color = WhiteSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.profile_titulo),
                    color = WhiteSoft,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta de Perfil Principal
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar con Resplandor
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(20.dp, CircleShape, spotColor = Primary)
                            .background(
                                Brush.linearGradient(listOf(Primary, PrimaryLight)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nombreCompleto.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = nombreCompleto,
                        color = WhiteSoft,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = GrayText,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = userEmail,
                            color = GrayText,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de Emergencias
                    Button(
                        onClick = { navController.navigate("emergency") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Danger),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = stringResource(R.string.profile_emergencias),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campos de Datos Personales
            ProfileField(
                label = stringResource(R.string.profile_nombre_usuario),
                value = nombreCompleto,
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            ProfileField(
                label = stringResource(R.string.profile_telefono),
                value = userPhone,
                icon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(14.dp))

            ProfileField(
                label = stringResource(R.string.profile_fe_na),
                value = userBirthdate,
                icon = Icons.Default.CalendarToday
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Menú de Opciones
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column {
                    OptionItem(title = stringResource(R.string.profile_planes)) {
                        navController.navigate("planes")
                    }

                    DividerDark()

                    OptionItem(title = stringResource(R.string.profile_plan_actual)) {
                        navController.navigate("premium")
                    }

                    DividerDark()

                    OptionItem(title = stringResource(R.string.profile_configuracion)) {
                        navController.navigate("settings")
                    }

                    DividerDark()

                    OptionItem(title = stringResource(R.string.profile_accesibilidad))

                    DividerDark()

                    OptionItem(title = stringResource(R.string.profile_legal)) {
                        navController.navigate("legal")
                    }

                    DividerDark()

                    OptionItem(title = stringResource(R.string.profile_privacidad)) {
                        navController.navigate("privacy")
                    }

                    DividerDark()

                    OptionItem(title = stringResource(R.string.profile_soporte)) {
                        navController.navigate("support")
                    }

                    DividerDark()

                    OptionItem(
                        title = stringResource(R.string.profile_cerrar_sesion),
                        isDanger = true
                    ) {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column {
        Text(
            text = label,
            color = GrayText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = FieldColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryLight,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = value,
                    color = WhiteSoft,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun OptionItem(
    title: String,
    isDanger: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = if (isDanger) Danger else WhiteSoft,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "›",
            color = if (isDanger) Danger else GrayText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DividerDark() {
    HorizontalDivider(
        color = Color.White.copy(alpha = 0.06f),
        thickness = 1.dp
    )
}