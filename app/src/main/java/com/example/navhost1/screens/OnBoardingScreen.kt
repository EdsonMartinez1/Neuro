package com.example.navhost1.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import kotlinx.coroutines.launch
import kotlin.random.Random

// Paleta NeuraBloom Dark Premium
private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF090D16)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFC084FC)
private val PurpleGlow = Color(0xFF6D28D9)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

private data class OnboardingParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

@Composable
fun OnboardingScreen(
    navController: NavController,
    username: String?
) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.AutoAwesome,
            title = stringResource(R.string.login_subtitulo),
            subtitle = stringResource(R.string.onboarding_subtitulo)
        ),
        OnboardingPage(
            icon = Icons.Default.SelfImprovement,
            title = stringResource(R.string.onboarding_herramienta_titulo),
            subtitle = stringResource(R.string.onboarding_herramientas)
        ),
        OnboardingPage(
            icon = Icons.Default.Chat,
            title = stringResource(R.string.onboarding_disponible),
            subtitle = stringResource(R.string.onboarding_chat)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == pages.lastIndex

    // Partículas ambientales de fondo
    val particles = remember {
        List(25) {
            OnboardingParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3.5f + 1.5f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                speedY = Random.nextFloat() * 0.0008f + 0.0003f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundTop, BackgroundBottom)
                )
            )
    ) {
        // Fondo de Partículas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                p.y -= p.speedY
                if (p.y < 0f) p.y = 1f

                drawCircle(
                    color = PrimaryLight.copy(alpha = p.alpha),
                    radius = p.radius.dp.toPx(),
                    center = Offset(p.x * width, p.y * height)
                )
            }
        }

        // Resplandores Ambientales
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-90).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleGlow.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Contenedor deslizable (Swipe)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                val currentPage = pages[pageIndex]

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Contenedor del Icono Principal con Elevación y Resplandor
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .shadow(28.dp, RoundedCornerShape(44.dp), spotColor = Primary)
                            .background(
                                Brush.linearGradient(listOf(Primary, PrimaryLight)),
                                RoundedCornerShape(44.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = currentPage.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(44.dp))

                    Text(
                        text = currentPage.title,
                        color = WhiteSoft,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentPage.subtitle,
                        color = GrayText,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // Indicadores de Página (Dots)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { index, _ ->
                    val selected = pagerState.currentPage == index

                    val width by animateDpAsState(
                        targetValue = if (selected) 32.dp else 10.dp,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "indicator"
                    )

                    val color by animateColorAsState(
                        targetValue = if (selected) PrimaryLight else Color.White.copy(alpha = 0.2f),
                        label = "dot"
                    )

                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Principal
            Button(
                onClick = {
                    if (isLast) {
                        navController.navigate("home/${username ?: "Usuario"}") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isLast) "Comenzar" else "Siguiente",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "→",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Opcional de Saltar
            if (!isLast) {
                TextButton(
                    onClick = {
                        navController.navigate("home/${username ?: "Usuario"}") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = "Saltar",
                        color = GrayText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}