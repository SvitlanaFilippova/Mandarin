package com.mandarinkafe.mandarin.core.domain.api

import kotlinx.coroutines.flow.Flow

interface ObserveCartCountUseCase {
    operator fun invoke(): Flow<Int>
}