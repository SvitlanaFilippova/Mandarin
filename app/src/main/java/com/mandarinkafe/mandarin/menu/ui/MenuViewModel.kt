package com.mandarinkafe.mandarin.menu.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.cart.Cart
import com.mandarinkafe.mandarin.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.domain.models.getName
import com.mandarinkafe.mandarin.menu.domain.usecase.MenuInteractor
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
            is MenuContract.Event.ScrollToCategory -> scrollToCategory(event.newIndex)
            is MenuContract.Event.ScrollToSubCategory -> scrollToSubCategory(event.newIndex)
            is MenuContract.Event.BannerClick -> handleBannerClick(event.targetName)
            is MenuContract.Event.OpenMealCustomization -> openMealCustomization(event.meal)
        }
    }

    private fun openMealCustomization(meal: Meal) {
        viewModelScope.launch {
            _effect.emit(MenuContract.Effect.OpenMealCustomization(meal))
        }
    }

    private fun scrollToCategory(newIndex: Int) {
        if (newIndex >= 0) {
            _state.update { it.copy(selectedTabIndex = newIndex, selectedSubTabIndex = -1) }
        }
    }

    private fun scrollToSubCategory(newIndex: Int) {
        if (newIndex >= 0) {
            _state.update { it.copy(selectedSubTabIndex = newIndex) }
        }
    }

    private fun loadMenu() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            menuInteractor.getMenu()
                .collect { (menu, errorMessage) ->
                    if (!menu.isNullOrEmpty()) {
                        _state.update {
                            it.copy(isLoading = false, menuItems = menu)
                        }
                        Log.d("DEBUG", "loadMenu. Меню получено. Ниже первые 10 пунктов из него")
                        menu.take(10).forEach {
                            Log.d("DEBUG", "$it")
                        }
                    } else {
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

    private fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            val isNowFavorite = if (meal.isFavorite) {
                favoritesInteractor.removeFromFavorites(meal)
                false
            } else {
                favoritesInteractor.addToFavorites(meal)
                true
            }

            _state.update { state ->
                val index = state.menuItems.indexOfFirst {
                    it is MenuRVItem.MealItem && it.meal.id == meal.id
                }
                if (index == -1) return@update state // Если не нашли, ничего не делаем

                val updatedList = state.menuItems.toMutableList()
                val mealItem = updatedList[index] as MenuRVItem.MealItem
                updatedList[index] =
                    mealItem.copy(meal = mealItem.meal.copy(isFavorite = isNowFavorite))

                state.copy(menuItems = updatedList)
            }
        }
        Log.d("DEBUG", "ViewModel toggleFavorite for $meal")
    }

    private fun addToCart(meal: Meal) {
        Cart.addItem(meal)
        Log.d("DEBUG", "ViewModel addToCart for $meal")
    }

    private fun removeFromCart(meal: Meal) {
        /* Жду реализацию логики корзины */
        Log.d("DEBUG", "ViewModel removeFromCart for $meal")
    }

    private fun handleBannerClick(targetName: String) {
        viewModelScope.launch {
            var menuItems = state.value.menuItems

            // Ищем сначала точное совпадение, затем частичное
            val targetIndex = menuItems
                .indexOfFirst { item ->
                    item.getName()?.equals(targetName, ignoreCase = true) == true
                }
                .takeIf { it >= 0 }
                ?: menuItems.indexOfFirst { item ->
                    item.getName()?.contains(targetName, ignoreCase = true) == true
                }
                    .takeIf { it >= 0 }
                ?: 0

            _state.update { it.copy(selectedBannerIndex = targetIndex) }
        }
    }
}
