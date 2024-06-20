package com.deenislamic.sdk.utils

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat
import androidx.customview.widget.ViewDragHelper

internal class DraggableView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private var viewDragHelper: ViewDragHelper
    private var gestureDetector: GestureDetectorCompat

    private val initialX: Int // set the initial X position
    private val initialY: Int // set the initial Y position

    private var dragReleaseCallback: DragReleaseCallback? = null


    interface DragReleaseCallback {
        fun onDragReleased()
        fun onMiniQuranPlayerClicked()
    }

    fun setDragReleaseCallback(callback: DragReleaseCallback) {
        this.dragReleaseCallback = callback
    }


    init {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, DragHelperCallback())
        gestureDetector = GestureDetectorCompat(context, GestureListener())
        // Initialize initialX and initialY with the initial position of the view
        initialX = width / 2 // Adjust this as per your initial X position
        initialY = height / 2 // Adjust this as per your initial Y position
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        viewDragHelper.processTouchEvent(event)
        return true
    }

    private inner class DragHelperCallback : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            val leftBound = -width / 2
            val rightBound = width / 2
            return left.coerceIn(leftBound, rightBound)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return initialY // Lock the view vertically to the initial Y position
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            // Snap back to the initial position
            viewDragHelper.settleCapturedViewAt(initialX, initialY)
            invalidate()

            // Check if the released position is beyond 50% of the view width
            if (releasedChild.left >= width / 2 || releasedChild.right <= width / 2) {
                // The view is released beyond the threshold, hide the view
                visibility = View.GONE

                // Notify the callback in MainActivity
                dragReleaseCallback?.onDragReleased()
            }
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // Handle click event here
            performClick()
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
    }

    override fun performClick(): Boolean {
        // Handle the click event
        // Call super method to ensure accessibility events are properly handled.
        super.performClick()
        // Notify the callback in MainActivity
        dragReleaseCallback?.onMiniQuranPlayerClicked()
        return true
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate()
        }
    }
}