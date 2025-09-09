package com.mandarinkafe.mandarin.core.presentation.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R

sealed class UiError(
    @StringRes val msgRes: Int,
    @StringRes val extraMsgRes: Int? = null,
    @DrawableRes val imgRes: Int,
) {

    object NoInternet : UiError(
        msgRes = R.string.error_no_internet,
        extraMsgRes = R.string.placeholder_message_call_us,
        imgRes = R.drawable.placeholder_no_internet,
    )

    object OtherError : UiError(
        msgRes = R.string.error_something_wrong,
        extraMsgRes = R.string.placeholder_message_call_us,
        imgRes = R.drawable.placeholder_server_error,
    )

    object MenuEmpty : UiError(
        msgRes = R.string.error_empty_menu,
        extraMsgRes = R.string.placeholder_message_call_us,
        imgRes = R.drawable.placeholder_server_error,
    )

    object DataEmpty : UiError(
        msgRes = R.string.error_empty_data,
        extraMsgRes = R.string.placeholder_message_call_us,
        imgRes = R.drawable.placeholder_server_error,
    )

    object FavoritesEmpty : UiError(
        msgRes = R.string.error_empty_favorites,
        extraMsgRes = R.string.error_empty_favorites_extra,
        imgRes = R.drawable.placeholder_empty_favorites,
    )

    object CartEmpty : UiError(
        msgRes = R.string.error_empty_cart,
        imgRes = R.drawable.placeholder_empty_cart,
    )

    object SearchEmpty : UiError(
        msgRes = R.string.nothing_found,
        extraMsgRes = R.string.nothing_found_extra,
        imgRes = R.drawable.placeholder_nothing_found,
    )
}