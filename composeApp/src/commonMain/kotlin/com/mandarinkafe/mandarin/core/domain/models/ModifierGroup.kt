package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class ModifierGroup(
    val id: String,
    val name: String,
    val items: List<ModifierItem>,
    val isSingleChoice: Boolean,
    val isRequired: Boolean,
    val maxQuantity: Int,
)

/**
 * Сравнивает два списка ModifierGroup только по ID групп и ID элементов,
 * игнорируя остальные поля (name, price, isSingleChoice и т.д.)
 * Это нужно для корректного сравнения сохраненных избранных с текущими версиями из меню.
 */
fun List<ModifierGroup>.hasSameContentAs(other: List<ModifierGroup>): Boolean {
    if (this.size != other.size) return false
    
    val thisMap = this.associate { group ->
        group.id to group.items.map { it.id }.toSet()
    }
    val otherMap = other.associate { group ->
        group.id to group.items.map { it.id }.toSet()
    }
    
    return thisMap == otherMap
}