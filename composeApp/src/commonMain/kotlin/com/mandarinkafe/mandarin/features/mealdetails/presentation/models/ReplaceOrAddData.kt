package com.mandarinkafe.mandarin.features.mealdetails.presentation.models

import dev.icerock.moko.resources.StringResource

data class ReplaceOrAddData(
    val messageRes: StringResource,
    val onAddNew: () -> Unit,
    val onReplace: () -> Unit
)





