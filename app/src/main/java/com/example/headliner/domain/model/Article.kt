package com.example.headliner.domain.model

/** A display-ready news article independent from API and database models. */
data class Article(
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceName: String,
    val sourceUrl: String?
)

/** User settings that affect API requests and typography scale. */
data class AppSettings(
    val country: Country = Country("India", "in"),
    val language: Language = Language("English", "en")
)

data class Country(val label: String, val code: String, val flag: String = "")

data class Language(val label: String, val code: String)

sealed interface NewsResult<out T> {
    data class Success<T>(val data: T) : NewsResult<T>
    data class Error(val message: String, val throwable: Throwable? = null) : NewsResult<Nothing>
}
