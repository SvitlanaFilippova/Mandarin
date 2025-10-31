package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import YandexMapKit.YMKMapView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectZero
import platform.UIKit.UIEvent
import platform.UIKit.UIScrollView
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
class MapContainerView(frame: CValue<CGRect>) : UIView(frame) {
    val mapView: YMKMapView

    init {
        mapView = YMKMapView(frame = CGRectZero.readValue())
        addSubview(mapView)
        mapView.translatesAutoresizingMaskIntoConstraints = false
        val leading = mapView.leadingAnchor.constraintEqualToAnchor(leadingAnchor)
        val trailing = mapView.trailingAnchor.constraintEqualToAnchor(trailingAnchor)
        val top = mapView.topAnchor.constraintEqualToAnchor(topAnchor)
        val bottom = mapView.bottomAnchor.constraintEqualToAnchor(bottomAnchor)

        listOf(leading, trailing, top, bottom).forEach { it.active = true }
    }

    private fun findEnclosingScrollView(): UIScrollView? {
        var v: UIView? = superview
        while (v != null) {
            if (v is UIScrollView) return v
            v = v.superview
        }
        return null
    }

    override fun didMoveToSuperview() {
        super.didMoveToSuperview()
        containingScrollView = findEnclosingScrollView()
    }

    private var containingScrollView: UIScrollView? = null

    override fun touchesBegan(touches: Set<*>, withEvent: UIEvent?) {
        containingScrollView?.setScrollEnabled(false)
        super.touchesBegan(touches, withEvent = withEvent)
    }

    override fun touchesEnded(touches: Set<*>, withEvent: UIEvent?) {
        containingScrollView?.setScrollEnabled(true)
        super.touchesEnded(touches, withEvent = withEvent)
    }

    override fun touchesCancelled(touches: Set<*>, withEvent: UIEvent?) {
        containingScrollView?.setScrollEnabled(true)
        super.touchesCancelled(touches, withEvent = withEvent)
    }
}
