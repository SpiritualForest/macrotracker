package com.macrotracker.database

import android.app.Instrumentation
import android.content.Context
import android.content.pm.InstrumentationInfo
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DatabaseTest {

    private lateinit var db: MacroTrackerDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }
}