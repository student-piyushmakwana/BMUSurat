package com.piyush.bmusurat.data.repository

import android.util.Log
import com.piyush.bmusurat.data.ApiService
import com.piyush.bmusurat.data.datastore.PreferencesDataStore
import com.piyush.bmusurat.data.local.HomeDao
import com.piyush.bmusurat.data.local.toDto
import com.piyush.bmusurat.data.local.toEntity
import com.piyush.bmusurat.data.models.DataModel
import com.piyush.bmusurat.data.models.HomeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeRepositoryImpl  @Inject constructor(
    private val apiService: ApiService,
    private val dao: HomeDao,
    private val preferencesDataStore: PreferencesDataStore
) : HomeRepository {

    private val TAG = "HomeRepositoryImpl"

    override suspend fun getHomeData(): HomeData {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching BMU data from API...")
                val response = apiService.getBmuData()
                if (response.success) {
                    Log.d(TAG, "Data fetched successfully from API. Clearing cache and saving new data.")
                    dao.clearLatestNews()
                    dao.clearUpcomingEvents()
                    dao.clearStudentTestimonials()

                    dao.insertLatestNews(response.data.latestNews.map { it.toEntity() })
                    dao.insertUpcomingEvents(response.data.upcomingEvents.map { it.toEntity() })
                    dao.insertStudentTestimonials(response.data.studentTestimonials.map { it.toEntity() })

                    preferencesDataStore.saveImageUrl(response.imageUrl)

                    Result.success(response)
                } else {
                    Log.w(TAG, "API request was not successful (success=false). Attempting to load from cache.")
                    loadDataFromCache(Exception("API request was not successful"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network or parsing error fetching data: ${e.message}. Attempting to load from cache.", e)
                loadDataFromCache(e)
            }
        }
    }

    private suspend fun loadDataFromCache(originalException: Exception): HomeData {
        try {
            Log.d(TAG, "Loading data from cache...")
            val cachedNews = dao.getLatestNews().map { it.toDto() }
            val cachedEvents = dao.getUpcomingEvents().map { it.toDto() }
            val cachedTestimonials = dao.getStudentTestimonials().map { it.toDto() }
            val cachedImageUrl = preferencesDataStore.getImageUrl().firstOrNull() ?: ""

            if (cachedNews.isEmpty() && cachedEvents.isEmpty() && cachedTestimonials.isEmpty()) {
                Log.w(TAG, "Cache is empty. Returning original network failure.")
                return Result.failure(originalException)
            }

            Log.d(TAG, "Successfully loaded data from cache.")
            val cachedDataDto = DataModel(
                latestNews = cachedNews,
                studentTestimonials = cachedTestimonials,
                upcomingEvents = cachedEvents
            )
            val cachedResponse = HomeResponse(
                data = cachedDataDto,
                imageUrl = cachedImageUrl,
                success = true
            )
            return Result.success(cachedResponse)

        } catch (cacheException: Exception) {
            Log.e(TAG, "Error reading from cache: ${cacheException.message}", cacheException)
            return Result.failure(originalException)
        }
    }
}