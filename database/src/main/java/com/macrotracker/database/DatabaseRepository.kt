package com.macrotracker.database

import android.content.Context
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.MacroEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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

    private val coroutineScope = CoroutineScope(dispatcher)

    /**
     * Calculate the [item]'s macros based on [weight] and add them
     * to the macros database, with the [date] as epoch seconds
     * and [time] as seconds elapsed since the day began
     */
    fun addFoodItem(
        item: FoodItem,
        weight: Int,
        date: Int = todayEpochDays(),
        time: Int = nowToSecondOfDay(),
    ) {
        if (weight < 1) {
            throw IllegalArgumentException("Weight must be at least 1")
        }

        val calculationResult = calculateMacrosByWeight(food = item, weight = weight)

        coroutineScope.launch {
            val trackedMacroItems = macroDao.getAllByDate(date).firstOrNull()
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
                    date = date,
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
                    date = date,
                    time = time,
                )
            )
        }
    }

    /**
     * Removes [item] from the foods database and
     * then removes the calculated macro values
     * from the macros tracked on the [date]
     */
    fun removeFoodItem(
        item: FoodItem,
        date: Int,
        time: Int
    ) {
        coroutineScope.launch {
            val foodItem = foodDao.getFoodByNameDateTime(
                name = item.name,
                date = date,
                time = time
            ).firstOrNull() ?: return@launch
            val trackedItems = macroDao.getAllByDate(date).firstOrNull()
            trackedItems?.firstOrNull()?.let {
                val calculationResult =
                    calculateMacrosByWeight(food = item, weight = foodItem.weight)
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
                foodDao.delete(foodItem)
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

    fun getFoods(): List<FoodEntity> {
        return foodDao.getAll()
    }

    suspend fun getTrackedFoodByName(name: String): List<FoodEntity> {
        return withContext(dispatcher) {
            foodDao.getAllByName(name)
        }
    }

    fun clearDatabase() {
        db.clearAllTables()
    }
}

private const val TAG = "DatabaseRepository"