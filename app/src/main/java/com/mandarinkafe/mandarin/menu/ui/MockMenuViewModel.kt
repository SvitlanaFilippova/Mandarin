package com.mandarinkafe.mandarin.menu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.ui.RVItem
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import kotlinx.coroutines.launch


class MockMenuViewModel(
    private val menuInteractor: MenuInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {


    private var screenState = MutableLiveData<ScreenState>(ScreenState.Loading)
    fun getScreenState(): LiveData<ScreenState> = screenState

    fun getMenu() {
        screenState.postValue(ScreenState.Loading)

        viewModelScope.launch {
            try {
                // Получить данные меню
                val menuItems = menuInteractor.getMockMenu()
                screenState.postValue(ScreenState.Content(menuItems))
            } catch (e: Exception) {
                // Обработка ошибок
                screenState.postValue(ScreenState.Error(e.message ?: "Unknown error"))
            }
        }
    }


    fun toggleFavorite(meal: Meal) {
        if (meal.isFavorite) {
            favoritesInteractor.removeFromFavorites(meal)
        } else {
            favoritesInteractor.addToFavorites(meal)
        }
    }

    sealed interface ScreenState {
        data object Loading : ScreenState
        data class Error(val message: String) : ScreenState
        data class Content(var menuItems: List<RVItem>) : ScreenState
    }
}



