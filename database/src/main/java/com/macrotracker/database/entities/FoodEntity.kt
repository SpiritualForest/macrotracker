package com.macrotracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "meal_id") val mealId: Int,
)

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods")
    fun getAll(): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE meal_id = :mealId")
    fun getAllByMealId(mealId: Int): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name AND meal_id = :mealId")
    fun getAllByNameAndMealId(name: String, mealId: Int): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name")
    fun getAllByName(name: String): List<FoodEntity>

    @Insert
    fun add(foodEntity: FoodEntity)

    @Update
    fun update(foodEntity: FoodEntity)

    @Delete
    fun delete(foodEntity: FoodEntity)
}
