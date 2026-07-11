package com.example.headliner.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.headliner.domain.model.Article

@Entity(tableName = "saved_articles")
data class SavedArticle(
    @PrimaryKey val url: String,
    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceName: String,
    val sourceUrl: String?,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Article = Article(
        id = Article.generateId(url, title, publishedAt),
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        sourceName = sourceName,
        sourceUrl = sourceUrl
    )
}

fun Article.toSavedArticle(savedAt: Long = System.currentTimeMillis()): SavedArticle = SavedArticle(
    url = url,
    title = title,
    description = description,
    content = content,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    sourceName = sourceName,
    sourceUrl = sourceUrl,
    savedAt = savedAt
)
