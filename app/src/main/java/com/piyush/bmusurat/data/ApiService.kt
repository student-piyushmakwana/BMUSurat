package com.piyush.bmusurat.data

import com.piyush.bmusurat.data.models.HomeResponse
import retrofit2.http.GET

interface ApiService {
    @GET("api/bmu")
    suspend fun getBmuData(): HomeResponse
}