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
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.isFavorite
import com.mandarinkafe.mandarin.features.cart.domain.Mapper.toCartItem
import com.mandarinkafe.mandarin.features.mealdetails.presentation.models.ReplaceOrAddData
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.dialogs.Dialogs
import com.mandarinkafe.mandarin.features.mealdetails.presentation.viewmodel.MealDetailsContract
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
import kotlinx.coroutines.flow.Flow

@Composable
fun MealDetailsBottomSheet(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    mealId: String?,
    initItem: CartItem?,
    isEditMode: Boolean,
    addsIds: List<String> = emptyList(),
    modifierIds: Map<String, List<String>> = emptyMap(),
    comment: String = "",
    cartItemId: String? = null,
) {
    val viewModel = rememberMealDetailsViewModel()
    if (initItem == null && mealId == null) return

    val state by viewModel.state.collectAsState()
    
    // Мемоизируем favorites, чтобы избежать лишних recomposition
    val favorites by remember(sharedViewModel) {
        sharedViewModel.favoritesItemsFlow
    }.collectAsState(initial = emptyList())
    
    val effectFlow = viewModel.effect

    val onSharedEvent = sharedViewModel::onEvent
    val onEvent = viewModel::onEvent

    var showFavoriteVariantChoiceDialog by remember { mutableStateOf(false) }
    var showRequiredModifiersDialog by remember { mutableStateOf(false) }
    var showMaxModifiersQuantity by remember { mutableStateOf(false) }
    var showReplaceOrAddDialog by remember { mutableStateOf(false) }
    var replaceOrAddData by remember { mutableStateOf<ReplaceOrAddData?>(null) }
    var pendingCloseAndShowMessage by remember { mutableStateOf<CloseAndShowMessage?>(null) }

    val handleClose: () -> Unit = { navController.popBackStack() }

    LaunchedEffect(initItem, mealId, addsIds, modifierIds, comment, cartItemId) {
        viewModel.onEvent(
            MealDetailsEvent.SetInitData(
                item = initItem,
                mealId = mealId,
                isEditMode = isEditMode,
                addsIds = addsIds,
                modifierIds = modifierIds,
                comment = comment,
                cartItemId = cartItemId,
            )
        )
    }

    val customizedMeal = state.customizedMeal ?: initItem?.customizedMeal

    customizedMeal?.let { meal ->
        val isFavorite by remember(meal, favorites) {
            derivedStateOf { meal.isFavorite(favorites) }
        }

        RenderMealDetailsContent(
            state = state,
            meal = meal,
            isFavorite = isFavorite,
            isEditMode = isEditMode,
            onSharedEvent = onSharedEvent,
            onEvent = onEvent,
            onToggleFavorite = { handleToggleFavorite(meal, isFavorite, onSharedEvent) },
            onClose = handleClose,
            navController = navController,
        )
    }

    HandleMealDetailsEffects(
        effectFlow = effectFlow,
        onShowRequiredModifiers = { showRequiredModifiersDialog = true },
        onShowMaxModifiers = { showMaxModifiersQuantity = true },
        onAskReplaceOrAdd = { data ->
            replaceOrAddData = data
            showReplaceOrAddDialog = true
        },
        onCloseAndShowMessage = { pendingCloseAndShowMessage = it },
    )

    HandleCloseAndShowMessageEffect(
        pendingCloseAndShowMessage = pendingCloseAndShowMessage,
        state = state,
        onSharedEvent = onSharedEvent,
        onClose = handleClose,
        resetMessage = { pendingCloseAndShowMessage = null }
    )

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
        onToggleFavorite = { onSharedEvent(SharedContract.SharedEvent.ToggleFavorite(item = it)) },
        onSharedEvent = onSharedEvent
    )
}


@Composable
private fun RenderMealDetailsContent(
    state: MealDetailsContract.MealDetailsState,
    meal: CustomizedMeal,
    isFavorite: Boolean,
    isEditMode: Boolean,
    onSharedEvent: (SharedContract.SharedEvent) -> Unit,
    onEvent: (MealDetailsEvent) -> Unit,
    onToggleFavorite: () -> Unit,
    onClose: () -> Unit,
    navController: NavHostController,
) {
    when {
        state.isLoading -> LoadingScreen()
        state.error != null -> PlaceholderScreen(
            error = state.error,
            onCallClick = { onSharedEvent(SharedContract.SharedEvent.OnPhoneClick()) },
        )

        else -> {
            MealDetailsContainer(onDismissRequest = { navController.popBackStack() }) {
                MealDetailsContentScreen(
                    customizedMeal = meal,
                    onEvent = onEvent,
                    selectedTabIndex = state.selectedTabIndex,
                    addons = state.addons,
                    isFavorite = isFavorite,
                    isEditMode = isEditMode,
                    onClose = onClose,
                    onRequestAddMeal = { onEvent(TryAddMeal(state.actualCartItem)) },
                    onEdit = {
                        onEvent(EditMealInCart(state.actualCartItem ?: meal.toCartItem()))
                    },
                    onToggleFavorite = onToggleFavorite,
                    comment = state.comment
                )
            }
        }
    }
}

private fun handleToggleFavorite(
    meal: CustomizedMeal,
    isFavorite: Boolean,
    onSharedEvent: (SharedContract.SharedEvent) -> Unit,
) {
    when {
        !isFavorite && meal.isCustomized -> {
            onSharedEvent(SharedContract.SharedEvent.ShowFavoriteDialog(meal))
        }

        isFavorite && meal.isCustomized -> {
            onSharedEvent(SharedContract.SharedEvent.ToggleFavorite(item = meal))
        }

        else -> {
            onSharedEvent(SharedContract.SharedEvent.ToggleFavorite(meal = meal.meal))
        }
    }
}

@Composable
private fun HandleMealDetailsEffects(
    effectFlow: Flow<MealDetailsContract.MealDetailsEffect>,
    onShowRequiredModifiers: () -> Unit,
    onShowMaxModifiers: () -> Unit,
    onAskReplaceOrAdd: (ReplaceOrAddData) -> Unit,
    onCloseAndShowMessage: (CloseAndShowMessage) -> Unit,
) {
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is ShowRequiredModifiersDialog -> onShowRequiredModifiers()
                is ShowMaxModifiersQuantity -> onShowMaxModifiers()
                is AskReplaceOrAdd -> onAskReplaceOrAdd(
                    ReplaceOrAddData(
                        messageRes = effect.message,
                        mealName = effect.mealName,
                        onAddNew = effect.onAddNew,
                        onReplace = effect.onReplace
                    )
                )

                is CloseAndShowMessage -> onCloseAndShowMessage(effect)
            }
        }
    }
}

@Composable
private fun HandleCloseAndShowMessageEffect(
    pendingCloseAndShowMessage: CloseAndShowMessage?,
    state: MealDetailsContract.MealDetailsState,
    onSharedEvent: (SharedContract.SharedEvent) -> Unit,
    onClose: () -> Unit,
    resetMessage: () -> Unit,
) {
    val formattedMessage = pendingCloseAndShowMessage?.let { effect ->
        effect.message?.let { messageRes ->
            effect.mealName?.let { stringResource(messageRes, it) } ?: stringResource(messageRes)
        }
    }

    LaunchedEffect(pendingCloseAndShowMessage) {
        pendingCloseAndShowMessage?.let {
            formattedMessage?.let { message ->
                onSharedEvent(
                    SharedContract.SharedEvent.ShowSnackbar(
                        message = message,
                        showToCartButton = !state.isEditMode
                    )
                )
            }
            onClose()
            resetMessage()
        }
    }
}

@Composable
expect fun MealDetailsContainer(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
