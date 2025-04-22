package com.mandarinkafe.mandarin.menu.domain.models

sealed class BaseMeal {
    abstract val id: String
    abstract val name: String
    abstract val weight: Int
    abstract val price: Int
    abstract val isHidden: Boolean
}