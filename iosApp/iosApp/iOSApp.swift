import SwiftUI

@main
struct iOSApp: App {
    init() {
        // Инициализация Koin
           KoinInitializerKt.initKoinIOS()

        // Настройка MapKit
        // MapKitFactory.setApiKey(ApiKeys.MAPKIT_API_KEY)
        // MapKitFactory.setLocale(locale: "ru_RU")

        // Инициализация Napier (логгер)
        //     Napier.base(DebugAntilog())
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}