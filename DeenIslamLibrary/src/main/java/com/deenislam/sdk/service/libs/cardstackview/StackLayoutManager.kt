package com.deenislam.sdk.service.libs.cardstackview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.utils.dp


internal class StackLayoutManager(context: Context) : LinearLayoutManager(context, VERTICAL, false) {
    // Define how much items should overlap
    private val itemOverlap = 12.dp  // This defines the amount the second and third items will peek from the top

    override fun canScrollVertically(): Boolean {
        return false
    }
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        val itemCount = state.itemCount

        // Calculate yOffset for the topmost card
        var yOffset = 0
        var widthReduction = 0

        for (position in itemCount - 1 downTo 0) {
            val view = recycler.getViewForPosition(position)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            var width = getDecoratedMeasuredWidth(view)
            val height = getDecoratedMeasuredHeight(view)

            // Adjust the width for the top three items
            if (position == 0) {
                // No reduction for the topmost card
                widthReduction = 0
            } else if (position == 1) {
                widthReduction = (width * 0.08).toInt()  // 2nd card reduced by 5% from the original width
                width -= widthReduction
            } else if (position == 2) {
                widthReduction += (width * 0.16).toInt()  // 3rd card is reduced by an additional 5%
                width -= widthReduction
            }

            val horizontalCenter = (getHorizontalSpace() - width) / 2
            val left = horizontalCenter
            val right = left + width

            layoutDecorated(view, left, yOffset, right, yOffset + height)

            // Ensure only top card and the peeking cards have an increased yOffset. All others remain hidden.
            if (position <= 1) {
                yOffset += itemOverlap
            }
            else if (position <= 2) {
                yOffset += itemOverlap - 2.dp
            } else {
                view.visibility = View.GONE  // Hide the views below the third card
            }
        }
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingRight - paddingLeft
    }
}





















