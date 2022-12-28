package com.arthur.examples.shorturl.data.repository

import com.arthur.examples.shorturl.data.DataResult
import com.arthur.examples.shorturl.data.local.dao.AliasDao
import com.arthur.examples.shorturl.data.local.models.AliasLocal
import com.arthur.examples.shorturl.data.remote.models.Alias
import com.arthur.examples.shorturl.data.remote.models.AliasUrl
import com.arthur.examples.shorturl.data.remote.service.AliasRemoteDataSource
import javax.inject.Inject

class AliasRepository @Inject constructor(
    private val remoteDataSource: AliasRemoteDataSource,
    private val localDataSource: AliasDao
) {

    fun getStoredAlias() = localDataSource.getAll()

    suspend fun shortUrl(url: String): DataResult<Alias> {
        val result = remoteDataSource.shortenUrl(AliasUrl(url))
        if (result is DataResult.Success) {
            result.data?.let {
                localDataSource.insert(
                    AliasLocal(it.alias, it.link.self, it.link.shorted)
                )
            }
        }
        return result
    }

}