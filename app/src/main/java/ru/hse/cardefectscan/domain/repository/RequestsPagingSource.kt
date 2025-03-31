package ru.hse.cardefectscan.domain.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.hse.generated.apis.RequestsApi
import ru.hse.generated.models.ImageRequestElement

class RequestsPagingSource(
    private val requestsApi: RequestsApi,
) : PagingSource<Int, ImageRequestElement>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageRequestElement> {
        val nextPageNumber = params.key ?: 0
        return try {
            Log.d("RequestsPagingSource", "Loading page $nextPageNumber")
            val response = withContext(Dispatchers.IO) {
                requestsApi.apiV1RequestsGet(nextPageNumber, params.loadSize)
            }
            val nextKey = if (response.currentPage < response.totalPages - 1)
                response.currentPage + 1 else null
            LoadResult.Page(
                data = response.content,
                prevKey = null,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            Log.e("RequestsPagingSource", "Error loading page", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageRequestElement>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}