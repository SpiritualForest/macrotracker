package com.macrotracker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.todayEpochDays
import com.macrotracker.ui.destination.NavDestination
import com.macrotracker.features.home.HomeScreen
import com.macrotracker.features.home.HomeScreenViewModel
import com.macrotracker.features.tracking.DayScreen
import com.macrotracker.features.tracking.DayScreenViewModel
import com.macrotracker.features.tracking.FoodSelectionScreen
import com.macrotracker.features.tracking.FoodSelectionScreenViewModel
import com.macrotracker.ui.components.theme.MacroTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databaseRepository = DatabaseRepository(this)
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
    Log.d(TAG, "NavigationGraph function called")
    val homeVm = HomeScreenViewModel(databaseRepository)
    NavHost(
        navController = navController,
        startDestination = NavDestination.Home.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        Log.d(TAG,"NavHost created")
        composable(NavDestination.Home.route) {
            Log.d(TAG, "Opening HomeScreen")
            HomeScreen(
                viewModel = homeVm,
                onFabClick = {
                    navController.navigate(NavDestination.FoodSelection.route) {
                        launchSingleTop = true
                    }
                },
                onMacroItemClick = {
                    navController.navigate("${NavDestination.Tracking.route}/$it") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = NavDestination.FoodSelection.route,
        ) {
            val viewModel = FoodSelectionScreenViewModel(
                databaseRepository = databaseRepository,
                appContext = activity,
            )
            FoodSelectionScreen(viewModel = viewModel)
        }
        composable(
            route = "${NavDestination.Tracking.route}/{date}",
            arguments = listOf(navArgument("date") { NavType.IntType })
        ) {
            val viewModel = DayScreenViewModel(
                databaseRepository = databaseRepository,
                date = it.arguments?.getString("date")?.toInt() ?: todayEpochDays()
            )
            DayScreen(
                viewModel = viewModel
            )
        }
    }
}

private const val TAG = "MainActivity"