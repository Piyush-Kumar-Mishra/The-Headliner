package com.example.headliner.domain.model

import java.security.MessageDigest

data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceName: String,
    val sourceUrl: String?
) {
    companion object {
        fun generateId(url: String, title: String, publishedAt: String): String {
            val input = "$url|$title|$publishedAt"
            val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}

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
