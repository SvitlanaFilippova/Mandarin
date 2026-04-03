package com.mandarinkafe.mandarin.features.menu.presentation.models

/** Баннер про приём заказов на стартовом экране меню. */
sealed interface OrderClosingBannerUi {
    data class WithClosingTime(val hhMm: String) : OrderClosingBannerUi
    data object ClosedToday : OrderClosingBannerUi

    /** Заказы принимаются, но до конца приёма осталось меньше порога (только главная). */
    data class AcceptanceEndingSoon(val orderAcceptanceEndTime: String) : OrderClosingBannerUi
}
