package com.example.snaptask.data.repository

import com.example.snaptask.data.local.PreferencesManager
import com.example.snaptask.data.network.*
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.first
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService,
    private val prefsManager: PreferencesManager,
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
                if (jsonObject.has("message")) {
                    return jsonObject.get("message").asString
                }
            }
            "Request failed (${response.code()})"
        } catch (_: Exception) {
            "Request failed (${response.code()})"
        }
    }

    suspend fun logout(): Resource<Unit> {
        return try {
            apiService.logout()
            prefsManager.clearAuthData()
            Resource.Success(Unit)
        } catch (e: Exception) {
            prefsManager.clearAuthData()
            Resource.Success(Unit)
        }
    }

    suspend fun isLoggedIn(): Boolean = prefsManager.isLoggedIn.first()
    suspend fun isOnboardingDone(): Boolean = prefsManager.onboardingDone.first()
    suspend fun setOnboardingDone() = prefsManager.setOnboardingDone()
}

