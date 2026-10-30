package com.example.snaptask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snaptask.data.network.*
import com.example.snaptask.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val settings: UserSettingsDto? = null,
    val automationSettings: AutomationSettingsDto? = null,
    val cleanupRules: List<CleanupRuleDto> = emptyList(),
    val error: String? = null,
    val saveSuccess: Boolean = false,
)

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val r = repository.getSettings()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(settings = r.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = r.message)
                is Resource.Loading -> Unit
            }
            when (val r = repository.getAutomationSettings()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(automationSettings = r.data)
                else -> Unit
            }
            when (val r = repository.getCleanupRules()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(cleanupRules = r.data)
                else -> Unit
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun saveSettings(settings: UserSettingsDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val r = repository.updateSettings(settings)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, settings = r.data, saveSuccess = true)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun saveAutomationSettings(settings: AutomationSettingsDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val r = repository.updateAutomationSettings(settings)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, automationSettings = r.data, saveSuccess = true)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun addCleanupRule(request: CreateCleanupRuleRequest) {
        viewModelScope.launch {
            when (repository.createCleanupRule(request)) {
                is Resource.Success -> loadAll()
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Could not add rule")
                is Resource.Loading -> Unit
            }
        }
    }

    fun deleteCleanupRule(id: Long) {
        viewModelScope.launch {
            when (repository.deleteCleanupRule(id)) {
                is Resource.Success -> loadAll()
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Could not delete rule")
                is Resource.Loading -> Unit
            }
        }
    }

    fun clearMessages() { _uiState.value = _uiState.value.copy(error = null, saveSuccess = false) }

    class Factory(private val repo: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(repo) as T
    }
}

