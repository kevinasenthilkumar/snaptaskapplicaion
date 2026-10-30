package com.example.snaptask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snaptask.data.network.CreateDatedItemRequest
import com.example.snaptask.data.network.DatedItemDto
import com.example.snaptask.data.network.Resource
import com.example.snaptask.data.repository.DatedItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DatedItemsUiState(
    val isLoading: Boolean = false,
    val items: List<DatedItemDto> = emptyList(),
    val selectedItem: DatedItemDto? = null,
    val error: String? = null,
    val actionSuccess: String? = null,
)

class DatedItemsViewModel(private val repository: DatedItemRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DatedItemsUiState())
    val uiState: StateFlow<DatedItemsUiState> = _uiState.asStateFlow()

    init { loadItems() }

    fun loadItems(status: String? = null, category: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val r = repository.getDatedItems(status = status, category = category)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, items = r.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun selectItem(id: Long) {
        viewModelScope.launch {
            when (val r = repository.getDatedItem(id)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(selectedItem = r.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun completeItem(id: Long) {
        viewModelScope.launch {
            when (repository.complete(id)) {
                is Resource.Success -> { _uiState.value = _uiState.value.copy(actionSuccess = "Marked complete"); loadItems() }
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Could not mark complete")
                is Resource.Loading -> Unit
            }
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            when (repository.deleteDatedItem(id)) {
                is Resource.Success -> { _uiState.value = _uiState.value.copy(actionSuccess = "Deleted"); loadItems() }
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = "Delete failed")
                is Resource.Loading -> Unit
            }
        }
    }

    fun createItem(request: CreateDatedItemRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val r = repository.createDatedItem(request)) {
                is Resource.Success -> { _uiState.value = _uiState.value.copy(isLoading = false, actionSuccess = "Item created"); loadItems() }
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = r.message)
                is Resource.Loading -> Unit
            }
        }
    }

    fun clearMessages() { _uiState.value = _uiState.value.copy(error = null, actionSuccess = null) }

    class Factory(private val repo: DatedItemRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DatedItemsViewModel(repo) as T
    }
}

