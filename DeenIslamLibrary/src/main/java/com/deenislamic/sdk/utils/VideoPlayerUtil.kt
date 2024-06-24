package com.deenislamic.sdk.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.deenislamic.sdk.R

fun topControlHideWithAnim(view: View)
{
    /*view.animate()
        .translationY(-view.height.toFloat())  // Notice the negative sign
        .setDuration(300)  // duration of the animation in milliseconds
        .withEndAction {
            view.visibility = View.GONE
        }
        .start()*/

    view.animate()
        .alpha(0f)  // 0f means fully transparent, which is essentially a fade out effect
        .setDuration(300)
        .withEndAction {
            view.visibility = View.GONE
        }
        .start()


}

fun rootControlHideWithAnim(view: View)
{
    /*view.animate()
        .translationY(-view.height.toFloat())  // Notice the negative sign
        .setDuration(300)  // duration of the animation in milliseconds
        .withEndAction {
            view.visibility = View.GONE
        }
        .start()*/

    view.animate()
        //.alpha(0f)  // 0f means fully transparent, which is essentially a fade out effect
        .setDuration(300)
        .withEndAction {
            view.background = null
        }
        .start()


}

fun topControlShowWithAnim(view: View)
{

    /*view.translationY = -view.height.toFloat()
    view.visibility = View.VISIBLE
    view.animate()
        .translationY(0f)
        .setDuration(300)
        .start()*/

    //view.translationY = -view.height.toFloat()
    view.alpha = 0f  // start from fully transparent
    view.visibility = View.VISIBLE
    view.animate()
        .translationY(0f)
        .alpha(1f)  // animate to fully opaque
        .setDuration(300)
        .start()


}

 fun bottomControlHideWithAnim(view:View)
{
   /* view.animate()
        .translationY(view.height.toFloat())
        .setDuration(300)  // duration of the animation in milliseconds
        .withEndAction {
            view.visibility = View.GONE
        }
        .start()*/

    view.animate()
        .alpha(0f)  // 0f means fully transparent, which is essentially a fade out effect
        .setDuration(300)
        .withEndAction {
            view.visibility = View.GONE
        }
        .start()

}


 fun bottomControlShowWithAnim(view:View)
{
   /* view.translationY = view.height.toFloat()  // start position
    view.visibility = View.VISIBLE
    view.animate()
        .translationY(0f)  // final position
        .setDuration(300)
        .start()*/

    view.alpha = 0f  // start from fully transparent
    view.visibility = View.VISIBLE
    view.animate()
        .translationY(0f)
        .alpha(1f)  // animate to fully opaque
        .setDuration(300)
        .start()

}

 fun Activity.enterFullScreen(vPlayerTopLayer:View?=null,vPlayerBottomLayer:View?=null) {

     vPlayerTopLayer?.let {
         it.setPadding(
             56.dp,
             it.paddingTop,
             it.paddingRight,
             it.paddingBottom)

     }

     vPlayerBottomLayer?.let {
         it.setPadding(
             56.dp,
             it.paddingTop,
             it.paddingRight,
             it.paddingBottom)
     }


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

        val attributes = this.window.attributes
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        this.window.attributes = attributes

    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        this.window.insetsController?.let {
            it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        this.window.setDecorFitsSystemWindows(false)
    } else {
        @Suppress("DEPRECATION")
        this.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)

    }
}

 fun Activity.exitFullScreen(vPlayerTopLayer:View?=null,vPlayerBottomLayer:View?=null) {


     vPlayerTopLayer?.let {
        it.setPadding(
            0.dp,
            it.paddingTop,
            it.paddingRight,
            it.paddingBottom)
    }

     vPlayerBottomLayer?.let {

        it.setPadding(
            0.dp,
            it.paddingTop,
            it.paddingRight,
            it.paddingBottom)
    }



    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val attributes = this.window.attributes
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        this.window.attributes = attributes
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val controller = this.window.insetsController
        controller?.let {
            it.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
        }
        this.window.setDecorFitsSystemWindows(true)
    } else {

        @Suppress("DEPRECATION")
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}