package com.macrotracker.features.tracking

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// Contains all the meal cards

@Composable
fun MealScreen(
    date: Int? = null,
) {
    Text(text = "Date is $date")
}