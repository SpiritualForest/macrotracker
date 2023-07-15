package com.macrotracker.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.macrotracker.database.entities.FoodData
import com.macrotracker.database.entities.MacroDaoAccessor
import com.macrotracker.database.entities.loadMacroJsonData
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentedTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: MacroTrackerDatabase
    private lateinit var macroAccessor: MacroDaoAccessor
    private lateinit var foods: FoodData

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            context = appContext,
            klass = MacroTrackerDatabase::class.java
        ).build()

        foods = loadMacroJsonData(appContext)

        macroAccessor = MacroDaoAccessor(db.macroDao())
    }

    @Test
    fun testAddFood() = runTest {
        val food = foods.vegetables.first()
        macroAccessor.add(food, 100)

        val data = db.macroDao().getAll().first()
        assertTrue(data.size == 1)

        val item = data.first()
        assertTrue(item.calories == food.calories)
        assertTrue(item.fat == food.fat)
        assertTrue(item.fiber == food.fiber)
        assertTrue(item.carbs == food.carbs)
        assertTrue(item.water == food.water)
        assertTrue(item.protein == food.protein)
        assertTrue(item.sodium == food.sodium)
    }
}