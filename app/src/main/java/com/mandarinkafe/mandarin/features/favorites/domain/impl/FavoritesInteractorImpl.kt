package com.mandarinkafe.mandarin.features.favorites.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.favorites.data.mapper.FavoriteMapper.toFavoriteRecord
import com.mandarinkafe.mandarin.features.favorites.domain.usecase.ValidateFavoritesUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesInteractorImpl(
    private val validator: ValidateFavoritesUseCase,
    private val reader: FavoritesReader,
    private val writer: FavoritesWriter,
) : FavoritesApi {
    private val _favoritesItemsFlow =
        MutableStateFlow<Resource<List<CustomizedMeal>>>(ErrorEmptyData())

    override fun observeFavoritesItems() = _favoritesItemsFlow.asStateFlow()

    private val _favoritesBaseMealIDsFlow = MutableStateFlow<Set<String>>(emptySet())
    override fun observeFavoritesBaseMealIDs() = _favoritesBaseMealIDsFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Подписываемся на обновления из репозитория
            launch { observeFavoritesUpdates() }
            launch { observeBaseIdsUpdates() }
        }
    }

    override suspend fun getFavorites(): Resource<List<CustomizedMeal>> {
        return _favoritesItemsFlow.value
    }

    override suspend fun checkIfFavorite(custom: CustomizedMeal): Boolean {
        return reader.checkIfFavorite(custom.toFavoriteRecord(getTimeStamp()))
    }

    override suspend fun checkIfFavorite(meal: Meal): Boolean {
        return reader.checkIfFavorite(meal.toFavoriteRecord(getTimeStamp()))
    }

    override suspend fun toggleFavorite(custom: CustomizedMeal): Boolean {
        val record = custom.toFavoriteRecord(getTimeStamp())
        return writer.toggleFavorite(record)
    }

    override suspend fun toggleFavorite(meal: Meal): Boolean {
        val record = meal.toFavoriteRecord(getTimeStamp())
        return writer.toggleFavorite(record)

    }

    private fun getTimeStamp(): Long {
        return System.currentTimeMillis()
    }

    private suspend fun observeFavoritesUpdates() {
        reader.observeRawFavorites().collect { resource ->
            // Исправление: передаем напрямую resource, а не Resource.Success
            when (resource) {
                is Success -> {
                    val records = resource.data ?: emptySet()
                    val validated = validator.invoke(records)
                    _favoritesItemsFlow.value = validated

                    // Обновляем базовые ID
                    _favoritesBaseMealIDsFlow.value = resource.data
                        ?.filterIsInstance<FavoriteRecord.Base>()
                        ?.map { it.mealId }
                        ?.toSet() ?: emptySet()
                }

                is ErrorEmptyData -> {
                    _favoritesItemsFlow.value = ErrorEmptyData()
                    _favoritesBaseMealIDsFlow.value = emptySet()
                }

                is ErrorNoInternet -> _favoritesItemsFlow.value = ErrorNoInternet()
                is ErrorOther -> _favoritesItemsFlow.value = ErrorOther(resource.message.orEmpty())
                is Idle -> _favoritesItemsFlow.value = Idle()
                is Loading -> _favoritesItemsFlow.value = Loading()
            }
        }
    }

    private suspend fun observeBaseIdsUpdates() {
        reader.observeBaseFavoritesIds().collect { ids ->
            _favoritesBaseMealIDsFlow.value = ids
        }
    }
}