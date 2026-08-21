package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.navhost1.habits.HabitRepository
import com.google.firebase.auth.FirebaseAuth

private val Background = Color(0xFF0F172A)
private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)
private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

@Composable
fun CrearHabitScreen(
    navController: NavHostController
) {

    val repository = remember {
        HabitRepository()
    }

    val auth = FirebaseAuth.getInstance()

    var nombre by remember {
        mutableStateOf("")
    }

    var categoria by remember {
        mutableStateOf("")
    }

    var descripcion by remember {
        mutableStateOf("")
    }

    var guardando by remember {
        mutableStateOf(false)
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        Text(
            text = "Nuevo hábito 🌱",
            color = WhiteSoft,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Crea un pequeño hábito para acompañar tu crecimiento.",
            color = GrayText,
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                mensajeError = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Nombre del hábito")
            },
            placeholder = {
                Text("Ej. Leer 10 páginas")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = {
                categoria = it
                mensajeError = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Categoría")
            },
            placeholder = {
                Text("Ej. LECTURA")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
                mensajeError = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Descripción")
            },
            placeholder = {
                Text("¿Qué debe hacer el usuario?")
            },
            minLines = 3
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (mensajeError != null) {

            Text(
                text = mensajeError!!,
                color = Color(0xFFFF8A8A),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Button(
            onClick = {

                val uid =
                    auth.currentUser?.uid

                if (uid == null) {

                    mensajeError =
                        "No hay un usuario autenticado."

                    return@Button
                }

                if (nombre.isBlank()) {

                    mensajeError =
                        "Escribe el nombre del hábito."

                    return@Button
                }

                if (categoria.isBlank()) {

                    mensajeError =
                        "Escribe una categoría."

                    return@Button
                }

                if (descripcion.isBlank()) {

                    mensajeError =
                        "Escribe una descripción."

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

                        mensajeError =
                            error.message
                                ?: "No se pudo crear el hábito."
                    }
                )
            },

            enabled = !guardando,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            )
        ) {

            Text(
                text = if (guardando) {
                    "Guardando..."
                } else {
                    "Guardar hábito"
                },
                color = WhiteSoft,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Cancelar",
                color = PrimaryLight
            )
        }
    }
}