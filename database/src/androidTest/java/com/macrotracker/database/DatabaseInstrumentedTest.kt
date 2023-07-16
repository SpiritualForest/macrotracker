package com.macrotracker.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.macrotracker.database.entities.FoodData
import com.macrotracker.database.entities.loadMacroJsonData
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentedTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: Database
    private lateinit var foods: FoodData

    @Before
    fun setup() {
        db = Database(context = appContext, createInMemory = true)

        foods = loadMacroJsonData(appContext)
    }

    @After
    fun teardown() {
        db.clearDatabase()
    }

    @Test
    fun testAddFood() = runTest {
        val food = foods.vegetables.first()
        db.add(food, 100)

        val data = db.macroDao.getAll()
        assertTrue(data.size == 1)

        val item = data.first()
        assertTrue(item.calories == food.calories)
        assertTrue(item.fat == food.fat)
        assertTrue(item.fiber == food.fiber)
        assertTrue(item.carbs == food.carbs)
        assertTrue(item.water == food.water)
        assertTrue(item.protein == food.protein)
        assertTrue(item.sodium == food.sodium)

        val foodData = db.foodDao.getAllByName(food.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 100)
    }

    @Test
    fun testUpdateMacros() = runTest {
        val food = foods.vegetables.first()
        db.add(food, 100)

        val data = db.macroDao.getAll()

        assertTrue(data.size == 1)

        val affected = db.add(food, 100)

        assertTrue(affected == 1)


        assertTrue(data.size == 1)
        val item = db.macroDao.getAll().first()

        assert(item.calories == food.calories * 2)
        assert(item.fat == food.fat * 2)
        assert(item.fiber == food.fiber * 2)
        assert(item.protein == food.protein * 2)
        assert(item.carbs == food.carbs * 2)
        assert(item.water == food.water * 2)
        assert(item.sodium == food.sodium * 2)
    }
}