package com.macrotracker.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.macrotracker.ui.R
import com.macrotracker.ui.components.MacroCard
import com.macrotracker.ui.destination.NavDestination

@Composable
fun HomeScreen(
    navController: NavController = rememberNavController(),
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(NavDestination.Tracking.route) {
                    launchSingleTop = true
                }
            }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = ""
                )
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(paddingValues)
        ) {
            if (uiState.macroEntities.isNotEmpty()) {
                items(uiState.macroEntities) {
                    MacroCard(data = it)
                }
            } else {
                item {
                    Text(
                        text = "Nothing tracked yet",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
