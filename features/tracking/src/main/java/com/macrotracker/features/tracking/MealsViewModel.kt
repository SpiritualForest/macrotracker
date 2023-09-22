package com.macrotracker.features.tracking

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.entities.MealEntity
import com.macrotracker.database.todayEpochDays
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class MealsUiState(
    val meals: List<MealEntity> = listOf(),
)

class MealsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val date: Int = todayEpochDays(),
) : ViewModel() {

    internal var uiState by mutableStateOf(MealsUiState())
        private set
    private var mealsJob: Job? = null

    init {
        Log.d(TAG, "init called")
        updateMeals()
    }

    private fun updateMeals() {
        mealsJob?.cancel()
        mealsJob = viewModelScope.launch {
            Log.d(TAG, "Testing message lulz")
            uiState = uiState.copy(
                meals = databaseRepository.getMealsByDate(date)
            )
            Log.d(TAG, "Meals: ${uiState.meals}")
        }
    }
}

private const val TAG = "MealsViewModel"