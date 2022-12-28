package com.arthur.examples.shorturl.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.arthur.examples.shorturl.R
import com.arthur.examples.shorturl.data.local.AppDatabase
import com.arthur.examples.shorturl.data.local.dao.AliasDao
import com.arthur.examples.shorturl.data.local.models.AliasLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.hamcrest.core.IsNot.not

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    private lateinit var aliasDao: AliasDao
    private lateinit var database: AppDatabase

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        // Create a test context
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Get an instance of AppDatabase
        database = AppDatabase.getDatabase(context)

        // Get an instance of AliasDao from AppDatabase
        aliasDao = database.aliasDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testDisplaysStoredAliases_whenThereAreStoredAliases() = runBlocking {
        // Insert an alias into the database using AliasDao
        val alias = AliasLocal(
            alias = "123456",
            self = "http://www.example.com",
            shorted = "http://localhost:3000/api/alias/123456"
        )
        aliasDao.insert(alias)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            aliasDao.getAll().collect {
                assertTrue(it.isNotEmpty())
                latch.countDown()
            }
        }
        latch.await()

        // Check if the list is displayed
        onView(withId(R.id.rv_shortened_urls)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_empty_message)).check(matches(not(isDisplayed())))

        job.cancelAndJoin()
    }

    @Test
    fun testHidesListAndDisplaysEmptyMessage_whenThereAreNoStoredAliases() = runBlocking {
        // Delete all stored aliases
        aliasDao.deleteAll()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            aliasDao.getAll().collect {
                assertTrue(it.isEmpty())
                latch.countDown()
            }
        }
        latch.await()

        onView(withId(R.id.rv_shortened_urls)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_empty_message)).check(matches(isDisplayed()))

        job.cancelAndJoin()
    }

}