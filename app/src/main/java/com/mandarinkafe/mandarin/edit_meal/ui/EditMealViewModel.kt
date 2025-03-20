package com.mandarinkafe.mandarin.edit_meal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mandarinkafe.mandarin.menu.domain.api.FavoritesInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditMealViewModel @Inject constructor(
    //private var meal: Meal,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private var isFavoriteLiveData = MutableLiveData<Boolean>()
    fun getIsFavoriteLiveData(): LiveData<Boolean> {
        return isFavoriteLiveData
    }

    fun checkIfFavorite() {
        //isFavoriteLiveData.value = favoritesInteractor.checkIfFavorite(meal.id)
    }

    fun toggleFavorite() {
        /*viewModelScope.launch {
            if (meal.isFavorite) {
                favoritesInteractor.removeFromFavorites(meal)
            } else {
                favoritesInteractor.addToFavorites(meal)
            }
        }
        isFavoriteLiveData.value = !meal.isFavorite
        val newMeal = meal.copy(isFavorite = !meal.isFavorite)
        this.meal = newMeal*/
    }
}