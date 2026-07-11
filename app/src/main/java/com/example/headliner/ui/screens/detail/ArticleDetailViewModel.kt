package com.example.headliner.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.headliner.domain.model.Article
import com.example.headliner.domain.model.ArticleNote
import com.example.headliner.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NewsRepository
) : ViewModel() {
    var hasDecodeError = false
        private set

    private fun safeDecode(value: String?, fallback: String = ""): String {
        if (value.isNullOrEmpty()) return fallback
        return try {
            URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        } catch (e: Exception) {
            hasDecodeError = true
            fallback
        }
    }

    val url: String = safeDecode(savedStateHandle.get<String>("encodedUrl"), "")
    val title: String = safeDecode(savedStateHandle.get<String>("title") ?: url, url)
    val imageUrl: String? = savedStateHandle.get<String>("imageUrl")?.takeIf { it.isNotEmpty() }?.let { safeDecode(it, "") }
    val sourceName: String = savedStateHandle.get<String>("sourceName")?.let { safeDecode(it, "Unknown Source") }?.ifBlank { "Unknown Source" } ?: "Unknown Source"
    val publishedAt: String = safeDecode(savedStateHandle.get<String>("publishedAt"), "")
    val description: String? = savedStateHandle.get<String>("description")?.takeIf { it.isNotEmpty() }?.let { safeDecode(it, "") }
    val content: String? = savedStateHandle.get<String>("content")?.takeIf { it.isNotEmpty() }?.let { safeDecode(it, "") }
    val sourceUrl: String? = savedStateHandle.get<String>("sourceUrl")?.takeIf { it.isNotEmpty() }?.let { safeDecode(it, "") }

    val saved: StateFlow<Boolean> = repository.savedArticle(url)
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        val articleId = Article.generateId(url, title, publishedAt)
        viewModelScope.launch { repository.markArticleViewed(articleId) }
    }

    fun toggleSaved() {
        viewModelScope.launch {
            if (saved.value) {
                repository.deleteArticle(url)
            } else {
                repository.saveArticle(
                    Article(
                        id = Article.generateId(url, title, publishedAt),
                        title = title,
                        description = description,
                        content = content,
                        url = url,
                        imageUrl = imageUrl,
                        publishedAt = publishedAt,
                        sourceName = sourceName,
                        sourceUrl = sourceUrl
                    )
                )
            }
        }
    }

    fun saveNote(noteTitle: String, noteContent: String) {
        viewModelScope.launch {
            repository.saveNote(
                ArticleNote(
                    articleUrl = url,
                    articleTitle = title,
                    articleImageUrl = imageUrl,
                    articleSourceName = sourceName,
                    articlePublishedAt = publishedAt,
                    title = noteTitle,
                    content = noteContent
                )
            )
        }
    }
}
