//
// Created by Svitlana Filippova on 17.11.2025.
//
import Foundation

// Протокол для реализации YooKassaWrapper
@objc public protocol YooKassaWrapperImplementation {
    func initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String,
        clientApplicationKey: String,
        shopId: String,
        callback: @escaping (Bool, String?, String?) -> Void
    )
    
    func openPaymentUrl(
        confirmationUrl: String,
        callback: @escaping (Bool, String?, String?) -> Void
    )
}

@objc public class YooKassaWrapper: NSObject {
    
    @objc public static var shared: YooKassaWrapper = YooKassaWrapper()
    
    // Ссылка на реализацию (устанавливается в iOSApp.init())
    private var implementation: YooKassaWrapperImplementation?
    
    // Метод для установки реализации (вызывается из iOSApp.init())
    @objc public func setImplementation(_ impl: YooKassaWrapperImplementation) {
        self.implementation = impl
    }
    
    @objc public func initializePayment(
        amount: Double,
        subtitle: String,
        userPhone: String,
        clientApplicationKey: String,
        shopId: String,
        callback: @escaping (Bool, String?, String?) -> Void
    ) {
        guard let impl = implementation else {
            callback(false, nil, "YooKassaWrapper implementation not set")
            return
        }
        impl.initializePayment(
            amount: amount,
            subtitle: subtitle,
            userPhone: userPhone,
            clientApplicationKey: clientApplicationKey,
            shopId: shopId,
            callback: callback
        )
    }
    
    @objc public func openPaymentUrl(
        confirmationUrl: String,
        callback: @escaping (Bool, String?, String?) -> Void
    ) {
        guard let impl = implementation else {
            callback(false, nil, "YooKassaWrapper implementation not set")
            return
        }
        impl.openPaymentUrl(confirmationUrl: confirmationUrl, callback: callback)
    }
}
