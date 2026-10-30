package com.example.snaptask.data.repository

import com.example.snaptask.data.network.*

class NonDatedItemRepository(private val apiService: ApiService) {
    suspend fun getItems(page: Int = 1, category: String? = null): Resource<List<NonDatedItemDto>> =
        safeApiCall { apiService.getNonDatedItems(page = page, category = category) }

    suspend fun getItem(id: Long): Resource<NonDatedItemDto> = safeApiCall { apiService.getNonDatedItem(id) }

    suspend fun createItem(request: CreateNonDatedItemRequest): Resource<NonDatedItemDto> =
        safeApiCall { apiService.createNonDatedItem(request) }

    suspend fun updateItem(id: Long, request: CreateNonDatedItemRequest): Resource<NonDatedItemDto> =
        safeApiCall { apiService.updateNonDatedItem(id, request) }

    suspend fun deleteItem(id: Long): Resource<Unit> = safeApiCall { apiService.deleteNonDatedItem(id) }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean): Resource<NonDatedItemDto> = safeApiCall {
        if (isFavorite) apiService.unfavoriteNonDatedItem(id) else apiService.favoriteNonDatedItem(id)
    }
}

