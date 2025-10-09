package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.mandarinkafe.mandarin.util.Constants.RATIO_FOR_IMAGE_CROP_MAX
import com.mandarinkafe.mandarin.util.Constants.RATIO_FOR_IMAGE_CROP_MIN
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

@Composable
fun KamelSubcomposeAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
    error: Painter? = null,
    crossfade: Boolean = true,
    tint: Color? = null,
    contentScale: ContentScale? = null,
    onStateChange: ((Resource<Painter>) -> Unit)? = null,
) {
    if (model == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            (placeholder ?: error)?.let {
                Image(
                    painter = it,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        return
    }

    val resource: Resource<Painter> = asyncPainterResource(data = model)
    onStateChange?.invoke(resource)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (resource) {
            is Resource.Loading -> {
                placeholder?.let {
                    Image(
                        painter = it,
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: CircularProgressIndicator()
            }

            is Resource.Success -> {
                val painter = resource.value

                // Умная адаптация contentScale по пропорции
                val ratio = if (painter.intrinsicSize.height != 0f) {
                    painter.intrinsicSize.width / painter.intrinsicSize.height
                } else {
                    1f
                }

                val finalContentScale = when {
                    contentScale != null -> contentScale
                    ratio in RATIO_FOR_IMAGE_CROP_MIN..RATIO_FOR_IMAGE_CROP_MAX -> ContentScale.Crop
                    else -> ContentScale.Fit
                }

                if (crossfade) {
                    Crossfade(targetState = painter, label = "imageCrossfade") { p ->
                        Image(
                            painter = p,
                            contentDescription = contentDescription,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = finalContentScale
                        )
                    }
                } else {
                    Image(
                        painter = painter,
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = finalContentScale,
                        colorFilter = tint?.let { androidx.compose.ui.graphics.ColorFilter.tint(it) }
                    )
                }
            }

            is Resource.Failure -> {
                error?.let {
                    Image(
                        painter = it,
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}