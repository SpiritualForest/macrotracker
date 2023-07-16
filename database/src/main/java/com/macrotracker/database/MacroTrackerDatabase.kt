package com.macrotracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.FoodDao
import com.macrotracker.database.entities.FoodItem
import com.macrotracker.database.entities.MacroEntity
import com.macrotracker.database.entities.MacroDao
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Database(
    context: Context,
    createInMemory: Boolean = false
) {
    // TODO: support operations on dates other than today

    private var todayEpochDays: Int
    private val db = MacroTrackerDatabase.getDatabase(
        context = context,
        createInMemory = createInMemory,
    )

    // FIXME: these should be private. Write wrapper methods for all operations.
    val macroDao = db.macroDao()
    val foodDao = db.foodDao()

    init {
        val now = Clock.System.now()
        todayEpochDays = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays()
    }

    fun add(item: FoodItem, weight: Int): Int {
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
            val affected = macroDao.update(entity)

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

            val newItems = macroDao.getAll()
            println(newItems)
            return affected
        }
        return 0
    }

    fun subtract(item: FoodItem, weight: Int) {
        // Subtract
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
                sodium = it.sodium - calculationResult.water,
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

    fun clearDatabase() {
        db.clearAllTables()
    }

    companion object {
        @Database(entities = [MacroEntity::class, FoodEntity::class], version = 1)
        abstract class MacroTrackerDatabase : RoomDatabase() {
            abstract fun macroDao(): MacroDao
            abstract fun foodDao(): FoodDao

            companion object {

                private var INSTANCE: MacroTrackerDatabase? = null

                fun getDatabase(
                    context: Context,
                    name: String = "macros-database",
                    createInMemory: Boolean = false
                ): MacroTrackerDatabase {
                    return INSTANCE ?: synchronized(this) {
                        val instance = if (createInMemory) {
                            // Use the in memory one for testing purposes
                            Room.inMemoryDatabaseBuilder(
                                context = context.applicationContext,
                                klass = MacroTrackerDatabase::class.java,
                            ).build()
                        } else {
                            Room.databaseBuilder(
                                context = context.applicationContext,
                                klass = MacroTrackerDatabase::class.java,
                                name = name,
                            ).build()
                        }
                        INSTANCE = instance
                        instance
                    }
                }
            }
        }
    }
}