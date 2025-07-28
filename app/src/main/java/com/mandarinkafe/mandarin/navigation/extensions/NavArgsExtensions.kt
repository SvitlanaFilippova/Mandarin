package com.mandarinkafe.mandarin.navigation.extensions

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

inline fun <reified T> NavBackStackEntry.decodeJsonArg(key: String, gson: Gson): T? {
    val encoded = arguments?.getString(key) ?: return null
    val decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())

    return gson.fromJson<T>(decoded, object : TypeToken<T>() {}.type)
}

fun jsonNavArg(key: String) = navArgument(key) { type = NavType.StringType }

fun boolNavArg(key: String) = navArgument(key) { type = NavType.BoolType }