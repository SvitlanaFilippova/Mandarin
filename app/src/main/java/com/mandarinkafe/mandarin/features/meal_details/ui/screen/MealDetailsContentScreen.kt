package com.mandarinkafe.mandarin.features.meal_details.ui.screen

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.extensions.hasSelectedAllRequiredModifiers
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.extensions.isOnlySingleRequiredChoice
import com.mandarinkafe.mandarin.core.domain.models.extensions.totalPrice
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.meal_details.ui.components.BottomSheetHeader
import com.mandarinkafe.mandarin.features.meal_details.ui.components.MakeMoreDeliciousBlock
import com.mandarinkafe.mandarin.features.meal_details.ui.components.MealInfo
import com.mandarinkafe.mandarin.features.meal_details.ui.components.ToCartButton
import com.mandarinkafe.mandarin.features.meal_details.ui.components.modifiers.ModifierMultiSelectItem
import com.mandarinkafe.mandarin.features.meal_details.ui.components.modifiers.ModifierSingleSelectItem
import com.mandarinkafe.mandarin.features.meal_details.ui.components.pizza_ads.AddsCategoryTabsRow
import com.mandarinkafe.mandarin.features.meal_details.ui.components.pizza_ads.AddsItem
import com.mandarinkafe.mandarin.features.meal_details.ui.components.pizza_ads.ChosenOptionsChipsRow
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsContract
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.util.Constants.SCROLL_TARGET_KEY
import kotlinx.coroutines.launch

@Composable
fun MealDetailsContentScreen(
    state: MealDetailsContract.MealDetailsState,
    onEvent: (MealDetailsEvent) -> Unit,
    onAddToCart: (CustomizedMeal) -> Unit,
    onClose: () -> Unit,
    initItem: CustomizedMeal
) {
    val customizedMeal = state.customizedMeal ?: initItem
    val meal = customizedMeal.meal
    val listState = rememberLazyListState()
    val chosenModifiers = state.customizedMeal?.modifiers ?: emptyList()
    val showToCartButton = customizedMeal.hasSelectedAllRequiredModifiers()
    val coroutineScope = rememberCoroutineScope()
    val scrollTargetKey = SCROLL_TARGET_KEY
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
                if (meal.isCustomizable()) {
                    item(key = scrollTargetKey) {
                        MakeMoreDeliciousBlock(onClick = handleMakeMoreDeliciousClick)
                    }
                }

                // Выбор модификаторов
                if (meal.modifiers.isNotEmpty()) {
                    itemsIndexed(meal.modifiers) { index, modifierGroup ->
                        val modifierGroupName = if (modifierGroup.isRequired) {
                            modifierGroup.name + " *"
                        } else {
                            modifierGroup.name
                        }
                        Text(
                            modifier = Modifier.padding(vertical = Dimens.MarginSmall8),
                            text = modifierGroupName,
                            style = Typography.TitleStyle
                        )
                        Log.d(
                            "DEBUG MealDetailsBottomSheet",
                            "modifier ${modifierGroup.name} isRequired ${modifierGroup.isRequired}, maxQ: " +
                                    "${modifierGroup.maxQuantity}"
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
                            Spacer(modifier = Modifier.height(Dimens.MarginBig24))
                        }
                    }
                }
                // Выбор добавок
                if (meal.isAddable) {
                    item {
                        Text(
                            text = stringResource(id = R.string.adds),
                            modifier = Modifier.padding(
                                top = Dimens.MarginStandard16,
                                bottom = Dimens.MarginSmall8
                            ),
                            style = Typography.TitleStyle
                        )
                    }

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
                // Если это не позиция, где должна быть выбрана всего одная опция - показываем перечень выбранных опций
                if (!meal.isOnlySingleRequiredChoice() && customizedMeal.isCustomized()) {
                    item {
                        Text(
                            modifier = Modifier.padding(
                                top = Dimens.MarginBig24,
                                bottom = Dimens.MarginSmall8
                            ),
                            text = stringResource(id = R.string.chosen),
                            style = Typography.RegularLightTextStyle,
                            fontWeight = FontWeight.Light,
                            color = Colors.LightGrey
                        )
                    }

                    item {
                        ChosenOptionsChipsRow(
                            adds = customizedMeal.adds,
                            onAddClick = { add ->
                                onEvent(
                                    MealDetailsEvent.ChangeAdds(
                                        add = add,
                                        isChecked = false
                                    )
                                )
                            },
                            modifiers = customizedMeal.modifiers,
                            onModifierClick = { group, item ->
                                onEvent(
                                    MealDetailsEvent.ChooseMultiModifiers(
                                        modifierGroup = group,
                                        modifierItem = item,
                                        isChecked = false
                                    )
                                )
                            }
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
