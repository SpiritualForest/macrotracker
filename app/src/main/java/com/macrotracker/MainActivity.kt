package com.macrotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.macrotracker.ui.destination.NavDestination
import com.macrotracker.features.home.HomeScreen
import com.macrotracker.features.tracking.FoodSelectionScreen
import com.macrotracker.features.tracking.MealScreen
import com.macrotracker.ui.components.theme.MacroTrackerTheme
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
                    composable(
                        route = NavDestination.Tracking.route,
                    ) {
                        FoodSelectionScreen()
                    }
                    composable(
                        route = "${NavDestination.Meal.route}/{date}",
                        arguments = listOf(navArgument("date") { type = NavType.IntType })
                    ) {
                        MealScreen(it.arguments?.getInt("date"))
                    }
                }
                navController.navigate(NavDestination.Home.route) {
                    launchSingleTop = true
                }
            }
        }
    }
}
