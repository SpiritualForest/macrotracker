package com.macrotracker.features.tracking

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color

// Contains all the meal cards

@Composable
fun DailyMealScreen(
    viewModel: MealsViewModel,
) {
    val uiState = viewModel.uiState.collectAsState()

    Log.d(TAG, "The uiState: $uiState")
    Column {
        uiState.value.meals.forEach {
            Text(
                text = it.id.toString(),
                color = Color.Red
            )
        }
    }
}

private const val TAG = "DailyMealScreen"