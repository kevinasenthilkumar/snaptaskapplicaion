package com.example.snaptask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snaptask.data.network.CreateReminderRequest
import com.example.snaptask.data.network.ReminderDto
import com.example.snaptask.data.network.Resource
import com.example.snaptask.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RemindersUiState(
    val isLoading: Boolean = false,
    val reminders: List<ReminderDto> = emptyList(),
    val error: String? = null,
    val actionSuccess: String? = null,
)

class RemindersViewModel(private val repository: ReminderRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init { loadReminders() }

    fun loadReminders(status: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val r = repository.getReminders(status = status)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, reminders = r.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun createReminder(request: CreateReminderRequest) {
        viewModelScope.launch {
            when (repository.createReminder(request)) {
                is Resource.Success -> { _uiState.value = _uiState.value.copy(actionSuccess = "Reminder set"); loadReminders() }
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Could not create reminder")
                is Resource.Loading -> Unit
            }
        }
    }

    fun completeReminder(id: Long) {
        viewModelScope.launch {
            when (repository.complete(id)) {
                is Resource.Success -> loadReminders()
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Could not complete reminder")
                is Resource.Loading -> Unit
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            when (repository.deleteReminder(id)) {
                is Resource.Success -> loadReminders()
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Delete failed")
                is Resource.Loading -> Unit
            }
        }
    }

    fun clearMessages() { _uiState.value = _uiState.value.copy(error = null, actionSuccess = null) }

    class Factory(private val repo: ReminderRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = RemindersViewModel(repo) as T
    }
}

