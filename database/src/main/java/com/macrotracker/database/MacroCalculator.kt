package com.macrotracker.database
fun calculateMacrosByWeight(food: FoodItem, weight: Int): FoodItem {
    val weightDividedByHundred = weight / 100
    return FoodItem(
        name = food.name,
        calories = food.calories * weightDividedByHundred,
        fat = food.fat * weightDividedByHundred,
        carbs = food.carbs * weightDividedByHundred,
        fiber = food.fiber * weightDividedByHundred,
        protein = food.protein * weightDividedByHundred,
        water = food.water * weightDividedByHundred,
        sodium = food.sodium * weightDividedByHundred,
    )
}