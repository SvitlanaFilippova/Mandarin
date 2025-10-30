import SwiftUI
import YandexMapsMobile
import composeApp

@main
struct iOSApp: App {
    init() {
        // Initialize Yandex MapKit with API key
        YMKMapKit.setApiKey(BuildConfig.shared.MAPKIT_API_KEY)
        YMKMapKit.sharedInstance()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}