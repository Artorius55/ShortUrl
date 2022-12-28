package com.arthur.examples.shorturl

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arthur.examples.shorturl.data.local.AppDatabase
import com.arthur.examples.shorturl.data.local.dao.AliasDao
import com.arthur.examples.shorturl.data.local.models.AliasLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AliasDaoTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var aliasDao: AliasDao

    @Before
    fun setupDatabase() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        aliasDao = appDatabase.aliasDao()
    }

    @After
    fun closeDatabase() {
        appDatabase.close()
    }

    @Test
    fun insertAliasReturnsTrue() = runBlocking {
        val alias = AliasLocal(
            "1244095381",
            "http://www.google.com",
            "http://localhost:3000/api/alias/124409538"
        )

        aliasDao.insert(alias)


        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            aliasDao.getAll().collect {
                assertTrue(it.contains(alias))
                latch.countDown()

            }
        }
        latch.await()
        job.cancelAndJoin()
    }
}