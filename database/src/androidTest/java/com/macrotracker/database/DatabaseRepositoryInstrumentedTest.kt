package com.macrotracker.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class DatabaseRepositoryInstrumentedTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var repository: DatabaseRepository
    private lateinit var foods: FoodData

    @Before
    fun setup() {
        repository = DatabaseRepositoryImpl(
            context = appContext,
            createDatabaseInMemory = true,
        )

        foods = loadMacroJsonData(appContext)
    }

    @After
    fun teardown() {
        // repository.clearDatabase()
    }

    @Test
    fun testAddFood() = runTest(UnconfinedTestDispatcher()) {
        val foodItem = foods.vegetables.first()
        repository.add(foodItem, 100)
        advanceUntilIdle()

        val items = repository.getTrackedMacros().firstOrNull()

        advanceUntilIdle()

        assertTrue(items?.size == 1)

        val entity = items!!.first()
        assertTrue(entity.calories == foodItem.calories)
        assertTrue(entity.fat == foodItem.fat)
        assertTrue(entity.fiber == foodItem.fiber)
        assertTrue(entity.carbs == foodItem.carbs)
        assertTrue(entity.water == foodItem.water)
        assertTrue(entity.protein == foodItem.protein)
        assertTrue(entity.sodium == foodItem.sodium)

        val foodData = repository.getTrackedFoodByName(foodItem.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 100)
    }

    @Test
    fun testUpdateMacros() = runTest {
        val foodItem = foods.vegetables.first()

        repository.add(foodItem, 100)
        advanceUntilIdle()

        var data = repository.getTrackedMacros().firstOrNull()
        assertTrue(data?.size == 1)

        repository.add(foodItem, 100)
        advanceUntilIdle()

        data = repository.getTrackedMacros().firstOrNull()
        assertTrue(data?.size == 1)
        val entity = data!!.first()

        assert(entity.calories == foodItem.calories * 2)
        assert(entity.fat == foodItem.fat * 2)
        assert(entity.fiber == foodItem.fiber * 2)
        assert(entity.protein == foodItem.protein * 2)
        assert(entity.carbs == foodItem.carbs * 2)
        assert(entity.water == foodItem.water * 2)
        assert(entity.sodium == foodItem.sodium * 2)

        val foodData = repository.getTrackedFoodByName(foodItem.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 200)
    }

    @Test
    fun testRemoveMacros() = runTest {
        val foodItem = foods.vegetables.first()

        repository.add(foodItem, 100)
        advanceUntilIdle()

        var data = repository.getTrackedMacros().firstOrNull()
        assertTrue(data?.size == 1)

        repository.add(foodItem, 100)
        advanceUntilIdle()

        data = repository.getTrackedMacros().firstOrNull()
        assertTrue(data?.size == 1)
        var entity = data!!.first()

        assert(entity.calories == foodItem.calories * 2)
        assert(entity.fat == foodItem.fat * 2)
        assert(entity.fiber == foodItem.fiber * 2)
        assert(entity.protein == foodItem.protein * 2)
        assert(entity.carbs == foodItem.carbs * 2)
        assert(entity.water == foodItem.water * 2)
        assert(entity.sodium == foodItem.sodium * 2)

        var foodData = repository.getTrackedFoodByName(foodItem.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 200)

        // Now perform the removal
        repository.remove(foodItem, 100)
        advanceUntilIdle()

        data = repository.getTrackedMacros().firstOrNull()
        assert(data?.size == 1)
        entity = data!!.first()

        assert(entity.calories == foodItem.calories)
        assert(entity.fat == foodItem.fat)
        assert(entity.fiber == foodItem.fiber)
        assert(entity.protein == foodItem.protein)
        assert(entity.carbs == foodItem.carbs)
        assert(entity.water == foodItem.water)
        assert(entity.sodium == foodItem.sodium)

        foodData = repository.getTrackedFoodByName(foodItem.name)
        assertTrue(foodData.size == 1)
        assertTrue(foodData.first().weight == 100)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testGetMacrosWithEndDateSmallerThanStartDateRaisesException() {
        repository.getTrackedMacrosByDateRange(startDate = 2, endDate = 1)
    }
}