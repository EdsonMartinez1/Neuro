package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DiaryScreen(navController: NavController) {

    var text by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEDE7FF), // morado muy claro
                        Color(0xFFD1C4E9)  // morado suave
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // 🔝 HEADER
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    navController.popBackStack()
                }) {
                    Text("←")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Mi Diario",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 📝 CAJA DE TEXTO
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("¿Cómo te sientes hoy?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9575CD),
                    unfocusedBorderColor = Color(0xFFB39DDB)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 💾 BOTÓN GUARDAR
            Button(
                onClick = {
                    // Aquí luego puedes guardar
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9575CD)
                )
            ) {
                Text("Guardar entrada")
            }
        }
    }
}