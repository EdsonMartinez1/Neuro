package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

@Composable
private fun PolicyScreen(
    navController: NavController,
    title: String,
    sections: List<Pair<String, String>>,
    iconType: String
) {

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
                        imageVector =
                            if (iconType == "legal")
                                Icons.Default.Gavel
                            else
                                Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = title,
                        color = WhiteSoft,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text =
                            if (iconType == "legal")
                                "Información legal y términos"
                            else
                                "Protección y privacidad de datos",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            sections.forEach { (heading, body) ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardColor.copy(alpha = 0.96f)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {

                        if (heading.isNotEmpty()) {

                            Text(
                                text = heading,
                                color = WhiteSoft,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Text(
                            text = body,
                            color = GrayText,
                            fontSize = 14.sp,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
fun LegalScreen(navController: NavController) {

    PolicyScreen(
        navController = navController,
        title = stringResource(R.string.legal_titulo),
        iconType = "legal",
        sections = listOf(
            "" to stringResource(R.string.legal_subtitulo),

            stringResource(R.string.legal_terminos) to
                    stringResource(R.string.legal_terminos_subtitulo),

            stringResource(R.string.legal_responsabilidad) to
                    stringResource(R.string.legal_responsabilidad_subtitulo),

            stringResource(R.string.legal_modificaciones) to
                    stringResource(R.string.legal_modificaciones_subtitulo)
        )
    )
}

@Composable
fun PrivacyScreen(navController: NavController) {

    PolicyScreen(
        navController = navController,
        title = stringResource(R.string.privacidad_titulo),
        iconType = "privacy",
        sections = listOf(
            "" to stringResource(R.string.privacidad_subtitulo),

            stringResource(R.string.privacidad_datos) to
                    stringResource(R.string.privacidad_datos_subtitulo),

            stringResource(R.string.privacidad_uso) to
                    stringResource(R.string.privacidad_uso_subtitulo),

            stringResource(R.string.privacidad_derechos) to
                    stringResource(R.string.privacidad_derechos_subtitulo)
        )
    )
}