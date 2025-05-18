package com.example.mc_assignment3.data.repository

import android.util.Log
import com.example.mc_assignment3.data.local.WikipediaDao
import com.example.mc_assignment3.data.model.Coordinates
import com.example.mc_assignment3.data.model.WikipediaArticle
import com.example.mc_assignment3.data.remote.OpenAIClient
import com.example.mc_assignment3.data.remote.WikipediaApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Repository class that coordinates between local database (offline cache) and remote API.
 */
class WikipediaRepository(
    private val wikipediaDao: WikipediaDao,
    private val openAIClient: OpenAIClient
) {
    private val apiService = WikipediaApiClient.apiService

    /**
     * Search for Wikipedia articles by query.
     * @param query The search query
     * @return List of matching articles
     */
    suspend fun searchArticles(query: String): List<WikipediaArticle> = withContext(Dispatchers.IO) {
        try {
            // First check if we have cached results
            val cachedArticles = wikipediaDao.searchArticlesByTitle(query).flowOn(Dispatchers.IO).first()
            if (cachedArticles.isNotEmpty()) {
                return@withContext cachedArticles
            }

            // If not found locally, fetch from API
            val searchResponse = apiService.searchArticles(searchQuery = query)

            // Convert search results to page IDs for detail fetch
            val pageIds = searchResponse.query.search.map { it.pageid.toString() }.joinToString("|")

            if (pageIds.isNotEmpty()) {
                val articleDetails = apiService.getArticleDetails(pageIds = pageIds)

                // Map API response to our data model and save to database
                val articles = articleDetails.query.pages.map { (_, page) ->
                    WikipediaArticle(
                        pageid = page.pageid,
                        title = page.title,
                        extract = page.extract,
                        thumbnail = page.thumbnail?.source,
                        fullUrl = page.fullurl,
                        coordinates = page.coordinates?.firstOrNull()?.let {
                            Coordinates(it.lat, it.lon)
                        }
                    )
                }

                // Cache results in local database
                wikipediaDao.insertArticles(articles)

                return@withContext articles
            }

            emptyList()
        } catch (e: Exception) {
            Log.e("WikipediaRepository", "Error searching articles", e)
            emptyList()
        }
    }

    /**
     * Get a specific article by ID, either from cache or API.
     */
    suspend fun getArticle(articleId: Int): WikipediaArticle? = withContext(Dispatchers.IO) {
        try {
            // Check local database first
            val localArticle = wikipediaDao.getArticleById(articleId)

            if (localArticle == null) {
                // Fetch from API if not found locally
                val articleDetails = apiService.getArticleDetails(pageIds = articleId.toString())

                val fetchedArticle = articleDetails.query.pages[articleId.toString()]?.let { page ->
                    WikipediaArticle(
                        pageid = page.pageid,
                        title = page.title,
                        extract = page.extract,
                        thumbnail = page.thumbnail?.source,
                        fullUrl = page.fullurl,
                        coordinates = page.coordinates?.firstOrNull()?.let {
                            Coordinates(it.lat, it.lon)
                        }
                    )
                }

                // Cache in local database
                if (fetchedArticle != null) {
                    wikipediaDao.insertArticle(fetchedArticle)
                }

                return@withContext fetchedArticle
            } else {
                // If found locally, update the last accessed time
                val updatedArticle = localArticle.copy(lastAccessed = System.currentTimeMillis())
                wikipediaDao.insertArticle(updatedArticle)
                return@withContext updatedArticle
            }
        } catch (e: Exception) {
            Log.e("WikipediaRepository", "Error getting article", e)
            null
        }
    }

    /**
     * Get recently viewed articles.
     */
    fun getRecentArticles(limit: Int = 10): Flow<List<WikipediaArticle>> {
        return wikipediaDao.getRecentArticles(limit).flowOn(Dispatchers.IO)
    }

    /**
     * Get favorite articles as Flow
     */
    fun getFavoriteArticles(): Flow<List<WikipediaArticle>> {
        return wikipediaDao.getFavoriteArticles().flowOn(Dispatchers.IO)
    }

    /**
     * Get favorite articles as direct List (not Flow)
     */
    suspend fun getFavoriteArticlesList(): List<WikipediaArticle> = withContext(Dispatchers.IO) {
        return@withContext wikipediaDao.getFavoriteArticlesList()
    }

    /**
     * Toggle favorite status for an article.
     */
    suspend fun toggleFavorite(articleId: Int, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        wikipediaDao.updateFavoriteStatus(articleId, isFavorite)
    }

    /**
     * Generate an AI summary for an article.
     */
    suspend fun generateAiSummary(articleId: Int) = withContext(Dispatchers.IO) {
        try {
            val article = wikipediaDao.getArticleById(articleId) ?: return@withContext

            if (article.aiSummary == null) {
                val summary = openAIClient.generateSummary(article.extract)
                wikipediaDao.updateAiSummary(articleId, summary)
            }
        } catch (e: Exception) {
            Log.e("WikipediaRepository", "Error generating AI summary", e)
        }
    }

    /**
     * Get articles near a location.
     */
    suspend fun getNearbyArticles(latitude: Double, longitude: Double): List<WikipediaArticle> = withContext(Dispatchers.IO) {
        try {
            val coordinates = "$latitude|$longitude"
            // Add logging for debugging
            Log.d("WikipediaRepository", "Fetching nearby articles for coordinates: $coordinates")

            val nearbyResponse = apiService.getNearbyArticles(coordinates = coordinates)

            if (nearbyResponse.query.geoSearch.isEmpty()) {
                Log.w("WikipediaRepository", "No nearby articles found for coordinates: $coordinates")
                return@withContext emptyList()
            }

            val pageIds = nearbyResponse.query.geoSearch.map { it.pageid.toString() }.joinToString("|")
            Log.d("WikipediaRepository", "Found ${nearbyResponse.query.geoSearch.size} nearby articles")

            if (pageIds.isNotEmpty()) {
                val articleDetails = apiService.getArticleDetails(pageIds = pageIds)

                val articles = articleDetails.query.pages.map { (_, page) ->
                    WikipediaArticle(
                        pageid = page.pageid,
                        title = page.title,
                        extract = page.extract,
                        thumbnail = page.thumbnail?.source,
                        fullUrl = page.fullurl,
                        coordinates = page.coordinates?.firstOrNull()?.let {
                            Coordinates(it.lat, it.lon)
                        }
                    )
                }

                // Cache results
                wikipediaDao.insertArticles(articles)

                return@withContext articles
            }

            emptyList()
        } catch (e: Exception) {
            Log.e("WikipediaRepository", "Error getting nearby articles", e)
            // Return empty list instead of throwing exception
            emptyList()
        }
    }

    /**
     * Sync favorite articles by refreshing their content from the API.
     * @return The number of articles successfully synced
     */
    suspend fun syncFavoriteArticles(): Int = withContext(Dispatchers.IO) {
        try {
            // Get all favorite articles
            val favoriteArticles = wikipediaDao.getFavoriteArticles().first()
            if (favoriteArticles.isEmpty()) {
                return@withContext 0
            }

            var syncedCount = 0

            // Update each favorite article
            favoriteArticles.forEach { article ->
                try {
                    // Fetch the latest version from the API
                    val articleDetails = apiService.getArticleDetails(pageIds = article.pageid.toString())

                    articleDetails.query.pages[article.pageid.toString()]?.let { page ->
                        // Create updated article with latest content but preserve favorite status
                        val updatedArticle = WikipediaArticle(
                            pageid = page.pageid,
                            title = page.title,
                            extract = page.extract,
                            thumbnail = page.thumbnail?.source,
                            fullUrl = page.fullurl,
                            lastAccessed = System.currentTimeMillis(),
                            isFavorite = true, // Maintain favorite status
                            aiSummary = article.aiSummary, // Preserve existing AI summary
                            coordinates = page.coordinates?.firstOrNull()?.let {
                                Coordinates(it.lat, it.lon)
                            }
                        )

                        // Update in database
                        wikipediaDao.insertArticle(updatedArticle)
                        syncedCount++

                        // Also update AI summary if needed
                        if (updatedArticle.aiSummary == null) {
                            generateAiSummary(article.pageid)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WikipediaRepository", "Error syncing article ${article.pageid}", e)
                    // Continue with next article if one fails
                }
            }

            return@withContext syncedCount
        } catch (e: Exception) {
            Log.e("WikipediaRepository", "Error syncing favorite articles", e)
            return@withContext 0
        }
    }

    /**
     * Clean up old cached articles that haven't been accessed recently.
     * This helps manage the cache size.
     */
    suspend fun cleanupOldArticles() = withContext(Dispatchers.IO) {
        try {
            // Delete articles older than 30 days that aren't favorites
            val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
            val oldArticles = wikipediaDao.getOldArticles(thirtyDaysAgo)
            if (oldArticles.isNotEmpty()) {
                wikipediaDao.deleteArticles(oldArticles)
            }
            else {
            }
        } catch (e: Exception) {
            Log.e("WikipediaRepository", "Error cleaning up old articles", e)
        }
    }
}