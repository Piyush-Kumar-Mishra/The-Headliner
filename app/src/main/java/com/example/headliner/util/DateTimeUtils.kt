package com.example.headliner.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toRelativeTime(): String {
    val instant = runCatching { Instant.parse(this) }.getOrNull() ?: return ""
    val duration = Duration.between(instant, Instant.now())
    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} min ago"
        duration.toHours() < 24 -> "${duration.toHours()} hours ago"
        duration.toDays() == 1L -> "1 day ago"
        duration.toDays() < 14 -> "${duration.toDays()} days ago"
        duration.toDays() < 60 -> "${duration.toDays() / 7} weeks ago"
        else -> DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
            .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
    }
}

fun LocalDate.toApiStart(): String = "${this}T00:00:00Z"

fun LocalDate.toApiEnd(): String = "${this}T23:59:59Z"

fun Long.toSavedDate(): String = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    .format(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate())
