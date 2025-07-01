package com.mandarinkafe.mandarin.navigation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST

@Composable
fun CartIconBox(cartCount: Int, painterResource: Int, stringResource: Int) {
    @OptIn(ExperimentalAnimationApi::class)
    BadgedBox(
        badge = {
            AnimatedContent(
                targetState = cartCount,
                transitionSpec = {
                    (scaleIn(tween(ANIMATION_DURATION_FAST)) + fadeIn()).togetherWith(
                        scaleOut(
                            tween(
                                ANIMATION_DURATION_FAST
                            )
                        ) + fadeOut()
                    )
                }
            ) { count ->
                if (count > 0) {
                    Badge(
                        containerColor = Colors.Orange,
                        contentColor = Colors.AppBlack
                    ) {
                        Text(count.toString())
                    }
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(painterResource),
            contentDescription = stringResource(stringResource)
        )
    }
}