package com.example.navhost1.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedTree(
    nivel: Int
) {

    val infiniteTransition =
        rememberInfiniteTransition(label = "tree")

    val movimiento by infiniteTransition.animateFloat(

        initialValue = -4f,
        targetValue = 4f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                durationMillis = 2500,
                easing = EaseInOutSine
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "move"
    )

    Canvas(
        modifier = Modifier.size(290.dp)
    ) {

        // Tronco

        drawRect(

            color = Color(0xFF8D6E63),

            topLeft = Offset(
                size.width / 2 - 10f,
                size.height - 90f
            ),

            size = androidx.compose.ui.geometry.Size(
                20f,
                90f
            )
        )

        // Copa

        val radio = when(nivel){

            1 -> 28f
            2 -> 40f
            3 -> 55f
            4 -> 68f
            else -> 82f
        }

        drawCircle(

            color = Color(0xFF43A047),

            radius = radio,

            center = Offset(
                size.width/2 + movimiento,
                size.height-110f
            ),

            style = Fill
        )

    }
}