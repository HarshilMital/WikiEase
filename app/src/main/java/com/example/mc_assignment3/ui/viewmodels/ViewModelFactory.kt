package com.example.mc_assignment3.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mc_assignment3.data.local.WikipediaDatabase
import com.example.mc_assignment3.data.remote.OpenAIClient
import com.example.mc_assignment3.data.repository.WikipediaRepository
import com.example.mc_assignment3.util.LocationService
import com.example.mc_assignment3.util.ThemeManager

/**
 * Factory class for creating ViewModels with dependencies.
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    // Create OpenAI Client with your API key
    private val openAIClient by lazy {
        // You should store the API key securely, this is just for demonstration
        // In a real app, use BuildConfig or a secure storage solution
        OpenAIClient("YOUR_OPENAI_API_KEY_HERE")
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