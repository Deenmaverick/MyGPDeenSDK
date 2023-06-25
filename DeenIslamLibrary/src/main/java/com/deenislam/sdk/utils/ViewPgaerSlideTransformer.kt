package com.deenislam.sdk.utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2


class ViewPgaerSlideTransformer:ViewPager2.PageTransformer {

    private val MIN_SCALE = 0.75f
/*    override fun transformPage(view: View, position: Float) {
        Log.e("transformPage",position.toString())

        //view.translationX = view.width * position
        if (position <= -1.0f || position >= 1.0f) {
            view.alpha = 0.0f
        } else if (position == 0.0f) {
            view.alpha = 1.0f
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            //view.alpha = 1.0f - Math.abs(position)
            //view.translationX =   -(view.width * position)
            *//*view.translationX = view.width * position*//*
        }
    }*/

    override fun transformPage(page: View, position: Float) {
        val offset = 80 * position

            page.translationY = offset
        }


}