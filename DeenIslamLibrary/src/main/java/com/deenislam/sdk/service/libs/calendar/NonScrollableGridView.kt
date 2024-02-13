package com.deenislam.sdk.service.libs.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.GridView

internal class NonScrollableGridView(context: Context, attrs: AttributeSet) : GridView(context, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_MOVE) {
            return true
        }
        return super.dispatchTouchEvent(ev)
    }
}
