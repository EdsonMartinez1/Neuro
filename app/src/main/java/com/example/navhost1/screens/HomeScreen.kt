package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, username: String?) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEDE7FF),
                        Color(0xFFD1C4E9)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // 🔝 HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "Hola 👋",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = username ?: "Usuario",
                        color = Color(0xFF4A3F8F),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 👤 PERFIL
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9575CD))
                        .clickable {
                            navController.navigate("profile/${username ?: "usuario"}")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_myplaces),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Tu bienestar emocional 💜",
                color = Color(0xFF4A3F8F),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔲 GRID
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    MenuCard("Chat IA", android.R.drawable.ic_dialog_email) {
                        navController.navigate("chat")
                    }

                    MenuCard("Herramientas", android.R.drawable.ic_menu_manage) {
                        navController.navigate("tools")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    MenuCard("Diario", android.R.drawable.ic_menu_edit) {
                        navController.navigate("diary")
                    }

                    MenuCard("Contenido", android.R.drawable.ic_menu_info_details) {
                        navController.navigate("content")
                    }
                }
            }
        }
    }
}
@Composable
fun MenuCard(title: String, icon: Int, onClick: () -> Unit) {

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color(0xFF9575CD),
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = Color(0xFF4A3F8F),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}