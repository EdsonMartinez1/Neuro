package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

@Composable
fun AnxietyScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
            .padding(16.dp)
    ) {

        TextButton(onClick = { navController.popBackStack() }) {
            Text("←")
        }

        Text(stringResource(R.string.anxiety_control), fontSize = 22.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // BARRAS
        AnxietyBar(0.8f)
        AnxietyBar(0.5f)
        AnxietyBar(0.3f)

        Spacer(modifier = Modifier.height(30.dp))

        // CÍRCULO
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color.Gray.copy(0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("1,453", fontSize = 22.sp)
        }
    }
}

@Composable
fun AnxietyBar(progress: Float) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .background(Color.LightGray, RoundedCornerShape(10.dp))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(12.dp)
                .background(Color(0xFFFFB74D), RoundedCornerShape(10.dp))
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
}