package com.macrotracker.database

import android.content.Context
import android.util.Log
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.MacroEntity
import com.macrotracker.database.entities.MealEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseRepository(
    context: Context,
    createDatabaseInMemory: Boolean = false,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val db = MacroTrackerDatabase.getDatabase(
        context = context,
        createInMemory = createDatabaseInMemory,
    )

    private val macroDao = db.macroDao()
    private val foodDao = db.foodDao()
    private val mealDao = db.mealDao()

    private val coroutineScope = CoroutineScope(dispatcher)

    /**
     * Calculate the [item]'s macros based on [weight] and add them
     * to the macros database, with the [meal]'s date as the tracking day.
     */
    fun addFoodItem(item: FoodItem, weight: Int, meal: MealEntity) {
        if (weight < 1) {
            throw IllegalArgumentException("Weight must be at least 1")
        }

        val mealDate = meal.date

        val calculationResult = calculateMacrosByWeight(food = item, weight = weight)

        coroutineScope.launch {
            val trackedMacroItems = macroDao.getAllByDate(mealDate).firstOrNull()
            if (trackedMacroItems.isNullOrEmpty()) {
                // First addition on <mealDate>
                val entity = MacroEntity(
                    calories = calculationResult.calories,
                    fat = calculationResult.fat,
                    fiber = calculationResult.fiber,
                    protein = calculationResult.protein,
                    carbs = calculationResult.carbs,
                    water = calculationResult.water,
                    sodium = calculationResult.sodium,
                    date = mealDate,
                )
                macroDao.add(entity)
            } else {
                // Data was previously tracked on this date, update it
                val trackedMacroItem = trackedMacroItems.first()
                val entity = trackedMacroItem.copy(
                    id = trackedMacroItem.id,
                    calories = trackedMacroItem.calories + calculationResult.calories,
                    fat = trackedMacroItem.fat + calculationResult.fat,
                    fiber = trackedMacroItem.fiber + calculationResult.fiber,
                    protein = trackedMacroItem.protein + calculationResult.protein,
                    carbs = trackedMacroItem.carbs + calculationResult.carbs,
                    water = trackedMacroItem.water + calculationResult.water,
                    sodium = trackedMacroItem.sodium + calculationResult.sodium,
                    date = trackedMacroItem.date
                )
                macroDao.update(entity)
            }
            // Now add the food item and associate it with the tracked meal
            foodDao.add(
                FoodEntity(
                    name = item.name,
                    weight = weight,
                    mealId = meal.id
                )
            )
        }
    }

    /**
     * Removes [weight] of [item] from the foods database.
     * Calculates the macros of [item] based on [weight] and removes those
     * from the macros tracked on the [meal]'s date.
     */
    fun removeFoodItem(item: FoodItem, weight: Int, meal: MealEntity) {
        if (weight < 1) {
            throw IllegalArgumentException("Weight must be at least 1")
        }

        coroutineScope.launch {
            val trackedItems = macroDao.getAllByDate(meal.date).firstOrNull()
            trackedItems?.firstOrNull()?.let {
                val calculationResult = calculateMacrosByWeight(food = item, weight = weight)
                val entity = it.copy(
                    calories = it.calories - calculationResult.calories,
                    fat = it.fat - calculationResult.fat,
                    fiber = it.fiber - calculationResult.fiber,
                    protein = it.protein - calculationResult.protein,
                    carbs = it.carbs - calculationResult.carbs,
                    water = it.water - calculationResult.water,
                    sodium = it.sodium - calculationResult.sodium,
                )
                macroDao.update(entity)

                val trackedFoodEntity = foodDao.getAllByNameAndMealId(
                    name = item.name,
                    mealId = meal.id
                ).firstOrNull()
                trackedFoodEntity?.let {
                    when {
                        weight > trackedFoodEntity.weight -> {
                            throw IllegalArgumentException("Cannot remove a weight that is larger than what was tracked")
                        }

                        weight == trackedFoodEntity.weight -> {
                            // Remove the whole tracked entity
                            foodDao.delete(trackedFoodEntity)
                        }

                        else -> {
                            // Only subtract the weight from it
                            foodDao.update(
                                trackedFoodEntity.copy(
                                    weight = trackedFoodEntity.weight - weight
                                )
                            )
                        }
                    }
                }
                    ?: throw IllegalArgumentException("Could not find a food with the name '${item.name}' associated with the given meal")
            }
        }
    }

    fun getTrackedMacros(date: Int? = null): Flow<List<MacroEntity>> {
        return if (date == null) {
            macroDao.getAll()
        } else {
            macroDao.getAllByDate(date)
        }
    }

    suspend fun getTrackedMacrosByDateRange(startDate: Int, endDate: Int): List<MacroEntity> {
        if (startDate > endDate) {
            throw IllegalArgumentException("Start date must occur before end date")
        }
        return withContext(dispatcher) {
            macroDao.getAllByDateRange(
                start = startDate,
                end = endDate
            )
        }
    }

    suspend fun getTrackedFoodByName(name: String): List<FoodEntity> {
        return withContext(dispatcher) {
            foodDao.getAllByName(name)
        }
    }

    suspend fun getFoodByMealId(id: Int): List<FoodEntity> {
        return withContext(dispatcher) {
            foodDao.getAllByMealId(id)
        }
    }

    /* Meal related functions */

    suspend fun addMeal(date: Int): MealEntity {
        Log.d(TAG, "addMeal called with date: $date")
        return withContext(dispatcher) {
            val meal = MealEntity(date = date)
            mealDao.add(meal)
            meal
        }
    }

    fun removeMeal(id: Int) {
        val meal = mealDao.getAllById(id).firstOrNull() ?: run {
            throw IllegalArgumentException("No meal with $id exists")
        }
        mealDao.delete(meal)
    }

    suspend fun getMealById(id: Int): MealEntity? {
        return withContext(dispatcher) {
            mealDao.getAllById(id).firstOrNull()
        }
    }

    fun getMealsByDate(date: Int): Flow<List<MealEntity>> {
        return mealDao.getAllByDate(date)
    }

    fun clearDatabase() {
        db.clearAllTables()
    }
}

private const val TAG = "DatabaseRepository"