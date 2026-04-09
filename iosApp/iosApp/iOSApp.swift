import SwiftUI
import YandexMapsMobile
import composeApp

@main
struct iOSApp: App {
    init() {
        // Инициализируем Yandex MapKit
        YMKMapKit.setApiKey(ApiKeys.shared.mapKitApiKey)
        YMKMapKit.setLocale("ru_RU")
        YMKMapKit.sharedInstance()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // Обработка возврата из браузера после оплаты YooKassa
                    // URL формата: mandarin://payment/return?order_id=...
                    // После возврата из браузера PaymentViewModel начнет polling статуса
                    _ = url
                }
        }
    }
}
