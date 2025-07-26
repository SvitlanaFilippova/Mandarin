package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.hasSelectedAllRequiredModifiers
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.BottomSheetHeader
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.MealDetailsMainContent
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.ToCartButton
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Constants.SCROLL_TARGET_KEY
import kotlinx.coroutines.launch

@Composable
fun MealDetailsContentScreen(
    selectedTabIndex: Int,
    addons: List<MealAdditionalCategory>,
    customizedMeal: CustomizedMeal,
    isFavorite: Boolean,
    isEditMode: Boolean,
    onClose: () -> Unit,
    onEvent: (MealDetailsEvent) -> Unit,
    onAddToCart: () -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val meal = remember(customizedMeal) { customizedMeal.meal }
    val chosenModifiers = remember(customizedMeal) { customizedMeal.modifiers }
    val toCartShouldBeActive =
        remember(customizedMeal) { customizedMeal.hasSelectedAllRequiredModifiers() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val onMakeMoreDeliciousClick: () -> Unit = remember(listState) {
        {
            coroutineScope.launch {
                val index = listState.layoutInfo.visibleItemsInfo
                    .find { it.key == SCROLL_TARGET_KEY }
                    ?.index

                if (index != null) {
                    listState.animateScrollToItem(index + 1)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(Dimens.MarginSmall8)
    ) {
        BottomSheetHeader(
            meal = meal,
            onToggleFavorite = { onToggleFavorite() },
            onClose = onClose,
            isFavorite = isFavorite
        )

        Box {
            MealDetailsMainContent(
                customizedMeal = customizedMeal,
                listState = listState,
                selectedTabIndex = selectedTabIndex,
                addons = addons,
                chosenModifiers = chosenModifiers,
                onMakeMoreDeliciousClick = onMakeMoreDeliciousClick,
                onEvent = onEvent
            )

            // Кнопка "В корзину", закреплённая внизу
            ToCartButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Colors.Transparent),
                totalPrice = customizedMeal.totalPrice(),
                onAddToCart = {
                    onAddToCart()
                    onClose()
                },
                shouldBeActive = toCartShouldBeActive,
                isEditMode = isEditMode,
                onEdit = {
                    onEdit()
                    onClose()
                },
                onMissingRequiredOptions = { onEvent(MealDetailsEvent.OnToCartClickBeforeMandatoryChoice) }
            )
        }
    }
}
