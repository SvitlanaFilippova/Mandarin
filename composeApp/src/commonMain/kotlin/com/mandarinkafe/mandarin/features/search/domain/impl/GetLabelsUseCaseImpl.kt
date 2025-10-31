package com.mandarinkafe.mandarin.features.search.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.features.search.domain.api.GetLabelsUseCase
import com.mandarinkafe.mandarin.features.search.domain.api.LabelsRepository

class GetLabelsUseCaseImpl(private val repository: LabelsRepository) : GetLabelsUseCase {
    override suspend fun invoke(): List<Label> {
        return repository.getLabels()
    }
}