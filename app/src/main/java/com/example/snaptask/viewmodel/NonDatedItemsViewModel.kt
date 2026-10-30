package com.example.snaptask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snaptask.data.network.NonDatedItemDto
import com.example.snaptask.data.network.Resource
import com.example.snaptask.data.repository.NonDatedItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NonDatedItemsUiState(
    val isLoading: Boolean = false,
    val items: List<NonDatedItemDto> = emptyList(),
    val selectedItem: NonDatedItemDto? = null,
    val error: String? = null,
    val actionSuccess: String? = null,
)

class NonDatedItemsViewModel(private val repository: NonDatedItemRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NonDatedItemsUiState())
    val uiState: StateFlow<NonDatedItemsUiState> = _uiState.asStateFlow()

    init { loadItems() }

    fun loadItems(category: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val r = repository.getItems(category = category)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, items = r.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun selectItem(id: Long) {
        viewModelScope.launch {
            when (val r = repository.getItem(id)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(selectedItem = r.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun toggleFavorite(item: NonDatedItemDto) {
        viewModelScope.launch {
            when (repository.toggleFavorite(item.id, item.isFavorite)) {
                is Resource.Success -> loadItems()
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Could not update favourite")
                is Resource.Loading -> Unit
            }
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            when (repository.deleteItem(id)) {
                is Resource.Success -> { _uiState.value = _uiState.value.copy(actionSuccess = "Deleted"); loadItems() }
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Delete failed")
                is Resource.Loading -> Unit
            }
        }
    }

    fun clearMessages() { _uiState.value = _uiState.value.copy(error = null, actionSuccess = null) }

    class Factory(private val repo: NonDatedItemRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = NonDatedItemsViewModel(repo) as T
    }
}

