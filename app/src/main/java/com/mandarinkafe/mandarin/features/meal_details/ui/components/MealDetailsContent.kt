package com.mandarinkafe.mandarin.features.meal_details.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.totalPrice
import com.mandarinkafe.mandarin.features.meal_details.ui.components.modifiers.ModifierMultiSelectItem
import com.mandarinkafe.mandarin.features.meal_details.ui.components.modifiers.ModifierSingleSelectItem
import com.mandarinkafe.mandarin.features.meal_details.ui.components.pizza_ads.AddsCategoryTabsRow
import com.mandarinkafe.mandarin.features.meal_details.ui.components.pizza_ads.AddsItem
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsContract
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsContract.MealDetailsEvent
import kotlinx.coroutines.launch

@Composable
fun MealDetailsContent(
    state: MealDetailsContract.MealDetailsState,
    onEvent: (MealDetailsEvent) -> Unit,
    onAddToCart: (CartItem) -> Unit,
    onClose: () -> Unit,
    initItem: CartItem
) {
    val customizedMeal = state.customizedMeal ?: initItem
    val meal = customizedMeal.meal
    val listState = rememberLazyListState()
    val chosenModifiers = state.customizedMeal?.modifiers ?: emptyList()
    val showToCartButton =
        !(meal.editableType == EditableType.REQUIRED_SELECTION && customizedMeal.totalPrice() == 0)
    val coroutineScope = rememberCoroutineScope()
    val scrollTargetKey = "scrollTarget"
    val handleMakeMoreDeliciousClick: () -> Unit = remember(listState) {
        {
            coroutineScope.launch {
                val index = listState.layoutInfo.visibleItemsInfo
                    .find { it.key == scrollTargetKey }
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
            onToggleFavorite = { onEvent(MealDetailsEvent.ToggleFavorite) },
            onClose = onClose
        )

        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                state = listState

            ) {

                // Изображение блюда и нформация о нём
                item {
                    MealInfo(
                        meal = meal
                    )
                }

                // Заголовок для модификаторов/добавок, если блюдо и без них можно закаказать
                if (meal.editableType == EditableType.ADDABLE || meal.editableType == EditableType.PIZZA) {
                    item {
                        Text(
                            modifier = Modifier
                                .padding(top = Dimens.MarginSmall8)
                                .fillMaxWidth()
                                .clickable(enabled = true, onClick = handleMakeMoreDeliciousClick),
                            text = stringResource(id = R.string.make_more_delicious_description),
                            style = Typography.RegularLightTextStyle,
                            fontWeight = FontWeight.Bold,
                            color = Colors.White,
                            textAlign = TextAlign.Center
                        )

                    }
                    item(key = scrollTargetKey) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = handleMakeMoreDeliciousClick,
                                modifier = Modifier.size(Dimens.ButtonBox32)
                            ) {
                                Icon(
                                    modifier = Modifier.size(Dimens.ButtonToggleFavorite28),
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(id = R.string.make_more_delicious),
                                    tint = Colors.White
                                )
                            }
                        }
                    }

                }
                // Выбор модификаторов
                if (meal.modifiers.isNotEmpty()) {
                    itemsIndexed(meal.modifiers) { index, modifierGroup ->
                        Text(
                            modifier = Modifier.padding(vertical = Dimens.MarginSmall8),
                            text = modifierGroup.name,
                            style = Typography.TitleStyle
                        )

                        val selectedItem =
                            chosenModifiers.find { it.id == modifierGroup.id }?.items?.getOrNull(
                                0
                            )
                        0

                        if (modifierGroup.isSingleChoice) {
                            modifierGroup.items.forEach { item ->
                                ModifierSingleSelectItem(
                                    item = item,
                                    isAdded = item == selectedItem,
                                    onItemSelected = { item ->
                                        onEvent(
                                            MealDetailsEvent.ChooseSingleModifier(
                                                modifierGroup.copy(
                                                    items = listOf(
                                                        item
                                                    )
                                                )
                                            )
                                        )
                                    }
                                )
                            }

                        } else {
                            modifierGroup.items.forEach { item ->
                                ModifierMultiSelectItem(
                                    item = item,
                                    onCheckedChange = { isChecked ->
                                        onEvent(
                                            MealDetailsEvent.ChooseMultiModifiers(
                                                modifierGroup = modifierGroup,
                                                modifierItem = item,
                                                isChecked = isChecked
                                            )
                                        )
                                    },
                                    isAdded = chosenModifiers.find { it.id == modifierGroup.id }?.items?.contains(
                                        item
                                    ) == true
                                )
                            }

                            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))
                        }
                    }
                }
                // Выбор добавок для пиццы
                if (meal.editableType == EditableType.PIZZA) {
                    val selectedTabIndex = state.selectedTabIndex
                    item {
                        AddsCategoryTabsRow(
                            categories = state.pizzaAds.map { it.name },
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                onEvent(
                                    MealDetailsEvent.ChooseCategory(
                                        index
                                    )
                                )
                            }
                        )
                    }

                    val addsItems =
                        state.pizzaAds[selectedTabIndex].mealAdditionals ?: emptyList()

                    itemsIndexed(addsItems) { _, item ->
                        AddsItem(
                            add = item,
                            onCheckedChange = { isChecked, add ->
                                onEvent(
                                    MealDetailsEvent.ChangeAdds(
                                        add = add,
                                        isChecked = isChecked
                                    )
                                )
                            },
                            isAdded = customizedMeal.adds.contains(item)
                        )
                    }
                }

                // Отступ для кнопки "в корзину"
                item { Spacer(modifier = Modifier.height(Dimens.MarginForCartButton72)) }

            }
            // Кнопка "В корзину", закреплённая внизу
            ToCartButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Colors.Transparent),
                totalPrice = customizedMeal.totalPrice(),
                onClick = {
                    onAddToCart(customizedMeal)
                    onClose()
                },
                shouldBeActive = showToCartButton
            )
        }
    }
}
