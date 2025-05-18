package com.example.mc_assignment3.data.local

import androidx.room.*
import com.example.mc_assignment3.data.model.WikipediaArticle
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Wikipedia articles table.
 */
@Dao
interface WikipediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: WikipediaArticle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<WikipediaArticle>)

    @Query("SELECT * FROM articles WHERE pageid = :id")
    suspend fun getArticleById(id: Int): WikipediaArticle?

    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%'")
    fun searchArticlesByTitle(query: String): Flow<List<WikipediaArticle>>

    @Query("SELECT * FROM articles ORDER BY lastAccessed DESC LIMIT :limit")
    fun getRecentArticles(limit: Int = 10): Flow<List<WikipediaArticle>>

    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteArticles(): Flow<List<WikipediaArticle>>

    // New method to directly get favorite articles as a list (not Flow)
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY title ASC")
    suspend fun getFavoriteArticlesList(): List<WikipediaArticle>

    @Query("UPDATE articles SET isFavorite = :isFavorite WHERE pageid = :articleId")
    suspend fun updateFavoriteStatus(articleId: Int, isFavorite: Boolean)

    @Query("UPDATE articles SET aiSummary = :summary WHERE pageid = :articleId")
    suspend fun updateAiSummary(articleId: Int, summary: String)

    @Query("DELETE FROM articles WHERE pageid = :articleId")
    suspend fun deleteArticle(articleId: Int)

    @Query("SELECT * FROM articles WHERE coordinates IS NOT NULL")
    fun getArticlesWithLocation(): Flow<List<WikipediaArticle>>

    @Query("SELECT * FROM articles WHERE lastAccessed < :timestamp AND isFavorite = 0")
    suspend fun getOldArticles(timestamp: Long): List<WikipediaArticle>

    @Delete
    suspend fun deleteArticles(articles: List<WikipediaArticle>)
}