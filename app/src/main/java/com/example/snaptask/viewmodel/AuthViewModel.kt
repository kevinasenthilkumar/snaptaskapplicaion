package com.example.snaptask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snaptask.data.network.Resource
import com.example.snaptask.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isOnboardingDone: Boolean = false,
    val error: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init { checkSession() }

    private fun checkSession() {
        viewModelScope.launch {
            val loggedIn = authRepository.isLoggedIn()
            val onboardingDone = authRepository.isOnboardingDone()
            _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn, isOnboardingDone = onboardingDone)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false, isLoggedIn = true,
                    userName = result.data.user.name, userEmail = result.data.user.email,
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.register(name, email, password)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false, isLoggedIn = true,
                    userName = result.data.user.name, userEmail = result.data.user.email,
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState(isLoggedIn = false)
        }
    }

    fun setOnboardingDone() {
        viewModelScope.launch {
            authRepository.setOnboardingDone()
            _uiState.value = _uiState.value.copy(isOnboardingDone = true)
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(repository) as T
    }
}

