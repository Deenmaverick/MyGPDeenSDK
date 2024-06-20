package com.deenislamic.sdk.utils

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


internal class SpanningLinearLayoutManager : LinearLayoutManager {

    private var fixedWidthPerItem: Int = 0
    private var isFixedWidthEnabled = false

    constructor(context: Context, fixedWidthPerItem: Int) : super(context) {
        this.fixedWidthPerItem = fixedWidthPerItem
    }

    constructor(context: Context, orientation: Int, reverseLayout: Boolean, fixedWidthPerItem: Int) : super(context, orientation, reverseLayout) {
        this.fixedWidthPerItem = fixedWidthPerItem
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int, fixedWidthPerItem: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.fixedWidthPerItem = fixedWidthPerItem
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateDefaultLayoutParams())
    }

    override fun generateLayoutParams(c: Context, attrs: AttributeSet): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateLayoutParams(c, attrs))
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateLayoutParams(lp))
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
        return super.checkLayoutParams(lp)
    }

    private fun spanLayoutSize(layoutParams: RecyclerView.LayoutParams): RecyclerView.LayoutParams {
        if (orientation == HORIZONTAL) {
            if (isFixedWidthEnabled) {
                layoutParams.width = fixedWidthPerItem
            } else {
                val totalWidth = fixedWidthPerItem * itemCount
                if (totalWidth < width) {
                    layoutParams.width = (getHorizontalSpace() / itemCount.toDouble()).toInt()
                } else {
                    isFixedWidthEnabled = true
                    layoutParams.width = fixedWidthPerItem
                }
            }
        } else if (orientation == VERTICAL) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        return layoutParams
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        isFixedWidthEnabled = fixedWidthPerItem * itemCount >= width
    }

    override fun canScrollVertically(): Boolean {
        return false // Enable vertical scrolling
    }

    override fun canScrollHorizontally(): Boolean {
        return true // Enable horizontal scrolling
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingRight - paddingLeft
    }
}


