package com.macrotracker.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (uiState.macroEntities.isNotEmpty()) {
                items(uiState.macroEntities) {
                    MacroCard(
                        data = it,
                        modifier = Modifier.padding(16.dp)
                    )
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
