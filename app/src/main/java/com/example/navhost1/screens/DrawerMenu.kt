package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

@Composable
fun DrawerMenu(
    navController: NavController,
    closeDrawer: () -> Unit
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
                .size(260.dp)
                .offset(x = (-80).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(
                    Primary.copy(alpha = 0.10f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 22.dp,
                    vertical = 34.dp
                )
        ) {

            Box(
                modifier = Modifier
                    .size(86.dp)
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
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Neuro",
                color = WhiteSoft,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Bienestar emocional inteligente",
                color = GrayText,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(34.dp))

            DrawerItem(
                icon = Icons.Default.Login,
                text = "Login"
            ) {

                navController.navigate("login")
                closeDrawer()
            }

            DrawerItem(
                icon = Icons.Default.Home,
                text = "Home"
            ) {

                navController.navigate("home/test")
                closeDrawer()
            }

            DrawerItem(
                icon = Icons.Default.Person,
                text = "Perfil"
            ) {

                navController.navigate("profile/test")
                closeDrawer()
            }

            DrawerItem(
                icon = Icons.Default.Settings,
                text = "Configuración"
            ) {

                navController.navigate("settings")
                closeDrawer()
            }

            DrawerItem(
                icon = Icons.Default.Language,
                text = "Idioma"
            ) {

                navController.navigate("language")
                closeDrawer()
            }

            Spacer(modifier = Modifier.weight(1f))

            DrawerItem(
                icon = Icons.Default.Logout,
                text = "Cerrar sesión",
                isDanger = true
            ) {

                navController.navigate("logout")
                closeDrawer()
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    text: String,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (isDanger)
                        Color(0xFFEF4444).copy(alpha = 0.16f)
                    else
                        Primary.copy(alpha = 0.14f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint =
                    if (isDanger)
                        Color(0xFFEF4444)
                    else
                        PrimaryLight,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            color =
                if (isDanger)
                    Color(0xFFEF4444)
                else
                    WhiteSoft,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}