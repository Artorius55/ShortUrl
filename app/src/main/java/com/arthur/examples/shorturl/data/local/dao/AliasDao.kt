package com.arthur.examples.shorturl.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arthur.examples.shorturl.data.local.models.AliasLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface AliasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(aliasLocal: AliasLocal)

    @Query("SELECT * FROM alias")
    fun getAll(): Flow<List<AliasLocal>>

    @Query("DELETE FROM alias")
    fun deleteAll()
}