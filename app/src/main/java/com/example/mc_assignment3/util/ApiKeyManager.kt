package com.example.mc_assignment3.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.Properties

/**
 * Utility class to securely manage API keys
 */
object ApiKeyManager {
    
    private const val API_PROPERTIES_FILE = "api.properties"
    private const val TAG = "ApiKeyManager"
    
    /**
     * Initialize the API keys for the application.
     * This method should be called during application startup.
     * 
     * @param context The application context
     * @param openAiKey The OpenAI API key (from environment variables or CI/CD)
     */
    fun initialize(context: Context, openAiKey: String? = null) {
        try {
            // Create or update the properties file
            val properties = Properties()
            val propertiesFile = File(context.applicationContext.filesDir, API_PROPERTIES_FILE)
            
            // Try to load existing properties
            if (propertiesFile.exists()) {
                propertiesFile.inputStream().use { stream ->
                    properties.load(stream)
                }
            }
            
            // Set the OpenAI API key if provided
            if (!openAiKey.isNullOrBlank()) {
                properties.setProperty("OPENAI_API_KEY", openAiKey)
            }
            
            // Save the properties file
            FileOutputStream(propertiesFile).use { stream ->
                properties.store(stream, "API Keys - DO NOT COMMIT")
            }
            
            Log.d(TAG, "API keys initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing API keys", e)
        }
    }
    
    /**
     * Get the OpenAI API key
     * 
     * @param context The application context
     * @return The OpenAI API key or null if not found
     */
    fun getOpenAiApiKey(context: Context): String? {
        try {
            val properties = Properties()
            val propertiesFile = File(context.applicationContext.filesDir, API_PROPERTIES_FILE)
            
            if (propertiesFile.exists()) {
                propertiesFile.inputStream().use { stream ->
                    properties.load(stream)
                }
                
                return properties.getProperty("OPENAI_API_KEY")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting OpenAI API key", e)
        }
        
        return null
    }
}
