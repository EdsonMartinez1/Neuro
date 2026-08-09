package com.example.navhost1.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.navhost1.R

import androidx.compose.foundation.layout.Box


import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill




@Composable
fun AnimatedTree(
    nivel: Int,
    emocion: String
) {

    val image = when (nivel) {
        1 -> R.drawable.tree1
        2 -> R.drawable.tree2
        3 -> R.drawable.tree3
        4 -> R.drawable.tree4
        else -> R.drawable.tree5
    }
    val glowColor = when (emocion) {

        "😊 Feliz" -> Color(0xFFFFD54F)

        "😔 Triste" -> Color(0xFF64B5F6)

        "😟 Ansioso" -> Color(0xFFBA68C8)

        "😡 Enojado" -> Color(0xFFFF7043)

        "😴 Cansado" -> Color(0xFF90CAF9)

        else -> Color(0x668B5CF6)
    }



    val infiniteTransition = rememberInfiniteTransition(label = "tree")




    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )


    val maxScale = when (emocion) {

        "😊 Feliz" -> 1.08f

        "😔 Triste" -> 1.01f

        "😟 Ansioso" -> 1.06f

        "😡 Enojado" -> 1.04f

        "😴 Cansado" -> 1.02f

        else -> 1.05f
    }

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center
    ) {


        Canvas(
            modifier = Modifier.size(240.dp)
        ) {

            drawCircle(
                color = glowColor.copy(alpha = glowAlpha),
                radius = size.minDimension / 2.8f,
                center = center,
                style = Fill
            )
        }

        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .scale(scale)
        )
    }
}