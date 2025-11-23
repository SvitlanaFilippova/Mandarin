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
import com.mandarinkafe.mandarin.shared.BuildKonfig
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
                if (event == Lifecycle.Event.ON_DESTROY && source == activity) {
                    currentActivity = null
                    paymentLauncher = null
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
        subtitle: String,
        clientApplicationKey: String,
        shopId: String,
        userPhone: String,
        orderId: String,
    ): PaymentResult = suspendCancellableCoroutine { continuation ->
        val activity = currentActivity
        if (activity == null) {
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
            val paymentParameters = createPaymentParameters(
                amount = amount,
                subtitle = subtitle,
                clientApplicationKey = clientApplicationKey,
                shopId = shopId,
                userPhone = userPhone,
                orderId = orderId
            )
            launchPaymentIntent(activity, launcher, paymentParameters)
        } catch (e: Exception) {
            paymentContinuation = null
            continuation.resume(
                PaymentResult(
                    success = false,
                    error = "Ошибка инициализации платежа: ${e.message}"
                )
            )
        }
    }

    private fun createPaymentParameters(
        amount: Double,
        subtitle: String,
        clientApplicationKey: String,
        shopId: String,
        userPhone: String,
        orderId: String,
    ): PaymentParameters {
        // Формируем customReturnUrl для возврата после 3DS проверки
        // Сервер должен редиректить на mandarin://payment/return?order_id=...
        val baseUrl = BuildKonfig.SERVER_BASE_URL.removeSuffix("/")
        val customReturnUrl = "$baseUrl/payment/return?order_id=$orderId"

        return PaymentParameters(
            amount = Amount(
                value = amount.toBigDecimal(),
                currency = Currency.getInstance("RUB")
            ),
            title = "Оплата заказа",
            subtitle = subtitle,
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
            authCenterClientId = null,
            customReturnUrl = customReturnUrl // URL для возврата после 3DS проверки
        )
    }

    private fun launchPaymentIntent(
        activity: AppCompatActivity,
        launcher: ActivityResultLauncher<Intent>,
        paymentParameters: PaymentParameters,
    ) {
        val intent = Checkout.createTokenizeIntent(activity, paymentParameters)
        launcher.launch(intent)
    }

    private fun handlePaymentResult(result: ActivityResult) {
        val continuation = paymentContinuation ?: return
        paymentContinuation = null

        when (result.resultCode) {
            Activity.RESULT_OK -> {
                // successful tokenization
                val tokenizationResult = result.data?.let {
                    Checkout.createTokenizationResult(it)
                }

                if (tokenizationResult != null) {
                    continuation.resume(
                        PaymentResult(
                            success = true,
                            paymentToken = tokenizationResult.paymentToken
                        )
                    )
                } else {
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
                continuation.resume(
                    PaymentResult(
                        success = false,
                        error = "Платеж отменен пользователем"
                    )
                )
            }

            else -> {
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

