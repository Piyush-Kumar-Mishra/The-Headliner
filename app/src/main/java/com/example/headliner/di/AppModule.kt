package com.example.headliner.di

import android.content.Context
import androidx.room.Room
import com.example.headliner.data.local.db.HeadlinerDao
import com.example.headliner.data.local.db.HeadlinerDatabase
import com.example.headliner.data.remote.api.NewsApi
import com.example.headliner.data.repository.NewsRepositoryImpl
import com.example.headliner.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkDatabaseModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cache = Cache(File(context.cacheDir, "http_cache"), 10L * 1024L * 1024L)
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(logger)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(client: OkHttpClient): NewsApi = Retrofit.Builder()
        .baseUrl("https://gnews.io/api/v4/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HeadlinerDatabase =
        Room.databaseBuilder(context, HeadlinerDatabase::class.java, "headliner.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDao(database: HeadlinerDatabase): HeadlinerDao = database.dao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(repository: NewsRepositoryImpl): NewsRepository
}
