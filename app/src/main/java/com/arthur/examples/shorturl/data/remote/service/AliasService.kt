package com.arthur.examples.shorturl.data.remote.service

import com.arthur.examples.shorturl.data.remote.models.Alias
import com.arthur.examples.shorturl.data.remote.models.AliasUrl
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AliasService {

    @POST("/api/alias")
    suspend fun shortenUrl(@Body body: AliasUrl): Response<Alias>

    @GET("/api/alias/{id}")
    suspend fun readShortenedUrl(@Path("id") id: String) : Response<AliasUrl>
}