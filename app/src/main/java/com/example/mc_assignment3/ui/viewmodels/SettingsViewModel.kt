package com.example.mc_assignment3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mc_assignment3.util.ThemeManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing app settings, including theme preferences.
 */
class SettingsViewModel(private val themeManager: ThemeManager) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = themeManager.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    /**
     * Set the dark mode preference
     * @param isDark True for dark mode, false for light mode
     */
    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themeManager.setDarkMode(isDark)
        }
    }
    
    /**
     * Toggle between light and dark mode
     */
    fun toggleDarkMode() {
        viewModelScope.launch {
            themeManager.toggleDarkMode()
        }
    }
}