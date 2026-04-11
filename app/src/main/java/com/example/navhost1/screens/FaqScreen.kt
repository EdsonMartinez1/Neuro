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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ── Paleta ────────────────────────────────────────────────────────────────────
private val PurpleBar = Color(0xFF7B2FBE)
private val BgScreen  = Color(0xFFEDE7F6)
private val Magenta   = Color(0xFFCC00AA)
private val BgAnswer  = Color(0xFFF3E5F5)
private val TextDark  = Color(0xFF212121)
private val TextBody  = Color(0xFF424242)

data class FaqItem(val question: String, val answer: String)

private val faqList = listOf(
    FaqItem(
        question = "¿Cómo funciona la app?",
        answer   = "Neuro es una app de bienestar mental que te ofrece herramientas " +
                "como meditación, respiración, diario emocional y chat de apoyo."
    ),
    FaqItem(
        question = "¿Mis datos están seguros?",
        answer   = "Sí. Toda tu información es confidencial y está protegida. " +
                "Consulta nuestra política de privacidad para más detalles."
    ),
    FaqItem(
        question = "¿Qué incluye el plan Premium?",
        answer   = "El plan Premium desbloquea todas las herramientas, sesiones " +
                "guiadas ilimitadas, contenido exclusivo y seguimiento avanzado."
    ),
    FaqItem(
        question = "¿Puedo cancelar mi suscripción?",
        answer   = "Sí, puedes cancelar en cualquier momento desde tu perfil " +
                "en la sección de Plan actual."
    ),
    FaqItem(
        question = "¿La app reemplaza a un profesional?",
        answer   = "No. Neuro es un apoyo complementario. Si necesitas ayuda " +
                "urgente, usa el botón de Emergencias en tu perfil."
    ),
    FaqItem(
        question = "¿Cómo contacto a soporte?",
        answer   = "Ve a Perfil → Soporte → Contacto a soporte y envíanos " +
                "un mensaje. Te responderemos lo antes posible."
    ),
)

// ── Pantalla ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavController) {
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