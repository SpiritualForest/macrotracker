package com.macrotracker.database

import android.content.Context
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.MacroEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface DatabaseRepository {
    suspend fun add(item: FoodItem, weight: Int)
    suspend fun remove(item: FoodItem, weight: Int)
    fun getTrackedMacros(date: Int? = null): Flow<List<MacroEntity>>
    fun getTrackedMacrosByDateRange(startDate: Int, endDate: Int): List<MacroEntity>
    fun getTrackedFoods(date: Int? = null): List<FoodEntity>
    fun getTrackedFoodByName(name: String): List<FoodEntity>
    fun clearDatabase()
}

class DatabaseRepositoryImpl(
    context: Context,
    createDatabaseInMemory: Boolean = false,
) : DatabaseRepository {
    // TODO: support operations on dates other than today

    private var todayEpochDays: Int
    private val db = MacroTrackerDatabase.getDatabase(
        context = context,
        createInMemory = createDatabaseInMemory,
    )

    private val macroDao = db.macroDao()
    private val foodDao = db.foodDao()

    init {
        val now = Clock.System.now()
        todayEpochDays = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays()
    }

    override suspend fun add(item: FoodItem, weight: Int) {
        // Add macros
        val trackedMacroItems = macroDao.getAllByDate(todayEpochDays).firstOrNull()
        trackedMacroItems?.firstOrNull()?.let {
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
    }

    override suspend fun remove(item: FoodItem, weight: Int) {
        // Remove <weight> of <item> from the database on a given day
        val trackedItems = macroDao.getAllByDate(todayEpochDays).firstOrNull()
        trackedItems?.firstOrNull()?.let {
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

    override fun getTrackedMacros(date: Int?): Flow<List<MacroEntity>> {
        return if (date == null) {
            macroDao.getAll()
        } else {
            macroDao.getAllByDate(date)
        }
    }

    override fun getTrackedMacrosByDateRange(startDate: Int, endDate: Int): List<MacroEntity> {
        if (startDate > endDate) {
            throw IllegalArgumentException("Start date must occur before end date")
        }
        return macroDao.getAllByDateRange(
            start = startDate,
            end = endDate
        )
    }

    override fun getTrackedFoods(date: Int?): List<FoodEntity> {
        return if (date == null) {
            foodDao.getAll()
        } else {
            foodDao.getAllByDate(date)
        }
    }

    override fun getTrackedFoodByName(name: String): List<FoodEntity> {
        return foodDao.getAllByName(name)
    }

    override fun clearDatabase() {
        db.clearAllTables()
    }
}

