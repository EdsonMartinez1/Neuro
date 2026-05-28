package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

@Composable
fun ToolsScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BackgroundTop,
                        BackgroundBottom
                    )
                )
            )
    ) {

        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-90).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Color(0xFF8B5CF6).copy(alpha = 0.10f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 20.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = WhiteSoft
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {

                    Text(
                        text = stringResource(R.string.tools_titulo),
                        color = WhiteSoft,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Herramientas para tu bienestar emocional",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    ToolCard(
                        title = stringResource(R.string.tools_respiracion_titulo),
                        color1 = Color(0xFF3B82F6),
                        color2 = Color(0xFF60A5FA),
                        icon = Icons.Default.Waves
                    ) {
                        navController.navigate("breathing")
                    }

                    ToolCard(
                        title = stringResource(R.string.tools_meditacion_titulo),
                        color1 = Color(0xFF8B5CF6),
                        color2 = Color(0xFFA78BFA),
                        icon = Icons.Default.SelfImprovement
                    ) {
                        navController.navigate("meditation")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    ToolCard(
                        title = stringResource(R.string.tools_ansiedad_titulo),
                        color1 = Color(0xFF10B981),
                        color2 = Color(0xFF34D399),
                        icon = Icons.Default.Psychology
                    ) {
                        navController.navigate("anxiety")
                    }

                    ToolCard(
                        title = stringResource(R.string.tools_gratitud_titulo),
                        color1 = Color(0xFFF97316),
                        color2 = Color(0xFFFB923C),
                        icon = Icons.Default.Favorite
                    ) {
                        navController.navigate("gratitude")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    ToolCard(
                        title = stringResource(R.string.tools_autoestima_titulo),
                        color1 = Color(0xFFEC4899),
                        color2 = Color(0xFFF472B6),
                        icon = Icons.Default.Spa
                    ) {
                        navController.navigate("selfesteem")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ToolCard(
    title: String,
    color1: Color,
    color2: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .width(155.dp)
            .height(190.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            color1,
                            color2
                        )
                    )
                )
        ) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 70.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = 0.08f)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(alpha = 0.18f)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Column {

                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Explorar",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}