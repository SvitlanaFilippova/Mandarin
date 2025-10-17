package com.mandarinkafe.mandarin.shared.presentation.ui

import androidx.compose.runtime.Composable

/**
 * Платформо-независимый контроллер для управления системным UI.
 * Позволяет настраивать цвета статус-бара и навигационной панели.
 */
expect class SystemUiController {
    /**
     * Устанавливает цвет статус-бара
     * @param color Цвет статус-бара
     * @param darkIcons Использовать ли темные иконки
     */
    fun setStatusBarColor(color: Long, darkIcons: Boolean)

    /**
     * Устанавливает цвет навигационной панели
     * @param color Цвет навигационной панели
     * @param darkIcons Использовать ли темные иконки
     */
    fun setNavigationBarColor(color: Long, darkIcons: Boolean)
}

/**
 * Создает экземпляр SystemUiController для текущей платформы
 */
@Composable
expect fun rememberSystemUiController(): SystemUiController
