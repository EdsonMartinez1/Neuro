package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ToolsScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1FF))
    ) {

        // 🔝 HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF7E57C2))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "←",
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Herramientas de Bienestar",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔲 GRID
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ToolCard("Respiración", Color(0xFF42A5F5)) {
                    navController.navigate("breathing")
                }

                ToolCard("Meditación", Color(0xFFAB47BC)) {
                    navController.navigate("meditation")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ToolCard("Ansiedad", Color(0xFF66BB6A)) {
                    navController.navigate("anxiety")
                }

                ToolCard("Gratitud", Color(0xFFFF8A65)) {
                    navController.navigate("gratitude")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔽 CENTRADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ToolCard("Autoestima", Color(0xFFEC407A)) {
                    navController.navigate("selfesteem")
                }
            }
        }
    }
}
@Composable
fun ToolCard(title: String, color: Color, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔘 ICONO SIMPLE (puedes cambiar luego)
            Text(
                text = "★",
                fontSize = 40.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}