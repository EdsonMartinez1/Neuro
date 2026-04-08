package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SelfEsteemScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8BBD0))
            .padding(20.dp)
    ) {

        Text("←", fontSize = 22.sp,
            modifier = Modifier.clickable { navController.popBackStack() })

        Spacer(modifier = Modifier.height(20.dp))

        Text("Autoestima 💜", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(20.dp))

        SelfItem("Soy suficiente")
        SelfItem("Estoy creciendo")
        SelfItem("Confío en mí")
        SelfItem("Merezco cosas buenas")
    }
}

@Composable
fun SelfItem(text: String) {

    var checked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(checked = checked, onCheckedChange = { checked = it })

            Spacer(modifier = Modifier.width(8.dp))

            Text(text)
        }
    }
}