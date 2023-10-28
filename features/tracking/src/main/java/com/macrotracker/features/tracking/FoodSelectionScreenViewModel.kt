package com.macrotracker.features.tracking

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.macrotracker.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal data class FoodSelectionScreenUiState(
    val foodCategories: List<String> = listOf(),
    val selectedCategoryIndex: Int = 0,
    val foods: List<FoodItem> = listOf(),
)

class FoodSelectionScreenViewModel(
    private val databaseRepository: DatabaseRepository,
    appContext: Context,
) : ViewModel() {

    private val macroJsonData = loadMacroJsonData(appContext)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    internal var uiState by mutableStateOf(
        FoodSelectionScreenUiState(
            foodCategories = FoodCategory.values().map {
                it.name
            },
            foods = loadFoodItems(FoodCategory.values().first())
        )
    )
        private set

    internal fun selectFoodCategory(categoryIndex: Int) {
        uiState = uiState.copy(
            selectedCategoryIndex = categoryIndex,
            foods = loadFoodItems(
                FoodCategory.valueOf(uiState.foodCategories[categoryIndex])
            )
        )
    }

    internal fun addFood(foodItem: FoodItem, weight: Int) {
        coroutineScope.launch {
            databaseRepository.addFoodItem(foodItem, weight)
        }
        Log.d(TAG, "adding food: $foodItem")
    }

    private fun loadFoodItems(category: FoodCategory): List<FoodItem> {
        return when (category) {
            FoodCategory.Vegetables -> macroJsonData.vegetables
            FoodCategory.Fruits -> macroJsonData.fruits
            FoodCategory.Grains -> macroJsonData.grains
            FoodCategory.Beans -> macroJsonData.beans
        }
    }
}

private const val TAG = "FoodSelectionScreenViewModel"
