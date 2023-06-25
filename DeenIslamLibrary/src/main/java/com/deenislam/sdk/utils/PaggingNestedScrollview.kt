package com.deenislam.sdk.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView


internal class PaggingNestedScrollview(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    private var isScrollingEnabled = true

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (isScrollingEnabled) {
            super.onTouchEvent(ev)
        } else {
            // Consume touch events when scrolling is disabled
            true
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (isScrollingEnabled) {
            super.onInterceptTouchEvent(ev)
        } else {
            // Consume touch events when scrolling is disabled
            true
        }
    }

    fun setScrollingEnabled(enabled: Boolean) {
        isScrollingEnabled = enabled
    }
}
