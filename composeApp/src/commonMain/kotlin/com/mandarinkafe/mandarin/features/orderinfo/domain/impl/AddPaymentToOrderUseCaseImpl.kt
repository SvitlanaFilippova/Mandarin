package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.features.infrastructure.domain.api.GetPaymentTypesUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.AddPaymentToOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE
import com.mandarinkafe.mandarin.util.Resource

class AddPaymentToOrderUseCaseImpl(
    private val changeOrderRepository: ChangeOrderRepository,
    private val getPaymentTypesUseCase: GetPaymentTypesUseCase,
) : AddPaymentToOrderUseCase {
    override suspend fun invoke(orderId: String, amount: Double): Resource<Unit> {
        // Получаем список типов оплаты и ищем онлайн-оплату по коду
        val paymentTypesResult = getPaymentTypesUseCase()
        if (paymentTypesResult !is Resource.Success) {
            return when (paymentTypesResult) {
                is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
                else -> Resource.ErrorOther("Не удалось получить типы оплаты")
            }
        }

        val paymentTypes = paymentTypesResult.data ?: emptyList()
        val onlinePaymentType = paymentTypes.firstOrNull {
            it.code.equals(PAYMENT_ONLINE_CODE, ignoreCase = true)
        }

        if (onlinePaymentType == null) {
            return Resource.ErrorOther("Тип оплаты 'Онлайн' не найден")
        }

        // Отправляем платеж в iiko через репозиторий
        return changeOrderRepository.addPayment(
            orderId = orderId,
            paymentTypeId = onlinePaymentType.id,
            amount = amount
        )
    }
}

