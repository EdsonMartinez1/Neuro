package com.example.navhost1.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String
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

    var currentPage by remember {
        mutableStateOf(0)
    }

    val page = pages[currentPage]

    val isLast = currentPage == pages.lastIndex

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

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
                    Primary.copy(alpha = alphaAnim)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 28.dp,
                    vertical = 42.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(400)
                    ) togetherWith fadeOut(
                        animationSpec = tween(300)
                    )
                },
                label = "page"
            ) { current ->

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(40.dp))
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

                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(
                                    Color.White.copy(alpha = 0.12f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = current.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    Text(
                        text = current.title,
                        color = WhiteSoft,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = current.subtitle,
                        color = GrayText,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                pages.forEachIndexed { index, _ ->

                    val selected = currentPage == index

                    val width by animateDpAsState(
                        targetValue =
                            if (selected) 34.dp else 12.dp,
                        label = "indicator"
                    )

                    val color by animateColorAsState(
                        targetValue =
                            if (selected)
                                Primary
                            else
                                Color.White.copy(alpha = 0.25f),
                        label = "dot"
                    )

                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {

                    if (isLast) {

                        navController.navigate(
                            "home/${username ?: "Usuario"}"
                        ) {

                            popUpTo("onboarding") {
                                inclusive = true
                            }
                        }

                    } else {
                        currentPage++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {

                Text(
                    text =
                        if (isLast)
                            "Comenzar"
                        else
                            "Siguiente",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isLast) {

                TextButton(
                    onClick = {

                        navController.navigate(
                            "home/${username ?: "Usuario"}"
                        ) {

                            popUpTo("onboarding") {
                                inclusive = true
                            }
                        }
                    }
                ) {

                    Text(
                        text = "Saltar",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}