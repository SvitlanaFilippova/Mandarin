//
// Created by Svitlana Filippova on 17.11.2025.
//
import Foundation
import UIKit
import YooKassaPayments

class YooKassaWrapperImpl: NSObject, YooKassaWrapperImplementation, TokenizationModuleOutput {
    
    // Callback для передачи результатов в Kotlin
    // Параметры: success (Bool), paymentToken (String?), error (String?)
    typealias PaymentCallback = (Bool, String?, String?) -> Void
    
    private var currentCallback: PaymentCallback?
    
    func initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String,
        clientApplicationKey: String,
        shopId: String,
        callback: @escaping (Bool, String?, String?) -> Void
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
    
    func openPaymentUrl(
        confirmationUrl: String,
        callback: @escaping (Bool, String?, String?) -> Void
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
    
    // MARK: - TokenizationModuleOutput
    
    /// Вызывается при успешной токенизации платежа
    func tokenizationModule(
        _ module: TokenizationModuleInput,
        didTokenize token: Tokens,
        paymentMethodType: PaymentMethodType
    ) {
        // Вызываем callback с успешным результатом и payment token
        currentCallback?(true, token.paymentToken, nil)
        currentCallback = nil
        
        // Закрываем модуль токенизации
        dismissTokenizationModule()
    }
    
    /// Вызывается при завершении работы модуля токенизации
    func didFinish(
        on module: TokenizationModuleInput,
        with error: YooKassaPaymentsError?
    ) {
        // Если callback еще не был вызван (пользователь закрыл модуль без токенизации)
        if currentCallback != nil {
            let errorMessage = error?.localizedDescription ?? "Платеж отменен пользователем"
            currentCallback?(false, nil, errorMessage)
            currentCallback = nil
        }
        
        // Закрываем модуль
        dismissTokenizationModule()
    }
    
    /// Вызывается при успешном подтверждении платежа (3DS или Sberpay)
    func didFinishConfirmation(paymentMethodType: PaymentMethodType) {
        // Успешное подтверждение платежа
        // В данном случае токенизация уже была успешной, поэтому просто закрываем модуль
        dismissTokenizationModule()
    }
    
    /// Вызывается при ошибке подтверждения платежа
    func didFailConfirmation(error: YooKassaPaymentsError?) {
        // Ошибка подтверждения платежа
        if currentCallback != nil {
            let errorMessage = error?.localizedDescription ?? "Ошибка подтверждения платежа"
            currentCallback?(false, nil, errorMessage)
            currentCallback = nil
        }
        
        // Закрываем модуль
        dismissTokenizationModule()
    }
    
    /// Закрывает модуль токенизации
    private func dismissTokenizationModule() {
        DispatchQueue.main.async {
            guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                  let rootViewController = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController
            else {
                return
            }
            
            var topViewController = rootViewController
            while let presented = topViewController.presentedViewController {
                topViewController = presented
            }
            
            topViewController.dismiss(animated: true)
        }
    }
}

