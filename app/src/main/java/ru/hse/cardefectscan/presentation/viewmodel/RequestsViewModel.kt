package ru.hse.cardefectscan.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.hse.cardefectscan.domain.usecase.RequestsUseCase
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val requestsUseCase: RequestsUseCase,
) : CommonViewModel() {

}