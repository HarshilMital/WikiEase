package com.example.mc_assignment3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mc_assignment3.BuildConfig
import com.example.mc_assignment3.ui.navigation.AppNavigation
import com.example.mc_assignment3.ui.theme.MC_Assignment3Theme
import com.example.mc_assignment3.ui.viewmodels.SettingsViewModel
import com.example.mc_assignment3.ui.viewmodels.ViewModelFactory
import com.example.mc_assignment3.util.ApiKeyManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize API keys securely
        initializeApiKeys()
        
        setContent {
            // Get theme preference from SettingsViewModel
            val settingsViewModel: SettingsViewModel = viewModel(factory = ViewModelFactory(this))
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            
            MC_Assignment3Theme(
                darkTheme = isDarkMode,
                // Use dynamic colors only when not in dark mode (optional)
                dynamicColor = !isDarkMode && isSystemInDarkTheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }            }
        }
    }
    
    /**
     * Initialize API keys securely
     * The key priority order is:
     * 1. Environment variables
     * 2. BuildConfig (from local.properties)
     * 3. Default (empty key which will cause API calls to fail)
     */
    private fun initializeApiKeys() {
        // Get API key from environment variable first
        val envApiKey = System.getenv("OPENAI_API_KEY")
        
        // If not available, use the one from BuildConfig (local.properties)
        val apiKey = envApiKey ?: BuildConfig.OPENAI_API_KEY
        
        // Store the key securely in the app's private storage
        ApiKeyManager.initialize(applicationContext, apiKey)
    }
}