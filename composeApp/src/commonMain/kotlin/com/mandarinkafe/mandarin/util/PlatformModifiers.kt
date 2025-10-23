package com.mandarinkafe.mandarin.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Возвращает platform-specific модификатор для контента в ModalBottomSheet.
 * Для iOS добавляет fillMaxHeight(), для Android возвращает пустой модификатор.
 */
@Composable
expect fun Modifier.bottomSheetContentModifier(): Modifier


/**
 * Возвращает platform-specific модификатор для шапки в ModalBottomSheet.
 * Для iOS добавляет отступ сверху, для Android возвращает пустой модификатор.
 */
@Composable
expect fun Modifier.bottomSheetHeaderModifier(): Modifier
