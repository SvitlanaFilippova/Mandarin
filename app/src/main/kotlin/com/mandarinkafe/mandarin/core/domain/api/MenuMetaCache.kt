package com.mandarinkafe.mandarin.core.domain.api

import com.mandarinkafe.mandarin.core.domain.models.MenuMeta
import kotlinx.coroutines.flow.StateFlow

interface MenuMetaCache {
    val metaFlow: StateFlow<MenuMeta?>
    fun save(lastUpdated: String, revision: Int)
}