package com.example.headliner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NewspaperColorScheme = lightColorScheme(
    primary = NewspaperCharcoal,
    onPrimary = NewspaperCream,
    secondary = NewspaperCharcoal.copy(alpha = 0.72f),
    onSecondary = NewspaperCream,
    background = NewspaperCream,
    onBackground = NewspaperCharcoal,
    surface = NewspaperCream,
    onSurface = NewspaperCharcoal,
    surfaceVariant = NewspaperCream,
    onSurfaceVariant = NewspaperCharcoal.copy(alpha = 0.72f),
    outline = NewspaperCharcoal.copy(alpha = 0.45f),
    error = NewspaperCharcoal,
    onError = NewspaperCream
)

@Composable
fun HeadlinerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NewspaperColorScheme,
        typography = Typography,
        content = content
    )
}
