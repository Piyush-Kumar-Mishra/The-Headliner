package com.example.headliner.ui.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.headliner.ui.theme.NewspaperCharcoal
import com.example.headliner.ui.theme.NewspaperCream
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.headliner.domain.model.Article
import com.example.headliner.ui.components.ArticleRow
import com.example.headliner.ui.components.EmptyState
import com.example.headliner.ui.components.LoadingLines

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenArticle: (Article) -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NewspaperCharcoal,
                    modifier = Modifier.size(20.dp)
                )
            }
            BasicTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(51.dp)
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = NewspaperCharcoal),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color.Black, shape = CircleShape)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = NewspaperCharcoal.copy(alpha = 0.54f),
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (state.query.isEmpty()) {
                                Text(
                                    text = "Search headlines...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NewspaperCharcoal.copy(alpha = 0.54f)
                                )
                            }
                            innerTextField()
                        }
                        if (state.query.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear text",
                                tint = NewspaperCharcoal.copy(alpha = 0.54f),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.onQueryChange("") }
                            )
                        }
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when {
                state.query.isBlank() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            state.history.forEach { item ->
                                Card(
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, NewspaperCharcoal.copy(alpha = 0.25f)),
                                    colors = CardDefaults.cardColors(containerColor = NewspaperCream),
                                    modifier = Modifier.clickable {
                                        viewModel.onQueryChange(item.query)
                                        viewModel.submit(item.query)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = item.query,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = NewspaperCharcoal
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete",
                                            tint = NewspaperCharcoal.copy(alpha = 0.54f),
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { viewModel.deleteHistory(item.query) }
                                        )
                                    }
                                }
                            }
                        }
                        if (state.history.isNotEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                TextButton(onClick = viewModel::clearHistory) {
                                    Text("Clear All", color = NewspaperCharcoal.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
                state.loading -> LoadingLines()
                state.results.isEmpty() -> EmptyState("No articles found for '${state.query}'")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.results, key = { it.url }) { article ->
                        ArticleRow(article, onOpenArticle)
                    }
                }
            }
        }
    }
}
