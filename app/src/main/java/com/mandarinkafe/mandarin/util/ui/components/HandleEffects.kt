package com.mandarinkafe.mandarin.util.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import com.mandarinkafe.mandarin.core.domain.models.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.features.meal_details.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER_DEFAULT
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleEffects(
    effectFlow: Flow<SharedEffect>,
    context: Context
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is SharedEffect.OnPhoneClick -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = PHONE_NUMBER_DEFAULT.toUri()
                    }
                    context.startActivity(intent)
                }

                is SharedEffect.OpenMealDetailsBS -> {
                    // Игнорируем здесь, обработаем в HandleBottomSheetEffect
                }
            }
        }

    }

    //TODO с Customized не открывается - проверять
    HandleBottomSheetEffect<SharedEffect.OpenMealDetailsBS>(
        effectFlow = effectFlow,
        cast = { it as? SharedEffect.OpenMealDetailsBS }
    ) { effect, onDismiss ->
        MealDetailsBottomSheet(
            initItem = effect.meal?.toCustomizedMeal(),
            onDismiss = onDismiss,
            onFavoriteChanged = { string, boolean -> },
            onAddToCart = { }
        )
    }
}