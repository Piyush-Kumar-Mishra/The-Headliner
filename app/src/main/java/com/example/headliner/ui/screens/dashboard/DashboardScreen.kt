package com.example.headliner.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.headliner.domain.model.Article
import com.example.headliner.ui.components.CategoryChips
import com.example.headliner.ui.components.HeadlineBanner
import com.example.headliner.ui.components.HeadlineBannerShimmer
import com.example.headliner.ui.components.LoadingLines
import com.example.headliner.ui.components.Masthead
import com.example.headliner.ui.components.SearchPrompt
import com.example.headliner.ui.components.SwipeableNewsStack
import com.example.headliner.ui.components.ArticleNoteDialog
import com.example.headliner.ui.components.SlideIndicator
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.headliner.ui.theme.NewspaperCharcoal

@Composable
fun DashboardScreen(
    onSearch: () -> Unit,
    onOpenArticle: (Article) -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var noteArticleForDialog by remember { mutableStateOf<Article?>(null) }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Masthead()
        SearchPrompt(onSearch)
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Headlines", style = MaterialTheme.typography.titleLarge, color = NewspaperCharcoal)
            if (state.loading && state.bannerArticles.isEmpty()) {
                HeadlineBannerShimmer()
            } else {
                HeadlineBanner(
                    articles = state.bannerArticles,
                    onOpen = onOpenArticle,
                    onArticleVisible = viewModel::markAsRead
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text("Top Stories", style = MaterialTheme.typography.titleLarge, color = NewspaperCharcoal)
            CategoryChips(
                categories = listOf("general", "business", "technology", "entertainment", "sports", "science", "health"),
                selected = state.selectedCategory,
                onSelected = viewModel::selectCategory
            )
            SlideIndicator()
            Spacer(Modifier.height(1.dp))
            if (state.loading && state.articles.isEmpty()) {
                LoadingLines()
            } else {
                SwipeableNewsStack(
                    articles = state.articles,
                    savedUrls = state.savedUrls,
                    onRead = onOpenArticle,
                    onSaveToggle = viewModel::toggleSaved,
                    onNearEnd = viewModel::loadMore,
                    onNoteClick = { noteArticleForDialog = it },
                    onArticleVisible = viewModel::markAsRead
                )
            }
        }
        if (state.loadingMore) Text("Loading the next page...", color = NewspaperCharcoal.copy(alpha = 0.68f))
        Spacer(Modifier.height(80.dp))
    }

    noteArticleForDialog?.let { article ->
        ArticleNoteDialog(
            article = article,
            onDismiss = { noteArticleForDialog = null },
            onSave = { title, content -> viewModel.saveNote(article, title, content) }
        )
    }
}
