package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel

@Composable
fun MealDetailsBottomSheet(
    sharedViewModel: SharedViewModel,
    mealId: String?,
    initItem: CartItem?,
    isEditMode: Boolean,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Meal Details Bottom Sheet - KMP Migration Placeholder")
    }
}
