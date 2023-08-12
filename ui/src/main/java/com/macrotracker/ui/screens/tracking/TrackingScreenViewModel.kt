package com.macrotracker.ui.screens.tracking

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.macrotracker.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class TrackingScreenUiState(
    val foodCategories: List<String> = listOf(),
    val selectedCategoryIndex: Int = 0,
    val foods: List<FoodItem> = listOf(),
    val trackedMealItems: Map<FoodItem, Int> = mapOf(),
    val selectedFoodItem: FoodItem? = null,
)

@HiltViewModel
class TrackingScreenViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    @ApplicationContext appContext: Context,
) : ViewModel() {

    private val macroJsonData = loadMacroJsonData(appContext)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    internal var uiState by mutableStateOf(
        TrackingScreenUiState(
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

    internal fun selectFoodToTrack(item: FoodItem) {
        uiState = uiState.copy(
            selectedFoodItem = item
        )
    }

    internal fun addFood(weight: Int) {
        uiState.selectedFoodItem?.let { foodItem ->
            coroutineScope.launch {
                databaseRepository.addFoodItem(foodItem, weight)
            }
            Log.d(TAG, "adding food: $foodItem")
            val meal = uiState.trackedMealItems.toMutableMap()
            meal[foodItem] = weight
            uiState = uiState.copy(
                trackedMealItems = meal.toMap()
            )
        }
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

private const val TAG = "TrackingScreenViewModel"
