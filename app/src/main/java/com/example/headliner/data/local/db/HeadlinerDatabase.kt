package com.example.headliner.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SavedArticle::class, SearchHistory::class, ArticleNoteEntity::class, ViewedArticle::class],
    version = 3,
    exportSchema = false
)
abstract class HeadlinerDatabase : RoomDatabase() {
    abstract fun dao(): HeadlinerDao
}
