package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import platform.UIKit.UIApplication
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIModalPresentationPageSheet
import platform.UIKit.UIPresentationController
import platform.UIKit.UISheetPresentationController
import platform.UIKit.UISheetPresentationControllerDetent
import platform.UIKit.UIViewController
import platform.UIKit.presentationController
import platform.darwin.NSObject

private class DismissDelegate(private val onDismiss: () -> Unit) : NSObject(), UIAdaptivePresentationControllerDelegateProtocol {
    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        onDismiss()
    }
}

private fun topViewController(): UIViewController? {
    val keyWindow = UIApplication.sharedApplication.keyWindow
    var top = keyWindow?.rootViewController
    while (top?.presentedViewController != null) {
        top = top.presentedViewController
    }
    return top
}

@Composable
actual fun KmpModalBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    var presentedController: UIViewController? by remember { mutableStateOf(null) }

    LaunchedEffect(visible) {
        val topVC = topViewController()
        if (visible && presentedController == null && topVC != null) {
            val vc = ComposeUIViewController {
                Box(modifier = Modifier.background(Colors.AppBlack)) {
                    content()
                }
            }
            vc.modalPresentationStyle = UIModalPresentationPageSheet

            val sheet = vc.presentationController as? UISheetPresentationController
            sheet?.let {
                val medium = UISheetPresentationControllerDetent.mediumDetent()
                val large = UISheetPresentationControllerDetent.largeDetent()
                it.detents = listOf(medium, large)
                it.prefersGrabberVisible = true
                it.prefersScrollingExpandsWhenScrolledToEdge = true
            }

            vc.presentationController?.delegate = DismissDelegate {
                onDismissRequest()
                presentedController = null
            }

            topVC.presentViewController(vc, true, null)
            presentedController = vc
        } else if (!visible && presentedController != null) {
            presentedController?.dismissViewControllerAnimated(true, completion = null)
            presentedController = null
        }
    }
}
