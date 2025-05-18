package com.example.mc_assignment3.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Models for the Wikipedia API responses.
 */

// Search response
data class WikipediaSearchResponse(
    val query: SearchQuery
)

data class SearchQuery(
    val search: List<SearchResult>
)

data class SearchResult(
    val pageid: Int,
    val title: String,
    val snippet: String
)

// Article content response
data class WikipediaArticleResponse(
    val query: ArticleQuery
)

data class ArticleQuery(
    val pages: Map<String, ArticlePage>
)

data class ArticlePage(
    val pageid: Int,
    val title: String,
    val extract: String,
    val fullurl: String,
    val thumbnail: ThumbnailInfo? = null,
    val coordinates: List<Coordinate>? = null
)

data class ThumbnailInfo(
    val source: String,
    val width: Int,
    val height: Int
)

data class Coordinate(
    val lat: Double,
    val lon: Double,
    val primary: String
)

// Nearby articles response
data class WikipediaNearbyResponse(
    val query: NearbyQuery
)

data class NearbyQuery(
    @SerializedName("geosearch")
    val geoSearch: List<NearbyArticle>
)

data class NearbyArticle(
    val pageid: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    val dist: Float // Distance in meters
)