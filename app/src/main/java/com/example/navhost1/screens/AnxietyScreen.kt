package com.example.navhost1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import androidx.compose.ui.res.stringResource

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)
private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

@Composable
fun AnxietyScreen(navController: NavController) {

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val circleScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

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
                .size(320.dp)
                .offset(x = (-90).dp, y = (-50).dp)
                .clip(CircleShape)
                .background(
                    Primary.copy(alpha = 0.15f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.anxiety_control),
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Monitorea y regula tus niveles emocionales en tiempo real.",
                color = GrayText,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor.copy(alpha = 0.96f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "Indicadores",
                        color = WhiteSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    AnxietyBar(
                        title = "Estrés",
                        progress = 0.8f,
                        color = Color(0xFFFF6B6B)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AnxietyBar(
                        title = "Respiración",
                        progress = 0.5f,
                        color = Color(0xFFFFB86B)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AnxietyBar(
                        title = "Calma",
                        progress = 0.3f,
                        color = Color(0xFF6BCBFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Primary,
                                    PrimaryLight
                                )
                            )
                        )
                        .alpha(0.9f),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "1,453",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Frecuencia emocional",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {

                Text(
                    text = "Iniciar relajación",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AnxietyBar(
    title: String,
    progress: Float,
    color: Color
) {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = title,
                color = WhiteSoft,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                color = GrayText,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF263245))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(14.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color)
            )
        }
    }
}