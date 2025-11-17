import SwiftUI
import YandexMapsMobile
import composeApp

@main
struct iOSApp: App {
    init() {
        // Инициализируем Yandex MapKit
        YMKMapKit.setApiKey(ApiKeys.shared.mapKitApiKey)
        YMKMapKit.sharedInstance()
        
        // Устанавливаем реализацию для YooKassaWrapper
        YooKassaWrapper.shared.setImplementation(YooKassaWrapperImpl())
    }
        
        var body: some Scene {
            WindowGroup {
                ContentView()
            }
        }
   
}
