package com.piyush.bmusurat.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val IMAGE_URL_KEY = stringPreferencesKey("image_url")
    }

    suspend fun saveImageUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[IMAGE_URL_KEY] = url
        }
    }

    fun getImageUrl(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[IMAGE_URL_KEY] ?: ""
        }
    }
}