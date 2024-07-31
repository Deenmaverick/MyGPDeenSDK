package com.deenislamic.sdk.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
        val column = position % spanCount

        if (includeEdge) {
            // Add spacing to the left and right edges of the items
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            // Add spacing to the top and bottom of the items
            if (position >= spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            // Add spacing only between items, no extra space at the edges
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount

            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}

