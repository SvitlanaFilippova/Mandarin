package com.mandarinkafe.mandarin.search.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Label

interface LabelsRepository {
    suspend fun getLabels(): List<Label>
}