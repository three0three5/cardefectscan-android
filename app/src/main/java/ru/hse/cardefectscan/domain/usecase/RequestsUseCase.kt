package ru.hse.cardefectscan.domain.usecase

import ru.hse.cardefectscan.domain.repository.RequestsPagingSource

class RequestsUseCase(
    val pagingSource: RequestsPagingSource,
)