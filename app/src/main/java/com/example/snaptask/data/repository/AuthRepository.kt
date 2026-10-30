package com.example.snaptask.data.repository

import com.example.snaptask.data.local.PreferencesManager
import com.example.snaptask.data.network.*
import kotlinx.coroutines.flow.first

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
                Resource.Error(response.body()?.message ?: "Login failed")
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
                Resource.Error(response.body()?.message ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
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

