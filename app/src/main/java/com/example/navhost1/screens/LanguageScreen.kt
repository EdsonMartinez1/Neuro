package com.example.navhost1.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ── Paleta ────────────────────────────────────────────────────────────────────
private val PurpleBar  = Color(0xFF7B2FBE)
private val BgScreen   = Color(0xFFF3F0FF)
private val PurpleCheck = Color(0xFF7B2FBE)
private val BgCard     = Color(0xFFEDE7F6)

data class Language(val name: String, val flag: String)

private val languages = listOf(
    Language("Español", "🇲🇽"),
    Language("Inglés",  "🇺🇸"),
    Language("Francés", "🇫🇷"),
)

// ── Pantalla ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(navController: NavController) {

    var selectedLanguage by remember { mutableStateOf("Español") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleBar
                )
            )
        },
        containerColor = BgScreen
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            // ── Ícono de traducción ───────────────────────────────────────────
            Text(text = "🗨️", fontSize = 72.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text = "Idioma",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Lista de idiomas ──────────────────────────────────────────────
            languages.forEach { language ->
                LanguageItem(
                    language = language,
                    isSelected = selectedLanguage == language.name,
                    onSelect = { selectedLanguage = language.name }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ── Item de idioma ────────────────────────────────────────────────────────────
@Composable
private fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() },
            colors = CheckboxDefaults.colors(
                checkedColor   = PurpleCheck,
                uncheckedColor = Color(0xFFBDBDBD)
            ),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Tarjeta del idioma
        Row(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) PurpleCheck else Color.Transparent,
                    shape = RoundedCornerShape(28.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = BgCard,
                tonalElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = language.name,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = Color(0xFF212121)
                    )
                    Text(text = language.flag, fontSize = 26.sp)
                }
            }
        }
    }
}