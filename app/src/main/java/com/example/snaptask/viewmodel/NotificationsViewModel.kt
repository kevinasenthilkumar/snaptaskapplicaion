package com.example.snaptask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snaptask.data.network.NotificationDto
import com.example.snaptask.data.network.Resource
import com.example.snaptask.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null,
)

class NotificationsViewModel(private val repository: NotificationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init { loadNotifications() }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val r = repository.getNotifications()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    notifications = r.data,
                    unreadCount = r.data.count { !it.isRead },
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun markRead(id: Long) {
        viewModelScope.launch { repository.markRead(id); loadNotifications() }
    }

    fun markAllRead() {
        viewModelScope.launch { repository.markAllRead(); loadNotifications() }
    }

    class Factory(private val repo: NotificationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = NotificationsViewModel(repo) as T
    }
}

