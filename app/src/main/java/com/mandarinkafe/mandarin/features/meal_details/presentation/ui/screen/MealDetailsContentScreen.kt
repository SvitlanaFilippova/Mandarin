package com.mandarinkafe.mandarin.features.meal_details.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.BottomSheetHeader
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.MakeMoreDeliciousBlock
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.MealInfo
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.ToCartButton
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.modifiers.ModifierMultiSelectItem
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.modifiers.ModifierSingleSelectItem
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.pizza_ads.AddsCategoryTabsRow
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.pizza_ads.AddsItem
import com.mandarinkafe.mandarin.features.meal_details.presentation.ui.components.pizza_ads.ChosenOptionsChipsRow
import com.mandarinkafe.mandarin.features.meal_details.presentation.view_model.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.meal_details.presentation.view_model.MealDetailsContract.MealDetailsState
import com.mandarinkafe.mandarin.util.Constants.SCROLL_TARGET_KEY
import kotlinx.coroutines.launch

@Composable
fun MealDetailsContentScreen(
    state: MealDetailsState,
    initItem: CustomizedMeal,
    isFavorite: Boolean,
    isEditMode: Boolean,
    onClose: () -> Unit,
    onEvent: (MealDetailsEvent) -> Unit,
    onAddToCart: () -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
) {

    val customizedMeal = state.customizedMeal ?: initItem
    val meal = customizedMeal.meal
    val listState = rememberLazyListState()
    val chosenModifiers = state.customizedMeal?.modifiers ?: emptyList()
    val toCartShouldBeActive = customizedMeal.hasSelectedAllRequiredModifiers()
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
            onToggleFavorite = { onToggleFavorite() },
            onClose = onClose,
            isFavorite = isFavorite
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
                        Log.d(
                            "DEBUG MODIFIER",
                            "modifierGroup ${modifierGroup.name} isRequired:${modifierGroup.isRequired}"
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(Dimens.MarginSmall8)
                        ) {
                            Text(
                                text = modifierGroup.name,
                                style = Typography.TitleStyle
                            )
                            if (modifierGroup.isRequired) {
                                Text(
                                    modifier = Modifier.padding(start = Dimens.MarginSmall8),
                                    text = stringResource(R.string.mandatory),
                                    style = Typography.RegularTextStyle,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
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
                                start = Dimens.MarginSmall8,
                                top = Dimens.MarginBig24,
                                bottom = Dimens.MarginSmall8
                            ),
                            style = Typography.TitleStyle
                        )
                    }
                    // Категории добавок
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
                    // Список доступных добавок
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
                // Если это НЕ позиция, где должна быть выбрана всего одная опция - показываем перечень выбранных опций
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

                // Отступ для кнопки "В корзину"
                item { Spacer(modifier = Modifier.height(Dimens.MarginForCartButton72)) }

            }
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
