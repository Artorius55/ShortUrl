package com.arthur.examples.shorturl

import com.arthur.examples.shorturl.data.DataResult
import com.arthur.examples.shorturl.data.remote.models.AliasUrl
import com.arthur.examples.shorturl.data.remote.service.AliasRemoteDataSource
import com.arthur.examples.shorturl.data.remote.service.AliasService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RemoteServiceUnitTest {

    private val mockWebServer = MockWebServer()
    private lateinit var aliasService: AliasService
    private lateinit var aliasRemoteDataSource: AliasRemoteDataSource

    @Before
    fun setUp() {
        mockWebServer.start()
        val baseUrl = mockWebServer.url("/").toString()

        // Set up a Retrofit instance with a base URL and a JSON converter
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        // Create an instance of the AliasService interface
        aliasService = retrofit.create(AliasService::class.java)
        aliasRemoteDataSource = AliasRemoteDataSource(aliasService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `shortenUrl sends correct request and returns correct response`() = runBlocking {
        // Get mock response from resources
        val reader = MockResponseFileReader("postAliasSuccess.json")

        // Set up the mock response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(reader.content)
        mockWebServer.enqueue(mockResponse)

        // Call the method under test
        val result = aliasRemoteDataSource.shortenUrl(AliasUrl("http://www.google.com"))

        // Verify the results
        assertTrue(result is DataResult.Success)

        val alias = result.data

        assertNotNull(alias)
        assertEquals("1244095381", alias?.alias)
        assertEquals("http://www.google.com", alias?.link?.self)
        assertEquals("http://localhost:3000/api/alias/1244095381", alias?.link?.shorted)
    }

    @Test
    fun `shortenUrl sends correct request and manage error`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("Missing url parameter")
        mockWebServer.enqueue(mockResponse)

        // Call the method under test
        val result = aliasRemoteDataSource.shortenUrl(AliasUrl("http://www.google.com"))

        // Verify the results
        assertTrue(result is DataResult.Error)
        assertNotNull(result.message)
    }

    @Test
    fun `shortenUrl sends correct request and server is not available`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        // Call the method under test
        val result = aliasRemoteDataSource.shortenUrl(AliasUrl("http://www.google.com"))

        // Verify the results
        assertTrue(result is DataResult.Error)
        assertNotNull(result.message)
    }

    @Test
    fun `readShortenedUrl sends correct request and returns correct response`() = runBlocking {
        // Get mock response from resources
        val reader = MockResponseFileReader("getAliasSuccess.json")

        // Set up the mock response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(reader.content)
        mockWebServer.enqueue(mockResponse)

        // Call the method under test
        val result = aliasRemoteDataSource.readShortenedUrl("1244095381")

        // Verify the results
        assertTrue(result is DataResult.Success)

        val aliasUrl = result.data

        assertNotNull(aliasUrl)
        assertFalse(aliasUrl?.url.isNullOrBlank())
    }

    @Test
    fun `readShortenedUrl sends correct request and manage error`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("Not found")
        mockWebServer.enqueue(mockResponse)

        // Call the method under test
        val result = aliasRemoteDataSource.readShortenedUrl("invalid")

        // Verify the results
        assertTrue(result is DataResult.Error)
        assertNotNull(result.message)
    }

    @Test
    fun `readShortenedUrl sends correct request and server is not available`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        // Call the method under test
        val result = aliasRemoteDataSource.readShortenedUrl("invalid")

        // Verify the results
        assertTrue(result is DataResult.Error)
        assertNotNull(result.message)
    }
}