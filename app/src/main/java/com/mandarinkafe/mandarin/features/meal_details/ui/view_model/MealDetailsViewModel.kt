package com.mandarinkafe.mandarin.features.meal_details.ui.view_model

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.BaseViewModel
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsContract.MealDetailsEffect
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.meal_details.ui.view_model.MealDetailsContract.MealDetailsState
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : BaseViewModel<MealDetailsEvent, MealDetailsEffect, MealDetailsState>() {
    override fun setInitialState() = MealDetailsState()

    init {
        getAddons()
    }

    override fun onEvent(event: MealDetailsEvent) {
        when (event) {
            is MealDetailsEvent.ToggleFavorite -> toggleFavorite()
            is MealDetailsEvent.ChangeAdds -> changeAdds(
                add = event.add,
                isAdded = event.isChecked
            )

            is MealDetailsEvent.ChooseSingleModifier -> chooseSingleModifiers(
                modifierGroup = event.modifierGroup
            )

            is MealDetailsEvent.ChooseMultiModifiers -> chooseMultiModifiers(
                group = event.modifierGroup,
                item = event.modifierItem, isChecked = event.isChecked
            )

            is MealDetailsEvent.SetItem -> setMeal(item = event.item)
            is MealDetailsEvent.ChooseCategory -> chooseCategory(newIndex = event.newIndex)

        }
    }

    private fun chooseMultiModifiers(
        group: ModifierGroup,
        item: ModifierItem,
        isChecked: Boolean
    ) {
        setState {
            val currentItem = customizedMeal ?: return@setState this
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

            copy(
                customizedMeal = currentItem.copy(modifiers = modifiersList)
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

            copy(
                customizedMeal = currentItem.copy(modifiers = modifiersList)
            )

        }
    }

    private fun chooseCategory(newIndex: Int) {
        if (newIndex >= 0) {
            setState {
                copy(
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

            setState {
                val customizedMeal = customizedMeal
                if (customizedMeal != null) {
                    copy(
                        customizedMeal = customizedMeal.copy(
                            meal = customizedMeal.meal.copy(isFavorite = isNowFavorite)
                        )
                    )
                } else {
                    this
                }
            }
        }
    }

    private fun setMeal(item: CustomizedMeal) {
        setState {
            copy(customizedMeal = item)
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

    private fun getAddons() {
        setState { copy(isLoading = true) }

        if (!state.value.pizzaAds.isEmpty()) {
            setState { copy(isLoading = false) }
        } else {
            viewModelScope.launch {
                menuInteractor.getAddons().collectLatest { result ->
                    setLoading(result is Resource.Loading)
                    when (result) {
                        is Resource.Success -> setData(result.data)
                        is Resource.Error -> setError(result.message)
                        is Resource.Loading -> {}
                    }
                }
            }
        }
    }

    private fun setData(data: List<MealAdditionalCategory>?) {
        if (!data.isNullOrEmpty()) {
            setState {
                copy(
                    pizzaAds = data,
                    errorMessage = null
                )
            }
        }
    }

    private fun setError(errorMessage: String?) {
        setState { copy(errorMessage = errorMessage) }
    }

    private fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    //                (adds, errorMessage) ->
//                    if (!adds.isNullOrEmpty()) {
//                        setState { copy(isLoading = false, pizzaAds = adds) }
//                    } else {
//                        // Обработка ошибки
//                        setState {
//                            copy(
//                                isLoading = false,
//                                errorMessage = errorMessage
//                            )
//                        }
//                    }
//                }

}


