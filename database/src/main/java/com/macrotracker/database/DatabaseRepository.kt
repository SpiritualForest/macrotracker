package com.macrotracker.database

import android.content.Context
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.MacroEntity
import com.macrotracker.database.entities.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface DatabaseRepository {
    suspend fun addFoodItem(item: FoodItem, weight: Int, meal: MealEntity)
    suspend fun removeFoodItem(item: FoodItem, weight: Int, meal: MealEntity)
    suspend fun addMeal(date: Int = todayEpochDays()): MealEntity
    suspend fun removeMeal(id: Int)
    fun getMealById(id: Int): MealEntity?
    fun getFoodByMealId(id: Int): List<FoodEntity>
    fun getTrackedMacros(date: Int? = null): Flow<List<MacroEntity>>
    fun getTrackedMacrosByDateRange(startDate: Int, endDate: Int): List<MacroEntity>
    fun getTrackedFoodByName(name: String): List<FoodEntity>
    fun clearDatabase()
}

class DatabaseRepositoryImpl(
    context: Context,
    createDatabaseInMemory: Boolean = false,
) : DatabaseRepository {

    private val db = MacroTrackerDatabase.getDatabase(
        context = context,
        createInMemory = createDatabaseInMemory,
    )

    private val macroDao = db.macroDao()
    private val foodDao = db.foodDao()
    private val mealDao = db.mealDao()

    override suspend fun addFoodItem(item: FoodItem, weight: Int, meal: MealEntity) {
        // Add macros
        if (weight < 1) {
            throw IllegalArgumentException("Weight must be at least 1")
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

    override suspend fun removeFoodItem(item: FoodItem, weight: Int, meal: MealEntity) {
        // Remove <weight> of <item> from the database
        if (weight < 1) {
            throw IllegalArgumentException("Weight must be at least 1")
        }
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

    override fun getFoodByMealId(id: Int): List<FoodEntity> {
        return foodDao.getAllByMealId(id)
    }

    /* Meal related functions */

    override suspend fun addMeal(date: Int): MealEntity {
        val meal = MealEntity(date = date)
        mealDao.add(meal)
        return meal
    }

    override suspend fun removeMeal(id: Int) {
        val meal = mealDao.getAllById(id).firstOrNull() ?: run {
            throw IllegalArgumentException("No meal with $id exists")
        }
        mealDao.delete(meal)
    }

    override fun getMealById(id: Int): MealEntity? {
        return mealDao.getAllById(id).firstOrNull()
    }

    override fun clearDatabase() {
        db.clearAllTables()
    }
}

private const val TAG = "DatabaseRepository"