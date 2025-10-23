package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.dialogs

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.mealdetails.presentation.models.ReplaceOrAddData
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun Dialogs(
    showRequiredModifiersDialog: Boolean,
    showMaxModifiersQuantity: Boolean,
    showReplaceOrAddDialog: Boolean,
    showFavoriteVariantChoiceDialog: Boolean,
    replaceOrAddData: ReplaceOrAddData?,
    customizedMeal: CustomizedMeal?,
    onRequiredModifiersDismiss: () -> Unit,
    onMaxModifiersDismiss: () -> Unit,
    onReplaceOrAddDismiss: () -> Unit,
    onFavoriteVariantDismiss: () -> Unit,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onSharedEvent: (SharedContract.SharedEvent) -> Unit,
) {
    RequiredModifiersDialog(
        show = showRequiredModifiersDialog,
        onDismiss = onRequiredModifiersDismiss
    )

    MaxModifiersDialog(
        show = showMaxModifiersQuantity,
        onDismiss = onMaxModifiersDismiss
    )

    customizedMeal?.let { meal ->
        FavoriteVariantDialog(
            show = showFavoriteVariantChoiceDialog,
            onBaseSelected = {
                onSharedEvent(SharedContract.SharedEvent.ToggleFavorite(meal = meal.meal))
                onFavoriteVariantDismiss()
            },
            onCustomSelected = {
                onToggleFavorite(meal)
                onFavoriteVariantDismiss()
            },
            onDismiss = onFavoriteVariantDismiss
        )
    }

    replaceOrAddData?.let { data ->
        if (showReplaceOrAddDialog) {
            ReplaceOrAddDialog(
                message = stringResource(data.messageRes),
                onDismiss = onReplaceOrAddDismiss,
                onAddNew = data.onAddNew,
                onReplace = data.onReplace
            )
        }
    }
}
