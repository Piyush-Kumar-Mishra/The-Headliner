package com.example.headliner.ui.screens.detail

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.headliner.ui.theme.NewspaperCharcoal
import com.example.headliner.ui.theme.NewspaperCream
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    onBack: () -> Unit,
    viewModel: ArticleDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val saved by viewModel.saved.collectAsStateWithLifecycle()
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var webView: WebView? by remember { mutableStateOf(null) }

    LaunchedEffect(viewModel.hasDecodeError, viewModel.url) {
        if (viewModel.hasDecodeError || viewModel.url.isBlank() || !viewModel.url.startsWith("http")) {
            Toast.makeText(context, "not supported content", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    BackHandler {
        if (webView?.canGoBack() == true) webView?.goBack() else onBack()
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(NewspaperCream)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = viewModel.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = NewspaperCharcoal
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${viewModel.title}\n${viewModel.url}")
                            },
                            "Share article"
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = NewspaperCharcoal
                    )
                }
                IconButton(onClick = viewModel::toggleSaved) {
                    Icon(
                        imageVector = if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = NewspaperCharcoal
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NewspaperCream,
                titleContentColor = NewspaperCharcoal
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NewspaperCharcoal.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = error.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = NewspaperCharcoal
                    )
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { error = null; loading = true; webView?.reload() }) {
                        Text("Retry", color = NewspaperCharcoal)
                    }
                }
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webView = this
                            settings.javaScriptEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    loading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    loading = false
                                }


                                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, webError: WebResourceError?) {
                                    if (request?.isForMainFrame == true) {
                                        loading = false
                                        error = "Unable to load this article. Please try again."
                                    }
                                }
                            }
                            loadUrl(viewModel.url)
                        }
                    }
                )
                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NewspaperCream),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NewspaperCharcoal)
                    }
                }
            }
        }
    }
}
