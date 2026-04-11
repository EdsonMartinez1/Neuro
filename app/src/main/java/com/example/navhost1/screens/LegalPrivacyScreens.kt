package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ── Paleta compartida ─────────────────────────────────────────────────────────
private val PurpleBar  = Color(0xFF7B2FBE)
private val BgLegal    = Color(0xFFEDE7F6)
private val TextBody   = Color(0xFF424242)
private val TextDark   = Color(0xFF212121)

// ── Componente base reutilizable ──────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PolicyScreen(
    navController: NavController,
    title: String,
    sections: List<Pair<String, String>>,
    charCount: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleBar
                )
            )
        },
        containerColor = BgLegal
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                sections.forEach { (heading, body) ->
                    if (heading.isNotEmpty()) {
                        Text(
                            text = heading,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Text(
                        text = body,
                        fontSize = 14.sp,
                        color = TextBody,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                // Espaciado para que el contador no tape el texto
                Spacer(modifier = Modifier.height(48.dp))
            }

            // ── Contador de caracteres (esquina inferior derecha) ─────────────
            Text(
                text = "$charCount",
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 12.dp)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  PANTALLA LEGAL
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun LegalScreen(navController: NavController) {
    PolicyScreen(
        navController = navController,
        title = "Legal",
        charCount = 110,
        sections = listOf(
            "" to "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Nam gravida venenatis accumsan. In mi massa, tempus at quam id, " +
                    "vehicula blandit arcu. Nullam vel dui aliquet, placerat urna vel, " +
                    "gravida eros.",
            "Términos de uso" to "Pellentesque habitant morbi tristique senectus et netus " +
                    "et malesuada fames ac turpis egestas. Vestibulum tortor quam, " +
                    "feugiat vitae, ultricies eget, tempor sit amet, ante.",
            "Responsabilidad" to "Donec eu libero sit amet quam egestas semper. " +
                    "Aenean ultricies mi vitae est. Mauris placerat eleifend leo. " +
                    "Quisque sit amet est et sapien ullamcorper pharetra.",
            "Modificaciones" to "Vestibulum erat wisi, condimentum sed, commodo vitae, " +
                    "ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, " +
                    "eros ipsum rutrum orci, sagittis tempus lacus enim ac dui."
        )
    )
}

// ══════════════════════════════════════════════════════════════════════════════
//  PANTALLA PRIVACIDAD
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun PrivacyScreen(navController: NavController) {
    PolicyScreen(
        navController = navController,
        title = "Privacidad",
        charCount = 98,
        sections = listOf(
            "" to "Tu privacidad es importante para nosotros. Esta política explica " +
                    "qué información recopilamos, cómo la usamos y qué opciones tienes " +
                    "sobre ella.",
            "Datos que recopilamos" to "Recopilamos información que nos proporcionas " +
                    "directamente, como tu nombre, correo electrónico y preferencias " +
                    "dentro de la aplicación.",
            "Uso de la información" to "Usamos la información para personalizar tu " +
                    "experiencia, mejorar nuestros servicios y comunicarnos contigo " +
                    "cuando sea necesario.",
            "Tus derechos" to "Puedes solicitar el acceso, corrección o eliminación " +
                    "de tus datos personales en cualquier momento a través de la " +
                    "sección de Soporte."
        )
    )
}