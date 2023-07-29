package com.macrotracker.ui.screens.tracking

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.FoodCategory
import com.macrotracker.database.FoodItem
import com.macrotracker.database.loadMacroJsonData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal data class TrackingScreenUiState(
    val foodCategories: List<String> = listOf(),
    val selectedCategoryIndex: Int = 0,
    val foods: List<FoodItem> = listOf(),
)

@HiltViewModel
class TrackingScreenViewModel @Inject constructor(
    databaseRepository: DatabaseRepository,
    @ApplicationContext appContext: Context,
): ViewModel() {

    private val macroJsonData = loadMacroJsonData(appContext)

    internal var uiState by mutableStateOf(
        TrackingScreenUiState(
            foodCategories = FoodCategory.values().map {
                it.name
            },
            foods = loadFoodItems(FoodCategory.values().first())
        )
    )
        private set

    private fun loadFoodItems(category: FoodCategory): List<FoodItem> {
        return when (category) {
            FoodCategory.Vegetables -> macroJsonData.vegetables
            FoodCategory.Fruits -> macroJsonData.fruits
            FoodCategory.Grains -> macroJsonData.grains
            FoodCategory.Beans -> macroJsonData.beans
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
}
