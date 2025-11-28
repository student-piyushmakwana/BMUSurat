package com.piyush.bmusurat.data.repository

import android.content.Context


interface AuthRepository {
    suspend fun signInWithGoogle(context: Context): Result<String>
}