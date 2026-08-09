package com.example.navhost1.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)
private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)
private val CardColor = Color(0xFF111827)
private val FieldColor = Color(0xFF1F2937)
private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)
private val BorderColor = Color(0xFF334155)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(navController: NavController) {

    var isLogin by remember { mutableStateOf(true) }

    val auth = FirebaseAuth.getInstance()

    val db = FirebaseFirestore.getInstance()

    var errorMessage by remember {
        mutableStateOf("")
    }

    var successMessage by remember {
        mutableStateOf("")
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirm by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundTop,
                        BackgroundBottom
                    )
                )
            )
    ) {

        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-80).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(
                    Primary.copy(alpha = 0.18f)
                )
        )

        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .clip(CircleShape)
                .background(
                    PrimaryLight.copy(alpha = 0.12f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 42.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(20.dp, CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Primary,
                                PrimaryLight
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "N",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteSoft
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu espacio inteligente para bienestar emocional",
                color = GrayText,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(38.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(30.dp)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                FieldColor,
                                RoundedCornerShape(18.dp)
                            )
                            .padding(6.dp)
                    ) {

                        LoginToggle(
                            text = stringResource(R.string.login_boton_tab),
                            selected = isLogin,
                            modifier = Modifier.weight(1f)
                        ) {
                            isLogin = true
                        }

                        LoginToggle(
                            text = stringResource(R.string.registro_tab),
                            selected = !isLogin,
                            modifier = Modifier.weight(1f)
                        ) {
                            isLogin = false
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    if (successMessage.isNotEmpty()) {
                        Text(
                            text = successMessage,
                            color = Color.Green,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    AnimatedContent(
                        targetState = isLogin,
                        transitionSpec = {
                            (fadeIn() + scaleIn()) togetherWith
                                    (fadeOut() + scaleOut())
                        },
                        label = "auth"
                    ) { login ->

                        if (login) {

                            Column {

                                ModernField(
                                    value = email,
                                    onValueChange = { email = it },
                                    placeholder = stringResource(R.string.login_email),
                                    icon = Icons.Default.Email
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                ModernField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = stringResource(R.string.login_password),
                                    icon = Icons.Default.Lock,
                                    isPassword = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = stringResource(R.string.login_olvide),
                                    color = PrimaryLight,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember {
                                                MutableInteractionSource()
                                            }
                                        ) {}
                                )

                                Spacer(modifier = Modifier.height(28.dp))

                                Button(
                                    onClick = {

                                        errorMessage = ""
                                        successMessage = ""

                                        if (email.isBlank() || password.isBlank()) {

                                            errorMessage = "Completa todos los campos"
                                            return@Button
                                        }

                                        auth.signInWithEmailAndPassword(
                                            email.trim(),
                                            password
                                        )
                                            .addOnSuccessListener {

                                                successMessage =
                                                    "Inicio de sesión exitoso"

                                                val username =
                                                    email.substringBefore("@")

                                                navController.navigate(
                                                    "onboarding/${username.replaceFirstChar { it.uppercase() }}"
                                                ) {
                                                    popUpTo("login") {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                            .addOnFailureListener {

                                                errorMessage =
                                                    it.localizedMessage
                                                        ?: "Error al iniciar sesión"
                                            }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(58.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Primary
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.login_boton),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                        } else {

                            Column {

                                ModernField(
                                    value = regName,
                                    onValueChange = { regName = it },
                                    placeholder = stringResource(R.string.registro_nombre),
                                    icon = Icons.Default.Person
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                ModernField(
                                    value = regEmail,
                                    onValueChange = { regEmail = it },
                                    placeholder = stringResource(R.string.login_email),
                                    icon = Icons.Default.Email
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                ModernField(
                                    value = regPassword,
                                    onValueChange = { regPassword = it },
                                    placeholder = stringResource(R.string.login_password),
                                    icon = Icons.Default.Lock,
                                    isPassword = true
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                ModernField(
                                    value = regConfirm,
                                    onValueChange = { regConfirm = it },
                                    placeholder = stringResource(R.string.registro_confirmar),
                                    icon = Icons.Default.Lock,
                                    isPassword = true
                                )

                                Spacer(modifier = Modifier.height(28.dp))

                                Button(
                                    onClick = {

                                        errorMessage = ""
                                        successMessage = ""

                                        if (
                                            regName.isBlank() ||
                                            regEmail.isBlank() ||
                                            regPassword.isBlank() ||
                                            regConfirm.isBlank()
                                        ) {

                                            errorMessage =
                                                "Completa todos los campos"

                                            return@Button
                                        }

                                        if (regPassword != regConfirm) {

                                            errorMessage =
                                                "Las contraseñas no coinciden"

                                            return@Button
                                        }

                                        auth.createUserWithEmailAndPassword(
                                            regEmail.trim(),
                                            regPassword
                                        )
                                            .addOnSuccessListener {

                                                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                                                val usuario = hashMapOf(
                                                    "nombre" to regName,
                                                    "email" to regEmail,
                                                    "nivelEmocional" to 1,
                                                    "racha" to 0,
                                                    "arbolNivel" to 1,
                                                    "fechaRegistro" to System.currentTimeMillis()
                                                )

                                                db.collection("usuarios")
                                                    .document(uid)
                                                    .set(usuario)
                                                    .addOnSuccessListener {

                                                        successMessage =
                                                            "Cuenta creada correctamente"

                                                        navController.navigate(
                                                            "onboarding/${regName.replaceFirstChar { it.uppercase() }}"
                                                        ) {
                                                            popUpTo("login") {
                                                                inclusive = true
                                                            }
                                                        }
                                                    }
                                                    .addOnFailureListener {

                                                        errorMessage =
                                                            "Usuario creado pero no se pudo guardar el perfil"
                                                    }
                                            }
                                            .addOnFailureListener {

                                                errorMessage =
                                                    it.localizedMessage
                                                        ?: "Error al crear cuenta"
                                            }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(58.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Primary
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.registro_boton),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = BorderColor
                )

                Text(
                    text = stringResource(R.string.login_o),
                    color = GrayText,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = BorderColor
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            SocialLoginButton(
                text = stringResource(R.string.login_google),
                icon = "G"
            ) {}

            Spacer(modifier = Modifier.height(12.dp))

            SocialLoginButton(
                text = stringResource(R.string.login_apple),
                icon = ""
            ) {}

            Spacer(modifier = Modifier.height(12.dp))

            SocialLoginButton(
                text = stringResource(R.string.login_facebook),
                icon = "f"
            ) {}
        }
    }
}

@Composable
fun ModernField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = GrayText.copy(alpha = 0.7f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryLight
            )
        },
        visualTransformation = if (isPassword)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldColor,
            unfocusedContainerColor = FieldColor,
            focusedBorderColor = Primary,
            unfocusedBorderColor = BorderColor,
            focusedTextColor = WhiteSoft,
            unfocusedTextColor = WhiteSoft,
            cursorColor = Primary,
            focusedLeadingIconColor = Primary,
            unfocusedLeadingIconColor = PrimaryLight
        )
    )
}

@Composable
fun LoginToggle(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) Primary else Color.Transparent
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else GrayText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    icon: String,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(
                listOf(
                    BorderColor,
                    Primary.copy(alpha = 0.5f)
                )
            )
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardColor.copy(alpha = 0.9f),
            contentColor = WhiteSoft
        )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = icon,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(0.9f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}
