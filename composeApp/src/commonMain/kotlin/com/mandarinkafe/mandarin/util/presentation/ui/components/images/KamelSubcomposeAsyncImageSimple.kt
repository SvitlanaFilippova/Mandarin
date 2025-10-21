package com.mandarinkafe.mandarin.util.presentation.ui.components.images

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

@Composable
fun KamelSubcomposeAsyncImageSimple(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String?,
    crossfade: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop,
    tint: Color? = null,
    placeholder: ImageResource? = null,
    error: ImageResource? = null
) {
    if (model == null) {
        placeholder?.let {
            Image(
                painter = painterResource(it),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = tint?.let { ColorFilter.tint(it) }
            )
        }
        return
    }

    val resource: Resource<Painter> = asyncPainterResource(model)

    val painterToShow: Painter? = when (resource) {
        is Resource.Success -> resource.value
        is Resource.Loading -> placeholder?.let { painterResource(it) }
        is Resource.Failure -> error?.let { painterResource(it) } ?: placeholder?.let { painterResource(it) }
        else -> placeholder?.let { painterResource(it) }
    }

    painterToShow?.let { painter ->
        if (crossfade) {
            Crossfade(targetState = painter, label = "imageCrossfade") { p ->
                Image(
                    painter = p,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale,
                    colorFilter = tint?.let { ColorFilter.tint(it) }
                )
            }
        } else {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                colorFilter = tint?.let { ColorFilter.tint(it) }
            )
        }
    }
}

