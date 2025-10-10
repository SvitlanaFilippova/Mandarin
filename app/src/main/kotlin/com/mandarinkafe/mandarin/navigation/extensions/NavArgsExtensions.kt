package com.mandarinkafe.mandarin.navigation.extensions

import android.util.Base64
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.serialization.json.Json

inline fun <reified T> NavBackStackEntry.decodeJsonArg(key: String): T? {
    val encoded = arguments?.getString(key) ?: return null
    val decoded = String(Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_WRAP))
    return Json.decodeFromString<T>(decoded)
}

fun stringNavArg(key: String, nullable: Boolean = false) =
    navArgument(key) {
        type = NavType.StringType
        this.nullable = nullable
    }

fun boolNavArg(key: String) = navArgument(key) { type = NavType.BoolType }