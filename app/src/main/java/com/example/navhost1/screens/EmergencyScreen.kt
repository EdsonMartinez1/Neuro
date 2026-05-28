package com.example.navhost1.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

private val BackgroundTop = Color(0xFF3B0A0A)
private val BackgroundBottom = Color(0xFF7F1D1D)

private val EmergencyRed = Color(0xFFEF4444)
private val EmergencyLight = Color(0xFFF87171)

private val WhiteSoft = Color(0xFFF8FAFC)
private val CardColor = Color(0xFFFFFFFF)

@Composable
fun EmergencyScreen(navController: NavController) {

    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
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
                .size(340.dp)
                .offset(x = (-100).dp, y = (-80).dp)
                .clip(CircleShape)
                .background(
                    EmergencyLight.copy(alpha = alphaAnim)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                EmergencyLight,
                                EmergencyRed
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(62.dp)
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = stringResource(R.string.emergency_titulo),
                color = WhiteSoft,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.emergency_subtitulo),
                color = WhiteSoft.copy(alpha = 0.88f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(42.dp))

            EmergencyButton(
                label = stringResource(R.string.emergency_linea_crisis),
                icon = Icons.Default.Call,
                onClick = {

                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:5552598121")
                    }

                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            EmergencyButton(
                label = stringResource(R.string.emergency_emergencia),
                icon = Icons.Default.LocalHospital,
                onClick = {

                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:911")
                    }

                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            EmergencyButton(
                label = stringResource(R.string.emergency_ayuda_profesional),
                icon = Icons.Default.WarningAmber,
                onClick = {

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(
                            "https://simisae.com.mx/psicologos-en-linea"
                        )
                    }

                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "No estás solo",
                        color = WhiteSoft,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Buscar ayuda es un acto de valentía. Hay personas listas para escucharte y ayudarte.",
                        color = WhiteSoft.copy(alpha = 0.82f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CardColor,
            contentColor = EmergencyRed
        )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        EmergencyRed.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmergencyRed,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ">",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}