package com.macrotracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "weight") val weight: Int, // Always grams
    @ColumnInfo(name = "date") val date: Int, // Epoch in days
    @ColumnInfo(name = "timestamp") val timestamp: Long, // Epoch in milliseconds, serves as a link to MacroEntity
)

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods")
    fun getAll(): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name")
    fun getAllByName(name: String): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name AND date = :date AND timestamp = :timestamp")
    fun getFoodByNameDateTime(name: String, date: Int, timestamp: Int): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE date = :date")
    fun getAllByDate(date: Int): Flow<List<FoodEntity>>

    @Insert
    fun add(foodEntity: FoodEntity)

    @Update
    fun update(foodEntity: FoodEntity)

    @Delete
    fun delete(foodEntity: FoodEntity)
}
