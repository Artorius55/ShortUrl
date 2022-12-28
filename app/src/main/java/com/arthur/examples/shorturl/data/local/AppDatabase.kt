package com.arthur.examples.shorturl.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arthur.examples.shorturl.data.local.dao.AliasDao
import com.arthur.examples.shorturl.data.local.models.AliasLocal

@Database(
    entities = [AliasLocal::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun aliasDao(): AliasDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "urlalias")
                .fallbackToDestructiveMigration()
                .build()
    }
}