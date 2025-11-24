package com.piyush.bmusurat.data.repository

import com.piyush.bmusurat.data.models.HomeResponse

typealias HomeData = Result<HomeResponse>

interface HomeRepository {
    suspend fun getHomeData(): HomeData
}