package com.example.snaptask.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snaptask.data.ai.LocalClassifier
import com.example.snaptask.data.ai.LocalOcrProcessor
import com.example.snaptask.data.local.LocalScreenshotEntity
import com.example.snaptask.data.repository.ScreenshotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScanUiState(
    val isScanning: Boolean = false,
    val isComplete: Boolean = false,
    val totalCount: Int = 0,
    val scannedCount: Int = 0,
    val uploadedCount: Int = 0,
    val failedCount: Int = 0,
    val remainingCount: Int = 0,
    val progress: Float = 0f,
    val statusText: String = "",
    val recentScreenshots: List<LocalScreenshotEntity> = emptyList(),
    val errorMessage: String? = null,
)

class ScreenshotViewModel(
    private val repository: ScreenshotRepository,
    private val ocrProcessor: LocalOcrProcessor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    val allScreenshots: StateFlow<List<LocalScreenshotEntity>> = repository.allScreenshots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = allScreenshots.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.refreshFromDb()
        }
        repository.registerContentObserver {
            viewModelScope.launch {
                repository.queryAndSyncScreenshots()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterContentObserver()
    }

    fun startScanning() {
        viewModelScope.launch {
            _uiState.value = ScanUiState(isScanning = true, statusText = "Scanning gallery for screenshots...")
            try {
                val foundList = repository.queryAndSyncScreenshots()
                val total = foundList.size

                if (total == 0) {
                    _uiState.value = ScanUiState(
                        isScanning = false,
                        isComplete = true,
                        totalCount = 0,
                        scannedCount = 0,
                        uploadedCount = 0,
                        failedCount = 0,
                        remainingCount = 0,
                        progress = 1f,
                        statusText = "No screenshots found.",
                    )
                    return@launch
                }

                var uploaded = 0
                var failed = 0

                for ((index, item) in foundList.withIndex()) {
                    val remaining = total - (index + 1)
                    _uiState.value = _uiState.value.copy(
                        isScanning = true,
                        totalCount = total,
                        scannedCount = index + 1,
                        uploadedCount = uploaded,
                        failedCount = failed,
                        remainingCount = remaining,
                        progress = (index + 1).toFloat() / total.toFloat(),
                        statusText = "Processing & uploading ${index + 1} of $total...",
                        recentScreenshots = foundList.take(index + 1),
                    )

                    var currentEntity = item
                    if (item.ocrStatus != "DONE") {
                        val ocrRes = ocrProcessor.processImageUri(Uri.parse(item.contentUri))
                        val classification = LocalClassifier.classify(ocrRes.fullText, item.displayName)

                        currentEntity = item.copy(
                            ocrStatus = if (ocrRes.success) "DONE" else "FAILED",
                            ocrText = ocrRes.fullText,
                            category = classification.category,
                            confidence = classification.confidence,
                            displayName = classification.title ?: item.displayName,
                        )
                        repository.database.insertOrUpdate(currentEntity)
                    }

                    // Upload to backend
                    val uploadResult = repository.uploadScreenshotFile(currentEntity)
                    if (uploadResult != null) {
                        uploaded++
                    } else {
                        failed++
                    }

                    _uiState.value = _uiState.value.copy(
                        uploadedCount = uploaded,
                        failedCount = failed,
                        remainingCount = remaining,
                    )
                }

                repository.refreshFromDb()
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    isComplete = true,
                    totalCount = total,
                    scannedCount = total,
                    uploadedCount = uploaded,
                    failedCount = failed,
                    remainingCount = 0,
                    progress = 1f,
                    statusText = "Completed: $uploaded uploaded, $failed failed",
                )
            } catch (e: Exception) {
                _uiState.value = ScanUiState(
                    isScanning = false,
                    isComplete = true,
                    errorMessage = e.message,
                )
            }
        }
    }

    class Factory(
        private val repository: ScreenshotRepository,
        private val ocrProcessor: LocalOcrProcessor,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScreenshotViewModel(repository, ocrProcessor) as T
        }
    }
}
