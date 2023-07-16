package com.macrotracker.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.macrotracker.database.entities.FoodData
import com.macrotracker.database.entities.loadMacroJsonData
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DatabaseRepositoryInstrumentedTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var repository: DatabaseRepository
    private lateinit var foods: FoodData

    @Before
    fun setup() {
        repository = DatabaseRepository(context = appContext, createDatabaseInMemory = true)

        foods = loadMacroJsonData(appContext)
    }

    @After
    fun teardown() {
        repository.clearDatabase()
    }

    @Test
    fun testAddFood() = runTest {
        val food = foods.vegetables.first()
        repository.add(food, 100)

        val data = repository.macroDao.getAll().first()
        assertTrue(data.size == 1)

        val item = data.first()
        assertTrue(item.calories == food.calories)
        assertTrue(item.fat == food.fat)
        assertTrue(item.fiber == food.fiber)
        assertTrue(item.carbs == food.carbs)
        assertTrue(item.water == food.water)
        assertTrue(item.protein == food.protein)
        assertTrue(item.sodium == food.sodium)

        val foodData = repository.foodDao.getAllByName(food.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 100)
    }

    @Test
    fun testUpdateMacros() = runTest {
        val food = foods.vegetables.first()
        repository.add(food, 100)

        var data = repository.macroDao.getAll().first()
        assertTrue(data.size == 1)

        val affected = repository.add(food, 100)
        assertTrue(affected == 1)

        data = repository.macroDao.getAll().first()
        assert(data.size == 1)
        val item = data.first()

        assert(item.calories == food.calories * 2)
        assert(item.fat == food.fat * 2)
        assert(item.fiber == food.fiber * 2)
        assert(item.protein == food.protein * 2)
        assert(item.carbs == food.carbs * 2)
        assert(item.water == food.water * 2)
        assert(item.sodium == food.sodium * 2)
    }
}