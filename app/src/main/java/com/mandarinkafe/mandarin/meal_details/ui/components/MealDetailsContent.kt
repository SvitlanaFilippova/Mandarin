package com.mandarinkafe.mandarin.meal_details.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.totalPrice
import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.meal_details.ui.components.modifiers.ModifierMultiSelectItem
import com.mandarinkafe.mandarin.meal_details.ui.components.modifiers.ModifierSingleSelectItem
import com.mandarinkafe.mandarin.meal_details.ui.components.pizza_ads.AddsCategoryTabsRow
import com.mandarinkafe.mandarin.meal_details.ui.components.pizza_ads.AddsItem
import com.mandarinkafe.mandarin.meal_details.ui.view_model.MealDetailsContract
import com.mandarinkafe.mandarin.meal_details.ui.view_model.MealDetailsContract.Event

@Composable
fun MealDetailsContent(
    state: MealDetailsContract.State,
    onEvent: (Event) -> Unit,
    onAddToCart: (CartItem) -> Unit,
    onClose: () -> Unit,
    initItem: CartItem
) {
    val customizedMeal = state.customizedMeal ?: initItem
    val meal = customizedMeal.meal
    val listState = rememberLazyListState()
    val chosenModifiers = state.customizedMeal?.modifiers ?: emptyList()
    val showToCartButton = !(meal.editableType in listOf(
        EditableType.WOK,
        EditableType.MODIFIABLE
    ) && customizedMeal.totalPrice() == 0)

    Column(
        modifier = Modifier
            .padding(Dimens.MarginSmall8)
    ) {
        BottomSheetHeader(
            meal = meal,
            onToggleFavorite = { onEvent(Event.ToggleFavorite) },
            onClose = onClose
        )

        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.MarginForCartButton72),
                state = listState

            ) {
                // Изображение блюда
                if (meal.imageUrl.isNotEmpty()) {
                    item {
                        MealImage(
                            mealImg = meal.imageUrl,
                            mealName = meal.name,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // Текстовая информация о блюде
                item {
                    MealInfo(
                        meal = meal
                    )
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
                            chosenModifiers.find { it.id == modifierGroup.id }?.items?.getOrNull(0)
                        0

                        if (modifierGroup.isSingleChoice) {
                            modifierGroup.items.forEach { item ->
                                ModifierSingleSelectItem(
                                    item = item,
                                    isAdded = item == selectedItem,
                                    onItemSelected = { item ->
                                        onEvent(
                                            Event.ChooseSingleModifier(
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
                                            Event.ChooseMultiModifiers(
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
                        Text(
                            modifier = Modifier
                                .padding(vertical = Dimens.MarginSmall8)
                                .fillMaxWidth(),
                            text = stringResource(R.string.adds),
                            style = Typography.TitleStyle,
                            textAlign = TextAlign.Center
                        )
                    }
                    item {
                        AddsCategoryTabsRow(
                            categories = state.pizzaAds.map { it.name },
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { index -> onEvent(Event.ChooseCategory(index)) }
                        )
                    }

                    val addsItems =
                        state.pizzaAds[selectedTabIndex].mealAdditionals ?: emptyList()

                    itemsIndexed(addsItems) { _, item ->
                        AddsItem(
                            add = item,
                            onCheckedChange = { isChecked, add ->
                                onEvent(
                                    Event.ChangeAdds(
                                        add = add,
                                        isChecked = isChecked
                                    )
                                )
                            },
                            isAdded = customizedMeal.adds.contains(item)
                        )
                    }
                }

            }
            // Кнопка "В корзину", закреплённая внизу
            ToCartButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
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
