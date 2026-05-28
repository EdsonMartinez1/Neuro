package com.example.navhost1.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val PremiumGold = Color(0xFFF59E0B)
private val PremiumLight = Color(0xFFFCD34D)

private val FreePurple = Color(0xFF8B5CF6)
private val FreePurpleLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

@Composable
fun PlanesScreen(navController: NavController) {

    val premiumPerks = listOf(
        stringResource(R.string.planes_premium_funciones_1),
        stringResource(R.string.planes_premium_funciones_2),
        stringResource(R.string.planes_premium_funciones_3),
        stringResource(R.string.planes_premium_funciones_4),
    )

    val freePerks = listOf(
        stringResource(R.string.planes_freemium_funciones_1),
        stringResource(R.string.planes_freemium_funciones_2),
        stringResource(R.string.planes_freemium_funciones_3),
        stringResource(R.string.planes_freemium_funciones_4),
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
                    PremiumGold.copy(alpha = 0.10f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                PremiumGold,
                                PremiumLight
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(62.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.planes_titulo_premium),
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.planes_subtitulo_premium),
                color = GrayText,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(34.dp))

            PlanCard(
                title = "Premium",
                subtitle = "Experiencia completa",
                perks = premiumPerks,
                primaryColor = PremiumGold,
                secondaryColor = PremiumLight,
                buttonText = stringResource(R.string.planes_boton_suscribirse),
                buttonAction = {
                    navController.navigate("premium")
                },
                isCurrent = false
            )

            Spacer(modifier = Modifier.height(28.dp))

            PlanCard(
                title = stringResource(R.string.planes_titulo_free),
                subtitle = stringResource(R.string.planes_subtitulo_free),
                perks = freePerks,
                primaryColor = FreePurple,
                secondaryColor = FreePurpleLight,
                buttonText = "Plan actual",
                buttonAction = { },
                isCurrent = true
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    subtitle: String,
    perks: List<String>,
    primaryColor: Color,
    secondaryColor: Color,
    buttonText: String,
    buttonAction: () -> Unit,
    isCurrent: Boolean
) {

    val animatedColor by animateColorAsState(
        targetValue =
            if (isCurrent)
                primaryColor.copy(alpha = 0.10f)
            else
                CardColor.copy(alpha = 0.96f),
        label = "card"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = animatedColor
        )
    ) {

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                primaryColor,
                                secondaryColor
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                color = WhiteSoft,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitle,
                color = GrayText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            perks.forEach { perk ->

                FeatureRow(
                    text = perk,
                    tintColor = primaryColor
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (isCurrent) {

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = WhiteSoft
                    )
                ) {

                    Text(
                        text = buttonText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            } else {

                Button(
                    onClick = buttonAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    )
                ) {

                    Text(
                        text = buttonText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(
    text: String,
    tintColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            modifier = Modifier.size(34.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = tintColor.copy(alpha = 0.15f)
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✓",
                    color = tintColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = text,
            color = WhiteSoft,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}