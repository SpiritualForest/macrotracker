package com.macrotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.macrotracker.ui.destination.NavDestination
import com.macrotracker.features.home.HomeScreen
import com.macrotracker.features.tracking.TrackingScreen
import com.macrotracker.ui.theme.MacroTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MacroTrackerTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavDestination.Home.route
                ) {
                    composable(NavDestination.Home.route) {
                        HomeScreen(navController = navController)
                    }
                    composable(NavDestination.Tracking.route) {
                        com.macrotracker.features.tracking.TrackingScreen()
                    }
                }
                navController.navigate(NavDestination.Home.route) {
                    launchSingleTop = true
                }
            }
        }
    }
}
