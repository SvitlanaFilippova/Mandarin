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
-keep class dagger.hilt.android.internal.managers.** { *; }
-keep class javax.inject.** { *; }

# чтобы не срезало аннотации
-keepattributes *Annotation*

##############################
# Retrofit / Moshi / Gson / kotlinx.serialization
##############################
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

-keep class com.squareup.moshi.** { *; }
-keep class kotlinx.serialization.** { *; }

##############################
# Coil
##############################
-dontwarn coil3.**
-keep class coil3.** { *; }

##############################
# Твои модели (если используешь сериализацию/рефлексию)
##############################
-keepclassmembers class com.mandarinkafe.mandarin.core.domain.models.** {
    <fields>;
}
