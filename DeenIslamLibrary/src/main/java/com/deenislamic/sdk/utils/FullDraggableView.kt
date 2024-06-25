package com.deenislamic.sdk.utils

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.widget.NestedScrollView
import androidx.customview.widget.ViewDragHelper

internal class FullDraggableView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    private var viewDragHelper: ViewDragHelper
    private var gestureDetector: GestureDetectorCompat

    private var dragReleaseCallback: DragReleaseCallback? = null

    private var parentScrollview: NestedScrollView? = null

    private var childView1:View ? = null
    private var childView2:View ? = null


    interface DragReleaseCallback {
        fun onDragReleased()
    }

    fun setDragReleaseCallback(callback: DragReleaseCallback) {
        this.dragReleaseCallback = callback
    }

    fun setParentScrollview(scrollview: NestedScrollView) {
        this.parentScrollview = scrollview
    }

    fun setChildviews(view1: View, view2: View) {
        this.childView1 = view1
        this.childView2 = view2
    }

    init {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, DragHelperCallback())
        gestureDetector = GestureDetectorCompat(context, GestureListener())
    }

    /*override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev)
    }*/

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val shouldInterceptTouchEvent = viewDragHelper.shouldInterceptTouchEvent(ev)

      /*  when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // Disable nested scrolling when drag starts
                parentScrollview?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Enable nested scrolling when drag ends
                parentScrollview?.requestDisallowInterceptTouchEvent(false)
            }
        }*/

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // Check if the touch is inside the child view (in this case, TextView)
                if (isEventInsideChild(ev, this.childView1) || isEventInsideChild(ev, this.childView2)) {
                    // Disable nested scrolling when drag starts only if the touch is inside the child view
                    parentScrollview?.requestDisallowInterceptTouchEvent(true)
                } else {
                    // Allow nested scrolling when the touch is outside the child view
                    parentScrollview?.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Enable nested scrolling when drag ends
                parentScrollview?.requestDisallowInterceptTouchEvent(true)
            }
        }

        return shouldInterceptTouchEvent
    }


    private fun isEventInsideChild(event: MotionEvent, childView: View?): Boolean {
        if(childView==null)
            false
        else {
            val x = event.x.toInt()
            val y = event.y.toInt()
            return x >= childView.left && x <= childView.right && y >= childView.top && y <= childView.bottom
        }

        return false
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
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            // Notify the callback in MainActivity
            dragReleaseCallback?.onDragReleased()
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
        return true
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate()
        }
    }
}