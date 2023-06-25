package com.deenislam.sdk.utils

import android.view.View
import androidx.asynclayoutinflater.view.AsyncLayoutInflater

internal inline fun <T : View> prepareStubView(
    stub: AsyncViewStub,
    layoutID:Int,
    crossinline prepareBlock: T.() -> Unit
) {
    stub.layoutRes = layoutID
    val inflatedView = stub.inflatedId as? T

    if (inflatedView != null) {
        inflatedView.prepareBlock()
    } else {
        stub.inflate(AsyncLayoutInflater.OnInflateFinishedListener { view, _, _ ->
            (view as? T)?.prepareBlock()
        })
    }
}