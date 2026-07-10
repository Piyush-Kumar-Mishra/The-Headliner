package com.example.headliner.data.remote.api

import com.example.headliner.data.remote.dto.GNewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GNewsApi {
    @GET("top-headlines")
    suspend fun topHeadlines(
        @Query("category") category: String,
        @Query("lang") language: String,
        @Query("country") country: String,
        @Query("max") max: Int,
        @Query("page") page: Int,
        @Query("apikey") apiKey: String
    ): GNewsResponseDto

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("lang") language: String,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("max") max: Int,
        @Query("page") page: Int,
        @Query("apikey") apiKey: String
    ): GNewsResponseDto
}
