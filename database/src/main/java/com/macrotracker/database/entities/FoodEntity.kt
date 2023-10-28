package com.macrotracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "date") val date: Int,
    @ColumnInfo(name = "time") val time: Int, // Time of addition as seconds elapsed since the day began
)

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods")
    fun getAll(): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name")
    fun getAllByName(name: String): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name AND date = :date AND time = :time")
    fun getFoodByNameDateTime(name: String, date: Int, time: Int): List<FoodEntity>

    @Insert
    fun add(foodEntity: FoodEntity)

    @Update
    fun update(foodEntity: FoodEntity)

    @Delete
    fun delete(foodEntity: FoodEntity)
}
