package com.mandarinkafe.mandarin.core.ui.models

import com.mandarinkafe.mandarin.R

sealed class UiError(
    val msgRes: Int,
    val extraMsgRes: Int? = null,
    val imgRes: Int,
    val needRetry: Boolean
) {

    object NoInternet : UiError(
        msgRes = R.string.error_no_internet,
        extraMsgRes = R.string.placeholder_message_call_us,
        imgRes = R.drawable.placeholder_no_internet,
        needRetry = true,
    )

    object OtherError : UiError(
        msgRes = R.string.error_something_wrong,
        extraMsgRes = R.string.placeholder_message_call_us,
        imgRes = R.drawable.placeholder_server_error,
        needRetry = true,
    )

    object MenuEmpty : UiError(
        msgRes = R.string.error_empty_menu,
        extraMsgRes = R.string.placeholder_message_call_us,
        imgRes = R.drawable.placeholder_server_error,
        needRetry = true,
    )

    object AddonsEmpty : UiError(
        msgRes = R.string.error_empty_addons,
        extraMsgRes = R.string.placeholder_message_call_us,
        imgRes = R.drawable.placeholder_server_error,
        needRetry = true,
    )

    object FavoritesEmpty : UiError(
        msgRes = R.string.error_empty_favorites,
        extraMsgRes = R.string.error_empty_favorites_extra,
        imgRes = R.drawable.placeholder_empty_favorites,
        needRetry = false,
    )

    object CartEmpty : UiError(
        msgRes = R.string.error_empty_cart,
        imgRes = R.drawable.placeholder_empty_cart,
        needRetry = false,
    )

    object SearchEmpty : UiError(
        msgRes = R.string.nothing_found,
        extraMsgRes = R.string.nothing_found_extra,
        imgRes = R.drawable.placeholder_nothing_found,
        needRetry = false,
    )
}