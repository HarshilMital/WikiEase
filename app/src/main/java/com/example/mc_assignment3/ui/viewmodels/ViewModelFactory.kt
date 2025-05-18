package com.example.mc_assignment3.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mc_assignment3.data.local.WikipediaDatabase
import com.example.mc_assignment3.data.remote.OpenAIClient
import com.example.mc_assignment3.data.repository.WikipediaRepository
import com.example.mc_assignment3.util.ApiKeyManager
import com.example.mc_assignment3.util.LocationService
import com.example.mc_assignment3.util.ThemeManager

/**
 * Factory class for creating ViewModels with dependencies.
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {    // Create OpenAI Client with API key from environment variable or properties
    private val openAIClient by lazy {
        // Get API key from secure source (environment variable or properties file)
        val apiKey = getSecureApiKey()
        OpenAIClient(apiKey)
    }
    
    /**
     * Gets the API key from a secure source (system property, environment variable, etc.)
     * This prevents hardcoded API keys from being committed to Git.
     */    private fun getSecureApiKey(): String {
        // Get the API key from our secure manager
        val apiKey = ApiKeyManager.getOpenAiApiKey(context)
        
        // Try to get from system environment variable as a fallback
        val envApiKey = System.getenv("OPENAI_API_KEY")
        if (apiKey.isNullOrBlank() && !envApiKey.isNullOrBlank()) {
            return envApiKey
        }
        
        // Return the API key or an empty string as a last resort
        return apiKey ?: ""
    }
    
    // Create Wikipedia DAO
    private val wikipediaDao by lazy {
        WikipediaDatabase.getDatabase(context).wikipediaDao()
    }
    
    // Create repository
    private val wikipediaRepository by lazy {
        WikipediaRepository(wikipediaDao, openAIClient)
    }
    
    // Create location service
    private val locationService by lazy {
        LocationService(context)
    }
    
    // Create theme manager
    private val themeManager by lazy {
        ThemeManager(context)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(wikipediaRepository, locationService) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(themeManager) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}