package com.macrotracker.database.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.macrotracker.database.todayEpochDays

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Int = todayEpochDays(),
)

@Dao
interface MealDao {

    @Query("SELECT * FROM meals WHERE date = :date")
    fun getAllByDate(date: Int): List<MealEntity>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    fun getAllById(mealId: Int): List<MealEntity>

    @Insert
    fun add(mealEntity: MealEntity)

    @Delete
    fun delete(mealEntity: MealEntity)
}