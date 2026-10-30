package com.example.snaptask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snaptask.data.network.DatedItemDto
import com.example.snaptask.data.network.Resource
import com.example.snaptask.data.network.ScreenshotStatsDto
import com.example.snaptask.data.repository.DatedItemRepository
import com.example.snaptask.data.repository.NotificationRepository
import com.example.snaptask.data.repository.ScreenshotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val stats: ScreenshotStatsDto? = null,
    val upcomingItems: List<DatedItemDto> = emptyList(),
    val unreadNotificationCount: Int = 0,
    val error: String? = null,
)

class HomeViewModel(
    private val screenshotRepository: ScreenshotRepository,
    private val datedItemRepository: DatedItemRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadDashboard() }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val r = screenshotRepository.getStats()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(stats = r.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = r.message)
                is Resource.Loading -> Unit
            }
            when (val r = datedItemRepository.getUpcoming()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(upcomingItems = r.data)
                else -> Unit
            }
            when (val r = notificationRepository.getUnreadCount()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(unreadNotificationCount = r.data.unreadCount)
                else -> Unit
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    class Factory(
        private val screenshotRepo: ScreenshotRepository,
        private val datedRepo: DatedItemRepository,
        private val notifRepo: NotificationRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(screenshotRepo, datedRepo, notifRepo) as T
    }
}

