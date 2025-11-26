package com.mandarinkafe.mandarin.features.menu.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.FavoritesApi
import com.mandarinkafe.mandarin.core.domain.api.ForceRefreshMenuUseCase
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.features.menu.domain.api.AnnouncementsRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.GetAnnouncementsUseCase
import com.mandarinkafe.mandarin.features.menu.domain.api.GetBannersUseCase
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuInteractor
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.presentation.mappers.MenuItemMapper.menuToMenuItems
import com.mandarinkafe.mandarin.features.menu.presentation.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.presentation.models.extensions.getName
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEffect
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.features.menu.presentation.viewmodel.MenuContract.MenuState
import com.mandarinkafe.mandarin.features.ordershistory.domain.api.OrdersHistoryInteractor
import com.mandarinkafe.mandarin.util.Constants.DEFAULT_UNSELECTED_INDEX
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.mandarinkafe.mandarin.util.tickerFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MenuViewModel(
    private val menuInteractor: MenuInteractor,
    private val favoritesApi: FavoritesApi,
    private val getBannersUseCase: GetBannersUseCase,
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase,
    private val announcementsRepository: AnnouncementsRepository,
    private val forceRefreshMenu: ForceRefreshMenuUseCase,
    private val ordersHistoryInteractor: OrdersHistoryInteractor,
) : BaseViewModel<MenuEvent, MenuEffect, MenuState>() {
    override fun setInitialState() = MenuState()

    private var activeOrdersPollingJob: Job? = null

    init {
        loadMenu()
        getBanners()
        getAnnouncements()
        observeFavorites()
        observeActiveOrders()
    }

    override fun onEvent(event: MenuEvent) {
        when (event) {
            is MenuEvent.BannerClick -> findMenuItemByBanner(event.banner)
            is MenuEvent.ResetSelectedMenuItemIndex -> resetSelectedMenuItemIndex()
            is MenuEvent.ForceRefresh -> forceRefresh()
        }
    }

    private fun forceRefresh() {
        viewModelScope.launch {
            setLoading()
            forceRefreshMenu()
            loadMenu()
            getBanners()
            // Принудительно обновляем объявления при форс рефреш
            refreshAnnouncements()
            // Обновляем активные заказы
            loadActiveOrders()
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    // Методы для загрузки меню
    private fun loadMenu() {
        viewModelScope.launch {
            menuInteractor.getMenu().collectLatest { resource ->
                setLoading(resource is Loading || resource is Idle)
                when (resource) {
                    is Idle -> {}
                    is Loading -> {}
                    is Success -> setData(resource.data)
                    else -> setError(resource)
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            val favoriteIdsFromStorage = mutableSetOf<String>()
            favoritesApi.observeFavoritesBaseMealIDs().collect { favoriteIdsFromStorage.addAll(it) }
            setState {
                copy(
                    favoriteIds = favoriteIdsFromStorage
                )
            }
        }
    }

    private fun setData(data: List<MealCategory>?) {
        if (!data.isNullOrEmpty()) {
            setState {
                copy(
                    menuItems = menuToMenuItems(data),
                    error = null
                )
            }
        }
    }

    private fun setError(resource: Resource<*>) {
        val error = when (resource) {
            is Resource.ErrorEmptyData<*> -> UiError.MenuEmpty
            is Resource.ErrorNoInternet<*> -> UiError.NoInternet
            is ErrorOther<*> -> UiError.OtherError
            else -> return
        }
        setState { copy(error = error) }
    }

    // Обработка кликов по баннерам - поиск подходящей категории/блюда в меню и скролл к нему
    private fun findMenuItemByBanner(banner: Banner) {
        // Если нет цели — сбрасываем и выходим
        if (banner.targetName.isBlank()) {
            resetSelectedMenuItemIndex()
            return
        }
        viewModelScope.launch {
            val menuItems = state.value.menuItems
            val targetIndex = findMenuItemIndex(banner, menuItems)
            setState { copy(selectedMenuItemIndex = targetIndex) }
        }
    }

    private fun findMenuItemIndex(banner: Banner, menuItems: List<MenuItem>): Int {
        val name = banner.targetName.trim()
        // Поиск по имени (точное совпадение)
        menuItems.indexOfFirst { it.getName()?.equals(name, ignoreCase = true) == true }
            .takeIf { it >= 0 }?.let { return it }
        // Поиск по имени (частичное совпадение)
        menuItems.indexOfFirst { it.getName()?.contains(name, ignoreCase = true) == true }
            .takeIf { it >= 0 }?.let { return it }

        // По умолчанию
        return DEFAULT_UNSELECTED_INDEX
    }

    private fun resetSelectedMenuItemIndex() {
        setState { copy(selectedMenuItemIndex = DEFAULT_UNSELECTED_INDEX) }
    }

    private fun getBanners() {
        viewModelScope.launch {
            setState { copy(bannersAreLoading = true) }
            val result = getBannersUseCase()
            when (result) {
                is Success -> {
                    val banners = result.data ?: emptyList()
                    setState { copy(banners = banners, bannersAreLoading = false) }
                }

                else -> {
                    setState { copy(bannersAreLoading = false) }
                }
            }
        }
    }

    private fun getAnnouncements() {
        viewModelScope.launch {
            val result = getAnnouncementsUseCase()
            when (result) {
                is Success -> {
                    val announcements = result.data ?: emptyList()
                    setState { copy(announcements = announcements) }
                }

                else -> {
                    // В случае ошибки просто оставляем пустой список
                    setState { copy(announcements = emptyList()) }
                }
            }
        }
    }

    /**
     * Принудительное обновление объявлений (используется при форс рефреш)
     */
    private fun refreshAnnouncements() {
        viewModelScope.launch {
            // Сначала обновляем кэш в репозитории
            announcementsRepository.loadAnnouncements()
            // Затем получаем обновленные данные
            getAnnouncements()
        }
    }

    /**
     * Периодическое обновление активных заказов
     */
    private fun observeActiveOrders() {
        stopObservingActiveOrders()
        activeOrdersPollingJob = viewModelScope.launch {
            tickerFlow(period = ORDER_STATUS_UPD_DELAY.seconds)
                .onStart { emit(Unit) }
                .map {
                    loadActiveOrders()
                }
                .collect { }
        }
    }

    private fun stopObservingActiveOrders() {
        activeOrdersPollingJob?.cancel()
        activeOrdersPollingJob = null
    }

    /**
     * Загрузка активных заказов (максимум 3)
     */
    private suspend fun loadActiveOrders() {
        val result = ordersHistoryInteractor.getHistory()
        when (result) {
            is Success -> {
                val allOrders = result.data ?: emptyList()
                val activeOrders = allOrders
                    .filter { it.isActive }
                    .sortedByDescending { it.timestamp }
                    .take(MAX_ACTIVE_ORDERS_COUNT)
                setState { copy(activeOrders = activeOrders) }
            }

            else -> {
                // В случае ошибки сохраняем предыдущее состояние
                // Не обновляем список, чтобы карточки не пропадали при временных проблемах с сетью
            }
        }
    }

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 60 // Обновление каждые 60 секунд
        const val MAX_ACTIVE_ORDERS_COUNT =
            3 // Максимальное количество активных заказов для отображения
    }
}
