package com.mandarinkafe.mandarin.features.infrastructure.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype.PaymentTypesResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.paymenttype.toDomain
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.PaymentTypesRepository
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class PaymentTypesRepositoryImpl(private val networkClient: IikoNetworkClient) :
    PaymentTypesRepository {
    override suspend fun getPaymentTypes(): Resource<List<PaymentType>> {
        val response = networkClient.getPaymentTypes()
        return when (response.resultCode) {
            NO_CONNECTION -> Resource.ErrorNoInternet()
            HTTP_SUCCESS -> {
                val iikoTypes = (response as PaymentTypesResponse).paymentTypes
                val domainTypes = iikoTypes.filterNot { it.isDeleted }.map { it.toDomain() }
                Resource.Success(domainTypes)
            }

            else -> Resource.ErrorOther("Не удалось получить от сервера доступные способы оплаты")
        }
    }
}