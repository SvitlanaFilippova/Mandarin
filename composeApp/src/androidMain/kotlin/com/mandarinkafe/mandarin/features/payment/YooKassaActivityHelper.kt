package com.mandarinkafe.mandarin.features.payment

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.github.aakira.napier.Napier
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import java.util.Currency
import kotlin.coroutines.resume

/**
 * Helper класс для работы с YooKassa SDK и Activity результатами
 */
object YooKassaActivityHelper {

    @JvmStatic
    var currentActivity: AppCompatActivity? = null
        private set

    private var paymentLauncher: ActivityResultLauncher<Intent>? = null

    /**
     * Регистрирует Activity для работы с платежами
     * Должен вызываться из onCreate Activity
     */
    fun registerActivity(activity: AppCompatActivity) {
        currentActivity = activity

        // Регистрируем launcher для получения результата платежа
        paymentLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handlePaymentResult(result)
        }

        // Отслеживаем lifecycle для очистки при уничтожении Activity
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (source == activity) {
                        currentActivity = null
                        paymentLauncher = null
                    }
                }
            }
        })
    }

    private var paymentContinuation: kotlin.coroutines.Continuation<PaymentResult>? = null

    /**
     * Запускает процесс получения payment_token
     */
    suspend fun initializePayment(
        amount: Double,
        orderId: String,
        clientApplicationKey: String,
        shopId: String,
        userPhone: String,
    ): PaymentResult = suspendCancellableCoroutine { continuation ->
        Napier.d("PaymentFlow: [ActivityHelper] initializePayment started - orderId=$orderId, amount=$amount, userPhone=$userPhone")
        val activity = currentActivity
        if (activity == null) {
            Napier.e("PaymentFlow: [ActivityHelper] Activity not registered")
            continuation.resume(
                PaymentResult(
                    success = false,
                    error = "Activity не зарегистрирована. Вызовите registerActivity() в onCreate"
                )
            )
            return@suspendCancellableCoroutine
        }

        val launcher = paymentLauncher
        if (launcher == null) {
            Napier.e("PaymentFlow: [ActivityHelper] Launcher not initialized")
            continuation.resume(
                PaymentResult(
                    success = false,
                    error = "Launcher не инициализирован"
                )
            )
            return@suspendCancellableCoroutine
        }

        try {
            paymentContinuation = continuation
            Napier.d("PaymentFlow: [ActivityHelper] Creating payment parameters and launching SDK...")

            val paymentParameters = PaymentParameters(
                amount = Amount(
                    value = amount.toBigDecimal(),
                    currency = Currency.getInstance("RUB")
                ),
                title = "Оплата заказа",
                subtitle = "Заказ №$orderId",
                clientApplicationKey = clientApplicationKey,
                shopId = shopId,
                savePaymentMethod = SavePaymentMethod.OFF,
                paymentMethodTypes = setOf(
                    PaymentMethodType.BANK_CARD,
                    PaymentMethodType.SBERBANK,
                    PaymentMethodType.SBP
                ),
                userPhoneNumber = userPhone,
                customerId = userPhone, // для возможности сохранения и привязки карты к ЛК
                authCenterClientId = null
            )

            val intent = Checkout.createTokenizeIntent(activity, paymentParameters)
            launcher.launch(intent)
        } catch (e: Exception) {
            Napier.e("PaymentFlow: [ActivityHelper] initializePayment - Exception", e)
            paymentContinuation = null
            continuation.resume(
                PaymentResult(
                    success = false,
                    error = "Ошибка инициализации платежа: ${e.message}"
                )
            )
        }
    }

    private fun handlePaymentResult(result: ActivityResult) {
        val continuation = paymentContinuation ?: return
        paymentContinuation = null

        Napier.d("PaymentFlow: [ActivityHelper] handlePaymentResult - resultCode=${result.resultCode}")

        when (result.resultCode) {
            Activity.RESULT_OK -> {
                // successful tokenization
                Napier.d("PaymentFlow: [ActivityHelper] RESULT_OK - extracting token...")
                val tokenizationResult = result.data?.let { 
                    Checkout.createTokenizationResult(it) 
                }
                
                if (tokenizationResult != null) {
                    Napier.d("PaymentFlow: [ActivityHelper] Token received successfully - token=${tokenizationResult.paymentToken.take(20)}...")
                    continuation.resume(
                        PaymentResult(
                            success = true,
                            paymentToken = tokenizationResult.paymentToken
                        )
                    )
                } else {
                    Napier.e("PaymentFlow: [ActivityHelper] Failed to extract token from result")
                    continuation.resume(
                        PaymentResult(
                            success = false,
                            error = "Не удалось получить payment_token"
                        )
                    )
                }
            }
            
            Activity.RESULT_CANCELED -> {
                // user canceled tokenization
                Napier.d("PaymentFlow: [ActivityHelper] RESULT_CANCELED - user canceled")
                continuation.resume(
                    PaymentResult(
                        success = false,
                        error = "Платеж отменен пользователем"
                    )
                )
            }
            
            else -> {
                Napier.e("PaymentFlow: [ActivityHelper] Unknown result code: ${result.resultCode}")
                continuation.resume(
                    PaymentResult(
                        success = false,
                        error = "Неизвестная ошибка при получении токена"
                    )
                )
            }
        }
    }
}

