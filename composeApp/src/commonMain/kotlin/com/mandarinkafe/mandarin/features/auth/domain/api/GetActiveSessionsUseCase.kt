package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.features.auth.domain.models.ActiveSession
import com.mandarinkafe.mandarin.util.Resource

interface GetActiveSessionsUseCase {
    suspend operator fun invoke(): Resource<List<ActiveSession>>
}






