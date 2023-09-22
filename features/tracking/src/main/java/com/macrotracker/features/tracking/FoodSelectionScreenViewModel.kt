package com.macrotracker.features.tracking

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.macrotracker.database.*
import com.macrotracker.database.entities.MealEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal data class FoodSelectionScreenUiState(
    val foodCategories: List<String> = listOf(),
    val selectedCategoryIndex: Int = 0,
    val foods: List<FoodItem> = listOf(),
    val trackedMealItems: Map<FoodItem, Int> = mapOf(),
)

class FoodSelectionScreenViewModel(
    private val databaseRepository: DatabaseRepository,
    appContext: Context,
    mealId: Int = -1,
) : ViewModel() {

    private val macroJsonData = loadMacroJsonData(appContext)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var mealEntity: MealEntity? = null

    internal var uiState by mutableStateOf(
        FoodSelectionScreenUiState(
            foodCategories = FoodCategory.values().map {
                it.name
            },
            foods = loadFoodItems(FoodCategory.values().first())
        )
    )
        private set

    init {
        coroutineScope.launch {
            mealEntity = if (mealId == -1) {
                Log.d(TAG, "Executing addMeal()")
                databaseRepository.addMeal(todayEpochDays())
            } else {
                Log.d(TAG, "Executing getMealById($mealId)")
                databaseRepository.getMealById(mealId)
            }
            Log.d(TAG, "Meal entity: $mealEntity")
        }
    }

    internal fun selectFoodCategory(categoryIndex: Int) {
        uiState = uiState.copy(
            selectedCategoryIndex = categoryIndex,
            foods = loadFoodItems(
                FoodCategory.valueOf(uiState.foodCategories[categoryIndex])
            )
        )
    }

    internal fun addFood(foodItem: FoodItem, weight: Int) {
        Log.d(TAG, "Meal entity is $mealEntity")
        mealEntity?.let {
            coroutineScope.launch {
                databaseRepository.addFoodItem(foodItem, weight, it)
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

private const val TAG = "FoodSelectionScreenViewModel"
