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
import com.mandarinkafe.mandarin.menu.ui.MenuContract.Event
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
        onEvent(Event.LoadMenu)
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.LoadMenu -> loadMenu()
            is Event.ForceRefreshMenu -> forceRefreshMenu()
            is Event.ToggleFavorite -> toggleFavorite(event.meal)
            is Event.AddToCart -> addToCart(event.meal)
            is Event.RemoveFromCart -> removeFromCart(event.meal)
            is Event.ScrollToCategory -> scrollToCategory(event.newIndex)
            is Event.ScrollToSubCategory -> scrollToSubCategory(event.newIndex)
            is Event.BannerClick -> handleBannerClick(event.targetName)
            is Event.OpenMealCustomization -> openMealCustomization(event.meal)
            is Event.SearchMealsByText -> filterMenu(event.searchText)
            is Event.ClearSearchInput -> clearSearchInput()

        }
    }

    private fun forceRefreshMenu() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            menuInteractor.forceRefresh()
            loadMenu()
        }
    }

    private fun openMealCustomization(meal: Meal) {
        viewModelScope.launch {
            _effect.emit(MenuContract.Effect.OpenMealCustomization(meal))
        }
    }

    private fun filterMenu(searchText: String? = null, filters: Any? = null) {
        if (!searchText.isNullOrEmpty()) {
            val filteredMenuItems = _state.value.menuItems.filter {
                it is MenuRVItem.MealItem && it.meal.name.contains(searchText, ignoreCase = true)
            }
                .sortedWith( // Дополнительная сортировка, чтобы в начале от ображались избранные блюда
                    compareByDescending<MenuRVItem> {
                        (it as MenuRVItem.MealItem).meal.isFavorite
                    }
                )
            _state.update {
                it.copy(
                    filteredMenuItems = filteredMenuItems,
                    latestSearchText = searchText
                )
            }
        }

    }

    private fun clearSearchInput() {
        _state.update { it.copy(filteredMenuItems = emptyList(), latestSearchText = "") }
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
                val updatedMenuItems = updateMealItemInList(state.menuItems, meal.id, isNowFavorite)
                val updatedFiltered = if (state.filteredMenuItems.isNotEmpty()) {
                    updateMealItemInList(state.filteredMenuItems, meal.id, isNowFavorite)
                } else {
                    state.filteredMenuItems
                }

                state.copy(
                    menuItems = updatedMenuItems,
                    filteredMenuItems = updatedFiltered
                )
            }
        }
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

    private fun updateMealItemInList(
        list: List<MenuRVItem>,
        mealId: String,
        isFavorite: Boolean
    ): List<MenuRVItem> {
        val index = list.indexOfFirst {
            it is MenuRVItem.MealItem && it.meal.id == mealId
        }

        if (index == -1) return list

        val updatedList = list.toMutableList()
        val mealItem = updatedList[index] as MenuRVItem.MealItem
        updatedList[index] = mealItem.copy(
            meal = mealItem.meal.copy(isFavorite = isFavorite)
        )
        return updatedList
    }
}