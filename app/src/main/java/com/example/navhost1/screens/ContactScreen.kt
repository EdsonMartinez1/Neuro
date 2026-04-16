package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

// ── Paleta ────────────────────────────────────────────────────────────────────
private val BgScreen   = Color(0xFFF5F5F5)
private val BgIconBox  = Color(0xFFEDE7F6)
private val BlueIcon   = Color(0xFF42A5F5)
private val Magenta    = Color(0xFFCC00AA)
private val BgField    = Color(0xFFE0E0E0)
private val TextHint   = Color(0xFF9E9E9E)
private val TextDark   = Color(0xFF212121)
private val BgResponse = Color(0xFFFFFFFF)
private val BorderGray = Color(0xFFBDBDBD)



// ── Pantalla ──────────────────────────────────────────────────────────────────
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

    var selectedProblem by remember { mutableStateOf(problemOptions[0]) }
    var expanded        by remember { mutableStateOf(false) }
    var messageText     by remember { mutableStateOf("") }
    var submitted       by remember { mutableStateOf(false) }

    // Respuestas de ejemplo (se reemplazarán con BD real)
    val responses = listOf<Pair<String, String>>() // vacío hasta que haya BD

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Magenta
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgScreen
                )
            )
        },
        containerColor = BgScreen
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Ícono de sobre ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BgIconBox),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = BlueIcon,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.contact_titulo),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Dropdown Problema ─────────────────────────────────────────────
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedProblem,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = TextDark
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = BgField,
                        focusedContainerColor   = BgField,
                        unfocusedBorderColor    = Color.Transparent,
                        focusedBorderColor      = Magenta
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    problemOptions.drop(1).forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedProblem = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Área de texto ─────────────────────────────────────────────────
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.contact_area_texto),
                        color = TextHint,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = BgField,
                    focusedContainerColor   = BgField,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = Magenta
                ),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botón Enviar ──────────────────────────────────────────────────
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
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Magenta,
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.contact_boton_enviar),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ── Confirmación de envío ─────────────────────────────────────────
            if (submitted) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.contact_confirmacion_envio),
                    color = Color(0xFF388E3C),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Sección de respuestas ─────────────────────────────────────────
            HorizontalDivider(color = BorderGray)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.contact_respuestas_soporte_titulo),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (responses.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgResponse)
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.contact_respuestas_soporte),
                        color = TextHint,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            } else {
                // Lista de respuestas (se llenará con BD real)
                responses.forEach { (fecha, mensaje) ->
                    ResponseCard(fecha = fecha, mensaje = mensaje)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Tarjeta de respuesta ──────────────────────────────────────────────────────
@Composable
private fun ResponseCard(fecha: String, mensaje: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgResponse)
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(text = fecha, fontSize = 11.sp, color = TextHint)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = mensaje, fontSize = 14.sp, color = TextDark, lineHeight = 20.sp)
    }
}