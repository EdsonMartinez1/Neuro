package com.example.navhost1.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

private val Primary = Color(0xFFF59E0B)
private val PrimaryLight = Color(0xFFFCD34D)

private val CardColor = Color(0xFF111827)
private val FieldColor = Color(0xFF1F2937)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)
private val BorderColor = Color(0xFF334155)

@Composable
fun GratitudeScreen(navController: NavController) {

    var text1 by remember {
        mutableStateOf("")
    }

    var text2 by remember {
        mutableStateOf("")
    }

    var text3 by remember {
        mutableStateOf("")
    }

    var saved by remember {
        mutableStateOf(false)
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
                .size(320.dp)
                .offset(x = (-90).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Primary.copy(alpha = 0.12f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Row(
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

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Primary,
                                    PrimaryLight
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = stringResource(R.string.tools_gratitud_titulo),
                        color = WhiteSoft,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Cultiva pensamientos positivos cada día",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor.copy(alpha = 0.96f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = PrimaryLight
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Momentos de gratitud",
                            color = WhiteSoft,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Reflexiona sobre lo bueno que existe en tu vida y fortalece tu bienestar emocional.",
                        color = GrayText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = text1,
                        onValueChange = {
                            text1 = it
                            saved = false
                        },
                        label = {
                            Text(
                                stringResource(R.string.gratitude_persona)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldColor,
                            unfocusedContainerColor = FieldColor,

                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,

                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,

                            focusedLabelColor = PrimaryLight,
                            unfocusedLabelColor = GrayText,

                            cursorColor = Primary
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = text2,
                        onValueChange = {
                            text2 = it
                            saved = false
                        },
                        label = {
                            Text(
                                stringResource(R.string.gratitude_lugar)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldColor,
                            unfocusedContainerColor = FieldColor,

                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,

                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,

                            focusedLabelColor = PrimaryLight,
                            unfocusedLabelColor = GrayText,

                            cursorColor = Primary
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = text3,
                        onValueChange = {
                            text3 = it
                            saved = false
                        },
                        label = {
                            Text(
                                stringResource(R.string.gratitude_aprendizaje)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldColor,
                            unfocusedContainerColor = FieldColor,

                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,

                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,

                            focusedLabelColor = PrimaryLight,
                            unfocusedLabelColor = GrayText,

                            cursorColor = Primary
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {

                            if (
                                text1.isNotBlank() ||
                                text2.isNotBlank() ||
                                text3.isNotBlank()
                            ) {

                                registrarUsoHerramienta(
                                    "gratitud"
                                )

                                saved = true
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
                            text = stringResource(R.string.gratitude_guardar),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    AnimatedVisibility(saved) {

                        Column {

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Reflexión guardada correctamente ✨",
                                color = Color(0xFF4ADE80),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}