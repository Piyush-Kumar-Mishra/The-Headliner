package com.example.headliner.ui.screens

import com.example.headliner.domain.model.Article

data class NewsListState(
    val articles: List<Article> = emptyList(),
    val bannerArticles: List<Article> = emptyList(),
    val selectedCategory: String = "general",
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val refreshing: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val savedUrls: Set<String> = emptySet()
)
