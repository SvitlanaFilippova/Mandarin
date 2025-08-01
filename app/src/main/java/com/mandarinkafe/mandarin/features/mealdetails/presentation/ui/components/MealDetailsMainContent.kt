package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.isOnlySingleRequiredChoice
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals.AddsItem
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals.ChosenOptions
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.modifiers.ModifierGroupItem
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Constants.SCROLL_TARGET_KEY

@Composable
fun MealDetailsMainContent(
    customizedMeal: CustomizedMeal,
    listState: LazyListState,
    selectedTabIndex: Int,
    addons: List<MealAdditionalCategory>,
    chosenModifiers: List<ModifierGroup>,
    onMakeMoreDeliciousClick: () -> Unit,
    onEvent: (MealDetailsEvent) -> Unit,
) {
    val meal = remember { customizedMeal.meal }
    val shouldShowChosen =
        remember(customizedMeal.isCustomized) { !meal.isOnlySingleRequiredChoice() && customizedMeal.isCustomized }

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
        if (meal.isCustomizable) {
            item(key = SCROLL_TARGET_KEY) {
                MakeMoreDeliciousBlock(onClick = onMakeMoreDeliciousClick)
            }
        }

        // Выбор модификаторов
        if (meal.modifiers.isNotEmpty()) {
            itemsIndexed(meal.modifiers) { index, modifierGroup ->
                ModifierGroupItem(
                    modifierGroup = modifierGroup,
                    chosenModifiers = chosenModifiers,
                    onChooseSingleModifier = { modifierGroup ->
                        onEvent(
                            MealDetailsEvent.ChooseSingleModifier(
                                modifierGroup
                            )
                        )
                    },
                    onChooseMultiModifiers = { modifierGroup, modifierItem, isChecked ->
                        onEvent(
                            MealDetailsEvent.ChooseMultiModifiers(
                                modifierGroup = modifierGroup,
                                modifierItem = modifierItem,
                                isChecked = isChecked
                            )
                        )
                    },
                )
            }
        }
        // Выбор добавок
        if (meal.isAddable) {
            item {
                AddsHeader(
                    selectedTabIndex = selectedTabIndex,
                    categories = addons.map { it.name },
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
                addons[selectedTabIndex].mealAdditionals ?: emptyList()
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
        if (shouldShowChosen) {
            item {
                ChosenOptions(
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
}