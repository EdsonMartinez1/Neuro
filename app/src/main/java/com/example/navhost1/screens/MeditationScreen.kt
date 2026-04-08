package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MeditationScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF8E24AA), Color(0xFFCE93D8))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

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

            Text("🧠", fontSize = 70.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Meditación guiada",
                color = Color.White,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Cierra los ojos y respira profundo",
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}