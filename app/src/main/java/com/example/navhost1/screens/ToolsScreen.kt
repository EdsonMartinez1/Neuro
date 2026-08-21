package com.example.navhost1.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import kotlin.random.Random

// Paleta NeuraBloom Dark Premium
private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF090D16)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFC084FC)
private val PurpleGlow = Color(0xFF6D28D9)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFF94A3B8)

private data class ToolParticle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speedY: Float
)

private data class ToolItemData(
    val titleRes: Int,
    val color1: Color,
    val color2: Color,
    val icon: ImageVector,
    val route: String
)

@Composable
fun ToolsScreen(navController: NavController) {

    val toolItems = remember {
        listOf(
            ToolItemData(
                titleRes = R.string.tools_respiracion_titulo,
                color1 = Color(0xFF2563EB),
                color2 = Color(0xFF60A5FA),
                icon = Icons.Default.Waves,
                route = "breathing"
            ),
            ToolItemData(
                titleRes = R.string.tools_meditacion_titulo,
                color1 = Color(0xFF7C3AED),
                color2 = Color(0xFFC084FC),
                icon = Icons.Default.SelfImprovement,
                route = "meditation"
            ),
            ToolItemData(
                titleRes = R.string.tools_ansiedad_titulo,
                color1 = Color(0xFF059669),
                color2 = Color(0xFF34D399),
                icon = Icons.Default.Psychology,
                route = "anxiety"
            ),
            ToolItemData(
                titleRes = R.string.tools_gratitud_titulo,
                color1 = Color(0xFFEA580C),
                color2 = Color(0xFFFDBA74),
                icon = Icons.Default.Favorite,
                route = "gratitude"
            ),
            ToolItemData(
                titleRes = R.string.tools_autoestima_titulo,
                color1 = Color(0xFFDB2777),
                color2 = Color(0xFFF472B6),
                icon = Icons.Default.Spa,
                route = "selfesteem"
            )
        )
    }

    // Partículas ambientales de fondo
    val particles = remember {
        List(20) {
            ToolParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 1.5f,
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
                .size(320.dp)
                .offset(x = (-90).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleGlow.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header Top
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Text(
                        text = "←",
                        color = WhiteSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.tools_titulo),
                        color = WhiteSoft,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Herramientas para tu bienestar emocional",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Grid Adaptativo para las Tarjetas
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(toolItems) { tool ->
                    ToolCard(
                        title = stringResource(tool.titleRes),
                        color1 = tool.color1,
                        color2 = tool.color2,
                        icon = tool.icon
                    ) {
                        navController.navigate(tool.route)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolCard(
    title: String,
    color1: Color,
    color2: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "cardScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .scale(animatedScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(color1, color2)
                    )
                )
        ) {
            // Círculo decorativo translúcido
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .offset(x = 65.dp, y = (-25).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Explorar →",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(120)
            isPressed = false
        }
    }
}