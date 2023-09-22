package com.macrotracker.features.tracking

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Contains all the meal cards

@Composable
fun DailyMealScreen(
    viewModel: MealsViewModel,
) {
    val uiState = viewModel.uiState

    uiState.meals.forEach {
        Text(
            text = it.id.toString(),
            color = Color.Red
        )
    }
}