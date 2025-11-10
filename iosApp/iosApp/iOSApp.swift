import SwiftUI
import YandexMapsMobile
import composeApp

@main
struct iOSApp: App {
    init() {
        // Initialize Yandex MapKit with API key
        YMKMapKit.setApiKey(ApiKeys.shared.mapKitApiKey)
        YMKMapKit.sharedInstance()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}