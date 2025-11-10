package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface RevokeSessionUseCase {
    suspend operator fun invoke(sessionId: String): Resource<Boolean>
}





