package com.mandarinkafe.mandarin.shared.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsViewModel
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressViewModel
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.features.favorites.presentation.viewmodel.FavoritesViewModel
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsViewModel
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuViewModel
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.AboutViewModel
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryViewModel
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DevFeedbackViewModel
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackViewModel
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderViewModel
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoViewModel
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryViewModel
import com.mandarinkafe.mandarin.features.savedadresses.presentation.viewmodel.SavedAddressesViewModel
import com.mandarinkafe.mandarin.features.search.presentation.viewmodel.SearchViewModel
import org.koin.mp.KoinPlatform.getKoin

/**
 * Функции для получения ViewModel через remember
 */
@Composable
fun rememberCartViewModel(): CartViewModel {
    val koin = getKoin()
    return remember { koin.get<CartViewModel>() }
}

@Composable
fun rememberAboutViewModel(): AboutViewModel {
    val koin = getKoin()
    return remember { koin.get<AboutViewModel>() }
}

@Composable
fun rememberAddressDetailsViewModel(): AddressDetailsViewModel {
    val koin = getKoin()
    return remember { koin.get<AddressDetailsViewModel>() }
}

@Composable
fun rememberAddressViewModel(): AddressViewModel {
    val koin = getKoin()
    return remember { koin.get<AddressViewModel>() }
}

@Composable
fun rememberDeliveryViewModel(): DeliveryViewModel {
    val koin = getKoin()
    return remember { koin.get<DeliveryViewModel>() }
}

@Composable
fun rememberDevFeedbackViewModel(): DevFeedbackViewModel {
    val koin = getKoin()
    return remember { koin.get<DevFeedbackViewModel>() }
}

@Composable
fun rememberFavoritesViewModel(): FavoritesViewModel {
    val koin = getKoin()
    return remember { koin.get<FavoritesViewModel>() }
}

@Composable
fun rememberFeedbackViewModel(): FeedbackViewModel {
    val koin = getKoin()
    return remember { koin.get<FeedbackViewModel>() }
}

@Composable
fun rememberMealDetailsViewModel(): MealDetailsViewModel {
    val koin = getKoin()
    return remember { koin.get<MealDetailsViewModel>() }
}

@Composable
fun rememberMenuViewModel(): MenuViewModel {
    val koin = getKoin()
    return remember { koin.get<MenuViewModel>() }
}

@Composable
fun rememberOrderViewModel(): OrderViewModel {
    val koin = getKoin()
    return remember { koin.get<OrderViewModel>() }
}

@Composable
fun rememberOrderInfoViewModel(): OrderInfoViewModel {
    val koin = getKoin()
    return remember { koin.get<OrderInfoViewModel>() }
}

@Composable
fun rememberOrdersHistoryViewModel(): OrdersHistoryViewModel {
    val koin = getKoin()
    return remember { koin.get<OrdersHistoryViewModel>() }
}

@Composable
fun rememberSavedAddressesViewModel(): SavedAddressesViewModel {
    val koin = getKoin()
    return remember { koin.get<SavedAddressesViewModel>() }
}

@Composable
fun rememberSearchViewModel(): SearchViewModel {
    val koin = getKoin()
    return remember { koin.get<SearchViewModel>() }
}

@Composable
fun rememberSharedViewModel(): SharedViewModel {
    val koin = getKoin()
    return remember { koin.get<SharedViewModel>() }
}
