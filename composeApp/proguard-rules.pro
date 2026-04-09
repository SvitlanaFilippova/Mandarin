# Koin: не вырезать контейнер при включении shrinker / minify
-keep class org.koin.** { *; }

# ViewModel'и, создаваемые через Koin
-keep class com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentViewModel { *; }
-keep class com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoViewModel { *; }
