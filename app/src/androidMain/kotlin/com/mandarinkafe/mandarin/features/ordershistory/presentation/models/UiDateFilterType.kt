package com.mandarinkafe.mandarin.features.ordershistory.presentation.models

import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R

enum class UiDateFilterType(
    @StringRes val nameRes: Int
) {
    TODAY(nameRes = R.string.date_filter_today),
    YESTERDAY(nameRes = R.string.date_filter_yesterday),
    LAST_7_DAYS(nameRes = R.string.date_filter_last_7_days),
    CURRENT_MONTH(nameRes = R.string.date_filter_current_month),
    CUSTOM_RANGE(nameRes = R.string.date_filter_custom_range)
}

// Маппинг домен -> UI
fun DateFilterType.toUi(): UiDateFilterType {
    return UiDateFilterType.valueOf(this.name)
}

// Маппинг UI -> домен
fun UiDateFilterType.toDomain(): DateFilterType {
    return DateFilterType.valueOf(this.name)
}