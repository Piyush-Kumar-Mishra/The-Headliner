package com.example.headliner.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.headliner.data.local.datastore.SettingsDataStore
import com.example.headliner.data.local.db.SearchHistory
import com.example.headliner.domain.model.Article
import com.example.headliner.domain.model.AppSettings
import com.example.headliner.domain.model.NewsResult
import com.example.headliner.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val results: List<Article> = emptyList(),
    val history: List<SearchHistory> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NewsRepository,
    settingsDataStore: SettingsDataStore
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()
    private var searchJob: Job? = null
    private var settings = AppSettings()
    private var viewedIds: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            repository.searchHistory().collectLatest { history ->
                _state.update { it.copy(history = history) }
            }
        }
        viewModelScope.launch {
            repository.viewedArticleIds().collectLatest { ids ->
                viewedIds = ids
            }
        }
        viewModelScope.launch {
            settingsDataStore.settings.collectLatest { settings ->
                this@SearchViewModel.settings = settings
            }
        }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, error = null) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(results = emptyList(), loading = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            search(query)
        }
    }

    fun submit(query: String = _state.value.query) {
        searchJob?.cancel()
        if (query.isNotBlank()) search(query)
    }

    fun deleteHistory(query: String) = viewModelScope.launch { repository.deleteSearchQuery(query) }
    fun clearHistory() = viewModelScope.launch { repository.clearSearchHistory() }

    private fun search(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            when (val result = repository.search(query.trim(), settings.language.code, max = 30, page = 1)) {
                is NewsResult.Success -> {
                    repository.addSearchQuery(query)
                    _state.update { it.copy(results = result.data.filterNot { a -> a.id in viewedIds }, loading = false) }
                }
                is NewsResult.Error -> _state.update { it.copy(loading = false, error = result.message, results = emptyList()) }
            }
        }
    }
}
