package com.mandarinkafe.mandarin.features.meal_details.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {
    private val _state =
        MutableStateFlow(MealDetailsContract.State())
    val state: StateFlow<MealDetailsContract.State> = _state.asStateFlow()

    init {
        getAddons()
    }

    fun onEvent(event: MealDetailsContract.Event) {
        when (event) {
            is MealDetailsContract.Event.ToggleFavorite -> toggleFavorite()
            is MealDetailsContract.Event.ChangeAdds -> changeAdds(
                add = event.add,
                isAdded = event.isChecked
            )

            is MealDetailsContract.Event.ChooseSingleModifier -> chooseSingleModifiers(
                modifierGroup = event.modifierGroup
            )

            is MealDetailsContract.Event.ChooseMultiModifiers -> chooseMultiModifiers(
                group = event.modifierGroup,
                item = event.modifierItem, isChecked = event.isChecked
            )

            is MealDetailsContract.Event.SetItem -> setMeal(item = event.item)
            is MealDetailsContract.Event.ChooseCategory -> chooseCategory(newIndex = event.newIndex)

        }
    }

    private fun chooseMultiModifiers(
        group: ModifierGroup,
        item: ModifierItem,
        isChecked: Boolean
    ) {
        _state.update { currentState ->
            val currentItem = currentState.customizedMeal ?: return
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

            currentState.copy(
                customizedMeal = currentItem.copy(modifiers = modifiersList)
            )
        }
    }

    private fun chooseSingleModifiers(modifierGroup: ModifierGroup) {
        _state.update { currentState ->
            val currentItem = currentState.customizedMeal ?: return
            val modifiersList = currentItem.modifiers.toMutableList()
            val groupIndex = modifiersList.indexOfFirst { it.id == modifierGroup.id }

            if (groupIndex != -1) {
                modifiersList.removeAt(groupIndex)
                modifiersList.add(modifierGroup)
            } else {
                modifiersList.add(modifierGroup)
            }

            currentState.copy(
                customizedMeal = currentItem.copy(modifiers = modifiersList)
            )

        }
    }

    private fun chooseCategory(newIndex: Int) {
        if (newIndex >= 0) {
            _state.update {
                it.copy(
                    selectedTabIndex = newIndex,
                )
            }
        }
    }

    private fun toggleFavorite() {
        val meal = state.value.customizedMeal?.meal ?: return

        viewModelScope.launch {
            val isNowFavorite = if (meal.isFavorite) {
                favoritesInteractor.removeFromFavorites(meal.toFavoriteMeal())
                false
            } else {
                favoritesInteractor.addToFavorites(meal.toFavoriteMeal())
                true
            }

            _state.update { currentState ->
                val customizedMeal = currentState.customizedMeal
                if (customizedMeal != null) {
                    currentState.copy(
                        customizedMeal = customizedMeal.copy(
                            meal = customizedMeal.meal.copy(isFavorite = isNowFavorite)
                        )
                    )
                } else {
                    currentState
                }
            }
        }
    }

    private fun setMeal(item: CartItem) {
        _state.update {
            it.copy(customizedMeal = item)
        }
    }

    private fun changeAdds(add: MealAdditional, isAdded: Boolean) {
        _state.update { currentState ->
            val currentMeal = currentState.customizedMeal ?: return
            val currentAdds = currentMeal.adds.toMutableList()

            if (isAdded) {
                if (!currentAdds.contains(add)) currentAdds += add
            } else {
                currentAdds.remove(add)
            }

            currentState.copy(
                customizedMeal = currentMeal.copy(adds = currentAdds)
            )

        }
    }

    private fun getAddons() {
        _state.update { it.copy(isLoading = true) }

        if (!state.value.pizzaAds.isEmpty()) {
            _state.update { it.copy(isLoading = false) }
        } else {
            viewModelScope.launch {
                menuInteractor.getAddons().collect { (adds, errorMessage) ->
                    if (!adds.isNullOrEmpty()) {
                        _state.update { it.copy(isLoading = false, pizzaAds = adds) }
                    } else {
                        // Обработка ошибки
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = errorMessage
                            )
                        }
                    }
                }
            }
        }
    }
}

