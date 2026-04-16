package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.navhost1.R
data class Message(val text: String, val isUser: Boolean)

@Composable
fun ChatScreen(navController: NavController) {

    var message by remember { mutableStateOf(TextFieldValue("")) }
    val initialGreeting = stringResource(R.string.chat_ia_saludo)
    var messages by remember {
        mutableStateOf(
            listOf(
                Message(initialGreeting, false)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4A2FF))
    ) {

        // 🔝 HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(onClick = {
                navController.popBackStack()
            }) {
                Text("←", color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.chat_ia_titulo),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // 💬 MENSAJES
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                MessageBubble(msg)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ✏️ INPUT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { stringResource(R.string.chat_ia_msg) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (message.text.isNotEmpty()) {
                        messages = messages + Message(message.text, true)

                        // Respuesta automática fake 🤖
                        messages = messages + Message("Estoy aquí para ayudarte 💜", false)

                        message = TextFieldValue("")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C4DFF)
                )
            ) {
                Text(stringResource(R.string.chat_ia_enviar))
            }
        }
    }
}
@Composable
fun MessageBubble(message: Message) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser)
            Arrangement.End else Arrangement.Start
    ) {

        Box(
            modifier = Modifier
                .background(
                    color = if (message.isUser)
                        Color(0xFF6C4DFF) else Color(0xFF1E1E1E),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White
            )
        }
    }
}