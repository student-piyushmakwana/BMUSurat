package com.piyush.bmusurat.data.repository

import android.util.Log
import com.google.gson.Gson
import com.piyush.bmusurat.data.ApiService
import com.piyush.bmusurat.data.datastore.PreferencesDataStore
import com.piyush.bmusurat.data.models.ProgramResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProgramRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferencesDataStore: PreferencesDataStore
) : ProgramRepository {

    private val gson = Gson()

    override suspend fun getProgramDetails(shortName: String): Result<ProgramResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProgramDetails(shortName)

                if (response.success) {
                    try {
                        val jsonString = gson.toJson(response)
                        preferencesDataStore.saveProgramDetails(shortName, jsonString)
                    } catch (e: Exception) {
                        Log.e("ProgramRepo", "Failed to cache program details", e)
                    }
                    Result.success(response)
                } else {
                    loadFromCache(shortName, Exception("API returned success=false"))
                }
            } catch (networkException: Exception) {
                Log.e("ProgramRepo", "Network error, attempting cache", networkException)
                loadFromCache(shortName, networkException)
            }
        }
    }

    private suspend fun loadFromCache(shortName: String, originalException: Exception): Result<ProgramResponse> {
        val cachedJson = preferencesDataStore.getProgramDetails(shortName)
        return if (!cachedJson.isNullOrBlank()) {
            try {
                val cachedResponse = gson.fromJson(cachedJson, ProgramResponse::class.java)
                Result.success(cachedResponse)
            } catch (parsingException: Exception) {
                Log.e("ProgramRepo", "Failed to parse cached data", parsingException)
                Result.failure(originalException)
            }
        } else {
            Result.failure(originalException)
        }
    }
}