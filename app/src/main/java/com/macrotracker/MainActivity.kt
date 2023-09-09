package com.macrotracker

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.DatabaseRepositoryImpl
import com.macrotracker.ui.destination.NavDestination
import com.macrotracker.features.home.HomeScreen
import com.macrotracker.features.home.HomeScreenViewModel
import com.macrotracker.features.tracking.DailyMealScreen
import com.macrotracker.features.tracking.FoodSelectionScreen
import com.macrotracker.features.tracking.FoodSelectionScreenViewModel
import com.macrotracker.ui.components.theme.MacroTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databaseRepository = DatabaseRepositoryImpl(this)
        setContent {
            val navController = rememberNavController()
            MacroTrackerTheme {
                NavigationGraph(
                    activity = this,
                    navController = navController,
                    databaseRepository = databaseRepository,
                )
                navController.navigate(NavDestination.Home.route) {
                    launchSingleTop = true
                }
            }
        }
    }
}

@Composable
private fun NavigationGraph(
    activity: Activity,
    navController: NavHostController,
    databaseRepository: DatabaseRepository,
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Home.route
    ) {
        composable(NavDestination.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = HomeScreenViewModel(databaseRepository),
            )
        }
        composable(
            route = "${NavDestination.Tracking.route}?mealId={mealId}",
            arguments = listOf(
                navArgument(name = "mealId") {
                    nullable = true
                }
            )
        ) { navBackStackEntry ->
            val mealIdParam = navBackStackEntry.arguments?.getInt("mealId")
            val viewModel = FoodSelectionScreenViewModel(
                databaseRepository = databaseRepository,
                appContext = activity,
                mealId = mealIdParam
            )
            FoodSelectionScreen(viewModel = viewModel)
        }
        composable(
            route = "${NavDestination.Meals.route}/{date}",
            arguments = listOf(navArgument("date") { type = NavType.IntType })
        ) {
            it.arguments?.getInt("date")?.let { date ->
                DailyMealScreen(date)
            }
        }
    }
}