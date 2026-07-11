package com.example.headliner.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HeadlinerDao {
    @Query("SELECT * FROM saved_articles ORDER BY savedAt DESC")
    fun getAllSavedArticles(): Flow<List<SavedArticle>>

    @Query("SELECT url FROM saved_articles")
    fun getSavedArticleUrls(): Flow<List<String>>

    @Query("SELECT * FROM saved_articles WHERE url = :url LIMIT 1")
    fun getSavedArticle(url: String): Flow<SavedArticle?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: SavedArticle)

    @Query("DELETE FROM saved_articles WHERE url = :url")
    suspend fun deleteArticle(url: String)

    @Query("DELETE FROM saved_articles")
    suspend fun deleteAllSavedArticles()

    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT 10")
    fun getSearchHistory(): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(query: SearchHistory)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearch(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("DELETE FROM search_history WHERE query NOT IN (SELECT query FROM search_history ORDER BY searchedAt DESC LIMIT 10)")
    suspend fun trimSearchHistory()

    @Query("SELECT * FROM article_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<ArticleNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ArticleNoteEntity)

    @Query("DELETE FROM article_notes WHERE id = :id")
    suspend fun deleteNote(id: Int)


    @Query("SELECT articleId FROM viewed_articles")
    fun getViewedArticleIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewedArticle(article: ViewedArticle)

    @Query("DELETE FROM viewed_articles WHERE articleId NOT IN (SELECT articleId FROM viewed_articles ORDER BY viewedAt DESC LIMIT 100)")
    suspend fun trimViewedArticles()

    @Query("DELETE FROM viewed_articles")
    suspend fun clearViewedArticles()
}
