package com.deenislam.sdk.utils

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

internal class RecylerPagerSnapHelper: PagerSnapHelper() {

    private var instance: PagerSnapHelper? = null

    fun getInstance(): PagerSnapHelper {
        if (instance == null)
            instance = PagerSnapHelper()

        return instance as PagerSnapHelper
    }

    fun smoothScrollToPosition(layoutManager: RecyclerView.LayoutManager, position: Int) {
        val smoothScroller = createScroller(layoutManager) ?: return
        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
    }
}