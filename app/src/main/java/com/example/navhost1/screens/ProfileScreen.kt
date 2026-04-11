package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController, username: String?) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1EAFE))
            .verticalScroll(rememberScrollState())
    ) {

        // 🔝 HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF9575CD), Color(0xFF7E57C2))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 22.sp,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Perfil de usuario",  color = Color.White, fontSize = 18.sp)
                    Text(text = username ?: "Usuario", color = Color.White, fontSize = 14.sp)
                    Text(text = "Correo electrónico",  color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }
        }

        // 🚨 BOTÓN EMERGENCIAS
        Button(
            onClick = { navController.navigate("emergency") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(50),
            modifier = Modifier.padding(16.dp).height(50.dp)
        ) {
            Text("Emergencias", color = Color.White)
        }

        // 📝 FORMULARIO
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ProfileField("Nombre de usuario", username ?: "Usuario")
            ProfileField("Teléfono", "")
            ProfileField("Fecha de nacimiento", "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📋 OPCIONES
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            OptionItem("Planes")       { navController.navigate("planes") }
            HorizontalDivider()
            OptionItem("Plan actual")  { navController.navigate("premium") }
            HorizontalDivider()
            OptionItem("Configuración") { navController.navigate("settings") }
            HorizontalDivider()
            OptionItem("Accesibilidad")
            HorizontalDivider()
            OptionItem("Legal")        { navController.navigate("legal") }
            HorizontalDivider()
            OptionItem("Privacidad")   { navController.navigate("privacy") }
            HorizontalDivider()
            OptionItem("Soporte")      { navController.navigate("support") }
            HorizontalDivider()
            OptionItem("Cerrar sesión") {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFB39DDB), Color(0xFF9575CD))
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = value, color = Color.White, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Composable
fun OptionItem(title: String, onClick: () -> Unit = {}) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        fontSize = 15.sp
    )
}