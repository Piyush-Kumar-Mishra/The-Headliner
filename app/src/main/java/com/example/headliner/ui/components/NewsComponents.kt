package com.example.headliner.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.example.headliner.domain.model.Article
import com.example.headliner.ui.theme.NewspaperCharcoal
import com.example.headliner.ui.theme.NewspaperCream
import com.example.headliner.util.toRelativeTime
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
fun Masthead(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "The Headliner",
            style = MaterialTheme.typography.displayLarge,
            color = NewspaperCharcoal,
            maxLines = 1
        )
        Text(
            text = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
            style = MaterialTheme.typography.bodyMedium,
            color = NewspaperCharcoal.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun ThinRule(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(NewspaperCharcoal.copy(alpha = 0.45f))
    )
}

@Composable
fun SearchPrompt(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = NewspaperCharcoal
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = NewspaperCharcoal.copy(alpha = 0.54f),
                modifier = Modifier.size(18.dp)
            )
            Text("Search headlines...", color = NewspaperCharcoal.copy(alpha = 0.54f))
        }
    }
}

@Composable
fun CategoryChips(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val isSelected = category == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelected(category) },
                label = { Text(category.replaceFirstChar { it.titlecase() }) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = NewspaperCream,
                    labelColor = NewspaperCharcoal,
                    selectedContainerColor = NewspaperCharcoal,
                    selectedLabelColor = NewspaperCream
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = NewspaperCharcoal,
                    selectedBorderColor = NewspaperCharcoal
                )
            )
        }
    }
}

@Composable
fun SlideIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "slideTransition")
    val slideOffset by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "slideOffset"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "slideAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = null,
            tint = NewspaperCharcoal.copy(alpha = alpha),
            modifier = Modifier
                .size(22.dp)
                .offset(y = slideOffset.dp)
        )
    }
}

@Composable
fun HeadlineBannerShimmer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val alpha by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.75f)
            .background(NewspaperCharcoal.copy(alpha = alpha), RoundedCornerShape(4.dp))
    )
}

@Composable
fun HeadlineBanner(
    articles: List<Article>,
    onOpen: (Article) -> Unit,
    modifier: Modifier = Modifier,
    onArticleVisible: (Article) -> Unit = {}
) {
    if (articles.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { articles.size })
    LaunchedEffect(pagerState.currentPage, articles) {
        if (articles.isNotEmpty()) {
            val article = articles.getOrNull(pagerState.currentPage)
            if (article != null) {
                onArticleVisible(article)
            }
        }
    }
    val context = LocalContext.current
    LaunchedEffect(articles) {
        if (articles.isNotEmpty()) {
            pagerState.scrollToPage(0)
            articles.forEach { a ->
                a.imageUrl?.let { url ->
                    if (url.isNotBlank()) {
                        val request = ImageRequest.Builder(context)
                            .data(url)
                            .build()
                        context.imageLoader.enqueue(request)
                    }
                }
            }
        }
        while (true) {
            delay(4500)
            if (!pagerState.isScrollInProgress && pagerState.pageCount > 0) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.75f)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val article = articles[page]
            Card(
                onClick = { onOpen(article) },
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, NewspaperCharcoal.copy(alpha = 0.35f)),
                colors = CardDefaults.cardColors(containerColor = NewspaperCharcoal)
            ) {
                Box(Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = article.imageUrl,
                        contentDescription = article.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        NewspaperCharcoal.copy(alpha = 0.08f),
                                        NewspaperCharcoal.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )
                    Column(
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 32.dp)
                    ) {
                        Text(
                            text = article.title,
                            color = NewspaperCream,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = 14.dp)
        ) {
            articles.forEachIndexed { index, _ ->
                Box(
                    Modifier
                        .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                        .background(NewspaperCream.copy(alpha = if (index == pagerState.currentPage) 1f else 0.45f), CircleShape)
                )
            }
        }
    }
}

@Composable
fun SwipeableNewsStack(
    articles: List<Article>,
    savedUrls: Set<String>,
    onRead: (Article) -> Unit,
    onSaveToggle: (Article) -> Unit,
    onNearEnd: () -> Unit,
    onNoteClick: (Article) -> Unit,
    modifier: Modifier = Modifier,
    isExplore: Boolean = false,
    onArticleVisible: (Article) -> Unit = {}
) {
    if (articles.isEmpty()) {
        EmptyState("No articles found", modifier)
        return
    }
    var index by rememberSaveable(articles.firstOrNull()?.url) { mutableIntStateOf(0) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(offsetY, label = "cardOffset")
    val article = articles.getOrNull(index) ?: articles.last()
    val context = LocalContext.current

    LaunchedEffect(article.id) {
        onArticleVisible(article)
    }

    LaunchedEffect(index, articles) {
        val nextArticles = articles.subList(
            (index + 1).coerceAtMost(articles.size),
            (index + 11).coerceAtMost(articles.size)
        )
        nextArticles.forEach { a ->
            a.imageUrl?.let { url ->
                if (url.isNotBlank()) {
                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .build()
                    context.imageLoader.enqueue(request)
                }
            }
        }
    }

    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        val visualModifier = Modifier
            .offset { IntOffset(0, animatedOffset.roundToInt()) }
            .graphicsLayer {
                alpha = 1f - ((-animatedOffset).coerceAtLeast(0f) / 900f)
            }
        val dragModifier = Modifier
            .pointerInput(index, articles.size) {
                detectDragGestures(
                    onDragEnd = {
                        if (offsetY < -120f && index < articles.lastIndex) {
                            index += 1
                            if (articles.size - index <= 5) onNearEnd()
                        }
                        offsetY = 0f
                    },
                    onDragCancel = { offsetY = 0f }
                ) { change, dragAmount ->
                    change.consume()
                    offsetY = (offsetY + dragAmount.y).coerceIn(-360f, 80f)
                }
            }

        NewsArticleCard(
            article = article,
            isSaved = savedUrls.contains(article.url),
            onRead = { onRead(article) },
            onSaveToggle = { onSaveToggle(article) },
            onShare = { context.shareArticle(article) },
            onNoteClick = { onNoteClick(article) },
            isExplore = isExplore,
            modifier = visualModifier,
            dragModifier = dragModifier
        )
    }
}

@Composable
fun NewsArticleCard(
    article: Article,
    isSaved: Boolean,
    onRead: () -> Unit,
    onSaveToggle: () -> Unit,
    onShare: () -> Unit,
    onNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier,
    isExplore: Boolean = false
) {
    var isContentExpanded by remember(article.url) { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(isContentExpanded) {
        if (!isContentExpanded) {
            scrollState.scrollTo(0)
        }
    }

    Card(
        modifier = modifier
            .then(if (isExplore && isContentExpanded) Modifier else dragModifier)
            .fillMaxWidth()
            .then(
                if (isExplore && isContentExpanded) Modifier.fillMaxHeight() else Modifier.wrapContentHeight()
            ),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, NewspaperCharcoal.copy(alpha = 0.45f)),
        colors = CardDefaults.cardColors(containerColor = NewspaperCream)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
            ) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NewspaperCharcoal.copy(alpha = 0.12f)),
                    contentScale = ContentScale.Crop
                )
                if (isExplore) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        NewspaperCharcoal.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Text(
                        text = article.title,
                        color = NewspaperCream,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                            .basicMarquee(iterations = Int.MAX_VALUE, velocity = 90.dp)
                    )
                }
            }

            val scrollModifier = if (isExplore && isContentExpanded) {
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            } else {
                Modifier.fillMaxWidth()
            }

            Column(modifier = scrollModifier) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .then(if (isExplore && isContentExpanded) dragModifier else Modifier)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NewspaperCream)
                        .padding(16.dp)
                ) {
                    if (!isExplore) {
                        Text(article.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${article.sourceName}  |  ${article.publishedAt.toRelativeTime()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NewspaperCharcoal.copy(alpha = 0.68f)
                            )
                            if (!isExplore) {
                                Spacer(Modifier.width(8.dp))
                                IconButton(
                                    onClick = onNoteClick,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.EditNote,
                                        contentDescription = "Add Note",
                                        tint = NewspaperCharcoal,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                        if (isExplore) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = onRead,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Launch,
                                        contentDescription = "Read",
                                        tint = NewspaperCharcoal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onSaveToggle,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Save",
                                        tint = NewspaperCharcoal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onShare,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = NewspaperCharcoal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onNoteClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.EditNote,
                                        contentDescription = "Add Note",
                                        tint = NewspaperCharcoal,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    if (isExplore) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = article.description.orEmpty(),
                                style = MaterialTheme.typography.titleLarge,
                                color = NewspaperCharcoal,
                                maxLines = if (isContentExpanded) Int.MAX_VALUE else 3,
                                overflow = if (isContentExpanded) TextOverflow.Clip else TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = article.content.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = NewspaperCharcoal.copy(alpha = 0.85f),
                                maxLines = if (isContentExpanded) Int.MAX_VALUE else 5,
                                overflow = if (isContentExpanded) TextOverflow.Clip else TextOverflow.Ellipsis
                            )
                        }
                        if (!article.content.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                TextButton(
                                    onClick = { isContentExpanded = !isContentExpanded },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = if (isContentExpanded) "Show Less" else "Load More...",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = NewspaperCharcoal
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            article.description ?: article.content.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (!isExplore) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = onRead) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Launch,
                                    contentDescription = "Read",
                                    modifier = Modifier.size(16.dp),
                                    tint = NewspaperCharcoal
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Read", color = NewspaperCharcoal)
                            }
                            TextButton(onClick = onSaveToggle) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Save",
                                    modifier = Modifier.size(16.dp),
                                    tint = NewspaperCharcoal
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(if (isSaved) "Unsave" else "Save", color = NewspaperCharcoal)
                            }
                            TextButton(onClick = onShare) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(16.dp),
                                    tint = NewspaperCharcoal
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Share", color = NewspaperCharcoal)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ArticleRow(
    article: Article,
    onOpen: (Article) -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    Card(
        onClick = { onOpen(article) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, NewspaperCharcoal.copy(alpha = 0.35f)),
        colors = CardDefaults.cardColors(containerColor = NewspaperCream)
    ) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                modifier = Modifier
                    .size(82.dp)
                    .background(NewspaperCharcoal.copy(alpha = 0.12f), RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.weight(1f)) {
                Text(article.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(article.sourceName, style = MaterialTheme.typography.bodyMedium, color = NewspaperCharcoal.copy(alpha = 0.7f))
                Text(article.publishedAt.toRelativeTime(), style = MaterialTheme.typography.bodyMedium, color = NewspaperCharcoal.copy(alpha = 0.7f))
            }
            trailing?.invoke()
        }
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) {
        Text(message, color = NewspaperCharcoal.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun LoadingLines(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(4) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(if (it == 0) 190.dp else 28.dp)
                    .background(NewspaperCharcoal.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
            )
        }
    }
}

fun Context.shareArticle(article: Article) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "${article.title}\n${article.url}")
    }
    startActivity(Intent.createChooser(intent, "Share article"))
}

@Composable
fun ArticleNoteDialog(
    article: Article? = null,
    dialogTitle: String = "Add Note",
    initialTitle: String = "",
    initialContent: String = "",
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var noteTitle by remember { mutableStateOf(initialTitle) }
    var noteContent by remember { mutableStateOf(initialContent) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = NewspaperCream),
            border = BorderStroke(1.dp, NewspaperCharcoal.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = NewspaperCharcoal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                article?.let { art ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White)
                            .border(BorderStroke(1.dp, NewspaperCharcoal.copy(alpha = 0.08f)), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!art.imageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = art.imageUrl,
                                contentDescription = art.title,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NewspaperCharcoal.copy(alpha = 0.05f)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = art.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = NewspaperCharcoal,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "${art.sourceName} • ${art.publishedAt.toRelativeTime()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = NewspaperCharcoal.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    androidx.compose.material3.OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Title", fontFamily = FontFamily.Serif) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NewspaperCharcoal,
                            unfocusedBorderColor = NewspaperCharcoal.copy(alpha = 0.2f),
                            cursorColor = NewspaperCharcoal
                        )
                    )

                    androidx.compose.material3.OutlinedTextField(
                        value = noteContent,
                        onValueChange = { if (it.length <= 500) noteContent = it },
                        label = { Text("Note content", fontFamily = FontFamily.Serif) },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(8.dp),
                        supportingText = {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                Text(
                                    text = "${noteContent.length} / 500",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = NewspaperCharcoal.copy(alpha = 0.5f),
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NewspaperCharcoal,
                            unfocusedBorderColor = NewspaperCharcoal.copy(alpha = 0.2f),
                            cursorColor = NewspaperCharcoal
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            color = NewspaperCharcoal.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                                onSave(noteTitle, noteContent)
                                onDismiss()
                            }
                        },
                        enabled = noteTitle.isNotBlank() && noteContent.isNotBlank()
                    ) {
                        Text(
                            text = "Save",
                            color = if (noteTitle.isNotBlank() && noteContent.isNotBlank()) NewspaperCharcoal else NewspaperCharcoal.copy(alpha = 0.25f),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }
        }
    }
}
