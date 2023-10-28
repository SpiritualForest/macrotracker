package com.macrotracker.database

import android.content.Context
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.MacroEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
     * to the macros database. If [addTomorrow] is true, will track the items
     * with tomorrow as the date and timestamp of entry.
     */
    fun addFoodItem(
        item: FoodItem,
        weight: Int,
        addTomorrow: Boolean = false,
    ) {
        if (weight < 1) {
            throw IllegalArgumentException("Weight must be at least 1")
        }

        val date = if (addTomorrow) tomorrowEpochDays() else todayEpochDays()
        val timestamp = if (addTomorrow) tomorrowEpochMilliseconds() else nowEpochMilliseconds()

        val calculationResult = calculateMacrosByWeight(food = item, weight = weight)

        coroutineScope.launch {
            val entity = MacroEntity(
                calories = calculationResult.calories,
                fat = calculationResult.fat,
                fiber = calculationResult.fiber,
                protein = calculationResult.protein,
                carbs = calculationResult.carbs,
                water = calculationResult.water,
                sodium = calculationResult.sodium,
                date = date,
                timestamp = timestamp,
            )
            macroDao.add(entity)
            // Now add the food item and associate it with the tracked macros using the timestamp
            foodDao.add(
                FoodEntity(
                    name = item.name,
                    weight = weight,
                    date = date,
                    timestamp = timestamp,
                )
            )
        }
    }

    /**
     * Removes [foodEntity] from the foods table and then removes
     * the macros associated with this food entity from the macros table
     */
    fun removeFoodItem(foodEntity: FoodEntity) {
        coroutineScope.launch {
            val macroEntity = macroDao.getAllByTimestamp(foodEntity.timestamp).first()
            macroDao.delete(macroEntity)
            foodDao.delete(foodEntity)
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

    fun getFoodsByDate(date: Int): Flow<List<FoodEntity>> {
        return foodDao.getAllByDate(date)
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