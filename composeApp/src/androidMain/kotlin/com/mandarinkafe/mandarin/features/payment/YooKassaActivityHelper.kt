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
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters
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
    private var confirmationLauncher: ActivityResultLauncher<Intent>? = null

    /**
     * Регистрирует Activity для работы с платежами
     * Должен вызываться из onCreate Activity
     */
    fun registerActivity(activity: AppCompatActivity) {
        currentActivity = activity

        // Регистрируем launcher для получения результата токенизации
        paymentLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handlePaymentResult(result)
        }

        // Регистрируем launcher для получения результата confirmation (3DS, СБП)
        confirmationLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleConfirmationResult(result)
        }

        // Отслеживаем lifecycle для очистки при уничтожении Activity
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY && source == activity) {
                    currentActivity = null
                    paymentLauncher = null
                    confirmationLauncher = null
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
                userPhone = userPhone
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
    ): PaymentParameters {
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
            customReturnUrl = null // Для 3DS через SDK не нужен
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
                            paymentToken = tokenizationResult.paymentToken,
                            paymentMethodType = tokenizationResult.paymentMethodType?.name
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

    /**
     * Конвертирует формат payment_method_type от сервера (snake_case lowercase)
     * в enum PaymentMethodType SDK
     */
    fun parsePaymentMethodType(serverType: String?): PaymentMethodType? {
        return when (serverType?.lowercase()) {
            "bank_card" -> PaymentMethodType.BANK_CARD
            "sbp" -> PaymentMethodType.SBP
            "sberbank" -> PaymentMethodType.SBERBANK
            else -> null
        }
    }

    private var confirmationContinuation: kotlin.coroutines.Continuation<PaymentResult>? = null

    /**
     * Запускает процесс подтверждения платежа через SDK (3DS, СБП)
     * @param confirmationUrl URL для подтверждения от сервера
     * @param paymentMethodType Тип платежного метода от сервера (bank_card, sbp, sberbank)
     * @param clientApplicationKey Ключ приложения
     * @param shopId ID магазина
     */
    suspend fun confirmPayment(
        confirmationUrl: String,
        paymentMethodType: String?,
        clientApplicationKey: String,
        shopId: String,
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

        val launcher = confirmationLauncher
        if (launcher == null) {
            continuation.resume(
                PaymentResult(
                    success = false,
                    error = "Confirmation launcher не инициализирован"
                )
            )
            return@suspendCancellableCoroutine
        }

        // Конвертируем формат сервера в enum SDK
        val methodType = parsePaymentMethodType(paymentMethodType)
        if (methodType == null) {
            continuation.resume(
                PaymentResult(
                    success = false,
                    error = "Неподдерживаемый тип платежного метода: $paymentMethodType"
                )
            )
            return@suspendCancellableCoroutine
        }

        try {
            confirmationContinuation = continuation

            val testParameters = TestParameters(showLogs = true)
            val intent = Checkout.createConfirmationIntent(
                activity,
                confirmationUrl,
                methodType,
                clientApplicationKey,
                shopId,
                testParameters = testParameters
            )
            launcher.launch(intent)
        } catch (e: Exception) {
            confirmationContinuation = null
            continuation.resume(
                PaymentResult(
                    success = false,
                    error = "Ошибка подтверждения платежа: ${e.message}"
                )
            )
        }
    }

    private fun handleConfirmationResult(result: ActivityResult) {
        val continuation = confirmationContinuation ?: return
        confirmationContinuation = null

        val resultCode = result.resultCode
        val intent = result.data

        when (resultCode) {
            Activity.RESULT_OK -> {
                // Процесс подтверждения завершён (3DS или СБП)
                // Не несет информацию о том, что процесс завершился успешно
                continuation.resume(
                    PaymentResult(
                        success = true
                    )
                )
            }

            Activity.RESULT_CANCELED -> {
                // Пользователь отменил подтверждение
                continuation.resume(
                    PaymentResult(
                        success = false,
                        error = "Подтверждение платежа отменено пользователем"
                    )
                )
            }

            Checkout.RESULT_ERROR -> {
                // Ошибка при подтверждении
                val errorCode = intent?.getIntExtra(Checkout.EXTRA_ERROR_CODE, -1)
                val errorDescription = intent?.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION)
                val failingUrl = intent?.getStringExtra(Checkout.EXTRA_ERROR_FAILING_URL)

                val errorMessage = buildString {
                    append("Ошибка подтверждения платежа")
                    if (errorCode != -1) append(" (код: $errorCode)")
                    if (errorDescription != null) append(": $errorDescription")
                    if (failingUrl != null) append(" (URL: $failingUrl)")
                }

                continuation.resume(
                    PaymentResult(
                        success = false,
                        error = errorMessage
                    )
                )
            }

            else -> {
                continuation.resume(
                    PaymentResult(
                        success = false,
                        error = "Неизвестная ошибка при подтверждении платежа (код: $resultCode)"
                    )
                )
            }
        }
    }
}

