package com.macrotracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "targets")
data class TargetEntity(
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
interface TargetDao {

    @Query("SELECT * FROM targets ORDER BY date DESC")
    fun getAll(): List<TargetEntity>

    @Insert
    fun add(targetEntity: TargetEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(targetEntity: TargetEntity)
}