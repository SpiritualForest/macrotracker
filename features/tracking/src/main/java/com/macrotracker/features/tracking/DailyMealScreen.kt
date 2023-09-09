package com.macrotracker.features.tracking

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.macrotracker.database.todayEpochDays

// Contains all the meal cards

@Composable
fun DailyMealScreen(
    date: Int = todayEpochDays(),
) {
    Text(text = "Date is $date")
}