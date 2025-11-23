package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface CancelOrderUseCase {
    suspend operator fun invoke(
        id: String,
        cancelCauseId: String? = null,
        cancelComment: String? = null,
    ): Resource<Unit>
}