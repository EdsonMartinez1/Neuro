package com.example.navhost1.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.navhost1.screens.*

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // 🔐 LOGIN
        composable("login") {
            LoginScreen(navController)
        }

        // 🏠 HOME
        composable("home/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            HomeScreen(navController, username)
        }

        // 👤 PERFIL
        composable("profile/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            ProfileScreen(navController, username)
        }

        // ⚙️ SETTINGS
        composable("settings") {
            SettingsScreen(navController)
        }

        // 🤖 CHAT
        composable("chat") {
            ChatScreen(navController)
        }

        // 🧘 HERRAMIENTAS
        composable("tools") {
            ToolsScreen(navController)
        }

        // 📓 DIARIO
        composable("diary") {
            DiaryScreen(navController)
        }

        // 🎓 CONTENIDO
        composable("content") {
            ContentScreen(navController)
        }

        // 🧪 OPCIONAL
        composable("register") {
            Text("Pantalla Registro")
        }

        // 🧘 SUB-HERRAMIENTAS
        composable("breathing")  { BreathingScreen(navController) }
        composable("meditation") { MeditationScreen(navController) }
        composable("anxiety")    { AnxietyScreen(navController) }
        composable("gratitude")  { GratitudeScreen(navController) }
        composable("selfesteem") { SelfEsteemScreen(navController) }

        // 💎 PREMIUM
        composable("premium") {
            PremiumScreen(navController)
        }

        // 📋 COMPARACIÓN DE PLANES
        composable("planes") {
            PlanesScreen(navController)
        }

        // 🚨 EMERGENCIAS
        composable("emergency") {
            EmergencyScreen(navController)
        }

        // 🛟 SOPORTE
        composable("support") {
            SupportScreen(navController)
        }

        // 📧 CONTACTO A SOPORTE
        composable("contact") {
            ContactScreen(navController)
        }

        // 🌐 IDIOMA
        composable("language") {
            LanguageScreen(navController)
        }

        // ⚖️ LEGAL
        composable("legal") {
            LegalScreen(navController)
        }

        // 🔒 PRIVACIDAD
        composable("privacy") {
            PrivacyScreen(navController)
        }

        // ❓ PREGUNTAS FRECUENTES
        composable("faq") {
            FaqScreen(navController)
        }
    }
}