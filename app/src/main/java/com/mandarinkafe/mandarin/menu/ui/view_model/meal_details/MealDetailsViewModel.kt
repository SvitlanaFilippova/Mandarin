package com.mandarinkafe.mandarin.menu.ui.view_model.meal_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.cart.Cart
import com.mandarinkafe.mandarin.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealAdditional
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsContract.Event
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
        onEvent(Event.GetAddons)
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.ToggleFavorite -> toggleFavorite()
            is Event.GetAddons -> getAddons()
            is Event.ChangeAdds -> changeAdds(event.add, event.isChecked)
            is Event.SetMeal -> setMeal(event.meal)
            is Event.AddToCart -> addToCart()
            is Event.ChooseCategory -> chooseCategory(event.newIndex)
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

    private fun addToCart() {
        val meal = state.value.meal
        if (meal != null) {
            Cart.addItem(meal)
            Log.d("DEBUG", "MealDetailsViewModel addToCart for $meal")
        }
    }

    private fun toggleFavorite() {
        val meal = state.value.meal
        if (meal != null) {
            viewModelScope.launch {
                val isNowFavorite = if (meal.isFavorite) {
                    favoritesInteractor.removeFromFavorites(meal)
                    false
                } else {
                    favoritesInteractor.addToFavorites(meal)
                    true
                }

                _state.update { currentState ->
                    currentState.copy(
                        meal = currentState.meal?.copy(isFavorite = isNowFavorite)
                    )
                }
            }
        }

    }

    private fun setMeal(meal: Meal?) {
        _state.update {
            it.copy(meal = meal)
        }
    }

    private fun changeAdds(add: MealAdditional, isAdded: Boolean) {
        _state.update { currentState ->
            val currentMeal = currentState.meal ?: return
            val currentAdds = currentMeal.adds.toMutableList()

            if (isAdded) {
                if (!currentAdds.contains(add)) currentAdds += add
            } else {
                currentAdds.remove(add)
            }

            currentState.copy(
                meal = currentMeal.copy(adds = currentAdds)
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

