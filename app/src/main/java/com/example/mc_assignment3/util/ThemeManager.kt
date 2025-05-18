package com.example.mc_assignment3.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manager for handling theme (light/dark mode) preferences.
 */
class ThemeManager(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }
    
    /**
     * Get the current theme mode preference as a Flow.
     */
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: false
        }
    
    /**
     * Update the theme mode preference.
     */
    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }
    
    /**
     * Toggle between light and dark mode.
     */
    suspend fun toggleDarkMode() {
        context.dataStore.edit { preferences ->
            val current = preferences[IS_DARK_MODE] ?: false
            preferences[IS_DARK_MODE] = !current
        }
    }
}