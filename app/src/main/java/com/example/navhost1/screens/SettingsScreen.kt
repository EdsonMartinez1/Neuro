package com.example.navhost1.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ── Paleta ────────────────────────────────────────────────────────────────────
private val Purple   = Color(0xFF7B2FBE)
private val TextPrim = Color(0xFF1A1A1A)
private val TextSec  = Color(0xFF757575)
private val Divider  = Color(0xFFE0E0E0)

// ── Pantalla ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    var darkMode by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuracion",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple
                )
            )
        },
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SettingsItem(
                title    = "Cuenta",
                subtitle = "Edita tu informacion personal",
                onClick  = { }
            )
            HorizontalDivider(color = Divider, thickness = 0.8.dp)

            SettingsItem(
                title    = "Preferencias Emocionales",
                subtitle = "Personaliza tu experiencia",
                onClick  = { }
            )
            HorizontalDivider(color = Divider, thickness = 0.8.dp)

            SettingsItem(
                title    = "Notificaciones",
                subtitle = "Configura recordatorios",
                onClick  = { }
            )
            HorizontalDivider(color = Divider, thickness = 0.8.dp)

            // ✅ Idioma conectado
            SettingsItem(
                title    = "Idioma",
                subtitle = "Cambia el idioma de la app",
                onClick  = { navController.navigate("language") }
            )
            HorizontalDivider(color = Divider, thickness = 0.8.dp)

            SettingsToggleItem(
                title           = "Apariencia",
                subtitle        = "Personaliza la app",
                checked         = darkMode,
                onCheckedChange = { darkMode = it }
            )
            HorizontalDivider(color = Divider, thickness = 0.8.dp)
        }
    }
}

// ── Item estándar ─────────────────────────────────────────────────────────────
@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title,    fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrim)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 13.sp, color = TextSec)
        }
    }
}

// ── Item con Switch ───────────────────────────────────────────────────────────
@Composable
private fun SettingsToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title,    fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrim)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 13.sp, color = TextSec)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = Purple,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBDBDBD)
            )
        )
    }
}