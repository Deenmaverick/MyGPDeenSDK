package com.deenislam.sdk.utils

import android.animation.ValueAnimator
import android.view.View
import androidx.viewpager2.widget.ViewPager2

internal class SlidePageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        //page.translationX = position * page.width
        //page.alpha = 1 - Math.abs(position)
        if (position <= -1.0f || position >= 1.0f) {
            page.alpha = 0.0f
        }
        else if (position == 0.0f) {
            page.alpha = 1.0f
        }
        else
            page.alpha = 1.0f - Math.abs(position)

    }
}
