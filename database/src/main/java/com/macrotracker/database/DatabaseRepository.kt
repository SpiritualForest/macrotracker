package com.macrotracker.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.FoodItem
import com.macrotracker.database.entities.MacroEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DatabaseRepository(
    context: Context,
    createDatabaseInMemory: Boolean = false
) {
    // TODO: support operations on dates other than today

    private var todayEpochDays: Int
    private val db = MacroTrackerDatabase.getDatabase(
        context = context,
        createInMemory = createDatabaseInMemory,
    )

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val macroDao = db.macroDao()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val foodDao = db.foodDao()

    init {
        val now = Clock.System.now()
        todayEpochDays = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays()
    }

    fun add(item: FoodItem, weight: Int) {
        // Add macros
        val trackedMacroItems = macroDao.getAllByDate(todayEpochDays)

        val calculationResult = calculateMacrosByWeight(food = item, weight = weight)
        if (trackedMacroItems.isEmpty()) {
            // First addition on <date>
            val entity = MacroEntity(
                calories = calculationResult.calories,
                fat = calculationResult.fat,
                fiber = calculationResult.fiber,
                protein = calculationResult.protein,
                carbs = calculationResult.carbs,
                water = calculationResult.water,
                sodium = calculationResult.sodium,
                date = todayEpochDays,
            )
            macroDao.add(entity)
            foodDao.add(
                FoodEntity(
                    name = item.name,
                    weight = weight,
                    date = todayEpochDays
                )
            )
        } else {
            // Data was previously tracked on this date, update it
            val trackedItem = trackedMacroItems.first()
            val entity = trackedItem.copy(
                id = trackedItem.id,
                calories = trackedItem.calories + calculationResult.calories,
                fat = trackedItem.fat + calculationResult.fat,
                fiber = trackedItem.fiber + calculationResult.fiber,
                protein = trackedItem.protein + calculationResult.protein,
                carbs = trackedItem.carbs + calculationResult.carbs,
                water = trackedItem.water + calculationResult.water,
                sodium = trackedItem.sodium + calculationResult.sodium,
                date = trackedItem.date
            )
            macroDao.update(entity)

            val trackedFoodItem = foodDao.getAllByNameAndDate(
                name = item.name,
                date = todayEpochDays
            ).first()

            foodDao.update(
                trackedFoodItem.copy(
                    name = item.name,
                    weight = trackedFoodItem.weight + weight,
                    date = todayEpochDays
                )
            )
        }
    }

    fun remove(item: FoodItem, weight: Int) {
        // Remove <weight> of <item> from the database on a given day
        val trackedItem = macroDao.getAllByDate(todayEpochDays).firstOrNull()
        trackedItem?.let {
            val calculationResult = calculateMacrosByWeight(food = item, weight = weight)
            val entity = MacroEntity(
                id = it.id,
                calories = it.calories - calculationResult.calories,
                fat = it.fat - calculationResult.fat,
                fiber = it.fiber - calculationResult.fiber,
                protein = it.protein - calculationResult.protein,
                carbs = it.carbs - calculationResult.carbs,
                water = it.water - calculationResult.water,
                sodium = it.sodium - calculationResult.sodium,
                date = it.date
            )
            macroDao.update(entity)

            val trackedFoodItem = foodDao.getAllByNameAndDate(
                name = item.name,
                date = todayEpochDays
            ).first()

            foodDao.update(
                FoodEntity(
                    id = trackedFoodItem.id,
                    name = item.name,
                    weight = trackedFoodItem.weight - weight,
                    date = todayEpochDays
                )
            )
        }
    }

    fun getTrackedMacros(date: Int? = null): List<MacroEntity> {
        return if (date == null) {
            macroDao.getAll()
        } else {
            macroDao.getAllByDate(date)
        }
    }

    fun getTrackedMacrosByDateRange(startDate: Int, endDate: Int): List<MacroEntity> {
        if (endDate > startDate) {
            throw IllegalArgumentException("End date must occur before start date")
        }
        return macroDao.getAllByDateRange(
            start = startDate,
            end = endDate
        )
    }

    fun getTrackedFoods(date: Int? = null): List<FoodEntity> {
        return if (date == null) {
            foodDao.getAll()
        } else {
            foodDao.getAllByDate(date)
        }
    }

    fun getTrackedFoodByName(name: String): List<FoodEntity> {
        return foodDao.getAllByName(name)
    }

    fun clearDatabase() {
        db.clearAllTables()
    }
}

