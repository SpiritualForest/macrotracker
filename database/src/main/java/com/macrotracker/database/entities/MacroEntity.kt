package com.macrotracker.database.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

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
    @ColumnInfo(name = "timestamp") val timestamp: Long, // Timestamp of addition, serves as a unique ID for us
)

@Dao
interface MacroDao {
    @Query("SELECT * FROM macros ORDER BY date DESC")
    fun getAll(): Flow<List<MacroEntity>>

    @Query("SELECT * FROM macros WHERE date = :date")
    fun getAllByDate(date: Int): Flow<List<MacroEntity>>

    @Query("SELECT * FROM macros WHERE date >= :start AND date <= :end")
    fun getAllByDateRange(start: Int, end: Int): List<MacroEntity>

    @Query("SELECT * FROM macros WHERE timestamp = :timestamp")
    fun getAllByTimestamp(timestamp: Long): List<MacroEntity>

    @Insert
    fun add(macroEntity: MacroEntity)

    @Delete
    fun delete(macroEntity: MacroEntity)
}