package com.macrotracker.database

import android.content.Context
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.MacroEntity
import com.macrotracker.database.entities.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface DatabaseRepository {
    suspend fun add(item: FoodItem, weight: Int, mealEntity: MealEntity?)
    suspend fun remove(item: FoodItem, weight: Int, mealEntity: MealEntity)
    fun getTrackedMacros(date: Int? = null): Flow<List<MacroEntity>>
    fun getTrackedMacrosByDateRange(startDate: Int, endDate: Int): List<MacroEntity>
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
    private val mealDao = db.mealDao()

    init {
        val now = Clock.System.now()
        todayEpochDays = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays()
    }

    override suspend fun add(item: FoodItem, weight: Int, mealEntity: MealEntity?) {
        // Add macros
        if (weight < 1) {
            throw IllegalArgumentException("Weight must be at least 1")
        }
        val meal = mealEntity ?: run {
            // Create a new meal if not given an existing one
            val newMeal = MealEntity()
            mealDao.add(newMeal)
            newMeal
        }

        val calculationResult = calculateMacrosByWeight(food = item, weight = weight)
        val trackedMacroItems = macroDao.getAllByDate(meal.date).firstOrNull()
        if (trackedMacroItems.isNullOrEmpty()) {
            // First addition on <date>
            val entity = MacroEntity(
                calories = calculationResult.calories,
                fat = calculationResult.fat,
                fiber = calculationResult.fiber,
                protein = calculationResult.protein,
                carbs = calculationResult.carbs,
                water = calculationResult.water,
                sodium = calculationResult.sodium,
                date = meal.date,
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
        foodDao.add(
            FoodEntity(
                name = item.name,
                weight = weight,
                mealId = meal.id
            )
        )
    }

    override suspend fun remove(item: FoodItem, weight: Int, mealEntity: MealEntity) {
        // Remove <weight> of <item> from the database
        if (weight < 1) {
            throw IllegalArgumentException("Weight must be at least 1")
        }
        val trackedItems = macroDao.getAllByDate(mealEntity.date).firstOrNull()
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
                mealId = mealEntity.id
            ).first()
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
            if (foodDao.getAllByMealId(mealEntity.id).isEmpty()) {
                // Everything from this meal was removed, so delete it completely.
                mealDao.delete(mealEntity)
            }
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

    override fun getTrackedFoodByName(name: String): List<FoodEntity> {
        return foodDao.getAllByName(name)
    }

    override fun clearDatabase() {
        db.clearAllTables()
    }
}

private const val TAG = "DatabaseRepository"