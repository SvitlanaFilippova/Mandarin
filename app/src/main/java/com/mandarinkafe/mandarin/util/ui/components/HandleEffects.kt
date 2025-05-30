package com.mandarinkafe.mandarin.util.ui.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.gson.Gson
import com.mandarinkafe.mandarin.core.domain.models.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER_DEFAULT
import kotlinx.coroutines.flow.Flow
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HandleEffects(
    effectFlow: Flow<SharedEffect>,
    navController: NavController
) {
    val context = LocalContext.current

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
                    val meal = effect.item
                        ?: effect.meal?.toCustomizedMeal()
                        ?: return@collect

                    val isEditMode = effect.isEditMode

                    val gson = Gson()
                    val json =
                        URLEncoder.encode(gson.toJson(meal), StandardCharsets.UTF_8.toString())
                    navController.navigate("meal_details/$json/$isEditMode")
                }
            }
        }
    }
}