package com.example.androsubmis2.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferenceManager(context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val Context.dataStore by preferencesDataStore(name = "settings")
    }

    private val dataStore = context.dataStore

    // Save dark mode preference
    suspend fun setDarkModeEnabled(isDarkModeEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkModeEnabled
        }
    }

    // Get dark mode preference
    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }
}
