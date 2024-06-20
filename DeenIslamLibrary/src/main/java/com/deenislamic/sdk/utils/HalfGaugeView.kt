package com.deenislamic.sdk.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

internal class HalfGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#F3F9F9")
        style = Paint.Style.STROKE
        strokeWidth = 12.dp.toFloat()
    }

    private val progressPaint = Paint().apply {
        color = Color.parseColor("#2FB68E")
        style = Paint.Style.STROKE
        strokeWidth = 12.dp.toFloat()
    }

    private val gapPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.STROKE
        strokeWidth = 12.dp.toFloat()
    }

    private var progressPercentage: Float = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()
        val centerX = width / 2
        val centerY = height / 2

        val radius = width.coerceAtMost(height) * 0.4f

        val startAngle = 140f
        val sweepAngle = 260f * progressPercentage / 100f

        // Draw background arc
        val backgroundRectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        canvas.drawArc(backgroundRectF, 140f, 260f, false, backgroundPaint)


        // Set up gradient colors for progress arc
        val gradientColors = intArrayOf(
            Color.parseColor("#2FB68E"),
            Color.parseColor("#A3DE7C")
        )
        val gradientPositions = floatArrayOf(0f, 1f)

        // Create a linear gradient shader
        val gradientShader = LinearGradient(
            centerX - radius, centerY,
            centerX + radius, centerY,
            gradientColors,
            gradientPositions,
            Shader.TileMode.CLAMP
        )

        // Set the shader to progressPaint
        progressPaint.shader = gradientShader

        // Draw progress arc with gap effect
        val progressRectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        val gapEffect: PathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        progressPaint.pathEffect = gapEffect
        canvas.drawArc(progressRectF, startAngle, sweepAngle, false, progressPaint)

        // Draw gap arc
        val gapRectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        gapPaint.pathEffect = gapEffect
        canvas.drawArc(gapRectF, startAngle + sweepAngle, 260f - sweepAngle, false, gapPaint)
    }

    fun setProgress(percentage: Float) {
        progressPercentage = percentage
        invalidate()
    }
}

