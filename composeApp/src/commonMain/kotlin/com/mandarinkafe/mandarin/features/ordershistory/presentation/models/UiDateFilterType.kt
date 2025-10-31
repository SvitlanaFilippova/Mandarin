package com.mandarinkafe.mandarin.features.ordershistory.presentation.models

import com.mandarinkafe.mandarin.MR
import dev.icerock.moko.resources.StringResource

enum class UiDateFilterType(
    val nameRes: StringResource,
) {
    TODAY(nameRes = MR.strings.date_filter_today),
    YESTERDAY(nameRes = MR.strings.date_filter_yesterday),
    LAST_7_DAYS(nameRes = MR.strings.date_filter_last_7_days),
    CURRENT_MONTH(nameRes = MR.strings.date_filter_current_month),
    CUSTOM_RANGE(nameRes = MR.strings.date_filter_custom_range)
}

fun DateFilterType.toUi(): UiDateFilterType {
    return UiDateFilterType.valueOf(this.name)
}

// Маппинг UI -> домен
fun UiDateFilterType.toDomain(): DateFilterType {
    return DateFilterType.valueOf(this.name)
}