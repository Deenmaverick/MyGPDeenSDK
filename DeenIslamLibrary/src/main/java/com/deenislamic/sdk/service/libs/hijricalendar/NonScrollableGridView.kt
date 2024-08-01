package com.deenislamic.sdk.service.libs.hijricalendar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.GridView

internal class NonScrollableGridView(context: Context, attrs: AttributeSet) : GridView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = MeasureSpec.makeMeasureSpec(
            Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, heightSpec)
        layoutParams.height = measuredHeight
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_MOVE) {
            return true
        }
        return super.dispatchTouchEvent(ev)
    }
}