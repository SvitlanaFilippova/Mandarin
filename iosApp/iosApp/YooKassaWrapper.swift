//
//  YooKassaWrapper.swift
//  iosApp
//
//  Обертка для доступа к YooKassaPayments SDK из Kotlin
//

import Foundation
import UIKit
import YooKassaPayments

@objc public class YooKassaWrapper: NSObject {

    // Callback для передачи результатов в Kotlin
    // Параметры: success (Bool), paymentToken (String?), error (String?)
    public typealias PaymentCallback = (Bool, String?, String?) -> Void

    private var currentCallback: PaymentCallback?

    @objc public static let shared = YooKassaWrapper()

    private override init() {
        super.init()
    }

    /**
     * Инициализация платежа и получение payment_token
     * Параметры соответствуют Android реализации:
     * - amount: сумма платежа
     * - subtitle: описание платежа
     * - userPhone: телефон пользователя
     * - clientApplicationKey: ключ приложения
     * - shopId: ID магазина
     * - callback: блок для возврата результата (success, paymentToken, error)
     */
    @objc public func initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String,
        clientApplicationKey: String,
        shopId: String,
        callback: @escaping PaymentCallback
    ) {
        // Сохраняем callback для использования в delegate
        currentCallback = callback

        // Получаем root view controller для показа SDK UI
        // Используем современный способ для iOS 13+ с поддержкой множественных сцен
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootViewController = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController
        else {
            callback(false, nil, "Не удалось получить root view controller")
            return
        }

        // Находим top-most view controller (для модального показа)
        var topViewController = rootViewController
        while let presented = topViewController.presentedViewController {
            topViewController = presented
        }

        // Создаем Amount для SDK
        let amountValue = Amount(value: Decimal(amount), currency: .rub)

        // Создаем TokenizationSettings
        let paymentMethodTypes = PaymentMethodTypes.all
        let tokenizationSettings = TokenizationSettings(
            paymentMethodTypes: paymentMethodTypes
        )

        // Создаем CustomizationSettings (обязательный параметр)
        let customizationSettings = CustomizationSettings()

        // Создаем TokenizationModuleInputData с правильными параметрами
        let tokenizationModuleInputData = TokenizationModuleInputData(
            clientApplicationKey: clientApplicationKey,
            shopName: "Mandarin", // Название магазина
            shopId: shopId,
            purchaseDescription: subtitle, // Описание покупки
            amount: amountValue,
            gatewayId: nil,
            tokenizationSettings: tokenizationSettings,
            testModeSettings: nil, // Для продакшена nil, для теста можно указать TestModeSettings
            cardScanning: nil,
            returnUrl: nil,
            isLoggingEnabled: false,
            userPhoneNumber: userPhone,
            customizationSettings: customizationSettings,
            savePaymentMethod: SavePaymentMethod.on,
            moneyAuthClientId: nil,
            applicationScheme: nil,
            customerId: userPhone, // для возможности сохранения и привязки карты к ЛК
            lang: nil // nil = автоматическое определение языка
        )

        // Создаем TokenizationFlow с кейсом .tokenization
        let inputData: TokenizationFlow = .tokenization(tokenizationModuleInputData)

        // Создаем и показываем модуль токенизации
        let viewController = TokenizationAssembly.makeModule(
            inputData: inputData,
            moduleOutput: self
        )

        let navigationController = UINavigationController(rootViewController: viewController)
        topViewController.present(navigationController, animated: true)
    }

    /**
     * Открытие confirmation_url для оплаты (3DS и т.д.)
     * Аналогично Android реализации через Intent.ACTION_VIEW
     */
    @objc public func openPaymentUrl(
        confirmationUrl: String,
        callback: @escaping PaymentCallback
    ) {
        guard let url = URL(string: confirmationUrl) else {
            callback(false, nil, "Некорректный URL")
            return
        }

        if UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:]) { success in
                callback(success, nil, success ? nil : "Не удалось открыть URL")
            }
        } else {
            callback(false, nil, "Не удалось открыть URL")
        }
    }
}

// MARK: - Helper функции для вызова из Kotlin
// Эти функции упрощают вызов из Kotlin, принимая простые типы
@objc public class YooKassaHelper: NSObject {

    @objc public static func initializePaymentSync(
        amount: Double,
        subtitle: String,
        userPhone: String,
        clientApplicationKey: String,
        shopId: String,
        completionHandler: @escaping (Bool, String?, String?) -> Void
    ) {
        YooKassaWrapper.shared.initializePayment(
            amount: amount,
            subtitle: subtitle,
            userPhone: userPhone,
            clientApplicationKey: clientApplicationKey,
            shopId: shopId,
            callback: completionHandler
        )
    }

    @objc public static func openPaymentUrlSync(
        confirmationUrl: String,
        completionHandler: @escaping (Bool, String?, String?) -> Void
    ) {
        YooKassaWrapper.shared.openPaymentUrl(
            confirmationUrl: confirmationUrl,
            callback: completionHandler
        )
    }
}

// MARK: - TokenizationModuleOutput
extension YooKassaWrapper: TokenizationModuleOutput {

    public func tokenizationModule(
        _ module: TokenizationModuleInput,
        didTokenize token: Tokens,
        paymentMethodType: PaymentMethodType
    ) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else {
                return
            }

            // Успешная токенизация - получаем paymentToken из токена
            let paymentToken = token.paymentToken
            self.currentCallback?(true, paymentToken, nil)
            self.currentCallback = nil

            // Закрываем модуль
            self.dismissPaymentModule()
        }
    }

    public func didFinish(
        on module: TokenizationModuleInput,
        with error: YooKassaPaymentsError?
    ) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else {
                return
            }

            // Ошибка токенизации
            let errorMessage: String
            if let yooKassaError = error {
                errorMessage = yooKassaError.localizedDescription
            } else {
                errorMessage = "Ошибка токенизации"
            }

            self.currentCallback?(false, nil, errorMessage)
            self.currentCallback = nil

            // Закрываем модуль
            self.dismissPaymentModule()
        }
    }

    public func didFinishConfirmation(
        paymentMethodType: PaymentMethodType
    ) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else {
                return
            }

            // Успешное подтверждение (3DS или Sberpay)
            // В этом случае токен уже был получен в tokenizationModule
            // Просто закрываем модуль
            self.dismissPaymentModule()
        }
    }

    public func didFailConfirmation(error: YooKassaPaymentsError?) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else {
                return
            }

            // Ошибка подтверждения (3DS или Sberpay)
            let errorMessage: String
            if let yooKassaError = error {
                errorMessage = "Ошибка подтверждения: \(yooKassaError.localizedDescription)"
            } else {
                errorMessage = "Ошибка подтверждения платежа"
            }

            self.currentCallback?(false, nil, errorMessage)
            self.currentCallback = nil

            // Закрываем модуль
            self.dismissPaymentModule()
        }
    }

    private func dismissPaymentModule() {
        // Закрываем модуль
        // Используем современный способ для iOS 13+ с поддержкой множественных сцен
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootViewController = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController {
            var topViewController = rootViewController
            while let presented = topViewController.presentedViewController {
                topViewController = presented
            }
            if topViewController != rootViewController {
                topViewController.dismiss(animated: true)
            }
        }
    }
}
