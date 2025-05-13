package com.mandarinkafe.mandarin.features.search.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Label

interface GetLabelsUseCase {
    suspend fun execute(): List<Label>
}