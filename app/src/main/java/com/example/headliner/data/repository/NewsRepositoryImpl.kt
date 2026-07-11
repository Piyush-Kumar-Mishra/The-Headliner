package com.example.headliner.data.repository

import com.example.headliner.BuildConfig
import com.example.headliner.data.local.db.HeadlinerDao
import com.example.headliner.data.local.db.SavedArticle
import com.example.headliner.data.local.db.SearchHistory
import com.example.headliner.data.local.db.ViewedArticle
import com.example.headliner.data.local.db.toSavedArticle
import com.example.headliner.data.remote.api.NewsApi
import com.example.headliner.domain.model.Article
import com.example.headliner.domain.model.NewsResult
import com.example.headliner.domain.repository.NewsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import com.example.headliner.domain.model.ArticleNote
import com.example.headliner.data.local.db.toEntity
import kotlinx.coroutines.flow.map

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val dao: HeadlinerDao
) : NewsRepository {
    override suspend fun getTopHeadlines(
        category: String,
        country: String,
        language: String,
        max: Int,
        page: Int
    ): NewsResult<List<Article>> = safeApiCall {
        api.topHeadlines(
            category = category,
            language = language,
            country = country,
            max = max,
            page = page,
            apiKey = getApiKey()
        ).articles.mapNotNull { it.toDomain() }
    }

    override suspend fun search(
        query: String,
        language: String,
        from: String?,
        to: String?,
        max: Int,
        page: Int
    ): NewsResult<List<Article>> = safeApiCall {
        api.search(
            query = query,
            language = language,
            from = from,
            to = to,
            max = max,
            page = page,
            apiKey = getApiKey()
        ).articles.mapNotNull { it.toDomain() }
    }

    override fun savedArticles(): Flow<List<SavedArticle>> = dao.getAllSavedArticles()
    override fun savedArticleUrls(): Flow<List<String>> = dao.getSavedArticleUrls()
    override fun savedArticle(url: String): Flow<SavedArticle?> = dao.getSavedArticle(url)
    override fun searchHistory(): Flow<List<SearchHistory>> = dao.getSearchHistory()
    override suspend fun saveArticle(article: Article) = dao.insertArticle(article.toSavedArticle())
    override suspend fun deleteArticle(url: String) = dao.deleteArticle(url)
    override suspend fun clearSavedArticles() = dao.deleteAllSavedArticles()

    override suspend fun addSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotBlank()) {
            dao.insertSearch(SearchHistory(trimmed))
            dao.trimSearchHistory()
        }
    }

    override suspend fun deleteSearchQuery(query: String) = dao.deleteSearch(query)
    override suspend fun clearSearchHistory() = dao.clearSearchHistory()

    override fun getAllNotes(): Flow<List<ArticleNote>> = dao.getAllNotes().map { list ->
        list.map { it.toDomain() }
    }

    override suspend fun saveNote(note: ArticleNote) {
        dao.insertNote(note.toEntity())
    }

    override suspend fun deleteNote(id: Int) {
        dao.deleteNote(id)
    }


    override fun viewedArticleIds(): Flow<Set<String>> =
        dao.getViewedArticleIds().map { it.toSet() }

    override suspend fun markArticleViewed(articleId: String) {
        dao.insertViewedArticle(ViewedArticle(articleId = articleId))
        dao.trimViewedArticles()
    }

    override suspend fun clearViewedHistory() = dao.clearViewedArticles()

    private suspend fun safeApiCall(block: suspend () -> List<Article>): NewsResult<List<Article>> {
        if (BuildConfig.GNEWS_API_KEY.isBlank()) return NewsResult.Error("Api missing")
        return try {
            NewsResult.Success(block())
        } catch (exception: Exception) {
            NewsResult.Error(exception.message ?: "Error occurred", exception)
        }
    }

    private fun getApiKey(): String = BuildConfig.GNEWS_API_KEY
}

