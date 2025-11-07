package com.mandarinkafe.mandarin.shared.config

import com.mandarinkafe.mandarin.shared.BuildKonfig

/**
 * Provides access to API keys from BuildKonfig.
 * This object is exported to iOS and can be used in Swift code.
 */
object ApiKeys {
    val mapKitApiKey: String = BuildKonfig.MAPKIT_API_KEY
}

