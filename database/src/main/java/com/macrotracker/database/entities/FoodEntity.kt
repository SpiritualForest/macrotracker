package com.macrotracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
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
    @ColumnInfo(name = "date") val date: Int, // epoch in days
)

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods")
    fun getAll(): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE date = :date")
    fun getAllByDate(date: Int): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE date >= :start AND date <= :end")
    fun getAllByDateRange(start: Int, end: Int): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name")
    fun getAllByName(name: String): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name AND date = :date")
    fun getAllByNameAndDate(name: String, date: Int): List<FoodEntity>

    @Insert
    fun add(foodEntity: FoodEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(foodEntity: FoodEntity)
}
