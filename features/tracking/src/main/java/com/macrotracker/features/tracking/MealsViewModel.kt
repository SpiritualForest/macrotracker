package com.macrotracker.features.tracking

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.entities.MealEntity
import com.macrotracker.database.todayEpochDays

data class MealsUiState(
    val meals: List<MealEntity> = listOf(),
)

class MealsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val date: Int = todayEpochDays(),
) : ViewModel() {

    internal var uiState by mutableStateOf(MealsUiState())
}
