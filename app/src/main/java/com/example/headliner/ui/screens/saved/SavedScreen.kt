package com.example.headliner.ui.screens.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.headliner.domain.model.Article
import com.example.headliner.ui.components.ArticleRow
import com.example.headliner.ui.components.EmptyState
import com.example.headliner.ui.components.shareArticle
import com.example.headliner.ui.theme.NewspaperCharcoal

@Composable
fun SavedScreen(
    onOpenArticle: (Article) -> Unit,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val saved by viewModel.articles.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Saved News")
        if (saved.isEmpty()) {
            EmptyState("No saved articles yet. Start saving articles you find interesting!")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(saved, key = { it.url }) { item ->
                    val article = item.toDomain()
                    ArticleRow(
                        article = article,
                        onOpen = onOpenArticle,
                        trailing = {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { context.shareArticle(article) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = NewspaperCharcoal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.delete(item.url) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = NewspaperCharcoal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
