package com.example.navhost1.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController) {

    val problemOptions = listOf(
        stringResource(R.string.contact_problema),
        stringResource(R.string.contact_problema_1),
        stringResource(R.string.contact_problema_2),
        stringResource(R.string.contact_problema_3),
        stringResource(R.string.contact_problema_4)
    )

    var selectedProblem by remember {
        mutableStateOf(problemOptions[0])
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var messageText by remember {
        mutableStateOf("")
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    val responses = listOf<Pair<String, String>>()

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

            Box(
                modifier = Modifier
                    .size(90.dp)
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
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.contact_titulo),
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Nuestro equipo está listo para ayudarte con cualquier inconveniente.",
                color = GrayText,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

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

                    Text(
                        text = "Selecciona un problema",
                        color = WhiteSoft,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {

                        OutlinedTextField(
                            value = selectedProblem,
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = GrayText
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = FieldColor,
                                unfocusedContainerColor = FieldColor,

                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BorderColor,

                                focusedTextColor = WhiteSoft,
                                unfocusedTextColor = WhiteSoft
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            },
                            containerColor = CardColor
                        ) {

                            problemOptions.drop(1).forEach { option ->

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            color = WhiteSoft
                                        )
                                    },
                                    onClick = {
                                        selectedProblem = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = {
                            messageText = it
                        },
                        placeholder = {

                            Text(
                                text = stringResource(R.string.contact_area_texto),
                                color = GrayText
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldColor,
                            unfocusedContainerColor = FieldColor,

                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,

                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,

                            cursorColor = Primary
                        ),
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {

                            if (messageText.isNotBlank()) {

                                submitted = true
                                messageText = ""
                                selectedProblem = problemOptions[0]
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = stringResource(R.string.contact_boton_enviar),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    AnimatedVisibility(submitted) {

                        Column {

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(R.string.contact_confirmacion_envio),
                                color = Color(0xFF4ADE80),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = stringResource(R.string.contact_respuestas_soporte_titulo),
                color = WhiteSoft,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (responses.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardColor.copy(alpha = 0.96f))
                        .border(
                            1.dp,
                            BorderColor,
                            RoundedCornerShape(24.dp)
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = stringResource(R.string.contact_respuestas_soporte),
                        color = GrayText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }

            } else {

                responses.forEach { (fecha, mensaje) ->

                    ResponseCard(
                        fecha = fecha,
                        mensaje = mensaje
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResponseCard(
    fecha: String,
    mensaje: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardColor.copy(alpha = 0.96f))
            .border(
                1.dp,
                BorderColor,
                RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {

        Text(
            text = fecha,
            fontSize = 12.sp,
            color = GrayText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mensaje,
            fontSize = 15.sp,
            color = WhiteSoft,
            lineHeight = 24.sp
        )
    }
}