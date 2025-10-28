import SwiftUI
import YandexMapsMobile
import composeAppKit

@main
struct iOSApp: App {
    init() {
        // Initialize Yandex MapKit with API key
         YMKMapKit.setApiKey(BuildConfig.shared.MAPKIT_API_KEY)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}