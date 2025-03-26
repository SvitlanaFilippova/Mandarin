package com.mandarinkafe.mandarin.menu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.cart.Cart
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<MenuViewState>(MenuViewState.Loading)
    val state: LiveData<MenuViewState> = _state

    fun handleIntent(intent: MenuIntent) {
        when (intent) {
            is MenuIntent.LoadMenu -> loadMenu()
            is MenuIntent.SelectCategory -> selectCategory(intent.index)
            is MenuIntent.SelectSubCategory -> selectSubCategory(intent.index)
            is MenuIntent.ToggleFavorite -> toggleFavorite(intent.meal)
            is MenuIntent.AddToCart -> addToCart(intent.meal)
            is MenuIntent.RemoveFromCart -> removeFromCart(intent.meal)
        }
    }

    private fun loadMenu() {
        _state.value = MenuViewState.Loading
        viewModelScope.launch {

            /*  Для получения реального меню из ikko
            menuInteractor.getMenu()
            .collect { (menu, errorMessage) ->
            if (!menu.isNullOrEmpty()) {
                _state.value = MenuViewState.Content(menuItems = menu)
            } else {
                _state.value = MenuViewState.Error
            }
*/

            /*  Для получения мок-меню */
            val menu = menuInteractor.getMockMenu()
            if (menu.isNotEmpty()) {
                _state.value = MenuViewState.Content(menuItems = menu)
            } else {
                _state.value = MenuViewState.Error

            }
        }
    }

    private fun selectCategory(index: Int) {
        val currentState = _state.value as? MenuViewState.Content ?: return
        _state.value = currentState.copy(selectedTabIndex = index, selectedSubTabIndex = -1)
    }

    private fun selectSubCategory(index: Int) {
        val currentState = _state.value as? MenuViewState.Content ?: return
        _state.value = currentState.copy(selectedSubTabIndex = index)
    }

    private fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            if (meal.isFavorite) {
                favoritesInteractor.removeFromFavorites(meal)
            } else {
                favoritesInteractor.addToFavorites(meal)
            }
        }
    }

    private fun addToCart(meal: Meal) {
        Cart.addItem(meal)
    }

    private fun removeFromCart(meal: Meal) {
        // TODO()
    }
}
