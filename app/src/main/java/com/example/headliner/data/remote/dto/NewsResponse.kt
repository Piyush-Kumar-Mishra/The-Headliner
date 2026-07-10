package com.example.headliner.data.remote.dto

import com.example.headliner.domain.model.Article

data class NewsResponse(
    val articles: List<RemoteArticle> = emptyList()
)

data class RemoteArticle(
    val title: String? = null,
    val description: String? = null,
    val content: String? = null,
    val url: String? = null,
    val image: String? = null,
    val publishedAt: String? = null,
    val source: RemoteSource? = null
) {
    fun toDomain(): Article? {
        val safeUrl = url?.takeIf { it.isNotBlank() } ?: return null
        val safeTitle = title.orEmpty().ifBlank { "Untitled article" }
        val safePublishedAt = publishedAt.orEmpty()
        return Article(
            id = Article.generateId(safeUrl, safeTitle, safePublishedAt),
            title = safeTitle,
            description = description,
            content = content,
            url = safeUrl,
            imageUrl = image,
            publishedAt = safePublishedAt,
            sourceName = source?.name.orEmpty().ifBlank { "Unknown source" },
            sourceUrl = source?.url
        )
    }
}

data class RemoteSource(
    val name: String? = null,
    val url: String? = null
)
