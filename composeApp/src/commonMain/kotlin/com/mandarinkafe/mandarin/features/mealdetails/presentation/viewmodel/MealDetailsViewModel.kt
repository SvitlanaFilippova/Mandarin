package com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.cart.domain.Mapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.domain.api.CartInteractor
import com.mandarinkafe.mandarin.features.cart.domain.models.MealAddResult
import com.mandarinkafe.mandarin.features.mealdetails.domain.api.GetAddonsUseCase
import com.mandarinkafe.mandarin.features.mealdetails.domain.api.GetMealByIdUseCase
import com.mandarinkafe.mandarin.features.mealdetails.domain.api.ReconstructCustomizedMealUseCase
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.CloseAndShowMessage
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
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MealDetailsViewModel(
    private val getAddonsUseCase: GetAddonsUseCase,
    private val getMealById: GetMealByIdUseCase,
    private val reconstructCustomizedMealUseCase: ReconstructCustomizedMealUseCase,
    private val cartInteractor: CartInteractor,
) : BaseViewModel<MealDetailsEvent, MealDetailsEffect, MealDetailsState>() {
    override fun setInitialState() = MealDetailsState()

    override fun onEvent(event: MealDetailsEvent) {
        when (event) {
            is MealDetailsEvent.SetInitData -> setInitData(
                item = event.item,
                mealId = event.mealId,
                isEditMode = event.isEditMode,
                addsIds = event.addsIds,
                modifierIds = event.modifierIds,
                comment = event.comment,
                cartItemId = event.cartItemId,
            )

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
            is MealDetailsEvent.TryAddMeal -> tryAddMeal(item = event.item)
            is MealDetailsEvent.EditMealInCart -> editMealInCart(
                newItem = event.newItem,
                oldItem = event.oldItem
            )
        }
    }

    private fun tryAddMeal(item: CartItem?) {
        if (item != null) {
            viewModelScope.launch {
                val result = cartInteractor.tryAddMeal(item)
                when (result) {
                    is MealAddResult.AlreadyExistBaseMeal -> showReplaceOrAddDialog(
                        newItem = item,
                        existingItem = result.existing,
                        message = MR.strings.replace_or_add_message
                    )

                    is MealAddResult.Added -> showMessageAndCloseMealDetails(
                        message = MR.strings.added_to_cart_template,
                        mealName = item.customizedMeal.meal.name
                    )
                }
            }

        }
    }

    private fun showMessageAndCloseMealDetails(
        message: StringResource?,
        mealName: String,
    ) {
        sendEffect(
            CloseAndShowMessage(message = message, mealName = mealName)
        )
    }

    private fun showReplaceOrAddDialog(
        newItem: CartItem,
        existingItem: CartItem,
        message: StringResource,
    ) {
        sendEffect(
            MealDetailsEffect.AskReplaceOrAdd(
                message = message,
                mealName = newItem.customizedMeal.meal.name,
                onAddNew = { addItem(newItem) },
                onReplace = {
                    editMealInCart(
                        newItem = newItem,
                        oldItem = existingItem
                    )
                }
            )
        )
    }

    private fun addItem(item: CartItem) {
        viewModelScope.launch {
            cartInteractor.addItem(cartItem = item)
        }

        showMessageAndCloseMealDetails(
            message = MR.strings.added_to_cart_template,
            mealName = item.customizedMeal.meal.name
        )

    }

    private fun editMealInCart(newItem: CartItem, oldItem: CartItem? = null) {
        viewModelScope.launch {
            val wasUpdated = cartInteractor.updateItem(newCartItem = newItem, oldItem = oldItem)
            val message = if (wasUpdated) MR.strings.edited_template else null
            val mealName = newItem.customizedMeal.meal.name

            showMessageAndCloseMealDetails(
                message = message,
                mealName = mealName
            )
        }
    }

    private fun loadMealById(mealId: String, isEditMode: Boolean) {
        viewModelScope.launch {
            setLoading()
            val result = getMealById(mealId)
            when (result) {
                is Success -> {
                    val meal = result.data
                    if (meal != null) {
                        val item = meal.toCartItem()
                        applyMealData(item, isEditMode)
                    } else {
                        setError(result)
                    }
                }

                else -> setError(result)
            }
        }
    }

    private fun setInitData(
        item: CartItem?,
        mealId: String?,
        isEditMode: Boolean,
        addsIds: List<String>,
        modifierIds: Map<String, List<String>>,
        comment: String,
        cartItemId: String?,
    ) {
        when {
            item != null -> viewModelScope.launch { applyMealData(item, isEditMode) }
            mealId != null -> {
                if (hasCustomizationParams(addsIds, modifierIds, comment, cartItemId)) {
                    // Реконструируем CustomizedMeal из параметров навигации
                    reconstructCustomizedMeal(
                        mealId,
                        addsIds,
                        modifierIds,
                        comment,
                        cartItemId,
                        isEditMode
                    )
                } else {
                    // Просто загружаем блюдо по ID
                    loadMealById(mealId, isEditMode)
                }
            }
        }
    }

    private fun hasCustomizationParams(
        addsIds: List<String>,
        modifierIds: Map<String, List<String>>,
        comment: String,
        cartItemId: String?,
    ): Boolean {
        return addsIds.isNotEmpty() ||
                modifierIds.isNotEmpty() ||
                comment.isNotEmpty() ||
                cartItemId != null
    }

    private fun reconstructCustomizedMeal(
        mealId: String,
        addsIds: List<String>,
        modifierIds: Map<String, List<String>>,
        comment: String,
        cartItemId: String?,
        isEditMode: Boolean,
    ) {
        viewModelScope.launch {
            setLoading()
            val result = reconstructCustomizedMealUseCase(
                mealId = mealId,
                addsIds = addsIds,
                modifierIds = modifierIds,
                comment = comment,
                cartItemId = cartItemId,
            )
            when (result) {
                is Success -> {
                    val cartItem = result.data
                    cartItem?.let {
                        applyMealData(it, isEditMode)
                    }
                }

                else -> setError(result)
            }
        }
    }

    private fun applyMealData(item: CartItem, isEditMode: Boolean) {
        setState {
            copy(
                isLoading = false,
                initItem = item,
                customizedMeal = item.customizedMeal,
                comment = item.comment,
                isEditMode = isEditMode
            )
        }
        with(item.customizedMeal.meal) {
            if (isAddable) {
                getAddons(path = categoryPath)
            }
        }
    }

    private fun chooseMultiModifiers(
        group: ModifierGroup,
        item: ModifierItem,
        isChecked: Boolean,
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
        selectedCount: Int,
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
        isChecked: Boolean,
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
                    is Success -> setAddonsData(result.data)
                    is Loading -> {}
                    is Idle -> {}
                    else -> setError(result)
                }
            }
        }
    }

    private fun setAddonsData(data: List<MealAdditionalCategory>?) {
        if (!data.isNullOrEmpty()) {
            setState {
                copy(
                    addons = data,
                    errorMessage = null
                )
            }
        }
    }

    private fun setComment(text: String) {
        setState { copy(comment = text) }
    }

    private fun setError(resource: Resource<*>) {
        val error = when (resource) {
            is Resource.ErrorNoInternet<*> -> UiError.NoInternet
            is Resource.ErrorEmptyData<*> -> UiError.DataEmpty
            is ErrorOther<*> -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}
