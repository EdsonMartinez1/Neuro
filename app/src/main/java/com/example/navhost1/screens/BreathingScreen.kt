package com.example.navhost1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager





private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF06B6D4)
private val PrimaryLight = Color(0xFF67E8F9)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

@Composable
fun BreathingScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current

    fun vibrar(duracion: Long) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                        as VibratorManager

            vibratorManager.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(
                    duracion,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE)
                        as Vibrator

            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duracion,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

        } else {

            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE)
                        as Vibrator

            @Suppress("DEPRECATION")
            vibrator.vibrate(duracion)
        }
    }

    var sesionIniciada by remember {
        mutableStateOf(false)
    }

    var sesionCompletada by remember {
        mutableStateOf(false)
    }
    var fase by remember {
        mutableStateOf("LISTO")
    }

    val escalaObjetivo =
        when (fase) {
            "INHALA" -> 1.25f
            "MANTÉN" -> 1.25f
            "EXHALA" -> 0.95f
            else -> 0.95f
        }

    val scale by animateFloatAsState(
        targetValue = escalaObjetivo,
        animationSpec = tween(
            durationMillis = when (fase) {
                "INHALA" -> 4000
                "EXHALA" -> 6000
                else -> 0
            },
            easing = FastOutSlowInEasing
        ),
        label = "breathingScale"
    )

    val alphaObjetivo =
        if (sesionIniciada) 0.8f else 0.3f

    val alpha by animateFloatAsState(
        targetValue = alphaObjetivo,
        animationSpec = tween(
            durationMillis = 1000
        ),
        label = "breathingAlpha"
    )



    LaunchedEffect(sesionIniciada) {

        if (sesionIniciada) {

            repeat(3) {

                fase = "INHALA"
                vibrar(150)
                delay(4000)

                fase = "MANTÉN"
                delay(4000)

                fase = "EXHALA"
                vibrar(150)
                delay(6000)
            }

            fase = "COMPLETADO"
            sesionCompletada = true
            sesionIniciada = false
        }
    }

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
                    Primary.copy(alpha = 0.12f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
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

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Respiración Guiada",
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Relaja tu mente siguiendo el ritmo de respiración.",
                color = GrayText,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.scale(
                    when (fase) {
                        "INHALA" -> scale
                        "MANTÉN" -> 1.25f
                        "EXHALA" -> scale
                        else -> 0.95f
                    }
                )
            ) {

                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .alpha(alpha * 0.4f)
                        .clip(CircleShape)
                        .background(Primary)
                )

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .alpha(alpha * 0.6f)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                )

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    PrimaryLight,
                                    Primary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = fase,
                color = WhiteSoft,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Inhala profundamente y exhala lentamente",
                color = GrayText,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {

                    if (sesionCompletada) {

                        sesionCompletada = false
                        sesionIniciada = true
                        fase = "INHALA"

                        registrarUsoHerramienta(
                            "respiracion"
                        )

                    } else {

                        sesionIniciada = true

                        registrarUsoHerramienta(
                            "respiracion"
                        )
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {

                Text(
                    text =
                        if (sesionCompletada)
                            "Comenzar otra sesión"
                        else
                            "Comenzar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (sesionCompletada) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "✨ Sesión completada",
                    color = WhiteSoft,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Has terminado tu ejercicio de respiración.",
                    color = GrayText,
                    fontSize = 14.sp
                )
            }
        }
    }
}