package com.example.snaptask.data.repository

import com.example.snaptask.data.network.*

class DatedItemRepository(private val apiService: ApiService) {
    suspend fun getDatedItems(page: Int = 1, status: String? = null, category: String? = null): Resource<List<DatedItemDto>> =
        safeApiCall { apiService.getDatedItems(page = page, status = status, category = category) }

    suspend fun getDatedItem(id: Long): Resource<DatedItemDto> = safeApiCall { apiService.getDatedItem(id) }

    suspend fun createDatedItem(request: CreateDatedItemRequest): Resource<DatedItemDto> =
        safeApiCall { apiService.createDatedItem(request) }

    suspend fun updateDatedItem(id: Long, request: CreateDatedItemRequest): Resource<DatedItemDto> =
        safeApiCall { apiService.updateDatedItem(id, request) }

    suspend fun deleteDatedItem(id: Long): Resource<Unit> = safeApiCall { apiService.deleteDatedItem(id) }

    suspend fun getUpcoming(): Resource<List<DatedItemDto>> = safeApiCall { apiService.getUpcomingDatedItems() }

    suspend fun getOverdue(): Resource<List<DatedItemDto>> = safeApiCall { apiService.getOverdueDatedItems() }

    suspend fun complete(id: Long): Resource<DatedItemDto> = safeApiCall { apiService.completeDatedItem(id) }

    suspend fun snooze(id: Long, until: String): Resource<DatedItemDto> =
        safeApiCall { apiService.snoozeDatedItem(id, SnoozeDatedItemRequest(until)) }
}

