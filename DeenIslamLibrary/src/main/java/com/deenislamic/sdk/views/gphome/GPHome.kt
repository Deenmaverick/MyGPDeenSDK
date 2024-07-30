package com.deenislamic.sdk.views.gphome

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.deenislamic.sdk.R

class GPHome @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.deen_layout_gphome, this, true)
    }
}
