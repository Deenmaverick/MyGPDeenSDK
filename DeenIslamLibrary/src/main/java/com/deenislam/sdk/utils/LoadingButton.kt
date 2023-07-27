package com.deenislam.sdk.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.deenislam.sdk.R

internal class LoadingButton {

    companion object
    {
        var instance: LoadingButton? = null
    }

    private lateinit var progressDrawable:CircularProgressDrawable
    private lateinit var drawableSpan:DynamicDrawableSpan
    private lateinit var spannableString:SpannableString
    private lateinit var  callback:Drawable.Callback

    fun getInstance(context: Context): LoadingButton {
        if (instance == null) {

            instance = LoadingButton()

            // create progress drawable
            instance?.progressDrawable = CircularProgressDrawable(context).apply {
                // let's use large style just to better see one issue
                setStyle(CircularProgressDrawable.DEFAULT)
                setColorSchemeColors(Color.WHITE)

                //bounds definition is required to show drawable correctly
                val size = (centerRadius + strokeWidth).toInt() * 2
                setBounds(0, 0, size, size)
            }

            // create a drawable span using our progress drawable
            instance?.drawableSpan = object : DynamicDrawableSpan() {
                override fun getDrawable() = instance?.progressDrawable
            }

            // create a SpannableString like "Loading [our_progress_bar]"
            instance?.spannableString = SpannableString(" ").apply {
                setSpan(instance?.drawableSpan, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

        }

        return instance as LoadingButton
    }

    fun loader(button: View,color:Int= R.color.white): SpannableString? {

        instance?.progressDrawable?.setColorSchemeColors(ContextCompat.getColor(button.context,color))
        instance?.progressDrawable?.start()
        instance?.callback = object : Drawable.Callback {
            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            }

            override fun invalidateDrawable(who: Drawable) {
                button.invalidate()
            }

            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            }
        }

        instance?.progressDrawable?.callback = callback

        return instance?.spannableString
    }

    fun removeLoader()
    {
        instance?.progressDrawable?.stop()
        instance?.progressDrawable?.callback = null
    }
}