package com.example.headliner.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.headliner.data.local.datastore.SettingsDataStore
import com.example.headliner.domain.model.Article
import com.example.headliner.domain.model.AppSettings
import com.example.headliner.domain.model.NewsResult
import com.example.headliner.domain.model.ArticleNote
import com.example.headliner.domain.repository.NewsRepository
import com.example.headliner.ui.screens.NewsListState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: NewsRepository,
    settingsDataStore: SettingsDataStore
) : ViewModel() {
    private val _state = MutableStateFlow(NewsListState(loading = true))
    val state: StateFlow<NewsListState> = _state.asStateFlow()
    private var page = 0
    private var settings = AppSettings()
    private var viewedIds: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            repository.savedArticleUrls().collectLatest { urls ->
                _state.update { it.copy(savedUrls = urls.toSet()) }
            }
        }
        viewModelScope.launch {
            repository.viewedArticleIds().collectLatest { ids ->
                viewedIds = ids
            }
        }
        viewModelScope.launch {
            settingsDataStore.settings.collectLatest {
                settings = it
                refresh()
            }
        }
    }

    fun selectCategory(category: String) {
        if (category == _state.value.selectedCategory) return
        _state.update { it.copy(selectedCategory = category, articles = emptyList()) }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, refreshing = true, error = null, endReached = false) }
            val banner = repository.getTopHeadlines("general", settings.country.code, settings.language.code, 8, 1)

            val category = _state.value.selectedCategory
            val newArticles = mutableListOf<Article>()
            var pageNum = 0
            var endReached = false
            var errorMsg: String? = null

            while (newArticles.size < TARGET_COUNT && pageNum < MAX_PAGES && !endReached) {
                pageNum++
                when (val result = repository.getTopHeadlines(
                    category, settings.country.code, settings.language.code, PAGE_SIZE, pageNum
                )) {
                    is NewsResult.Success -> {
                        if (result.data.isEmpty()) {
                            endReached = true
                        } else {
                            val articles = if (category == "general" && pageNum == 1) {
                                result.data.drop(8)
                            } else {
                                result.data
                            }
                            newArticles.addAll(articles.filterNot { it.id in viewedIds })
                        }
                    }
                    is NewsResult.Error -> {
                        errorMsg = result.message
                        break
                    }
                }
            }

            page = pageNum
            _state.update { current ->
                val banners = (banner as? NewsResult.Success)?.data ?: current.bannerArticles
                current.copy(
                    bannerArticles = banners.filterNot { it.id in viewedIds }.ifEmpty { banners },
                    articles = newArticles,
                    loading = false,
                    refreshing = false,
                    error = errorMsg ?: (banner as? NewsResult.Error)?.message,
                    endReached = endReached
                )
            }
        }
    }

    fun loadMore() {
        if (_state.value.loadingMore || _state.value.endReached) return
        viewModelScope.launch {
            _state.update { it.copy(loadingMore = true) }
            val category = _state.value.selectedCategory
            val newArticles = mutableListOf<Article>()
            var pageNum = page
            var endReached = false
            var tries = 0

            while (newArticles.isEmpty() && tries < MAX_EXTRA_PAGES && !endReached) {
                pageNum++
                tries++
                when (val result = repository.getTopHeadlines(
                    category, settings.country.code, settings.language.code, PAGE_SIZE, pageNum
                )) {
                    is NewsResult.Success -> {
                        if (result.data.isEmpty()) {
                            endReached = true
                        } else {
                            newArticles.addAll(result.data.filterNot { it.id in viewedIds })
                        }
                    }
                    is NewsResult.Error -> {
                        _state.update { it.copy(loadingMore = false, error = result.message) }
                        return@launch
                    }
                }
            }

            page = pageNum
            _state.update {
                it.copy(
                    articles = it.articles + newArticles,
                    loadingMore = false,
                    endReached = endReached
                )
            }
        }
    }

    fun toggleSaved(article: Article) {
        viewModelScope.launch {
            if (_state.value.savedUrls.contains(article.url)) repository.deleteArticle(article.url) else repository.saveArticle(article)
        }
    }

    fun saveNote(article: Article, title: String, content: String) {
        viewModelScope.launch {
            repository.saveNote(
                ArticleNote(
                    articleUrl = article.url,
                    articleTitle = article.title,
                    articleImageUrl = article.imageUrl,
                    articleSourceName = article.sourceName,
                    articlePublishedAt = article.publishedAt,
                    title = title,
                    content = content
                )
            )
        }
    }

    fun markAsRead(article: Article) {
        viewModelScope.launch {
            repository.markArticleViewed(article.id)
        }
    }

    private companion object {
        const val TARGET_COUNT = 20
        const val PAGE_SIZE = 30
        const val MAX_PAGES = 3
        const val MAX_EXTRA_PAGES = 2
    }
}
