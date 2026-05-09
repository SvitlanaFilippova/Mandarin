package com.mandarinkafe.mandarin.util

/**
 * Разделитель «внутрянки», дописываемой приложением (напр. блок оплаты для курьера).
 *
 * По значению совпадает с `COMMENT_DIVIDER_1` из [toVisibleComment] в `StringExtensions.kt`.
 * Осознанно вынесено отдельно: там же перечислены и другие варианты (`//`), чтобы администраторы
 * могли использовать разный синтаксис — список разделителей в `toVisibleComment` не меняли.
 */
const val COMMENT_TECH_VISIBILITY_DIVIDER = "\\\\"
