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
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

@Composable
fun KamelSubcomposeAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    previewModel: Any? = null,
    contentDescription: String?,
    placeholder: ImageResource? = null,
    error: ImageResource? = null,
    crossfade: Boolean = true,
    tint: Color? = null,
    contentScale: ContentScale? = null,
    onStateChange: ((Resource<Painter>) -> Unit)? = null,
) {
    if (model == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            (placeholder ?: error)?.let {
                Image(
                    painter = painterResource(it),
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

    val previewResource: Resource<Painter>? = previewModel?.let {
        asyncPainterResource(data = it)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (resource) {
            is Resource.Loading -> {
                LoadingStateContent(
                    previewResource = previewResource,
                    placeholderRes = placeholder                    ,
                    contentDescription = contentDescription,
                    contentScale = contentScale
                )

            }

            is Resource.Success -> {
                SuccessStateContent(
                    painter = resource.value,
                    contentDescription = contentDescription,
                    crossfade = crossfade,
                    tint = tint,
                    contentScale = contentScale
                )
            }

            is Resource.Failure -> {
                error?.let {
                    Image(
                        painter = painterResource(it),
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingStateContent(
    previewResource: Resource<Painter>?,
    placeholderRes: ImageResource?,
    contentDescription: String?,
    contentScale: ContentScale?
) {
    when {
        previewResource is Resource.Success -> {
            Image(
                painter = previewResource.value,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale ?: ContentScale.Crop
            )
        }

        previewResource is Resource.Loading && placeholderRes != null -> {
            Image(
                painter = painterResource(placeholderRes),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        previewResource is Resource.Failure && placeholderRes != null -> {
            Image(
                painter = painterResource(placeholderRes),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        placeholderRes != null -> {
            Image(
                painter = painterResource(placeholderRes),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        else -> CircularProgressIndicator()
    }
}

@Composable
private fun SuccessStateContent(
    painter: Painter,
    contentDescription: String?,
    crossfade: Boolean,
    tint: Color?,
    contentScale: ContentScale?
) {
    val finalContentScale = when {
        contentScale != null -> contentScale
        else -> {
            val ratio = if (painter.intrinsicSize.height != 0f) {
                painter.intrinsicSize.width / painter.intrinsicSize.height
            } else {
                1f
            }
            when {
                ratio in RATIO_FOR_IMAGE_CROP_MIN..RATIO_FOR_IMAGE_CROP_MAX -> ContentScale.Crop
                else -> ContentScale.Fit
            }
        }
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
