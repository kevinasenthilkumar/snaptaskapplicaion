# SnapTask Android Repository & ViewModel Code Examples

This document provides production Kotlin implementations for the Retrofit API service, DataStore TokenManager, Repositories, WorkManager Workers, Hilt modules, and ViewModel consumption.

---

## 1. Sealed Result Wrapper

```kotlin
package com.example.snaptask.core.util

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Error(val message: String, val code: String? = null) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}
```

---

## 2. Retrofit API Service (`ApiService.kt`)

```kotlin
package com.example.snaptask.data.remote

import com.example.snaptask.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<ApiResponse<AuthResponseDto>>

    @POST("auth/refresh")
    suspend fun refresh(@Body body: Map<String, String>): Response<ApiResponse<AuthResponseDto>>

    // Dashboard
    @GET("dashboard/summary")
    suspend fun getDashboardSummary(): Response<ApiResponse<DashboardSummaryDto>>

    // Screenshots
    @Multipart
    @POST("screenshots/upload")
    suspend fun uploadScreenshot(
        @Part file: MultipartBody.Part,
        @Part("local_media_id") localMediaId: RequestBody?,
        @Part("captured_at") capturedAt: RequestBody?,
    ): Response<ApiResponse<Map<String, ScreenshotDto>>>

    @POST("screenshots/{id}/analyze")
    suspend fun analyzeScreenshot(@Path("id") id: Long): Response<ApiResponse<Map<String, ScreenshotDto>>>

    // Dated Items
    @GET("dated-items")
    suspend fun getDatedItems(
        @Query("timeframe") timeframe: String? = null,
        @Query("sort") sort: String? = "date_asc",
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("dated-items/{id}/complete")
    suspend fun completeDatedItem(@Path("id") id: Long): Response<ApiResponse<Map<String, DatedItemDto>>>

    // Non-Dated Items
    @GET("non-dated-items")
    suspend fun getNonDatedItems(
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = "oldest",
    ): Response<ApiResponse<Map<String, Any>>>

    // Cleanup & Trash
    @GET("cleanup/recommendations")
    suspend fun getCleanupRecommendations(): Response<ApiResponse<Map<String, Any>>>

    @POST("cleanup/confirm")
    suspend fun confirmCleanup(@Body body: Map<String, Any>): Response<ApiResponse<Map<String, Any>>>

    @GET("trash")
    suspend fun getTrashItems(): Response<ApiResponse<Map<String, Any>>>

    @POST("trash/{id}/restore")
    suspend fun restoreTrashItem(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @POST("trash/restore-all")
    suspend fun restoreAllTrash(): Response<ApiResponse<Map<String, Int>>>
}
```

---

## 3. Token Manager (`TokenManager.kt`)

```kotlin
package com.example.snaptask.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "snaptask_auth_prefs")

class TokenManager(private val context: Context) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }

    suspend fun getAccessToken(): String? = context.dataStore.data.first()[ACCESS_TOKEN]

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = access
            prefs[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }
}
```

---

## 4. Auth Interceptor (`AuthInterceptor.kt`)

```kotlin
package com.example.snaptask.data.remote

import com.example.snaptask.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.getAccessToken() }
        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        requestBuilder.addHeader("Accept", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}
```

---

## 5. Dashboard Repository Implementation

```kotlin
package com.example.snaptask.data.repository

import com.example.snaptask.core.util.ResultWrapper
import com.example.snaptask.data.remote.ApiService
import com.example.snaptask.data.remote.dto.DashboardSummaryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

interface DashboardRepository {
    fun getSummary(): Flow<ResultWrapper<DashboardSummaryDto>>
}

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val api: ApiService
) : DashboardRepository {

    override fun getSummary(): Flow<ResultWrapper<DashboardSummaryDto>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val response = api.getDashboardSummary()
            if (response.isSuccessful && response.body()?.data != null) {
                emit(ResultWrapper.Success(response.body()!!.data!!))
            } else {
                emit(ResultWrapper.Error(response.body()?.message ?: "Failed to fetch summary"))
            }
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.localizedMessage ?: "Network error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
```

---

## 6. WorkManager Background Worker (`ScreenshotUploadWorker.kt`)

```kotlin
package com.example.snaptask.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.snaptask.data.remote.ApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@HiltWorker
class ScreenshotUploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val api: ApiService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uriString = inputData.getString("image_uri") ?: return Result.failure()
        val uri = Uri.parse(uriString)

        return try {
            val file = copyUriToTempFile(uri)
            val reqFile = file.asRequestBody("image/png".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, reqFile)
            val mediaId = uriString.toRequestBody("text/plain".toMediaTypeOrNull())

            val uploadRes = api.uploadScreenshot(body, mediaId, null)
            if (uploadRes.isSuccessful && uploadRes.body()?.data != null) {
                val screenshot = uploadRes.body()!!.data!!["screenshot"]
                if (screenshot != null) {
                    // Trigger asynchronous AI analysis
                    api.analyzeScreenshot(screenshot.id)
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun copyUriToTempFile(uri: Uri): File {
        val tempFile = File.createTempFile("snap_", ".png", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}
```

---

## 7. ViewModel & Compose UI State Consumption

```kotlin
package com.example.snaptask.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaptask.core.util.ResultWrapper
import com.example.snaptask.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeDashboardUiState(
    val isLoading: Boolean = false,
    val totalScreenshots: Int = 0,
    val cleanableCount: Int = 0,
    val reclaimableMb: Double = 0.0,
    val overdueRemindersCount: Int = 0,
    val upcomingRemindersCount: Int = 0,
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeDashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeDashboardUiState())
    val uiState: StateFlow<HomeDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            dashboardRepository.getSummary().collect { result ->
                when (result) {
                    is ResultWrapper.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is ResultWrapper.Success -> _uiState.update {
                        val d = result.data
                        it.copy(
                            isLoading = false,
                            totalScreenshots = d.totalScreenshots,
                            cleanableCount = d.cleanableCount,
                            reclaimableMb = d.reclaimableMb,
                            overdueRemindersCount = d.overdueRemindersCount,
                            upcomingRemindersCount = d.upcomingRemindersCount,
                            errorMessage = null
                        )
                    }
                    is ResultWrapper.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}
```

### Compose Screen Consumption:
```kotlin
@Composable
fun HomeDashboardRoute(
    viewModel: HomeDashboardViewModel = hiltViewModel(),
    onTakeAction: () -> Unit,
    onOpenDated: () -> Unit,
    onOpenNonDated: () -> Unit,
    onOpenReminders: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeDashboardScreen(
        onTakeAction = onTakeAction,
        onOpenDated = onOpenDated,
        onOpenNonDated = onOpenNonDated,
        onOpenReminders = onOpenReminders,
        onTabSelected = onTabSelected,
        // Pass dynamic state from ViewModel:
        // cleanableCount = state.cleanableCount,
        // reclaimableMb = state.reclaimableMb,
    )
}
```

