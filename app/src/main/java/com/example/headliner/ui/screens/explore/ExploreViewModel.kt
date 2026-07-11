package com.example.headliner.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.headliner.data.local.datastore.SettingsDataStore
import com.example.headliner.domain.model.Article
import com.example.headliner.domain.model.AppSettings
import com.example.headliner.domain.model.NewsResult
import com.example.headliner.domain.model.ArticleNote
import com.example.headliner.domain.repository.NewsRepository
import com.example.headliner.ui.screens.NewsListState
import com.example.headliner.util.toApiEnd
import com.example.headliner.util.toApiStart
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreState(
    val month: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val news: NewsListState = NewsListState(loading = true)
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: NewsRepository,
    settingsDataStore: SettingsDataStore
) : ViewModel() {
    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()
    private var page = 0
    private var settings = AppSettings()
    private var viewedIds: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            repository.savedArticleUrls().collectLatest { urls ->
                _state.update { state ->
                    state.copy(news = state.news.copy(savedUrls = urls.toSet()))
                }
            }
        }
        viewModelScope.launch {
            repository.viewedArticleIds().collectLatest { ids ->
                viewedIds = ids
            }
        }
        viewModelScope.launch {
            settingsDataStore.settings.collectLatest { settings ->
                this@ExploreViewModel.settings = settings
                refresh()
            }
        }
    }

    fun previousMonth() {
        _state.update { state ->
            val prev = state.month.minusMonths(1)
            state.copy(
                month = prev,
                selectedDate = prev.atDay(1),
                news = state.news.copy(articles = emptyList())
            )
        }
        refresh()
    }

    fun nextMonth() {
        val next = _state.value.month.plusMonths(1)
        if (next <= YearMonth.now()) {
            _state.update { state ->
                state.copy(
                    month = next,
                    selectedDate = next.atDay(1),
                    news = state.news.copy(articles = emptyList())
                )
            }
            refresh()
        }
    }

    fun selectDate(date: LocalDate) {
        _state.update { state ->
            state.copy(
                selectedDate = date,
                news = state.news.copy(articles = emptyList())
            )
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(news = it.news.copy(loading = true, refreshing = true, error = null, endReached = false)) }
            val date = _state.value.selectedDate

            val newArticles = mutableListOf<Article>()
            var pageNum = 0
            var endReached = false
            var errorMsg: String? = null

            while (newArticles.size < TARGET_COUNT && pageNum < MAX_PAGES && !endReached) {
                pageNum++
                when (val result = repository.search(
                    "news", settings.language.code, date.toApiStart(), date.toApiEnd(), PAGE_SIZE, pageNum
                )) {
                    is NewsResult.Success -> {
                        if (result.data.isEmpty()) {
                            endReached = true
                        } else {
                            newArticles.addAll(result.data.filterNot { it.id in viewedIds })
                        }
                    }
                    is NewsResult.Error -> {
                        errorMsg = result.message
                        break
                    }
                }
            }

            page = pageNum
            _state.update {
                it.copy(news = it.news.copy(
                    articles = newArticles,
                    loading = false,
                    refreshing = false,
                    error = errorMsg,
                    endReached = endReached
                ))
            }
        }
    }

    fun loadMore() {
        if (_state.value.news.loadingMore || _state.value.news.endReached) return
        viewModelScope.launch {
            _state.update { it.copy(news = it.news.copy(loadingMore = true)) }
            val date = _state.value.selectedDate
            val newArticles = mutableListOf<Article>()
            var pageNum = page
            var endReached = false
            var tries = 0

            while (newArticles.isEmpty() && tries < MAX_EXTRA_PAGES && !endReached) {
                pageNum++
                tries++
                when (val result = repository.search(
                    "news", settings.language.code, date.toApiStart(), date.toApiEnd(), PAGE_SIZE, pageNum
                )) {
                    is NewsResult.Success -> {
                        if (result.data.isEmpty()) {
                            endReached = true
                        } else {
                            newArticles.addAll(result.data.filterNot { it.id in viewedIds })
                        }
                    }
                    is NewsResult.Error -> {
                        _state.update { it.copy(news = it.news.copy(loadingMore = false, error = result.message)) }
                        return@launch
                    }
                }
            }

            page = pageNum
            _state.update {
                it.copy(news = it.news.copy(
                    articles = it.news.articles + newArticles,
                    loadingMore = false,
                    endReached = endReached
                ))
            }
        }
    }

    fun toggleSaved(article: Article) {
        viewModelScope.launch {
            if (_state.value.news.savedUrls.contains(article.url)) repository.deleteArticle(article.url) else repository.saveArticle(article)
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
