package com.deenislamic.sdk.service.libs.circularprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.deenislamic.sdk.R

internal class CircularProgressBar @JvmOverloads constructor (context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var progress: Int = 0
    private val borderPaint = Paint()
    private val progressPaint = Paint()

    init {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 2.0f
        borderPaint.color = ContextCompat.getColor(context, R.color.deen_card_bg)

        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = 2.0f
        progressPaint.color = ContextCompat.getColor(context, R.color.deen_primary)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = width / 2f - 10
        val cx = width / 2f
        val cy = height / 2f
        val sweepAngle = 72f * progress // 360/5 = 72 degrees per section

        // Draw the default circular border
        canvas.drawCircle(cx, cy, radius, borderPaint)
        // Draw the progress arc
        if (progress > 0) {
            canvas.drawArc(
                10f, 10f, width - 10f, height - 10f,
                -90f, sweepAngle, false, progressPaint
            )
        }
    }

    fun updateProgress(newProgress: Int) {
        progress = newProgress % 6 // Reset after full circle
        invalidate()
    }

    fun decreaseProgress(increment: Int) {
        progress = (progress - increment) % 6 // Reset after full circle
        invalidate()
    }
}