package com.mandarinkafe.mandarin.navigation.extensions

import android.util.Base64
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> NavBackStackEntry.decodeJsonArg(key: String, gson: Gson): T? {
    val encoded = arguments?.getString(key) ?: return null
    val decoded = String(Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_WRAP))
    return gson.fromJson<T>(decoded, object : TypeToken<T>() {}.type)
}

fun stringNavArg(key: String, nullable: Boolean = false) =
    navArgument(key) {
        type = NavType.StringType
        this.nullable = nullable
    }

fun boolNavArg(key: String) = navArgument(key) { type = NavType.BoolType }