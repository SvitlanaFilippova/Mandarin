package com.mandarinkafe.mandarin.features.payment.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface CancelPaymentUseCase {
    suspend operator fun invoke(orderId: String): Resource<Boolean>
}

