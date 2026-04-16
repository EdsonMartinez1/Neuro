package com.example.navhost1.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
private val Purple      = Color(0xFF9575CD)
private val PurpleLight = Color(0xFFEDE7F6)
private val PurpleDot   = Color(0xFF9575CD)
private val GrayDot     = Color(0xFFBDBDBD)
private val BgScreen    = Color(0xFFFFFFFF)
private val TextDark    = Color(0xFF1A1A1A)
private val TextSub     = Color(0xFF757575)

// ── Datos de cada slide ───────────────────────────────────────────────────────
data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String
)

// ── Pantalla ──────────────────────────────────────────────────────────────────
@Composable
fun OnboardingScreen(navController: NavController, username: String?) {

    val pages = listOf(
        OnboardingPage(
            emoji    = "🧠",
            title    = stringResource(R.string.login_subtitulo),
            subtitle = stringResource(R.string.onboarding_subtitulo)
        ),
        OnboardingPage(
            emoji    = "🧘",
            title    = stringResource(R.string.onboarding_herramienta_titulo),
            subtitle = stringResource(R.string.onboarding_herramientas)
        ),
        OnboardingPage(
            emoji    = "💬",
            title    = stringResource(R.string.onboarding_disponible),
            subtitle = stringResource(R.string.onboarding_chat)
        ),
    )

    var currentPage by remember { mutableStateOf(0) }
    val page = pages[currentPage]
    val isLast = currentPage == pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgScreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Ícono con fondo redondeado ────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(PurpleLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = page.emoji, fontSize = 80.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text       = page.title,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = TextDark,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Subtítulo ─────────────────────────────────────────────────────
            Text(
                text      = page.subtitle,
                fontSize  = 15.sp,
                color     = TextSub,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Indicadores de puntos ─────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { index, _ ->
                    val isSelected = index == currentPage
                    val color by animateColorAsState(
                        targetValue = if (isSelected) PurpleDot else GrayDot,
                        label = "dot_color"
                    )
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 28.dp else 12.dp, 12.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Botón siguiente / empezar ─────────────────────────────────────
            Button(
                onClick = {
                    if (isLast) {
                        navController.navigate("home/${username ?: "Usuario"}") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    } else {
                        currentPage++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    contentColor   = Color.White
                )
            ) {
                Text(
                    text       = if (isLast) "Comenzar" else "Siguiente",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Saltar (solo si no es el último) ─────────────────────────────
            if (!isLast) {
                TextButton(
                    onClick = {
                        navController.navigate("home/${username ?: "Usuario"}") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text     = "Saltar",
                        color    = TextSub,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}