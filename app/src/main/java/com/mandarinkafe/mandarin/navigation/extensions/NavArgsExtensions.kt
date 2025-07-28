package com.mandarinkafe.mandarin.navigation.extensions

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

inline fun <reified T> NavBackStackEntry.decodeJsonArg(key: String, gson: Gson): T? {
    val json = arguments?.getString(key)?.let {
        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
    }
    return gson.fromJson(json, T::class.java)
}

fun jsonNavArg(key: String) = navArgument(key) { type = NavType.StringType }

fun boolNavArg(key: String) = navArgument(key) { type = NavType.BoolType }