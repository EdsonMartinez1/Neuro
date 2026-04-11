package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ── Paleta ────────────────────────────────────────────────────────────────────
private val Yellow       = Color(0xFFFDD835)
private val YellowDark   = Color(0xFFF9A825)
private val Purple       = Color(0xFF7B2FBE)
private val TextOnYellow = Color(0xFF1A1A1A)
private val White        = Color.White

private val premiumPerks = listOf(
    "Acceso ilimitado a herramientas",
    "Meditación guiada",
    "Seguimiento avanzado",
    "Contenido exclusivo",
)

private val freePerks = listOf(
    "Herramientas básicas",
    "Diario emocional",
    "Contenido limitado",
    "Soporte estándar",
)

// ── Pantalla ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextOnYellow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Yellow
                )
            )
        },
        containerColor = Yellow
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ════════════════════════════════════════════════════════
            //  SECCIÓN PREMIUM
            // ════════════════════════════════════════════════════════

            // Estrella blanca con badge
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(110.dp)
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = YellowDark,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Text(
                text = "PREMIUM",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextOnYellow,
                letterSpacing = 2.sp
            )
            Text(
                text = "Desbloquea todas las funciones",
                fontSize = 13.sp,
                color = TextOnYellow,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            premiumPerks.forEach { perk ->
                FeatureRow(text = perk, tintColor = White)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate("premium") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = YellowDark
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Suscribirse",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = YellowDark
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Divisor
            HorizontalDivider(
                color = White.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ════════════════════════════════════════════════════════
            //  SECCIÓN FREEMIUM
            // ════════════════════════════════════════════════════════

            // Estrella morada con badge
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(110.dp)
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Text(
                text = "FREEMIUM",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextOnYellow,
                letterSpacing = 2.sp
            )
            Text(
                text = "Funciones gratuitas",
                fontSize = 13.sp,
                color = TextOnYellow,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            freePerks.forEach { perk ->
                FeatureRow(text = perk, tintColor = Purple)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Plan actual (deshabilitado / informativo)
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = White.copy(alpha = 0.3f),
                    contentColor = TextOnYellow
                ),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text(
                    text = "Plan actual",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}