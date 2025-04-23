package ru.hse.cardefectscan.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import coil3.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.cardefectscan.domain.usecase.RequestsUseCase
import ru.hse.cardefectscan.utils.PAGE_SIZE
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val requestsUseCase: RequestsUseCase,
    val imageLoader: ImageLoader,
) : CommonViewModel() {
    val pagerFlow = Pager(PagingConfig(
        pageSize = PAGE_SIZE,
        prefetchDistance = 5,
    )) {
        requestsUseCase.pagingSource
    }.flow.cachedIn(viewModelScope)
}