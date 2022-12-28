package com.arthur.examples.shorturl.di

import android.content.Context
import com.arthur.examples.shorturl.data.local.AppDatabase
import com.arthur.examples.shorturl.data.local.dao.AliasDao
import com.arthur.examples.shorturl.data.remote.service.AliasRemoteDataSource
import com.arthur.examples.shorturl.data.remote.service.AliasService
import com.arthur.examples.shorturl.data.repository.AliasRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //private const val BASE_URL = "https://url-shortener-nu.herokuapp.com"
    private const val BASE_URL = "http://192.168.0.11:3000"

    /** ------- Retrofit ------- **/
    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    //Services
    @Provides
    fun provideAliasService(retrofit: Retrofit): AliasService =
        retrofit.create(AliasService::class.java)

    /** ------- Database ------- **/
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        AppDatabase.getDatabase(appContext)

    //DAO's
    @Singleton
    @Provides
    fun provideAliasDao(db: AppDatabase) = db.aliasDao()

    /**  ------- Repositories ------- **/
    @Singleton
    @Provides
    fun provideAliasRepository(
        remoteDataSource: AliasRemoteDataSource,
        localDataSource: AliasDao
    ) = AliasRepository(remoteDataSource, localDataSource)

}