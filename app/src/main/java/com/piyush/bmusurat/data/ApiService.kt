package com.piyush.bmusurat.data

import com.piyush.bmusurat.data.models.HomeResponse
import com.piyush.bmusurat.data.models.ProgramResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/bmu")
    suspend fun getBmuData(): HomeResponse

    @POST("api/bmu/programs/{shortName}")
    suspend fun getProgramDetails(
        @Path("shortName") shortName: String
    ): ProgramResponse
}