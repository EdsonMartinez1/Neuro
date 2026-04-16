package com.example.navhost1.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

// ── Paleta ────────────────────────────────────────────────────────────────────
private val PurpleBar = Color(0xFF7B2FBE)
private val BgScreen  = Color(0xFFEDE7F6)
private val Magenta   = Color(0xFFCC00AA)
private val BgAnswer  = Color(0xFFF3E5F5)
private val TextDark  = Color(0xFF212121)
private val TextBody  = Color(0xFF424242)

data class FaqItem(val question: String, val answer: String)



// ── Pantalla ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavController) {

    val faqList = listOf(
        FaqItem(
            question = stringResource(R.string.faq_pregunta_1),
            answer   = stringResource(R.string.faq_respuesta_1)
        ),
        FaqItem(
            question = stringResource(R.string.faq_pregunta_2),
            answer   = stringResource(R.string.faq_respuesta_2)
        ),
        FaqItem(
            question = stringResource(R.string.faq_pregunta_3),
            answer   = stringResource(R.string.faq_respuesta_3)
        ),
        FaqItem(
            question = stringResource(R.string.faq_pregunta_4),
            answer   = stringResource(R.string.faq_respuesta_4)
        ),
        FaqItem(
            question = stringResource(R.string.faq_pregunta_5),
            answer   = stringResource(R.string.faq_respuesta_5)
        ),
        FaqItem(
            question = stringResource(R.string.faq_pregunta_6),
            answer   = stringResource(R.string.faq_respuesta_6)
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Preguntas frecuentes",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            faqList.forEach { item ->
                FaqCard(item = item)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ── Tarjeta expandible de pregunta ────────────────────────────────────────────
@Composable
private fun FaqCard(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // Botón de pregunta (magenta redondeado)
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Magenta,
                contentColor   = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Respuesta animada
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit  = shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = BgAnswer,
                        shape = RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd   = 16.dp
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = item.answer,
                    fontSize = 13.sp,
                    color = TextBody,
                    lineHeight = 20.sp
                )
            }
        }
    }
}