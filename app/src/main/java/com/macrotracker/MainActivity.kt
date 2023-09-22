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
import com.macrotracker.features.tracking.DailyMealScreen
import com.macrotracker.features.tracking.FoodSelectionScreen
import com.macrotracker.features.tracking.FoodSelectionScreenViewModel
import com.macrotracker.features.tracking.MealsViewModel
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
                    navController.navigate(NavDestination.Tracking.route) {
                        launchSingleTop = true
                    }
                },
                onMacroItemClick = {
                    navController.navigate("${NavDestination.Meals.route}/$it") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = "${NavDestination.Tracking.route}?mealId={mealId}",
            arguments = listOf(
                navArgument(name = "mealId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { navBackStackEntry ->
            val mealIdParam = navBackStackEntry.arguments?.getInt("mealId") ?: -1
            val viewModel = FoodSelectionScreenViewModel(
                databaseRepository = databaseRepository,
                appContext = activity,
                mealId = mealIdParam
            )
            FoodSelectionScreen(viewModel = viewModel)
        }
        composable(
            route = "${NavDestination.Meals.route}/{date}",
            arguments = listOf(
                navArgument("date") {
                    type = NavType.IntType
                }
            )
        ) {
            val date = it.arguments?.getInt("date")
            // Log.d(TAG, "Launching composable DailyMealsScreen")
            DailyMealScreen(
                viewModel = MealsViewModel(
                    databaseRepository = databaseRepository,
                    date = date ?: todayEpochDays()
                )
            )
        }
    }
}

private const val TAG = "MainActivity"