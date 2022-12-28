package com.arthur.examples.shorturl.data.remote.service

import com.arthur.examples.shorturl.data.remote.models.AliasUrl
import javax.inject.Inject

class AliasRemoteDataSource @Inject constructor(private val aliasService: AliasService) :
    BaseRemoteDataSource() {
    suspend fun shortenUrl(data : AliasUrl) = getResult { aliasService.shortenUrl(data) }
    suspend fun readShortenedUrl(id: String) = getResult { aliasService.readShortenedUrl(id) }
}