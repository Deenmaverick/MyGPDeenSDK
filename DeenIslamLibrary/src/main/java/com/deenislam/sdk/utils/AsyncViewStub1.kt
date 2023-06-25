package com.deenislam.sdk.utils

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.res.use

internal class AsyncViewStub1 @JvmOverloads constructor(
    context: Context, set: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, set, defStyleAttr) {
    private var layoutRes = 0
    private var inflatedView: View? = null
    private var inflateFinishedListener: OnInflateFinishedListener? = null

    init {
        val attrs = intArrayOf(android.R.attr.layout, android.R.attr.inflatedId)
        context.obtainStyledAttributes(set, attrs, defStyleAttr, 0).use {
            layoutRes = it.getResourceId(0, 0)
        }

        visibility = View.GONE
        setWillNotDraw(true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            inflateView()
        }
    }

    private fun inflateView() {
        Thread {
            val inflater = LayoutInflater.from(context)
            inflatedView = inflater.inflate(layoutRes, null)
            Handler(Looper.getMainLooper()).post {
                inflatedView?.let { inflatedView ->
                    val layoutParams = inflatedView.layoutParams
                    layoutParams?.let {
                        this.layoutParams = layoutParams
                    }
                    this.addView(inflatedView)
                    inflateFinishedListener?.onInflateFinished(inflatedView, layoutRes, this)
                }
            }
        }.start()
    }

    fun setOnInflateFinishedListener(listener: OnInflateFinishedListener) {
        inflateFinishedListener = listener
    }

    interface OnInflateFinishedListener {
        fun onInflateFinished(view: View, resId: Int, parent: ViewGroup?)
    }
}
