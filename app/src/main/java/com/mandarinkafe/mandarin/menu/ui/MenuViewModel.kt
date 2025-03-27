package com.mandarinkafe.mandarin.menu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.cart.Cart
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuInteractor: MenuInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(MenuContract.State()) // для хранения состояния ЮИ
    val state: StateFlow<MenuContract.State> = _state.asStateFlow()

    private val _effect =
        MutableSharedFlow<MenuContract.Effect>() // для одноразовых событий. Например, показа снекбар
    val effect: SharedFlow<MenuContract.Effect> = _effect.asSharedFlow()

    init {
        onEvent(MenuContract.Event.LoadMenu)
    }

    fun onEvent(event: MenuContract.Event) {
        when (event) {
            is MenuContract.Event.LoadMenu -> loadMenu()
            is MenuContract.Event.ToggleFavorite -> toggleFavorite(event.meal)
            is MenuContract.Event.AddToCart -> addToCart(event.meal)
            is MenuContract.Event.RemoveFromCart -> removeFromCart(event.meal)
            is MenuContract.Event.ScrollToCategory -> scrollToCategory(event.categoryId)
            is MenuContract.Event.ScrollToSubCategory -> scrollToSubCategory(event.categoryId)
        }
    }

    private fun scrollToSubCategory(categoryId: String) {
        val newIndex = state.value.menuItems
            .filterIsInstance<MenuRVItem.SubHeaderItem>()
            .indexOfFirst { it.id == categoryId }
        if (newIndex >= 0) {
            _state.update { it.copy(selectedSubTabIndex = newIndex) }
        }
    }

    private fun scrollToCategory(categoryId: String) {
        val newIndex = state.value.menuItems
            .filterIsInstance<MenuRVItem.HeaderItem>()
            .indexOfFirst { it.id == categoryId }
        if (newIndex >= 0) {
            _state.update { it.copy(selectedTabIndex = newIndex, selectedSubTabIndex = -1) }
        }
    }

    private fun selectCategory(index: Int) {
        _state.value = state.value.copy(selectedTabIndex = index, selectedSubTabIndex = -1)
    }

    private fun selectSubCategory(index: Int) {
        _state.value = state.value.copy(selectedSubTabIndex = index)
    }



    private fun loadMenu() {
        _state.update { it.copy(isLoading = true) }
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
                _state.update {
                    it.copy(isLoading = false, menuItems = menu)
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = " Кажется, что-по пошло не так - в меню ничего нет"
                    )
                }

            }
        }
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
        /* Жду реализацию логики корзины */
    }
}
