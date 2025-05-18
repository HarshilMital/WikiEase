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
import com.example.mc_assignment3.ui.navigation.AppNavigation
import com.example.mc_assignment3.ui.theme.MC_Assignment3Theme
import com.example.mc_assignment3.ui.viewmodels.SettingsViewModel
import com.example.mc_assignment3.ui.viewmodels.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
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
                }
            }
        }
    }
}