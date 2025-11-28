package com.piyush.bmusurat.data.repository

import com.piyush.bmusurat.data.models.ProgramResponse

interface ProgramRepository {
    suspend fun getProgramDetails(shortName: String): Result<ProgramResponse>
}