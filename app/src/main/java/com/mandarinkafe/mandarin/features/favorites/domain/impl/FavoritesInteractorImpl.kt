package com.mandarinkafe.mandarin.features.favorites.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.FavoritesReader
import com.mandarinkafe.mandarin.core.domain.api.FavoritesWriter
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
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
    private val forceRefreshMenu: ForceRefreshMenuUseCase

) : FavoritesApi {
    private val _favoritesItemsFlow =
        MutableStateFlow<Resource<List<CustomizedMeal>>>(ErrorEmptyData())

    override fun observeFavoritesItems() = _favoritesItemsFlow.asStateFlow()

    private val _favoritesBaseMealIDsFlow = MutableStateFlow<Set<String>>(emptySet())
    override fun observeFavoritesBaseMealIDs() = _favoritesBaseMealIDsFlow.asStateFlow()

    override suspend fun forceRefresh() {
        forceRefreshMenu()
        val resource = reader.getRawFavorites()
        handleFavoritesResource(resource)
    }

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

    override suspend fun toggleFavorite(custom: CustomizedMeal) {
        val record = custom.toFavoriteRecord(getTimeStamp())
        writer.toggleFavorite(record)
    }

    override suspend fun toggleFavorite(meal: Meal) {
        val record = meal.toFavoriteRecord(getTimeStamp())
        writer.toggleFavorite(record)

    }

    private fun getTimeStamp(): Long {
        return System.currentTimeMillis()
    }

    private suspend fun observeFavoritesUpdates() {
        reader.observeRawFavorites().collect { resource ->
            handleFavoritesResource(resource)
        }
    }

    private suspend fun observeBaseIdsUpdates() {
        reader.observeBaseFavoritesIds().collect { ids ->
            _favoritesBaseMealIDsFlow.value = ids
        }
    }

    private suspend fun handleFavoritesResource(resource: Resource<Set<FavoriteRecord>>) {
        when (resource) {
            is Success -> {
                val records = resource.data ?: emptySet()
                val validated = validator.invoke(records)
                _favoritesItemsFlow.value = validated

                _favoritesBaseMealIDsFlow.value = records
                    .filterIsInstance<FavoriteRecord.Base>()
                    .map { it.mealId }
                    .toSet()
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
