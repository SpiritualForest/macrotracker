package com.macrotracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "date") val date: Int, // epoch in days
)

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods WHERE date = :date")
    fun getAllByDate(date: Int): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE date >= :start AND date <= :end")
    fun getAllByDateRange(start: Int, end: Int): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE name = :name")
    fun getAllByName(name: String): List<FoodEntity>
}
