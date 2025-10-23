package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.isFavorite
import com.mandarinkafe.mandarin.features.cart.domain.Mapper.toCartItem
import com.mandarinkafe.mandarin.features.mealdetails.presentation.models.ReplaceOrAddData
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.dialogs.Dialogs
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.AskReplaceOrAdd
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.CloseAndShowMessage
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.ShowMaxModifiersQuantity
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEffect.ShowRequiredModifiersDialog
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent.EditMealInCart
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract.MealDetailsEvent.TryAddMeal
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberMealDetailsViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MealDetailsBottomSheet(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    mealId: String?,
    initItem: CartItem?,
    isEditMode: Boolean,
) {
    val viewModel = rememberMealDetailsViewModel()

    if (initItem == null && mealId == null) return
    val state by viewModel.state.collectAsState()

    LaunchedEffect(initItem, mealId) {
        viewModel.onEvent(
            MealDetailsEvent.SetInitData(
                item = initItem,
                isEditMode = isEditMode,
                mealId = mealId
            )
        )
    }

    val favorites by sharedViewModel.favoritesItemsFlow.collectAsState()

    val onSharedEvent = sharedViewModel::onEvent
    val onEvent = viewModel::onEvent
    val effectFlow = viewModel.effect

    val onToggleFavorite = { item: com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal ->
        onSharedEvent(SharedContract.SharedEvent.ToggleFavorite(item = item))
    }

    var showFavoriteVariantChoiceDialog by remember { mutableStateOf(false) }
    var showRequiredModifiersDialog by remember { mutableStateOf(false) }
    var showMaxModifiersQuantity by remember { mutableStateOf(false) }
    var showReplaceOrAddDialog by remember { mutableStateOf(false) }
    var replaceOrAddData by remember { mutableStateOf<ReplaceOrAddData?>(null) }
    var pendingCloseAndShowMessage by remember { mutableStateOf<CloseAndShowMessage?>(null) }

    val error = state.error
    val customizedMeal = state.customizedMeal ?: initItem?.customizedMeal

    var showSheet by remember { mutableStateOf(true) }

    // Закрытие bottom sheet с анимацией
    val handleClose: () -> Unit = {
        showSheet = false
    }
    LaunchedEffect(showSheet) {
        if (!showSheet) {
            navController.popBackStack()
        }
    }

    customizedMeal?.let { customizedMeal ->
        val isFavorite by remember(customizedMeal, favorites) {
            derivedStateOf { customizedMeal.isFavorite(favorites) }
        }

        when {
            state.isLoading -> LoadingScreen()
            error != null -> PlaceholderScreen(
                error = error,
                onCallClick = { onSharedEvent(SharedContract.SharedEvent.OnPhoneClick) },
            )

            else -> {
                MealDetailsContainer(
                    visible = showSheet,
                    onDismissRequest = { navController.popBackStack() }
                ) {
                    MealDetailsContentScreen(
                        customizedMeal = customizedMeal,
                        onEvent = viewModel::onEvent,
                        selectedTabIndex = state.selectedTabIndex,
                        addons = state.addons,
                        isFavorite = isFavorite,
                        isEditMode = isEditMode,
                        onClose = handleClose,
                        onRequestAddMeal = { onEvent(TryAddMeal(state.actualCartItem)) },
                        onEdit = {
                            onEvent(
                                EditMealInCart(
                                    state.actualCartItem ?: customizedMeal.toCartItem()
                                )
                            )
                        },
                        onToggleFavorite = {
                            if (!isFavorite && customizedMeal.isCustomized) {
                                onSharedEvent(
                                    SharedContract.SharedEvent.ShowFavoriteDialog(
                                        customizedMeal
                                    )
                                )
                            } else {
                                onToggleFavorite(customizedMeal)
                            }
                        },
                        comment = state.comment
                    )
                }
            }
        }
    }

    // Эффекты
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is ShowRequiredModifiersDialog -> showRequiredModifiersDialog = true
                is ShowMaxModifiersQuantity -> showMaxModifiersQuantity = true
                is AskReplaceOrAdd -> {
                    replaceOrAddData = ReplaceOrAddData(
                        messageRes = effect.message,
                        onAddNew = { effect.onAddNew() },
                        onReplace = { effect.onReplace() }
                    )
                    showReplaceOrAddDialog = true
                }

                is CloseAndShowMessage -> {
                    pendingCloseAndShowMessage = effect
                }
            }
        }
    }

    // Форматирование строки в Composable контексте
    val formattedMessage: String? = pendingCloseAndShowMessage?.let { effect ->
        effect.message?.let { messageRes ->
            if (effect.mealName != null) {
                // Форматируем строку с параметром (название блюда)
                stringResource(messageRes, effect.mealName)
            } else {
                // Используем строку без параметров
                stringResource(messageRes)
            }
        }
    }

    // Обработка CloseAndShowMessage
    LaunchedEffect(formattedMessage) {
        formattedMessage?.let { message ->
            onSharedEvent(
                SharedContract.SharedEvent.ShowSnackbar(
                    message = message,
                    showToCartButton = !state.isEditMode
                )
            )
            handleClose()
            pendingCloseAndShowMessage = null
        }
    }

    // Платформенные диалоги
    Dialogs(
        showRequiredModifiersDialog = showRequiredModifiersDialog,
        showMaxModifiersQuantity = showMaxModifiersQuantity,
        showReplaceOrAddDialog = showReplaceOrAddDialog,
        showFavoriteVariantChoiceDialog = showFavoriteVariantChoiceDialog,
        replaceOrAddData = replaceOrAddData,
        customizedMeal = customizedMeal,
        onRequiredModifiersDismiss = { showRequiredModifiersDialog = false },
        onMaxModifiersDismiss = { showMaxModifiersQuantity = false },
        onReplaceOrAddDismiss = { 
            showReplaceOrAddDialog = false
            replaceOrAddData = null
        },
        onFavoriteVariantDismiss = { showFavoriteVariantChoiceDialog = false },
        onToggleFavorite = onToggleFavorite,
        onSharedEvent = onSharedEvent
    )
}

@Composable
expect fun MealDetailsContainer(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
)
