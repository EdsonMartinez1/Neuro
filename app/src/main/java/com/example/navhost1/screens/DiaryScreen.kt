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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
private val FieldColor = Color(0xFF1F2937)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)
private val BorderColor = Color(0xFF334155)

@Composable
fun DiaryScreen(navController: NavController) {

    var text by remember {
        mutableStateOf(TextFieldValue(""))
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
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = stringResource(R.string.diario_titulo),
                        color = WhiteSoft,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Expresa tus emociones libremente",
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
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = PrimaryLight
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Tu espacio seguro",
                            color = WhiteSoft,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Escribe cómo te sientes hoy, qué piensas o qué deseas mejorar.",
                        color = GrayText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            saved = false
                        },
                        placeholder = {

                            Text(
                                text = stringResource(R.string.diario_subtitulo),
                                color = GrayText
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldColor,
                            unfocusedContainerColor = FieldColor,

                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,

                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,

                            cursorColor = Primary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {

                            if (text.text.isNotBlank()) {
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

                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = stringResource(R.string.diario_guardar),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    AnimatedVisibility(saved) {

                        Column {

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Entrada guardada correctamente ✨",
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