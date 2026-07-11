package com.example.headliner.ui.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.headliner.data.local.db.SavedArticle
import com.example.headliner.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    val articles: StateFlow<List<SavedArticle>> = repository.savedArticles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(url: String) = viewModelScope.launch { repository.deleteArticle(url) }
}
