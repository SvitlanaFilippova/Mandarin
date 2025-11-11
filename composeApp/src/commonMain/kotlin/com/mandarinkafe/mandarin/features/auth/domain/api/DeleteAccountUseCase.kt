package com.mandarinkafe.mandarin.features.auth.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface DeleteAccountUseCase {
    suspend operator fun invoke(): Resource<Boolean>
}

