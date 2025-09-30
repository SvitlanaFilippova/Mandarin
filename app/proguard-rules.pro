##############################
# Общие правила для Kotlin
##############################
-dontwarn kotlin.**
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

##############################
# Jetpack Compose
##############################
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.LifecycleObserver
-keep class androidx.lifecycle.DefaultLifecycleObserver

##############################
# Hilt / Dagger
##############################
-keep class dagger.hilt.** { *; }
-keep class dagger.hilt.android.internal.** { *; }
-keep class javax.inject.** { *; }
-keepattributes *Annotation*

##############################
# Ktor / OkHttp / Gson / Moshi / kotlinx.serialization
##############################
-keep class ktor.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

-keep class com.squareup.moshi.** { *; }
-keep class kotlinx.serialization.** { *; }

# Gson: оставляем имена полей и сериализацию
-keep class com.mandarinkafe.mandarin.data.auth.model.** { *; }
-keep class com.mandarinkafe.mandarin.core.domain.models.** { *; }

# AuthInterceptor: не срезаем логику
-keep class com.mandarinkafe.mandarin.core.data.network.AuthInterceptor { *; }
-keep class com.mandarinkafe.mandarin.core.data.dto.AuthRequest { *; }

##############################
# Coil
##############################
-dontwarn coil3.**
-keep class coil3.** { *; }
