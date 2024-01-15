package com.deenislam.sdk.service.libs.media3

import android.content.Context
import android.util.AttributeSet
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.TimeBar


class CustomTimeBar(context: Context, attrs: AttributeSet?) : DefaultTimeBar(context, attrs) {

    fun setScrubListener(listener: TimeBar.OnScrubListener) {
        addListener(listener)
    }
}