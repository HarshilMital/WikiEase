package com.example.mc_assignment3.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for the Wikipedia API.
 */
interface WikipediaApiService {
    
    /**
     * Search for Wikipedia articles.
     */
    @GET("w/api.php")
    suspend fun searchArticles(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "search",
        @Query("srsearch") searchQuery: String,
        @Query("srlimit") limit: Int = 20,
        @Query("utf8") utf8: Int = 1
    ): WikipediaSearchResponse
    
    /**
     * Get detailed information about specific articles.
     */
    @GET("w/api.php")
    suspend fun getArticleDetails(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("prop") properties: String = "extracts|pageimages|coordinates|info",
        @Query("pageids") pageIds: String,
        @Query("explaintext") plainText: Int = 1,
        @Query("exsectionformat") sectionFormat: String = "plain",
        @Query("exintro") introOnly: Int = 0,
        @Query("piprop") imageProperties: String = "thumbnail",
        @Query("pithumbsize") thumbSize: Int = 300,
        @Query("inprop") infoProperties: String = "url",
        @Query("utf8") utf8: Int = 1
    ): WikipediaArticleResponse
    
    /**
     * Get articles near a location.
     */
    @GET("w/api.php")
    suspend fun getNearbyArticles(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "geosearch",
        @Query("gscoord") coordinates: String, // Format: "latitude|longitude"
        @Query("gsradius") radiusInMeters: Int = 10000,
        @Query("gslimit") limit: Int = 20,
        @Query("utf8") utf8: Int = 1
    ): WikipediaNearbyResponse
}