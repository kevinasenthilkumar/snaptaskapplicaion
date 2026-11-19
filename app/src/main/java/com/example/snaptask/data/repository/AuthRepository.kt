package com.example.snaptask.data.repository

import com.example.snaptask.data.local.AppDatabase
import com.example.snaptask.data.local.PreferencesManager
import com.example.snaptask.data.network.*
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService,
    private val prefsManager: PreferencesManager,
    private val database: AppDatabase,
) {
    suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data!!
                prefsManager.saveAuthData(authData.accessToken, authData.refreshToken, authData.user)
                Resource.Success(authData)
            } else {
                val errorMsg = response.body()?.message ?: parseErrorMessage(response)
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun register(name: String, email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = apiService.register(RegisterRequest(email, password, name))
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()!!.data!!
                prefsManager.saveAuthData(authData.accessToken, authData.refreshToken, authData.user)
                Resource.Success(authData)
            } else {
                val errorMsg = response.body()?.message ?: parseErrorMessage(response)
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorJson = response.errorBody()?.string()
            if (!errorJson.isNullOrBlank()) {
                val jsonObject = JsonParser.parseString(errorJson).asJsonObject
                if (jsonObject.has("errors") && jsonObject.get("errors").isJsonObject) {
                    val errorsObj = jsonObject.getAsJsonObject("errors")
                    val firstKey = errorsObj.keySet().firstOrNull()
                    if (firstKey != null) {
                        val element = errorsObj.get(firstKey)
                        if (element.isJsonArray && element.asJsonArray.size() > 0) {
                            return element.asJsonArray.get(0).asString
                        }
                    }
                }
                if (jsonObject.has("message")) {
                    return jsonObject.get("message").asString
                }
            }
            "Request failed (${response.code()})"
        } catch (_: Exception) {
            "Request failed (${response.code()})"
        }
    }

    /**
     * Complete Unified Logout:
     * 1. Calls backend logout/revoke endpoint (swallows network failures)
     * 2. Clears stored tokens and preferences from DataStore
     * 3. Clears Room database tables so user B never sees user A's screenshots
     */
    suspend fun logout(): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.logout()
            } catch (_: Exception) {
                // Ignore network failure when revoking token on server
            } finally {
                prefsManager.clearAuthData()
                try {
                    database.clearAllTables()
                } catch (_: Exception) {
                }
            }
            Resource.Success(Unit)
        }
    }

    suspend fun isLoggedIn(): Boolean = prefsManager.isLoggedIn.first()
    suspend fun isOnboardingDone(): Boolean = prefsManager.onboardingDone.first()
    suspend fun setOnboardingDone() = prefsManager.setOnboardingDone()
    suspend fun hasSeenIntro(): Boolean = prefsManager.introSeen.first()
    suspend fun setIntroSeen() = prefsManager.setIntroSeen()
    val introSeen: Flow<Boolean> = prefsManager.introSeen
}