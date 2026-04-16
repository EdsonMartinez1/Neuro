package com.example.navhost1.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

// ── Paleta ────────────────────────────────────────────────────────────────────
private val Purple      = Color(0xFF9575CD)
private val PurpleDark  = Color(0xFF6C4DFF)
private val BgStart     = Color(0xFFEDE7FF)
private val BgEnd       = Color(0xFFD1C4E9)
private val TextGray    = Color(0xFF757575)
private val BorderGray  = Color(0xFFE0E0E0)
private val White       = Color.White

// ── Pantalla ──────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(navController: NavController) {

    var isLogin by remember { mutableStateOf(true) }

    // Campos login
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Campos registro
    var regName     by remember { mutableStateOf("") }
    var regEmail    by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirm  by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(BgStart, BgEnd))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo / Título ─────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PurpleDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.login_subtitulo),
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Toggle Login / Registro ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEDE7F6), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                ToggleTab(
                    text = stringResource(R.string.login_boton_tab),
                    isSelected = isLogin,
                    modifier = Modifier.weight(1f)
                ) { isLogin = true }

                ToggleTab(
                    text = stringResource(R.string.registro_tab),
                    isSelected = !isLogin,
                    modifier = Modifier.weight(1f)
                ) { isLogin = false }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Formulario animado ────────────────────────────────────────────
            AnimatedContent(
                targetState = isLogin,
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "form_transition"
            ) { showLogin ->

                if (showLogin) {
                    // ════════════════════════════════════════════════════════
                    //  FORMULARIO LOGIN
                    // ════════════════════════════════════════════════════════
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        AuthField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = stringResource(R.string.login_email),
                            icon = Icons.Default.Email
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        AuthField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = stringResource(R.string.login_password),
                            icon = Icons.Default.Lock,
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Olvidé contraseña
                        Text(
                            text = stringResource(R.string.login_olvide),
                            color = PurpleDark,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable { }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón iniciar sesión
                        Button(
                            onClick = {
                                val username = if (email.contains("@"))
                                    email.substringBefore("@")
                                else email
                                navController.navigate("onboarding/${username.replaceFirstChar { it.uppercase() }}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) {
                            Text(
                                text = stringResource(R.string.login_boton),
                                color = White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                } else {
                    // ════════════════════════════════════════════════════════
                    //  FORMULARIO REGISTRO
                    // ════════════════════════════════════════════════════════
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        AuthField(
                            value = regName,
                            onValueChange = { regName = it },
                            placeholder = stringResource(R.string.registro_nombre),
                            icon = Icons.Default.Person
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        AuthField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            placeholder = stringResource(R.string.login_email),
                            icon = Icons.Default.Email
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        AuthField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            placeholder = stringResource(R.string.login_password),
                            icon = Icons.Default.Lock,
                            isPassword = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        AuthField(
                            value = regConfirm,
                            onValueChange = { regConfirm = it },
                            placeholder = stringResource(R.string.registro_confirmar),
                            icon = Icons.Default.Lock,
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón crear cuenta
                        Button(
                            onClick = {
                                val username = if (regName.isNotBlank()) regName
                                else if (regEmail.contains("@")) regEmail.substringBefore("@")
                                else regEmail
                                navController.navigate("onboarding/${username.replaceFirstChar { it.uppercase() }}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) {
                            Text(
                                text = stringResource(R.string.registro_boton),
                                color = White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Divisor ───────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGray)
                Text(
                    text = stringResource(R.string.login_o),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = TextGray,
                    fontSize = 13.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Botones sociales ──────────────────────────────────────────────
            SocialButton(
                text = stringResource(R.string.login_google),
                emoji = "🇬",
                onClick = { /* TODO: Google Auth */ }
            )
            Spacer(modifier = Modifier.height(10.dp))

            SocialButton(
                text = stringResource(R.string.login_apple),
                emoji = "",
                onClick = { /* TODO: Apple Auth */ }
            )
            Spacer(modifier = Modifier.height(10.dp))

            SocialButton(
                text = stringResource(R.string.login_facebook),
                emoji = "f",
                onClick = { /* TODO: Facebook Auth */ }
            )
        }
    }
}

// ── Campo de texto reutilizable ───────────────────────────────────────────────
@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextGray) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null, tint = Purple)
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Purple,
            unfocusedBorderColor = BorderGray
        )
    )
}

// ── Tab del toggle ────────────────────────────────────────────────────────────
@Composable
private fun ToggleTab(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Purple else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) White else TextGray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ── Botón social ──────────────────────────────────────────────────────────────
@Composable
private fun SocialButton(
    text: String,
    emoji: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor   = Color(0xFF212121)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}