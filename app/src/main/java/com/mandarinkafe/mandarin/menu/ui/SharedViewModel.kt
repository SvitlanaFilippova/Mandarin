package com.mandarinkafe.mandarin.menu.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.ui.RVItem
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import kotlinx.coroutines.launch

class SharedViewModel(
    private val menuInteractor: MenuInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {


    private var screenState = MutableLiveData<ScreenState>(ScreenState.Loading)
    fun getScreenState(): LiveData<ScreenState> = screenState

    fun getMenu() {
        screenState.postValue(ScreenState.Loading)
        viewModelScope.launch {
            menuInteractor.getMenu()
                .collect { pair ->
                    processMenuResult(
                        pair.first,
                        pair.second
                    )
                }
        }
    }

    private fun processMenuResult(menu: List<RVItem>?, errorMessage: String?) {
        if (!menu.isNullOrEmpty()) {
            screenState.postValue(ScreenState.Content(menu))
        }
        if (errorMessage != null) {
            screenState.postValue(ScreenState.Error)
            Log.e("ERROR", "$errorMessage")
        }
    }


    fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            if (meal.isFavorite) {
                favoritesInteractor.removeFromFavorites(meal)
            } else {
                favoritesInteractor.addToFavorites(meal)
            }
        }
    }

}

sealed interface ScreenState {
    data object Loading : ScreenState
    data object Error : ScreenState
    data class Content(var menuItems: List<RVItem>) :
        ScreenState
}

