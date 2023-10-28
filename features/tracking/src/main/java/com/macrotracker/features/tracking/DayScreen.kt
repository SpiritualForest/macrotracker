package com.macrotracker.features.tracking

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.macrotracker.ui.components.LoggedFoodBar

@Composable
fun DayScreen(viewModel: DayScreenViewModel) {
    val uiState = viewModel.uiState

    Column {
        uiState.foods.forEach { foodEntity ->
            LoggedFoodBar(
                name = foodEntity.name,
                weight = foodEntity.weight.toString(),
            ) {
                viewModel.removeFood(foodEntity)
            }
        }
    }
}

private const val TAG = "DayScreen"