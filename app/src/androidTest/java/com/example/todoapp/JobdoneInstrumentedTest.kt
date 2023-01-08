package com.example.todoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JobdoneInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = getInstrumentation().targetContext
        assertEquals("com.example.todoapp", appContext.packageName)
    }
}