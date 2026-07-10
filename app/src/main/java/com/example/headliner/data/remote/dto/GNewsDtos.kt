package com.example.headliner.data.remote.dto

import com.example.headliner.domain.model.Article

data class GNewsResponseDto(
    val articles: List<ArticleDto> = emptyList()
)

data class ArticleDto(
    val title: String? = null,
    val description: String? = null,
    val content: String? = null,
    val url: String? = null,
    val image: String? = null,
    val publishedAt: String? = null,
    val source: SourceDto? = null
) {
    fun toDomain(): Article? {
        val safeUrl = url?.takeIf { it.isNotBlank() } ?: return null
        return Article(
            title = title.orEmpty().ifBlank { "Untitled article" },
            description = description,
            content = content,
            url = safeUrl,
            imageUrl = image,
            publishedAt = publishedAt.orEmpty(),
            sourceName = source?.name.orEmpty().ifBlank { "Unknown source" },
            sourceUrl = source?.url
        )
    }
}

data class SourceDto(
    val name: String? = null,
    val url: String? = null
)
