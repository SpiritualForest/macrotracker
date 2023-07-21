package com.macrotracker.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.macrotracker.database.entities.FoodData
import com.macrotracker.database.entities.loadMacroJsonData
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.*
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
        val foodItem = foods.vegetables.first()
        repository.add(foodItem, 100)

        val data = repository.macroDao.getAll()
        assertTrue(data.size == 1)

        val entity = data.first()
        assertTrue(entity.calories == foodItem.calories)
        assertTrue(entity.fat == foodItem.fat)
        assertTrue(entity.fiber == foodItem.fiber)
        assertTrue(entity.carbs == foodItem.carbs)
        assertTrue(entity.water == foodItem.water)
        assertTrue(entity.protein == foodItem.protein)
        assertTrue(entity.sodium == foodItem.sodium)

        val foodData = repository.foodDao.getAllByName(foodItem.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 100)
    }

    @Test
    fun testUpdateMacros() = runTest {
        val foodItem = foods.vegetables.first()
        repository.add(foodItem, 100)

        var data = repository.macroDao.getAll()
        assertTrue(data.size == 1)

        repository.add(foodItem, 100)

        data = repository.macroDao.getAll()
        assert(data.size == 1)
        val entity = data.first()

        assert(entity.calories == foodItem.calories * 2)
        assert(entity.fat == foodItem.fat * 2)
        assert(entity.fiber == foodItem.fiber * 2)
        assert(entity.protein == foodItem.protein * 2)
        assert(entity.carbs == foodItem.carbs * 2)
        assert(entity.water == foodItem.water * 2)
        assert(entity.sodium == foodItem.sodium * 2)

        val foodData = repository.foodDao.getAllByName(foodItem.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 200)
    }

    @Test
    fun testRemoveMacros() = runTest {
        val foodItem = foods.vegetables.first()
        repository.add(foodItem, 100)

        var data = repository.macroDao.getAll()
        assertTrue(data.size == 1)

        repository.add(foodItem, 100)

        data = repository.macroDao.getAll()
        assert(data.size == 1)
        var entity = data.first()

        assert(entity.calories == foodItem.calories * 2)
        assert(entity.fat == foodItem.fat * 2)
        assert(entity.fiber == foodItem.fiber * 2)
        assert(entity.protein == foodItem.protein * 2)
        assert(entity.carbs == foodItem.carbs * 2)
        assert(entity.water == foodItem.water * 2)
        assert(entity.sodium == foodItem.sodium * 2)

        var foodData = repository.foodDao.getAllByName(foodItem.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 200)

        // Now perform the removal
        repository.remove(foodItem, 100)
        data = repository.macroDao.getAll()
        entity = data.first()

        assert(entity.calories == foodItem.calories)
        assert(entity.fat == foodItem.fat)
        assert(entity.fiber == foodItem.fiber)
        assert(entity.protein == foodItem.protein)
        assert(entity.carbs == foodItem.carbs)
        assert(entity.water == foodItem.water)
        assert(entity.sodium == foodItem.sodium)

        foodData = repository.foodDao.getAllByName(foodItem.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 100)
    }
}