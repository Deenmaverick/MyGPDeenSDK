package com.deenislam.sdk.utils

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import coil.load
import com.deenislam.sdk.R

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

fun AppCompatImageView.imageLoad(
    url:String,
    ic_small:Boolean=false,
    ic_medium:Boolean=false,
    ic_large:Boolean=false
)
{
    this.load(url)
    {
        if(ic_small)
            error(R.drawable.ic_small_download_empty)
    }
}