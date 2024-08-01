package com.deenislamic.sdk.utils.pdfviewer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


internal class ZoomableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {

    private val MIN_ZOOM = 1f
    private val DEFAULT_MAX_ZOOM = 3f

    var isZoomEnabled: Boolean = true
    var maxZoom: Float = DEFAULT_MAX_ZOOM
    private var scaleFactor: Float = MIN_ZOOM

    private var isScaling = false

    private val scaleDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(context, ScaleListener())
    }
    private val gestureDetector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(context, GestureListener())
    }

    private var scaleFocusX = 0f
    private var scaleFocusY = 0f

    private var tranX = 0f
    private var tranY = 0f
    private var maxTranX = 0f
    private var maxTranY = 0f

    init {
        layoutManager = ZoomableLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun calculateScrollAmount(dy: Int): Int {
        return when {
            dy == 0 -> 0
            dy > 0 -> {
                if (tranY > -maxTranY) 0
                else (dy / scaleFactor).toInt()
            }
            else -> {
                if (tranY < 0) 0
                else (dy / scaleFactor).toInt()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isZoomEnabled) {
            return super.onTouchEvent(ev)
        }
        var returnValue = scaleDetector.onTouchEvent(ev)
        returnValue = gestureDetector.onTouchEvent(ev) || returnValue
        return super.onTouchEvent(ev) || returnValue
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.translate(tranX, tranY)
        canvas.scale(scaleFactor, scaleFactor)
        super.dispatchDraw(canvas)
    }

    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactorDelta = detector.scaleFactor
            val previousScaleFactor = scaleFactor

            // Calculate new scale factor
            scaleFactor *= scaleFactorDelta
            scaleFactor = scaleFactor.coerceIn(MIN_ZOOM, maxZoom)

            // Adjust translation to keep the focus point
            scaleFocusX = detector.focusX
            scaleFocusY = detector.focusY

            val newTranX = (tranX - scaleFocusX) * (scaleFactor / previousScaleFactor) + scaleFocusX
            val newTranY = (tranY - scaleFocusY) * (scaleFactor / previousScaleFactor) + scaleFocusY

            // Calculate new bounds
            maxTranX = (width * scaleFactor - width).coerceAtLeast(0f)
            maxTranY = (height * scaleFactor - height).coerceAtLeast(0f)

            // Constrain translations within bounds
            tranX = newTranX.coerceIn(-maxTranX, 0f)
            tranY = newTranY.coerceIn(-maxTranY, 0f)

            // Update overscroll mode based on zoom
            overScrollMode = if (scaleFactor > MIN_ZOOM) OVER_SCROLL_NEVER else OVER_SCROLL_IF_CONTENT_SCROLLS

            invalidate()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            isScaling = false
        }
    }


    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (!isScaling && scaleFactor > MIN_ZOOM) {
                tranX = (tranX - distanceX).coerceIn(-maxTranX, 0f)
                tranY = (tranY - distanceY).coerceIn(-maxTranY, 0f)
                invalidate()
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }
}

