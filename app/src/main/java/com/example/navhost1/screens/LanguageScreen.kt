package com.example.navhost1.screens

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import com.example.navhost1.utils.LocaleHelper

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val CardColor = Color(0xFF111827)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

data class Language(
    val name: String,
    val flag: String,
    val code: String
)

@Composable
fun LanguageScreen(navController: NavController) {

    val context = LocalContext.current

    val currentCode = LocaleHelper.getSavedLanguage(context)

    val languages = listOf(
        Language(
            stringResource(R.string.language_mx),
            "🇲🇽",
            "es"
        ),
        Language(
            stringResource(R.string.language_en),
            "🇺🇸",
            "en"
        ),
        Language(
            stringResource(R.string.language_fr),
            "🇫🇷",
            "fr"
        )
    )

    var selectedCode by rememberSaveable {
        mutableStateOf(currentCode)
    }

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
                .size(320.dp)
                .offset(x = (-90).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Primary.copy(alpha = 0.12f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
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

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
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
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.language_titulo),
                color = WhiteSoft,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Selecciona el idioma que deseas usar en la aplicación.",
                color = GrayText,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                languages.forEach { language ->

                    LanguageCard(
                        language = language,
                        isSelected = selectedCode == language.code,
                        onSelect = {

                            selectedCode = language.code

                            LocaleHelper.applyLanguage(
                                context,
                                language.code
                            )

                            (context as? Activity)?.recreate()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Los cambios se aplicarán automáticamente.",
                color = GrayText,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LanguageCard(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit
) {

    val animatedColor by animateColorAsState(
        targetValue =
            if (isSelected)
                Primary.copy(alpha = 0.18f)
            else
                CardColor.copy(alpha = 0.96f),
        label = "cardColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = animatedColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 22.dp,
                    vertical = 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = language.flag,
                fontSize = 34.sp
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = language.name,
                    color = WhiteSoft,
                    fontSize = 17.sp,
                    fontWeight =
                        if (isSelected)
                            FontWeight.Bold
                        else
                            FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text =
                        when (language.code) {
                            "es" -> "Español"
                            "en" -> "English"
                            "fr" -> "Français"
                            else -> ""
                        },
                    color = GrayText,
                    fontSize = 13.sp
                )
            }

            Icon(
                imageVector =
                    if (isSelected)
                        Icons.Default.RadioButtonChecked
                    else
                        Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint =
                    if (isSelected)
                        PrimaryLight
                    else
                        GrayText
            )
        }
    }
}