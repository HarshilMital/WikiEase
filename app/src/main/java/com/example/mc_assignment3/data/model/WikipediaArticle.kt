package com.example.mc_assignment3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model representing a Wikipedia article.
 * This model is used both for the Room database (offline caching) and for displaying articles.
 */
@Entity(tableName = "articles")
data class WikipediaArticle(
    @PrimaryKey
    val pageid: Int,
    val title: String,
    val extract: String, // The plain text summary of the article
    val thumbnail: String? = null, // URL to the thumbnail image
    val fullUrl: String, // Full URL to the Wikipedia article
    val lastAccessed: Long = System.currentTimeMillis(), // For tracking recently viewed
    val isFavorite: Boolean = false,
    val aiSummary: String? = null, // AI-generated summary
    val relatedKeywords: List<String> = emptyList(), // For related content suggestions
    val coordinates: Coordinates? = null // Geo coordinates if available
)

/**
 * Coordinates data class for location-based articles
 */
data class Coordinates(
    val lat: Double,
    val lon: Double
)