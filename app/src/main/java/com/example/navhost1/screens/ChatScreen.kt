package com.example.navhost1.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R
import androidx.compose.ui.res.stringResource

private val BackgroundTop = Color(0xFF0F172A)
private val BackgroundBottom = Color(0xFF1E293B)

private val Primary = Color(0xFF8B5CF6)
private val PrimaryLight = Color(0xFFA78BFA)

private val UserBubble = Color(0xFF8B5CF6)
private val BotBubble = Color(0xFF1F2937)

private val WhiteSoft = Color(0xFFF8FAFC)
private val GrayText = Color(0xFFCBD5E1)

data class Message(
    val text: String,
    val isUser: Boolean
)

@Composable
fun ChatScreen(navController: NavController) {

    var message by remember {
        mutableStateOf(TextFieldValue(""))
    }

    val initialGreeting = stringResource(R.string.chat_ia_saludo)

    var messages by remember {
        mutableStateOf(
            listOf(
                Message(initialGreeting, false)
            )
        )
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

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
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

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
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
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = stringResource(R.string.chat_ia_titulo),
                        color = WhiteSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Asistente emocional activo",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                reverseLayout = true
            ) {

                items(messages.reversed()) { msg ->

                    MessageBubble(msg)

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            Surface(
                color = Color(0xFF111827),
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(
                    topStart = 28.dp,
                    topEnd = 28.dp
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 18.dp,
                            vertical = 18.dp
                        ),
                    verticalAlignment = Alignment.Bottom
                ) {

                    OutlinedTextField(
                        value = message,
                        onValueChange = {
                            message = it
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.chat_ia_msg),
                                color = GrayText
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(),
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1F2937),
                            unfocusedContainerColor = Color(0xFF1F2937),

                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.Transparent,

                            focusedTextColor = WhiteSoft,
                            unfocusedTextColor = WhiteSoft,

                            cursorColor = Primary
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    FloatingActionButton(
                        onClick = {

                            if (message.text.isNotBlank()) {

                                messages = messages + Message(
                                    message.text,
                                    true
                                )

                                messages = messages + Message(
                                    "Estoy aquí para ayudarte 💜",
                                    false
                                )

                                message = TextFieldValue("")
                            }
                        },
                        containerColor = Primary,
                        contentColor = Color.White
                    ) {

                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (message.isUser)
                Arrangement.End
            else
                Arrangement.Start
    ) {

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomStart =
                            if (message.isUser) 22.dp else 4.dp,
                        bottomEnd =
                            if (message.isUser) 4.dp else 22.dp
                    )
                )
                .background(
                    if (message.isUser)
                        UserBubble
                    else
                        BotBubble
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                )
                .widthIn(max = 290.dp)
        ) {

            Text(
                text = message.text,
                color = WhiteSoft,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}