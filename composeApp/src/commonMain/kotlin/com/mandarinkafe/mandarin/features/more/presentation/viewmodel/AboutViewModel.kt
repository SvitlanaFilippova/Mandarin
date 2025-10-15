package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.MenuMetaCache
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.launch

class AboutViewModel(private val menuMetaCache: MenuMetaCache) :
    BaseViewModel<AboutContract.AboutEvent, AboutContract.AboutEffect, AboutContract.AboutState>() {
    init {
        getInitData()
    }

    override fun setInitialState() = AboutContract.AboutState()

    private fun getInitData() {
        viewModelScope.launch {
            menuMetaCache.metaFlow.collect { meta ->
                setState {
                    copy(
                        versionName = "1.00000", // TODO (реализовать передачу версии приложения)
                        lastUpdated = meta?.lastUpdated,
                        revision = meta?.revision
                    )
                }
            }
        }
    }

    override fun onEvent(event: AboutContract.AboutEvent) {
        // не актуально
    }

    override fun setLoading(isLoading: Boolean) {
        // не актуально
    }
}
