package com.example.navhost1.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.navhost1.R
import com.example.navhost1.habits.HabitRepository
import com.google.firebase.auth.FirebaseAuth
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
private val ErrorRed = Color(0xFFEF4444)

private data class CrearHabitParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CrearHabitScreen(
    navController: NavHostController
) {
    val repository = remember { HabitRepository() }
    val auth = FirebaseAuth.getInstance()

    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val categoriasSugeridas = listOf("BIENESTAR", "EJERCICIO", "LECTURA", "DESCANSO", "SUEÑO")

    // Partículas ambientales de fondo
    val particles = remember {
        List(20) {
            CrearHabitParticle(
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
            // Header con botón atrás
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
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Nuevo hábito 🌱",
                        color = WhiteSoft,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Construye tu bienestar paso a paso",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta Formulario Glassmorphism
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = CardColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {

                    // Mensaje de Error Animado
                    AnimatedVisibility(visible = mensajeError != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = ErrorRed.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = mensajeError ?: "",
                                    color = WhiteSoft,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Campo Nombre
                    Text(
                        text = "Nombre del hábito",
                        color = WhiteSoft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            mensajeError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. Meditar 10 minutos", color = GrayText.copy(alpha = 0.6f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Spa, contentDescription = null, tint = PrimaryLight)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
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

                    // Campo Categoría + Chips
                    Text(
                        text = "Categoría",
                        color = WhiteSoft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {
                            categoria = it
                            mensajeError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. BIENESTAR", color = GrayText.copy(alpha = 0.6f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Label, contentDescription = null, tint = PrimaryLight)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sugerencias Rápidas de Categoría
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categoriasSugeridas.forEach { cat ->
                            FilterChip(
                                selected = categoria.equals(cat, ignoreCase = true),
                                onClick = {
                                    categoria = cat
                                    mensajeError = null
                                },
                                label = { Text(cat, fontSize = 12.sp) },
                                shape = RoundedCornerShape(14.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = FieldColor,
                                    labelColor = GrayText
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = categoria.equals(cat, ignoreCase = true),
                                    borderColor = BorderColor,
                                    selectedBorderColor = PrimaryLight
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Campo Descripción
                    Text(
                        text = "Descripción",
                        color = WhiteSoft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            descripcion = it
                            mensajeError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("¿Qué harás cada día para cumplir este hábito?", color = GrayText.copy(alpha = 0.6f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = PrimaryLight)
                        },
                        shape = RoundedCornerShape(18.dp),
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón Guardar
                    Button(
                        onClick = {
                            val uid = auth.currentUser?.uid

                            if (uid == null) {
                                mensajeError = "No hay un usuario autenticado."
                                return@Button
                            }

                            if (nombre.isBlank()) {
                                mensajeError = "Escribe el nombre del hábito."
                                return@Button
                            }

                            if (categoria.isBlank()) {
                                mensajeError = "Escribe o selecciona una categoría."
                                return@Button
                            }

                            if (descripcion.isBlank()) {
                                mensajeError = "Escribe una descripción."
                                return@Button
                            }

                            guardando = true
                            mensajeError = null

                            repository.crearHabit(
                                uid = uid,
                                nombre = nombre.trim(),
                                categoria = categoria.trim().uppercase(),
                                descripcion = descripcion.trim(),
                                xp = 10,
                                generadoPorIA = false,
                                onSuccess = {
                                    guardando = false
                                    navController.popBackStack()
                                },
                                onError = { error ->
                                    guardando = false
                                    mensajeError = error.message ?: "No se pudo crear el hábito."
                                }
                            )
                        },
                        enabled = !guardando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        if (guardando) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Guardando...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Guardar hábito",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cancelar",
                            color = GrayText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}