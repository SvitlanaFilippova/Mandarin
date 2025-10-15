package com.mandarinkafe.mandarin.features.mealdetails.presentation.models

data class ReplaceOrAddData(
    val message: String,
    val onAddNew: () -> Unit,
    val onReplace: () -> Unit
)




