package com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.mealdetails.domain.usecase.GetAddonsUseCase
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.ShowMaxModifiersQuantity
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.ShowRequiredModifiersDialog
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsState
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    private val getAddonsUseCase: GetAddonsUseCase,
) : BaseViewModel<MealDetailsEvent, MealDetailsEffect, MealDetailsState>() {
    override fun setInitialState() = MealDetailsState()

    override fun onEvent(event: MealDetailsEvent) {
        when (event) {
            is MealDetailsEvent.SetInitItem -> setInitData(item = event.item)
            is MealDetailsEvent.ChangeAdds -> changeAdds(
                add = event.add,
                isAdded = event.isChecked
            )

            is MealDetailsEvent.ChooseSingleModifier -> chooseSingleModifiers(
                modifierGroup = event.modifierGroup
            )

            is MealDetailsEvent.ChooseMultiModifiers -> chooseMultiModifiers(
                group = event.modifierGroup,
                item = event.modifierItem,
                isChecked = event.isChecked
            )

            is MealDetailsEvent.ChooseCategory -> chooseAdsCategory(newIndex = event.newIndex)
            is MealDetailsEvent.OnToCartClickBeforeMandatoryChoice -> sendEffect(
                ShowRequiredModifiersDialog
            )

            is MealDetailsEvent.SetComment -> setComment(event.text)
        }
    }

    private fun setComment(text: String) {
        setState { copy(comment = text) }
    }

    private fun setInitData(item: CartItem) {
        viewModelScope.launch {
            setState {
                copy(customizedMeal = item.customizedMeal, comment = item.comment)
            }
            with(item.customizedMeal.meal) {
                if (isAddable) {
                    getAddons(path = categoryPath)
                }
            }
        }
    }

    private fun chooseMultiModifiers(
        group: ModifierGroup,
        item: ModifierItem,
        isChecked: Boolean
    ) {
        setState {
            val currentItem = customizedMeal ?: return@setState this

            val selectedCount = getSelectedModifiersCount(currentItem, group)
            if (isLimitExceeded(group, isChecked, selectedCount)) {
                showLimitExceededEffect(group)
                return@setState this
            }

            val updatedModifiers = updateModifierList(currentItem, group, item, isChecked)

            copy(
                customizedMeal = currentItem.copy(modifiers = updatedModifiers)
            )
        }
    }

    private fun chooseSingleModifiers(modifierGroup: ModifierGroup) {
        setState {
            val currentItem = customizedMeal ?: return@setState this
            val modifiersList = currentItem.modifiers.toMutableList()
            val groupIndex = modifiersList.indexOfFirst { it.id == modifierGroup.id }

            if (groupIndex != -1) {
                modifiersList.removeAt(groupIndex)
                modifiersList.add(modifierGroup)
            } else {
                modifiersList.add(modifierGroup)
            }

            // Сортировка: сначала SingleChoice группы
            modifiersList.sortByDescending { it.isSingleChoice }
            copy(
                customizedMeal = currentItem.copy(modifiers = modifiersList)
            )

        }
    }

    private fun getSelectedModifiersCount(currentItem: CustomizedMeal, group: ModifierGroup): Int {
        return currentItem.modifiers
            .find { it.id == group.id }
            ?.items
            ?.size ?: 0
    }

    private fun isLimitExceeded(
        group: ModifierGroup,
        isChecked: Boolean,
        selectedCount: Int
    ): Boolean {
        return group.maxQuantity > 1 && isChecked && selectedCount >= group.maxQuantity
    }

    private fun showLimitExceededEffect(group: ModifierGroup) {
        sendEffect(
            ShowMaxModifiersQuantity(
                max = group.maxQuantity,
                groupName = group.name
            )
        )
    }

    private fun updateModifierList(
        currentItem: CustomizedMeal,
        group: ModifierGroup,
        item: ModifierItem,
        isChecked: Boolean
    ): List<ModifierGroup> {
        val modifiersList = currentItem.modifiers.toMutableList()
        val groupIndex = modifiersList.indexOfFirst { it.id == group.id }

        if (groupIndex != -1) {
            val existingGroup = modifiersList[groupIndex]
            val updatedItems = existingGroup.items.toMutableList()

            if (isChecked) {
                if (item !in updatedItems) updatedItems.add(item)
            } else {
                updatedItems.remove(item)
            }

            if (updatedItems.isEmpty()) {
                modifiersList.removeAt(groupIndex)
            } else {
                modifiersList[groupIndex] = existingGroup.copy(items = updatedItems)
            }
        } else {
            modifiersList.add(group.copy(items = listOf(item)))
        }

        // Сортировка: сначала SingleChoice группы
        modifiersList.sortByDescending { it.isSingleChoice }

        return modifiersList
    }

    private fun chooseAdsCategory(newIndex: Int) {
        if (newIndex >= 0) {
            setState {
                copy(
                    selectedTabIndex = newIndex,
                )
            }
        }
    }

    private fun changeAdds(add: MealAdditional, isAdded: Boolean) {
        setState {
            val currentMeal = customizedMeal ?: return@setState this
            val currentAdds = currentMeal.adds.toMutableList()

            if (isAdded) {
                if (!currentAdds.contains(add)) currentAdds += add
            } else {
                currentAdds.remove(add)
            }

            copy(
                customizedMeal = currentMeal.copy(adds = currentAdds)
            )

        }
    }

    private fun getAddons(path: List<String>) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            getAddonsUseCase(categoryPath = path).collectLatest { result ->
                setLoading(result is Loading)
                when (result) {
                    is Success -> setData(result.data)
                    is Loading -> {}
                    is Idle -> {}
                    else -> setError(result)
                }
            }
        }
    }

    private fun setData(data: List<MealAdditionalCategory>?) {
        if (!data.isNullOrEmpty()) {
            setState {
                copy(
                    addons = data,
                    errorMessage = null
                )
            }
        }
    }

    private fun setError(resource: Resource<*>) {
        val error = when (resource) {
            is Resource.ErrorNoInternet<*> -> UiError.NoInternet
            is Resource.ErrorEmptyData<*> -> UiError.AddonsEmpty
            is ErrorOther<*> -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}