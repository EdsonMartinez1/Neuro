package com.example.navhost1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

@Composable
fun BreathingScreen(navController: NavController) {

    // 🔄 ANIMACIÓN
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val text = if (scale > 1.2f) stringResource(R.string.breathing_in) else stringResource(R.string.breathing_ex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF26C6DA)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔙 BACK
            Text(
                text = "←",
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(16.dp)
                    .clickable { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 🔵 CÍRCULOS ANIMADOS
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.scale(scale)
            ) {

                // CÍRCULO GRANDE
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                )

                // CÍRCULO MEDIO
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                )

                // CÍRCULO PEQUEÑO
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                )

                // ICONO
                Text("🌬️", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // TEXTO DINÁMICO
            Text(
                text = text,
                color = Color.White,
                fontSize = 22.sp
            )
        }
    }
}