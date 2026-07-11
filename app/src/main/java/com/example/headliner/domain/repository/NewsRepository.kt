package com.example.headliner.domain.repository

import com.example.headliner.data.local.db.SavedArticle
import com.example.headliner.data.local.db.SearchHistory
import com.example.headliner.domain.model.Article
import com.example.headliner.domain.model.NewsResult
import com.example.headliner.domain.model.ArticleNote
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getTopHeadlines(
        category: String,
        country: String,
        language: String,
        max: Int,
        page: Int
    ): NewsResult<List<Article>>

    suspend fun search(
        query: String,
        language: String,
        from: String? = null,
        to: String? = null,
        max: Int,
        page: Int
    ): NewsResult<List<Article>>

    fun savedArticles(): Flow<List<SavedArticle>>
    fun savedArticleUrls(): Flow<List<String>>
    fun savedArticle(url: String): Flow<SavedArticle?>
    fun searchHistory(): Flow<List<SearchHistory>>
    suspend fun saveArticle(article: Article)
    suspend fun deleteArticle(url: String)
    suspend fun clearSavedArticles()
    suspend fun addSearchQuery(query: String)
    suspend fun deleteSearchQuery(query: String)
    suspend fun clearSearchHistory()

    fun getAllNotes(): Flow<List<ArticleNote>>
    suspend fun saveNote(note: ArticleNote)
    suspend fun deleteNote(id: Int)

    fun viewedArticleIds(): Flow<Set<String>>
    suspend fun markArticleViewed(articleId: String)
    suspend fun clearViewedHistory()
}
