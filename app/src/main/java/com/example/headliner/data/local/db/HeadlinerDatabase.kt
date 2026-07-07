package com.example.headliner.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SavedArticle::class, SearchHistory::class],
    version = 1,
    exportSchema = false
)
abstract class HeadlinerDatabase : RoomDatabase() {
    abstract fun dao(): HeadlinerDao
}
