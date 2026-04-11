package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ── Paleta de colores del diseño ──────────────────────────────────────────────
private val BgDeep    = Color(0xFF0D1B2A)   // fondo principal azul muy oscuro
private val BgCard    = Color(0xFF1C2D3F)   // tarjetas / paneles
private val BgPlayer  = Color(0xFF162436)   // fondo del reproductor
private val AccentCir = Color(0xFFFFFFFF)   // círculo play
private val TextPrim  = Color(0xFFFFFFFF)
private val TextSec   = Color(0xFF8FA8C0)
private val BarBg     = Color(0xFF243548)
private val BarFill   = Color(0xFF4A90D9)

// ── Datos de ejemplo (se reemplazarán con BD real) ───────────────────────────
data class ContentItem(
    val id: Int,
    val title: String,
    val duration: String
)

private val sampleItems = listOf(
    ContentItem(1, "Introducción a la ansiedad", "5:30"),
    ContentItem(2, "Técnicas de respiración", "8:15"),
    ContentItem(3, "Mindfulness básico", "6:00"),
    ContentItem(4, "Gestión del estrés", "10:20"),
    ContentItem(5, "Autoestima positiva", "7:45"),
    ContentItem(6, "Meditación guiada", "12:00"),
)

// ── Pantalla principal ────────────────────────────────────────────────────────
@Composable
fun ContentScreen(navController: NavController) {

    var progress by remember { mutableStateOf(0.35f) }
    var selectedItem by remember { mutableStateOf(sampleItems.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {

        // ── Reproductor de video ──────────────────────────────────────────────
        VideoPlayer(
            item = selectedItem,
            progress = progress,
            onProgressChange = { progress = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Título del video actual ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgCard)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = selectedItem.title,
                color = TextPrim,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Barra de progreso
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = BarFill,
                trackColor = BarBg,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = selectedItem.duration,
                color = TextSec,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Cuadrícula de contenidos ──────────────────────────────────────────
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(sampleItems) { item ->
                ContentCard(
                    item = item,
                    isSelected = item.id == selectedItem.id,
                    onClick = {
                        selectedItem = item
                        progress = 0f
                    }
                )
            }
        }
    }
}

// ── Reproductor ───────────────────────────────────────────────────────────────
@Composable
private fun VideoPlayer(
    item: ContentItem,
    progress: Float,
    onProgressChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(BgPlayer),
        contentAlignment = Alignment.Center
    ) {
        // Ícono de play con círculo
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Borde del círculo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            )
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Reproducir",
                tint = AccentCir,
                modifier = Modifier.size(44.dp)
            )
        }

        // Texto del título en la parte inferior del player
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = item.title,
                color = TextPrim,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Tarjeta de contenido ──────────────────────────────────────────────────────
@Composable
private fun ContentCard(
    item: ContentItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) BarFill else Color.Transparent

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(BgCard)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Indicador de selección
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BarFill.copy(alpha = 0.15f))
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (isSelected) BarFill else TextSec,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.title,
                color = if (isSelected) TextPrim else TextSec,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 12.sp
            )
        }
    }
}
