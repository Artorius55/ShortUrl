package com.arthur.examples.shorturl

import com.arthur.examples.shorturl.data.DataResult
import com.arthur.examples.shorturl.data.local.dao.AliasDao
import com.arthur.examples.shorturl.data.local.models.AliasLocal
import com.arthur.examples.shorturl.data.remote.models.Alias
import com.arthur.examples.shorturl.data.remote.models.AliasUrl
import com.arthur.examples.shorturl.data.remote.models.Link
import com.arthur.examples.shorturl.data.remote.service.AliasRemoteDataSource
import com.arthur.examples.shorturl.data.repository.AliasRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class AliasRepositoryTest {
    private lateinit var aliasRepository: AliasRepository
    private lateinit var mockRemoteDataSource: AliasRemoteDataSource
    private lateinit var mockLocalDataSource: AliasDao

    @Before
    fun setUp() {
        mockRemoteDataSource = mock(AliasRemoteDataSource::class.java)
        mockLocalDataSource = mock(AliasDao::class.java)
        // Create an instance of the repository
        aliasRepository = AliasRepository(mockRemoteDataSource, mockLocalDataSource)
    }

    @Test
    fun `getStoredAlias returns correct result`() = runBlocking {
        // Set up test data
        val expectedResult = listOf(
            AliasLocal(
                alias = "123456",
                self = "http://www.google.com",
                shorted = "http://localhost:3000/api/alias/123456"
            )
        )
        `when`(mockLocalDataSource.getAll()).thenReturn(flowOf(expectedResult))

        // Call the method under test
        aliasRepository.getStoredAlias().collect {
            // Verify the results
            assertEquals(expectedResult, it)
        }
    }

    @Test
    fun `shortUrl returns success result`() = runBlocking {
        // Set up a mock response
        val alias = Alias(
            alias = "123456",
            link = Link(
                self = "http://www.google.com",
                shorted = "http://localhost:3000/api/alias/123456"
            )
        )
        val mockResponse = DataResult.Success(alias)
        `when`(mockRemoteDataSource.shortenUrl(AliasUrl("http://www.google.com"))).thenReturn(
            mockResponse
        )

        // Call the method under test
        val result = aliasRepository.shortUrl("http://www.google.com")

        // Verify the results
        assertEquals(mockResponse, result)
        verify(mockLocalDataSource).insert(
            AliasLocal(alias.alias, alias.link.self, alias.link.shorted)
        )
    }

    @Test
    fun `shortUrl returns error result`() = runBlocking {
        // Set up a mock response
        val mockResponse = DataResult.Error<Alias>("Error shortening URL")
        `when`(mockRemoteDataSource.shortenUrl(AliasUrl("http://www.google.com"))).thenReturn(
            mockResponse
        )

        // Call the method under test
        val result = aliasRepository.shortUrl("http://www.google.com")

        // Verify the results
        assertEquals(mockResponse, result)
    }
}