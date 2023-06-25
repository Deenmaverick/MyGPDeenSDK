package com.deenislam.sdk.utils

import android.content.Context
import androidx.core.content.ContextCompat

fun Context.get_color(color: Int) =
    ContextCompat.getColor(this, color)