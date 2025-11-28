package com.piyush.bmusurat.data.repository

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.piyush.bmusurat.util.Constant
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun signInWithGoogle(context: Context): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(Constant.WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        val uniqueId = extractSubFromJwt(idToken)

                        if (uniqueId != null) {
                            Result.success(uniqueId)
                        } else {
                            Result.failure(Exception("Failed to extract Unique ID from token"))
                        }
                    } else {
                        Result.failure(Exception("Unexpected credential type: ${credential.type}"))
                    }
                }
                else -> Result.failure(Exception("Unsupported credential type"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("AuthRepository", "Sign-in cancelled by user")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign-in failed", e)
            Result.failure(e)
        }
    }

    private fun extractSubFromJwt(jwt: String): String? {
        return try {
            val parts = jwt.split(".")
            if (parts.size < 2) return null

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)

            val jsonObject = JSONObject(payload)
            jsonObject.optString("sub")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error parsing JWT", e)
            null
        }
    }
}