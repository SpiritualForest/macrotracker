package com.macrotracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.macrotracker.database.entities.FoodDao
import com.macrotracker.database.entities.FoodEntity
import com.macrotracker.database.entities.MacroDao
import com.macrotracker.database.entities.MacroEntity
import com.macrotracker.database.entities.TargetEntity

@Database(
    entities = [
        MacroEntity::class,
        FoodEntity::class,
        TargetEntity::class,
    ],
    version = 1
)
internal abstract class MacroTrackerDatabase : RoomDatabase() {
    abstract fun macroDao(): MacroDao
    abstract fun foodDao(): FoodDao

    companion object {

        private var INSTANCE: MacroTrackerDatabase? = null

        fun getDatabase(
            context: Context,
            name: String = "macros-database",
            createInMemory: Boolean = false
        ): MacroTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = if (createInMemory) {
                    // Use the in memory one for testing purposes
                    Room.inMemoryDatabaseBuilder(
                        context = context.applicationContext,
                        klass = MacroTrackerDatabase::class.java,
                    ).build()
                } else {
                    Room.databaseBuilder(
                        context = context.applicationContext,
                        klass = MacroTrackerDatabase::class.java,
                        name = name,
                    ).build()
                }
                INSTANCE = instance
                instance
            }
        }
    }
}