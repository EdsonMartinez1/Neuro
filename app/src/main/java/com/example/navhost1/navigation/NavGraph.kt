package com.example.navhost1.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.navhost1.screens.*

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "splash",
        // 🌸 Transición de entrada suave para cualquier pantalla (Fade In)
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        // 🌸 Transición de salida suave (Fade Out)
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        popEnterTransition = { fadeIn(animationSpec = tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {

        // ✨ SPLASH SCREEN
        composable("splash") {
            SplashScreen(navController)
        }

        // 🔐 LOGIN
        composable("login") {
            LoginScreen(navController)
        }

        // 🎬 ONBOARDING
        composable("onboarding/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            OnboardingScreen(navController, username)
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
        composable("settings") { SettingsScreen(navController) }
        composable("chat") { ChatScreen(navController) }
        composable("tools") { ToolsScreen(navController) }
        composable("diary") { DiaryScreen(navController) }
        composable("estadisticas") { EstadisticasScreen() }
        composable("habitos") { HabitosScreen() }
        composable("content") { ContentScreen(navController) }
        composable("register") { Text("Pantalla Registro") }
        composable("breathing")  { BreathingScreen(navController) }
        composable("meditation") { MeditationScreen(navController) }
        composable("anxiety")    { AnxietyScreen(navController) }
        composable("gratitude")  { GratitudeScreen(navController) }
        composable("selfesteem") { SelfEsteemScreen(navController) }
        composable("premium") { PremiumScreen(navController) }
        composable("planes") { PlanesScreen(navController) }
        composable("emergency") { EmergencyScreen(navController) }
        composable("support") { SupportScreen(navController) }
        composable("contact") { ContactScreen(navController) }
        composable("language") { LanguageScreen(navController) }
        composable("legal") { LegalScreen(navController) }
        composable("privacy") { PrivacyScreen(navController) }
        composable("faq") { FaqScreen(navController) }
        composable("logout") { LogoutScreen(navController) }
    }
}