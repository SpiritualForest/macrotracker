package com.macrotracker.features.tracking

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.entities.MealEntity
import com.macrotracker.database.todayEpochDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MealsUiState(
    val meals: List<MealEntity> = listOf(),
)

class MealsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val date: Int = todayEpochDays(),
) : ViewModel() {

    internal var uiState = MutableStateFlow(MealsUiState())
        private set
    private var mealsJob: Job? = null

    init {
        mealsJob?.cancel()
        mealsJob = viewModelScope.launch {
            updateMeals()
        }
    }

    private suspend fun updateMeals() {
        withContext(Dispatchers.IO) {
            databaseRepository.getMealsByDate(date).collect {
                uiState.value = MealsUiState(meals = it)
            }
        }
    }
}

private const val TAG = "MealsViewModel"