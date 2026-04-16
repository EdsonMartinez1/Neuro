package com.example.navhost1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

// ── Paleta compartida ─────────────────────────────────────────────────────────
private val PurpleBar = Color(0xFF7B2FBE)
private val BgLegal   = Color(0xFFEDE7F6)
private val TextBody  = Color(0xFF424242)
private val TextDark  = Color(0xFF212121)

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
                Spacer(modifier = Modifier.height(48.dp))
            }

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
        title = stringResource(R.string.legal_titulo),
        charCount = 110,
        sections = listOf(
            "" to stringResource(R.string.legal_subtitulo),
            stringResource(R.string.legal_terminos)        to stringResource(R.string.legal_terminos_subtitulo),
            stringResource(R.string.legal_responsabilidad) to stringResource(R.string.legal_responsabilidad_subtitulo),
            stringResource(R.string.legal_modificaciones)  to stringResource(R.string.legal_modificaciones_subtitulo)
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
        title = stringResource(R.string.privacidad_titulo),
        charCount = 98,
        sections = listOf(
            ""                                           to stringResource(R.string.privacidad_subtitulo),
            stringResource(R.string.privacidad_datos)    to stringResource(R.string.privacidad_datos_subtitulo),
            stringResource(R.string.privacidad_uso)      to stringResource(R.string.privacidad_uso_subtitulo),
            stringResource(R.string.privacidad_derechos) to stringResource(R.string.privacidad_derechos_subtitulo)
        )
    )
}