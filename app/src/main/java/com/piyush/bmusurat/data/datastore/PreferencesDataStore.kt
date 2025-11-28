package com.piyush.bmusurat.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val IMAGE_URL_KEY = stringPreferencesKey("image_url")
        fun getProgramKey(shortName: String) = stringPreferencesKey("program_details_$shortName")
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

    suspend fun saveProgramDetails(shortName: String, jsonString: String) {
        val key = getProgramKey(shortName)
        dataStore.edit { preferences ->
            preferences[key] = jsonString
        }
    }

    suspend fun getProgramDetails(shortName: String): String? {
        val key = getProgramKey(shortName)
        return dataStore.data.map { preferences ->
            preferences[key]
        }.firstOrNull()
    }
}