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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
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
                    Text(text = stringResource(R.string.profile_titulo),   color = Color.White, fontSize = 18.sp)
                    Text(text = username ?: "Usuario", color = Color.White, fontSize = 14.sp)
                    Text(text = stringResource(R.string.profile_email),  color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
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
            Text(stringResource(R.string.profile_emergencias), color = Color.White)
        }

        // 📝 FORMULARIO
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ProfileField(stringResource(R.string.profile_nombre_usuario), username ?: "Usuario")
            ProfileField(stringResource(R.string.profile_telefono), "")
            ProfileField(stringResource(R.string.profile_fe_na), "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📋 OPCIONES
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            OptionItem(stringResource(R.string.profile_planes))        { navController.navigate("planes") }
            HorizontalDivider()
            OptionItem(stringResource(R.string.profile_plan_actual))   { navController.navigate("premium") }
            HorizontalDivider()
            OptionItem(stringResource(R.string.profile_configuracion)) { navController.navigate("settings") }
            HorizontalDivider()
            OptionItem(stringResource(R.string.profile_accesibilidad))
            HorizontalDivider()
            OptionItem(stringResource(R.string.profile_legal))         { navController.navigate("legal") }
            HorizontalDivider()
            OptionItem(stringResource(R.string.profile_privacidad))    { navController.navigate("privacy") }
            HorizontalDivider()
            OptionItem(stringResource(R.string.profile_soporte))       { navController.navigate("support") }
            HorizontalDivider()

            // ✅ Ahora navega al diálogo de confirmación
            OptionItem(stringResource(R.string.profile_cerrar_sesion)) {
                navController.navigate("logout")
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