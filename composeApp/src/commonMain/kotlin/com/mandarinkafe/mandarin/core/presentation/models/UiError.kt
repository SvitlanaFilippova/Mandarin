package com.mandarinkafe.mandarin.core.presentation.models

import com.mandarinkafe.mandarin.MR
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

sealed class UiError(
    val msg: StringResource,
    val extraMsg: StringResource? = null,
    val imageRes: ImageResource,
) {
    object NoInternet : UiError(
        MR.strings.error_no_internet,
        MR.strings.error_no_internet_extra,
        MR.images.placeholder_no_internet
    )

    object OtherError : UiError(
        MR.strings.error_other,
        MR.strings.error_other_extra,
        MR.images.placeholder_server_error
    )

    object MenuEmpty : UiError(
        MR.strings.error_menu_empty,
        MR.strings.error_menu_empty_extra,
        MR.images.placeholder_server_error
    )

    object DataEmpty : UiError(
        MR.strings.error_data_empty,
        MR.strings.error_data_empty_extra,
        MR.images.placeholder_server_error
    )

    object FavoritesEmpty : UiError(
        MR.strings.error_favorites_empty,
        MR.strings.error_favorites_empty_extra,
        MR.images.placeholder_empty_favorites
    )

    object CartEmpty : UiError(
        MR.strings.error_cart_empty,
        null,
        MR.images.placeholder_empty_cart
    )

    object SearchEmpty : UiError(
        MR.strings.error_search_empty,
        MR.strings.error_search_empty_extra,
        MR.images.placeholder_nothing_found
    )

    object EmptyOrderData : UiError(
        MR.strings.error_order_data_empty,
        MR.strings.error_order_data_empty_extra,
        MR.images.placeholder_server_error
    )
}