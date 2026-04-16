package com.example.navhost1.screens



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DrawerMenu(navController: NavController, closeDrawer: () -> Unit) {

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Menú",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        DrawerItem("Login") {
            navController.navigate("login")
            closeDrawer()
        }

        DrawerItem("Home") {
            navController.navigate("home")
            closeDrawer()
        }

        DrawerItem("Profile") {
            navController.navigate("profile")
            closeDrawer()
        }

        DrawerItem("Settings") {
            navController.navigate("settings")
            closeDrawer()
        }

        DrawerItem("Language") {
            navController.navigate("language")
            closeDrawer()
        }

        DrawerItem("Logout") {
            navController.navigate("logout")
            closeDrawer()
        }

    }
}

@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    )
}