package com.deenislamic.sdk.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class FullCircleGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#1F000000")
        style = Paint.Style.STROKE
        strokeWidth = 3.5F.dp.toFloat() // Adjust the strokeWidth as needed
    }

    private val progressPaint = Paint().apply {
        color = Color.parseColor("#2FB68E")
        style = Paint.Style.STROKE
        strokeWidth = 12.dp.toFloat()
        strokeCap = Paint.Cap.ROUND // Set round cap for corner radius
    }

    private val circlePaint = Paint().apply {
        color = Color.parseColor("#80FFFFFF") // Add the desired color
        style = Paint.Style.FILL
    }

    private var progressPercentage: Float = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()
        val centerX = width / 2
        val centerY = height / 2

        val radius = width.coerceAtMost(height) * 0.4f
        val backgroundRadius = radius // Keep the same radius for background arc

        val startAngle = -90f
        val sweepAngle = 360f * progressPercentage / 100f

        // Draw background arc with a thinner stroke
        val backgroundRectF = RectF(
            centerX - backgroundRadius,
            centerY - backgroundRadius,
            centerX + backgroundRadius,
            centerY + backgroundRadius
        )
        canvas.drawArc(backgroundRectF, startAngle, 360f, false, backgroundPaint)

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

        // Draw progress arc without gap and with corner radius
        val progressRectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        progressPaint.pathEffect = null

        canvas.drawCircle(centerX, centerY, radius, circlePaint)
        canvas.drawArc(progressRectF, startAngle, sweepAngle, false, progressPaint)

    }

    fun setProgress(percentage: Float) {
        progressPercentage = percentage
        invalidate()
    }
}



