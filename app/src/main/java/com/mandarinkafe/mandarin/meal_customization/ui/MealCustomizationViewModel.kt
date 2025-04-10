package com.mandarinkafe.mandarin.meal_customization.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.favorites.domain.usecase.FavoritesInteractor
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealCustomizationViewModel @Inject constructor(
    //private var meal: Meal,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private var isFavoriteLiveData = MutableLiveData<Boolean>()
    fun getIsFavoriteLiveData(): LiveData<Boolean> {
        return isFavoriteLiveData
    }

    fun loadMeal(meal: Meal) {
        viewModelScope.launch {

        }
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