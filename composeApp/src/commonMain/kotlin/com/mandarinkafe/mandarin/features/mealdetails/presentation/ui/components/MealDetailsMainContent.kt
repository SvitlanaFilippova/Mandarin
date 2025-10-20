package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.isOnlySingleRequiredChoice
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals.AddsItem
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals.ChosenOptions
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.modifiers.ModifierGroupItem
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent.SetComment
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Constants.SCROLL_TARGET_KEY
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MealDetailsMainContent(
    customizedMeal: CustomizedMeal,
    listState: LazyListState,
    selectedTabIndex: Int,
    addons: List<MealAdditionalCategory>,
    chosenModifiers: List<ModifierGroup>,
    onMakeMoreDeliciousClick: () -> Unit,
    onEvent: (MealDetailsEvent) -> Unit,
    comment: String,
    bottomContent: @Composable () -> Unit = { },
    imeVisible: Boolean,
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
            MealInfo(meal = meal)
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
                Text(
                    text = stringResource(MR.strings.adds),
                    modifier = Modifier.padding(
                        start = Dimens.MarginSmall8,
                        top = Dimens.MarginBig24,
                        bottom = Dimens.MarginSmall8
                    ),
                    style = Typography.TitleStyle
                )
            }

            if (addons.size > 1) {
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
            }
            if (addons.isNotEmpty()) {
                val addsItems =
                    addons[selectedTabIndex].items
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

        // Поле для комментария
        item {
            MyTextField(
                modifier = Modifier.padding(
                    top = Dimens.MarginSmall8,
                    bottom = Dimens.MarginBig24
                ),
                value = comment,
                labelRes = MR.strings.comment_for_meal,
                onValueChange = { onEvent(SetComment(it)) }
            )
        }

        // Отступ для кнопки "В корзину" или сама кнопка (если открыта клавиатура)
        item {
            if (imeVisible) {
                bottomContent()
            } else {
                Spacer(modifier = Modifier.height(Dimens.MarginForCartButton72))
            }
        }
    }
}