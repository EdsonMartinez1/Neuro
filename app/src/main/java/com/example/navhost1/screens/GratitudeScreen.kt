package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navhost1.R

@Composable
fun GratitudeScreen(navController: NavController) {

    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var text3 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
            .padding(16.dp)
    ) {

        Text("←", fontSize = 22.sp,
            modifier = Modifier.clickable { navController.popBackStack() })

        Text(stringResource(R.string.tools_gratitud_titulo), fontSize = 22.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = text1,
            onValueChange = { text1 = it },
            label = { Text(stringResource(R.string.gratitude_persona)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = text2,
            onValueChange = { text2 = it },
            label = { Text(stringResource(R.string.gratitude_lugar)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = text3,
            onValueChange = { text3 = it },
            label = { Text(stringResource(R.string.gratitude_aprendizaje)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { }) {
            Text(stringResource(R.string.gratitude_guardar))
        }
    }
}