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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

// ── Paleta ────────────────────────────────────────────────────────────────────
private val Yellow      = Color(0xFFFDD835)
private val YellowDark  = Color(0xFFF9A825)
private val TextOnYellow = Color(0xFF1A1A1A)
private val White       = Color.White
private val StarWhite   = Color.White



// ── Pantalla ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(navController: NavController) {

    val premiumFeatures = listOf(
        stringResource(R.string.plan_actual_funciones_1),
        stringResource(R.string.plan_actual_funciones_2),
        stringResource(R.string.plan_actual_funciones_3),
        stringResource(R.string.plan_actual_funciones_4),
    )

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

            // ── Estrella con badge ────────────────────────────────────────────
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarWhite,
                    modifier = Modifier.size(140.dp)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = YellowDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.plan_actual_titulo),
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextOnYellow,
                letterSpacing = 2.sp
            )
            Text(
                text = stringResource(R.string.plan_actual_subtitulo),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextOnYellow,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // ── Lista de funciones ────────────────────────────────────────────
            premiumFeatures.forEach { feature ->
                FeatureRow(text = feature, tintColor = White)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botón Suscribirse ─────────────────────────────────────────────
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = YellowDark
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.plan_actual_suscribirse),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = YellowDark
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Enlace comparar planes ────────────────────────────────────────
            TextButton(onClick = { navController.navigate("planes") }) {
                Text(
                    text = stringResource(R.string.plan_actual_comparar),
                    color = TextOnYellow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Fila de función ───────────────────────────────────────────────────────────
@Composable
fun FeatureRow(text: String, tintColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(tintColor.copy(alpha = 0.35f))
        ) {
            Text(
                text = text,
                color = tintColor,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}