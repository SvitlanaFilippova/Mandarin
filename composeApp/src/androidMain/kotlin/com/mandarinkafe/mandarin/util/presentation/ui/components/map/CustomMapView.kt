package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.yandex.mapkit.mapview.MapView

class CustomMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MapView(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}