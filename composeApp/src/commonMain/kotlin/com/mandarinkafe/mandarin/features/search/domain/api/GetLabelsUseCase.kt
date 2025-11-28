package com.mandarinkafe.mandarin.features.search.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Label

interface GetLabelsUseCase {
    suspend operator fun invoke(): List<Label>
}