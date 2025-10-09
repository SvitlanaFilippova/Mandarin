package com.mandarinkafe.mandarin.features.menu.data.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuMetaCache
import com.mandarinkafe.mandarin.core.domain.models.MenuMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuMetaCacheImpl : MenuMetaCache {
    private val _metaFlow = MutableStateFlow<MenuMeta?>(null)
    override val metaFlow: StateFlow<MenuMeta?> = _metaFlow.asStateFlow()

    override fun save(lastUpdated: String, revision: Int) {
        _metaFlow.value = MenuMeta(lastUpdated, revision)
    }
}