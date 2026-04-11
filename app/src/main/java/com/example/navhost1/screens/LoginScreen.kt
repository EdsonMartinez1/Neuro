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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEDE7FF), // morado claro
                        Color(0xFFD1C4E9)  // morado suave
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 🔥 TÍTULO
            Text(
                text = "NeuraBloom",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6C4DFF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu compañero emocional con IA",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 📧 EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔒 PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔥 BOTÓN LOGIN
            Button(
                onClick = {

                    val username = if (email.contains("@")) {
                        email.substringBefore("@")
                    } else {
                        email
                    }.replaceFirstChar { it.uppercase() }

                    navController.navigate("home/$username") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9575CD)
                )
            ) {
                Text("INICIAR SESIÓN", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🟣 REGISTRO (opcional)
            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text("¿No tienes cuenta? Crear una", color = Color(0xFF6C4DFF))
            }
        }
    }
}