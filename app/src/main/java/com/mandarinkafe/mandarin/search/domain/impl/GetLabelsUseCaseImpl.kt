package com.mandarinkafe.mandarin.search.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.search.domain.api.LabelsRepository
import com.mandarinkafe.mandarin.search.domain.usecase.GetLabelsUseCase

class GetLabelsUseCaseImpl(private val repository: LabelsRepository) : GetLabelsUseCase {
    override suspend fun execute(): List<Label> {
        return repository.getLabels()
    }
}