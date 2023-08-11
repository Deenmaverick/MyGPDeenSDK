package com.deenislam.sdk.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View


internal class DashTrackView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    interface VisibilityChangeListener {
        fun onVisibilityChangedOnce(visibility: Int)
    }

    private var listener: VisibilityChangeListener? = null
    private var hasTriggeredVisible = false
    private var hasTriggeredInvisible = false

    fun setVisibilityChangeListener(listener: VisibilityChangeListener) {
        this.listener = listener
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE && !hasTriggeredVisible) {
            hasTriggeredVisible = true
            hasTriggeredInvisible = false
            listener?.onVisibilityChangedOnce(visibility)
        } else if (visibility != VISIBLE && !hasTriggeredInvisible) {
            hasTriggeredInvisible = true
            hasTriggeredVisible = false
            listener?.onVisibilityChangedOnce(visibility)
        }
    }
}
