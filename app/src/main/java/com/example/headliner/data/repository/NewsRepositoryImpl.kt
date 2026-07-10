package com.example.headliner.data.repository

import com.example.headliner.BuildConfig
import com.example.headliner.data.local.db.HeadlinerDao
import com.example.headliner.data.local.db.SavedArticle
import com.example.headliner.data.local.db.SearchHistory
import com.example.headliner.data.local.db.toSavedArticle
import com.example.headliner.data.remote.api.GNewsApi
import com.example.headliner.domain.model.Article
import com.example.headliner.domain.model.NewsResult
import com.example.headliner.domain.repository.NewsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val api: GNewsApi,
    private val dao: HeadlinerDao
) : NewsRepository {
    override suspend fun getTopHeadlines(
        category: String,
        country: String,
        language: String,
        max: Int,
        page: Int
    ): NewsResult<List<Article>> = guardedNetworkCall {
        api.topHeadlines(
            category = category,
            language = language,
            country = country,
            max = max,
            page = page,
            apiKey = requireApiKey()
        ).articles.mapNotNull { it.toDomain() }
    }

    override suspend fun search(
        query: String,
        language: String,
        from: String?,
        to: String?,
        max: Int,
        page: Int
    ): NewsResult<List<Article>> = guardedNetworkCall {
        api.search(
            query = query,
            language = language,
            from = from,
            to = to,
            max = max,
            page = page,
            apiKey = requireApiKey()
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
        val normalized = query.trim()
        if (normalized.isNotBlank()) {
            dao.insertSearch(SearchHistory(normalized))
            dao.trimSearchHistory()
        }
    }

    override suspend fun deleteSearchQuery(query: String) = dao.deleteSearch(query)
    override suspend fun clearSearchHistory() = dao.clearSearchHistory()

    private suspend inline fun guardedNetworkCall(crossinline block: suspend () -> List<Article>): NewsResult<List<Article>> {
        if (BuildConfig.GNEWS_API_KEY.isBlank()) return NewsResult.Error("Api missing")
        return try {
            NewsResult.Success(block())
        }
        catch (exception: IOException) {
            NewsResult.Error("No internet connection. Please check your network.", exception)
        }
        catch (exception: HttpException) {
            NewsResult.Error("Something went wrong. Please try again.", exception)
        }
        catch (exception: Exception) {
            NewsResult.Error(exception.message ?: "Something went wrong. Please try again.", exception)
        }
    }

    private fun requireApiKey(): String = BuildConfig.GNEWS_API_KEY
}
