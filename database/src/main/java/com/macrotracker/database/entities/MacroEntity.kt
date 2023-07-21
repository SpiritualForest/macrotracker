package com.macrotracker.database.entities

import android.content.Context
import androidx.room.*
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
    fun getAll(): List<MacroEntity>

    @Query("SELECT * FROM macros WHERE date = :date")
    fun getAllByDate(date: Int): List<MacroEntity>

    @Query("SELECT * FROM macros WHERE date >= :start AND date <= :end")
    fun getAllByDateRange(start: Int, end: Int): List<MacroEntity>

    @Insert
    fun add(macroEntity: MacroEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(macroEntity: MacroEntity): Int
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