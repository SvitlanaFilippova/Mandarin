package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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
import com.mandarinkafe.mandarin.util.bottomSheetContentModifier
import com.mandarinkafe.mandarin.util.bottomSheetHeaderModifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealDetailsContentScreen(
    selectedTabIndex: Int,
    addons: List<MealAdditionalCategory>,
    customizedMeal: CustomizedMeal,
    isFavorite: Boolean,
    isEditMode: Boolean,
    onClose: () -> Unit,
    onEvent: (MealDetailsEvent) -> Unit,
    onRequestAddMeal: () -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    comment: String,
    modifier: Modifier = Modifier
) {
    val meal = remember(customizedMeal) { customizedMeal.meal }
    val chosenModifiers = remember(customizedMeal) { customizedMeal.modifiers }
    val toCartShouldBeActive =
        remember(customizedMeal) { customizedMeal.hasSelectedAllRequiredModifiers() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val imeInsets = WindowInsets.ime
    val imeHeight = imeInsets.getBottom(LocalDensity.current)
    val imeVisible = imeHeight > 0

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
        modifier = modifier
            .padding(vertical = Dimens.MarginSmall8, horizontal = Dimens.MarginStandard16)
    ) {
        BottomSheetHeader(
            meal = meal,
            onToggleFavorite = { onToggleFavorite() },
            onClose = onClose,
            isFavorite = isFavorite,
            modifier = Modifier.bottomSheetHeaderModifier()
        )

        Box(modifier = Modifier.bottomSheetContentModifier()) {
            MealDetailsMainContent(
                customizedMeal = customizedMeal,
                listState = listState,
                selectedTabIndex = selectedTabIndex,
                addons = addons,
                chosenModifiers = chosenModifiers,
                onMakeMoreDeliciousClick = onMakeMoreDeliciousClick,
                onEvent = onEvent,
                comment = comment,
                imeVisible = imeVisible,
                bottomContent = {
                    if (imeVisible) {
                        ToCartButton(
                            totalPrice = customizedMeal.totalPrice(),
                            onRequestAddMeal = onRequestAddMeal,
                            shouldBeActive = toCartShouldBeActive,
                            isEditMode = isEditMode,
                            onEdit = {
                                onEdit()
                                onClose()
                            },
                            onMissingRequiredOptions = {
                                onEvent(MealDetailsEvent.OnToCartClickBeforeMandatoryChoice)
                            }
                        )
                    }
                }
            )

            // Кнопка "В корзину", закреплённая внизу (только если нет клавиатуры)
            if (!imeVisible) {
                ToCartButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Colors.Transparent),
                    totalPrice = customizedMeal.totalPrice(),
                    onRequestAddMeal = onRequestAddMeal,
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
}
