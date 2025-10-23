package com.mandarinkafe.mandarin.features.mealdetails.presentation.models

import dev.icerock.moko.resources.StringResource

data class ReplaceOrAddData(
    val messageRes: StringResource,
    val mealName: String? = null,
    val onAddNew: () -> Unit,
    val onReplace: () -> Unit
)





