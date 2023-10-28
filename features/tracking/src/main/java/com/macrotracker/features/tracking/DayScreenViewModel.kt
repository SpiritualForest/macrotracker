package com.macrotracker.features.tracking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.entities.FoodEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DayScreenUiState(val foods: List<FoodEntity> = listOf())

class DayScreenViewModel(
    private val databaseRepository: DatabaseRepository,
    date: Int,
) : ViewModel() {

    internal var uiState by mutableStateOf(DayScreenUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getFoodsByDate(date).collect { foods ->
                withContext(Dispatchers.Main) {
                    // We have to set the context to Main to prevent the snapshot leaking
                    // More info here: https://stackoverflow.com/a/66892156
                    uiState = uiState.copy(foods = foods)
                }
            }
        }
    }

    internal fun removeFood(food: FoodEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.removeFoodItem(food)
        }
    }
}

private const val TAG = "DayScreenViewModel"