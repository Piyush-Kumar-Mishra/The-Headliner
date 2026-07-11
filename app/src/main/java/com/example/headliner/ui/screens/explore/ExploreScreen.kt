package com.example.headliner.ui.screens.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.headliner.domain.model.Article
import com.example.headliner.ui.components.LoadingLines
import com.example.headliner.ui.components.SwipeableNewsStack
import com.example.headliner.ui.components.ArticleNoteDialog
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.headliner.ui.theme.NewspaperCharcoal
import com.example.headliner.ui.theme.NewspaperCream
import java.time.format.DateTimeFormatter

@Composable
fun ExploreScreen(
    onOpenArticle: (Article) -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var noteArticleForDialog by remember { mutableStateOf<Article?>(null) }
    LaunchedEffect(state.news.error) {
        state.news.error?.let { snackbarHostState.showSnackbar(it) }
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = viewModel::previousMonth) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = NewspaperCharcoal
                )
            }
            Text(
                text = state.month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NewspaperCharcoal,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = viewModel::nextMonth) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Month",
                    tint = NewspaperCharcoal
                )
            }
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items((1..state.month.lengthOfMonth()).toList()) { day ->
                val date = state.month.atDay(day)
                val selected = date == state.selectedDate
                val dayOfWeekStr = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()).uppercase()
                
                Card(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (selected) Color.Black else NewspaperCharcoal.copy(alpha = 0.15f)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) NewspaperCharcoal else Color.White
                    ),
                    modifier = Modifier
                        .width(54.dp)
                        .clickable { viewModel.selectDate(date) }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = dayOfWeekStr,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = if (selected) NewspaperCream.copy(alpha = 0.7f) else NewspaperCharcoal.copy(alpha = 0.6f)
                        )
                        Text(
                            text = day.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selected) NewspaperCream else NewspaperCharcoal
                        )
                    }
                }
            }
        }
        if (state.news.loading && state.news.articles.isEmpty()) {
            LoadingLines()
        } else {
            SwipeableNewsStack(
                articles = state.news.articles,
                savedUrls = state.news.savedUrls,
                onRead = onOpenArticle,
                onSaveToggle = viewModel::toggleSaved,
                onNearEnd = viewModel::loadMore,
                onNoteClick = { noteArticleForDialog = it },
                isExplore = true,
                modifier = Modifier.weight(1f),
                onArticleVisible = viewModel::markAsRead
            )
        }
        Spacer(Modifier.height(70.dp))
    }

    noteArticleForDialog?.let { article ->
        ArticleNoteDialog(
            article = article,
            onDismiss = { noteArticleForDialog = null },
            onSave = { title, content -> viewModel.saveNote(article, title, content) }
        )
    }
}
