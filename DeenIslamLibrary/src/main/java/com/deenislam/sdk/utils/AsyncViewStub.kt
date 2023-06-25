package com.deenislam.sdk.utils


import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.UiThread
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.res.use


internal class AsyncViewStub @JvmOverloads constructor(
    context: Context, set: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, set, defStyleAttr) {
    private val inflater: AsyncLayoutInflater by lazy { AsyncLayoutInflater(context) }

    var inflatedId = NO_ID
        get
    var layoutRes = 0

    init {
        val attrs = intArrayOf(android.R.attr.layout, android.R.attr.inflatedId)
        context.obtainStyledAttributes(set, attrs, defStyleAttr, 0).use {
            layoutRes =  it.getResourceId(0, 0)
            inflatedId = it.getResourceId(1, NO_ID)
        }

        visibility = View.GONE
        setWillNotDraw(true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            val view = inflate(context, layoutRes, null)
            (parent as? ViewGroup)?.let {
                //it.addViewSmart(view, it.indexOfChild(this), layoutParams)
                // Customize the layout parameters, including margins
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    val inflatedLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams = ViewGroup.MarginLayoutParams(inflatedLayoutParams).apply {
                        setMargins(
                            inflatedLayoutParams.leftMargin,
                            inflatedLayoutParams.topMargin,
                            inflatedLayoutParams.rightMargin,
                            inflatedLayoutParams.bottomMargin
                        )
                    }
                } else {
                    layoutParams = view.layoutParams
                }
                it.removeViewInLayout(this)
            }
        }
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(0, 0)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas) {}

    @UiThread
    fun inflate(listener: AsyncLayoutInflater.OnInflateFinishedListener? = null) {
        val viewParent = parent

        if (viewParent != null && viewParent is ViewGroup) {
            if (layoutRes != 0) {
                inflater.inflate(layoutRes, viewParent) { view, resId, parent ->
                    if (inflatedId != NO_ID) {
                        view.id = inflatedId
                    }

                    val stub = this
                    val index = parent?.removeViewInLayoutIndexed(stub)
                    if (index != null) {
                        parent.addViewSmart(view, index)
                        if (view.layoutParams is ViewGroup.MarginLayoutParams && layoutParams is LinearLayout.LayoutParams) {
                            val inflatedLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
                            val stubLayoutParams = layoutParams as LinearLayout.LayoutParams
                            val marginLayoutParams = LinearLayout.LayoutParams(inflatedLayoutParams)
                            marginLayoutParams.setMargins(
                                inflatedLayoutParams.leftMargin + stubLayoutParams.leftMargin,
                                inflatedLayoutParams.topMargin + stubLayoutParams.topMargin,
                                inflatedLayoutParams.rightMargin + stubLayoutParams.rightMargin,
                                inflatedLayoutParams.bottomMargin + stubLayoutParams.bottomMargin
                            )
                            view.layoutParams = marginLayoutParams
                        }
                    }
                    listener?.onInflateFinished(view, resId, parent)
                }
            } else {
                throw IllegalArgumentException("AsyncViewStub must have a valid layoutResource")
            }
        } else {
            throw IllegalStateException("AsyncViewStub must have a non-null ViewGroup viewParent")
        }
    }

    private fun ViewGroup.removeViewInLayoutIndexed(view: View): Int {
        val index = indexOfChild(view)
        removeViewInLayout(view)
        return index
    }

    private fun ViewGroup.addViewSmart(child: View, index: Int, params: ViewGroup.LayoutParams? = null) {
        if (params == null) addView(child, index)
        else addView(child, index, params)
    }
}

