package com.mandarinkafe.mandarin.core.data.network

actual class NetworkMonitor {
    actual fun isNetworkAvailable(): Boolean {
        // Простая реализация для iOS - всегда возвращаем true
        // В реальном приложении здесь можно добавить проверку сети через Reachability
        return true
    }
}
