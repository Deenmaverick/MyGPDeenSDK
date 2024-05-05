package com.deenislam.sdk.utils

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.deenislam.sdk.R
import com.google.android.material.button.MaterialButton

internal fun MaterialButton.setActiveState()
{
    this.strokeWidth = 0
    this.setBackgroundColor(ContextCompat.getColor(this.context, R.color.deen_primary))
    this.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.deen_primary))
    this.setTextColor(ContextCompat.getColor(this.context, R.color.deen_white))
}

internal fun MaterialButton.setInactiveState(backgroundColor:Int=0)
{
    this.setBackgroundColor(if(backgroundColor==0) backgroundColor else ContextCompat.getColor(this.context,backgroundColor))
    this.strokeWidth = 1.dp
    this.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this.context,R.color.deen_border))
    this.setTextColor(ContextCompat.getColor(this.context,R.color.deen_txt_ash))
}

fun MaterialButton.setActiveState(textColor:Int,BackgroundColor:Int)
{
    this.setTextColor( ContextCompat.getColor(
        this.context,
        textColor
    ))
    this.setBackgroundColor(ContextCompat.getColor(this.context,BackgroundColor))

}