package com.deenislam.sdk.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.deenislam.sdk.R

internal class AspectRatioImageView : AppCompatImageView {

    private var aspectRatio: Float = 1f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttributes(attrs)
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DeenAspectRatioImageView)
        aspectRatio = a.getFloat(R.styleable.DeenAspectRatioImageView_imageAspectRatio, 1f)
        a.recycle()
    }

    fun setImageAspectRatio(ratio: Float) {
        aspectRatio = ratio
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        val height = (width / aspectRatio).toInt()
        setMeasuredDimension(width, height)
    }
}
