package com.example.navhost1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.navhost1.navigation.NavGraph
import com.example.navhost1.screens.DrawerMenu
import com.example.navhost1.utils.LocaleHelper
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    // ✅ Se aplica el idioma guardado ANTES de que se infle cualquier vista
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyCurrentLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val drawerState   = rememberDrawerState(DrawerValue.Closed)
            val scope         = rememberCoroutineScope()

            val currentRoute = navController
                .currentBackStackEntryAsState().value?.destination?.route

            // Pantallas que NO muestran drawer ni topBar
            val hideDrawer = listOf("login", "splash", "onboarding")
                .any { currentRoute?.startsWith(it) == true }

            if (hideDrawer) {
                NavGraph(navController)
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            DrawerMenu(navController) {
                                scope.launch { drawerState.close() }
                            }
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("NeuraBloom") },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch { drawerState.open() }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menú"
                                        )
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            NavGraph(navController)
                        }
                    }
                }
            }
        }
    }
}