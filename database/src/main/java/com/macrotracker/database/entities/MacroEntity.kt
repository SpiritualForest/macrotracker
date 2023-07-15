package com.macrotracker.database.entities

import android.content.Context
import androidx.room.*
import com.macrotracker.database.calculateMacrosByWeight
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.*

// Macros table: macroName: macroValue
// Calories, fat, fiber, carbs, protein, water, sodium
// All units are in grams except sodium, which is milligrams

// Represents the macros that were tracked on <date>
@Entity(tableName = "macros")
data class MacroEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "fat") val fat: Float,
    @ColumnInfo(name = "carbs") val carbs: Float,
    @ColumnInfo(name = "fiber") val fiber: Float,
    @ColumnInfo(name = "protein") val protein: Float,
    @ColumnInfo(name = "water") val water: Float,
    @ColumnInfo(name = "sodium") val sodium: Float,
    @ColumnInfo(name = "date") val date: Int, // epoch in days
)

@Dao
interface MacroDao {
    @Query("SELECT * FROM macros")
    fun getAll(): Flow<List<MacroEntity>>

    @Query("SELECT * FROM macros WHERE date = :date")
    fun getAllByDate(date: Int): List<MacroEntity>

    @Query("SELECT * FROM macros WHERE date >= :start AND date <= :end")
    fun getAllByDateRange(start: Int, end: Int): List<MacroEntity>

    @Insert
    fun add(macroEntity: MacroEntity)

    @Update
    fun update(macroEntity: MacroEntity)
}

class MacroDaoAccessor(private val dao: MacroDao) {

    // TODO: support operations on dates other than today

    private var today: LocalDate
    init {
        val now = Clock.System.now()
        today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    fun add(item: FoodItem, weight: Int) {
        // Add macros
        val now = Clock.System.now()
        val items = dao.getAllByDate(today.toEpochDays())

        val calculationResult = calculateMacrosByWeight(food = item, weight = weight)
        if (items.isEmpty()) {
            // First addition on <date>
            val entity = MacroEntity(
                calories = calculationResult.calories,
                fat = calculationResult.fat,
                fiber = calculationResult.fiber,
                protein = calculationResult.protein,
                carbs = calculationResult.carbs,
                water = calculationResult.water,
                sodium = calculationResult.sodium,
                date = today.toEpochDays(),
            )
            dao.add(entity)
        } else {
            // Data was previously tracked on this date, update it
            val trackedItem = items.first()
            val entity = MacroEntity(
                id = trackedItem.id,
                calories = trackedItem.calories + calculationResult.calories,
                fat = trackedItem.fat + calculationResult.fat,
                fiber = trackedItem.fiber + calculationResult.fiber,
                protein = trackedItem.protein + calculationResult.protein,
                carbs = trackedItem.carbs + calculationResult.carbs,
                water = trackedItem.water + calculationResult.water,
                sodium = trackedItem.sodium + calculationResult.water,
                date = trackedItem.date
            )
            dao.update(entity)
        }
    }

    fun subtract(item: FoodItem, weight: Int) {
        // Subtract
        val trackedItem = dao.getAllByDate(today.toEpochDays()).firstOrNull()
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
            dao.update(entity)
        }
    }
}

/* JSON data parsing */

@Serializable
data class FoodItem(
    val name: String,
    val calories: Int,
    val fat: Float,
    val fiber: Float,
    val protein: Float,
    val carbs: Float,
    val water: Float,
    val sodium: Float,
)

@Serializable
data class FoodData(
    val vegetables: List<FoodItem>,
    val fruits: List<FoodItem>,
    val grains: List<FoodItem>,
    val beans: List<FoodItem>,
)

@OptIn(ExperimentalSerializationApi::class)
fun loadMacroJsonData(context: Context): FoodData {
    // Load the Macros JSON file
    val stream = context.assets.open(MACROS_FILE)
    val jsonData: FoodData = Json.decodeFromStream(stream)
    stream.close()
    return jsonData
}

private const val MACROS_FILE = "foodData.json"