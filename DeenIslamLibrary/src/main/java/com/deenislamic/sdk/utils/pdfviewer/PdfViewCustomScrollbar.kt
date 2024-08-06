package com.deenislamic.sdk.utils.pdfviewer

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show

/*
internal class PdfViewCustomScrollbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var pageInfo: ConstraintLayout? = null
    private var recyclerView: RecyclerView? = null
    private var thumbHeight: Float = 0f
    private var thumbOffset: Float = 0f
    private var initialTouchY = 0f
    private var initialThumbY = 0f
    private var thumbView: View? = null

    init {
        inflate(context, R.layout.custom_scrollbar_for_pdfview_vertical, this)
        thumbView = findViewById(R.id.scrollbar_thumb)


        thumbView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    initialTouchY = event.rawY
                    initialThumbY = thumbView!!.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    pageInfo?.show()
                    val newThumbY = (event.rawY - initialTouchY + initialThumbY).coerceIn(0f, (height - thumbHeight))
                    thumbView?.y = newThumbY
                    setRecyclerViewPosition(newThumbY)
                    true
                }
                else -> {
                    pageInfo?.hide()
                    false
                }
            }
        }

        // Ensure thumbHeight is initialized correctly
        thumbView?.post {
            thumbHeight = thumbView?.height?.toFloat() ?: 0f
        }
    }

    fun setRecyclerView(
        recyclerView: RecyclerView,
        pageInfo: ConstraintLayout
    ) {
        this.pageInfo = pageInfo
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateThumbPosition()
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        thumbView?.draw(canvas)
    }

    private fun updateThumbPosition() {
        val verticalScrollOffset = recyclerView?.computeVerticalScrollOffset() ?: 0
        val verticalScrollRange = recyclerView?.computeVerticalScrollRange() ?: 0
        val verticalScrollExtent = recyclerView?.computeVerticalScrollExtent() ?: 0

        // Calculate thumbOffset correctly
        thumbOffset = if (verticalScrollRange > verticalScrollExtent) {
            (height.toFloat() - thumbHeight) * verticalScrollOffset / (verticalScrollRange - verticalScrollExtent)
        } else {
            0f
        }

        thumbView?.y = thumbOffset.coerceIn(0f, height - thumbHeight)
        invalidate()
    }

   */
/* private fun setRecyclerViewPosition(y: Float) {
        val proportion = y / (height - thumbHeight)
        val targetPos = (recyclerView?.adapter?.itemCount?.times(proportion))?.toInt() ?: 0
        (recyclerView?.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(targetPos, 0)
    }*//*


    private fun setRecyclerViewPosition(y: Float) {
        val recyclerViewHeight = height - thumbHeight
        val proportion = y / recyclerViewHeight
        val verticalScrollRange = recyclerView?.computeVerticalScrollRange() ?: 0
        val verticalScrollExtent = recyclerView?.computeVerticalScrollExtent() ?: 0
        val totalScrollableHeight = verticalScrollRange - verticalScrollExtent

        // Calculate the target offset in pixels
        val targetOffset = (totalScrollableHeight * proportion).toInt()

        // Calculate the current scroll offset
        val currentScrollOffset = recyclerView?.computeVerticalScrollOffset() ?: 0

        // Calculate the amount to scroll by
        val scrollByOffset = targetOffset - currentScrollOffset

        // Scroll the RecyclerView by the calculated offset
        recyclerView?.post {
            recyclerView?.scrollBy(0, scrollByOffset)
        }

    }


}*/


internal class PdfViewCustomScrollbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    enum class Orientation {
        VERTICAL, HORIZONTAL
    }

    private var pageInfo: ConstraintLayout? = null
    private var pageInfoCurrent: AppCompatTextView? = null
    private var pageInfoTotal: AppCompatTextView? = null
    private var recyclerView: RecyclerView? = null
    private var thumbHeight: Float = 0f
    private var thumbWidth: Float = 0f
    private var thumbOffset: Float = 0f
    private var initialTouch: Float = 0f
    private var initialThumbPosition: Float = 0f
    private var thumbView: AppCompatTextView? = null
    private var orientation: Orientation = Orientation.VERTICAL

    private val handler = Handler(Looper.getMainLooper())
    private val hideRunnableThumb = Runnable {
        this.thumbView?.hide()
    }


    init {
        setOrientation(Orientation.VERTICAL) // Default orientation
    }

    fun setOrientation(orientation: Orientation) {
        this.orientation = orientation
        removeAllViews()
        val layoutId = when (orientation) {
            Orientation.VERTICAL -> R.layout.custom_scrollbar_for_pdfview_vertical
            Orientation.HORIZONTAL -> R.layout.custom_scrollbar_for_pdfview_horizontal
        }
        inflate(context, layoutId, this)
        thumbView = findViewById(R.id.scrollbar_thumb)

        thumbView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    initialTouch = if (orientation == Orientation.VERTICAL) event.rawY else event.rawX
                    initialThumbPosition = if (orientation == Orientation.VERTICAL) thumbView!!.y else thumbView!!.x
                    thumbView?.show()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    pageInfo?.show()
                    val newPosition = if (orientation == Orientation.VERTICAL) {
                        (event.rawY - initialTouch + initialThumbPosition).coerceIn(0f, (height - thumbHeight))
                    } else {
                        (event.rawX - initialTouch + initialThumbPosition).coerceIn(0f, (width - thumbWidth))
                    }
                    if (orientation == Orientation.VERTICAL) {
                        thumbView?.y = newPosition
                    } else {
                        thumbView?.x = newPosition
                    }
                    setRecyclerViewPosition(newPosition)
                    true
                }
                else -> {
                    // Remove any pending hide callbacks
                    handler.removeCallbacks(hideRunnableThumb)
                    // Reset the hide delay
                    handler.postDelayed(hideRunnableThumb, 1500)
                    pageInfo?.hide()
                    false
                }
            }
        }

        // Ensure thumb dimensions are initialized correctly
        thumbView?.post {
            thumbHeight = thumbView?.height?.toFloat() ?: 0f
            thumbWidth = thumbView?.width?.toFloat() ?: 0f
        }
    }

    fun showThumb() = thumbView?.show()

    fun setRecyclerView(
        recyclerView: RecyclerView,
        pageInfo: ConstraintLayout
    ) {
        this.pageInfo = pageInfo
        this.recyclerView = recyclerView
        this.pageInfoCurrent = pageInfo.findViewById(R.id.currentPage)
        this.pageInfoTotal = pageInfo.findViewById(R.id.totalPage)
        this.pageInfoCurrent?.text = "1".numberLocale()
        this.thumbView?.text = "1".numberLocale()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                thumbView?.show()
                updateThumbPosition()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // RecyclerView is idle
                    // Remove any pending hide callbacks
                    handler.removeCallbacks(hideRunnableThumb)
                    // Reset the hide delay
                    handler.postDelayed(hideRunnableThumb, 1500)
                }

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        thumbView?.draw(canvas)
    }

    private fun updateThumbPosition() {
        val scrollOffset = if (orientation == Orientation.VERTICAL) {
            recyclerView?.computeVerticalScrollOffset() ?: 0
        } else {
            recyclerView?.computeHorizontalScrollOffset() ?: 0
        }
        val scrollRange = if (orientation == Orientation.VERTICAL) {
            recyclerView?.computeVerticalScrollRange() ?: 0
        } else {
            recyclerView?.computeHorizontalScrollRange() ?: 0
        }
        val scrollExtent = if (orientation == Orientation.VERTICAL) {
            recyclerView?.computeVerticalScrollExtent() ?: 0
        } else {
            recyclerView?.computeHorizontalScrollExtent() ?: 0
        }

        thumbOffset = if (scrollRange > scrollExtent) {
            if (orientation == Orientation.VERTICAL) {
                (height.toFloat() - thumbHeight) * scrollOffset / (scrollRange - scrollExtent)
            } else {
                (width.toFloat() - thumbWidth) * scrollOffset / (scrollRange - scrollExtent)
            }
        } else {
            0f
        }

        if (orientation == Orientation.VERTICAL) {
            thumbView?.y = thumbOffset.coerceIn(0f, height - thumbHeight)
        } else {
            thumbView?.x = thumbOffset.coerceIn(0f, width - thumbWidth)
        }

        // Get the current position of the RecyclerView
        val layoutManager = recyclerView?.layoutManager as? LinearLayoutManager
        val currentPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0

        // Set the position to the thumb view text
        thumbView?.text = "${currentPosition + 1}".numberLocale()
        invalidate()
    }

    private fun setRecyclerViewPosition(position: Float) {
        val recyclerViewSize = if (orientation == Orientation.VERTICAL) height - thumbHeight else width - thumbWidth
        val proportion = position / recyclerViewSize
        val scrollRange = if (orientation == Orientation.VERTICAL) {
            recyclerView?.computeVerticalScrollRange() ?: 0
        } else {
            recyclerView?.computeHorizontalScrollRange() ?: 0
        }
        val scrollExtent = if (orientation == Orientation.VERTICAL) {
            recyclerView?.computeVerticalScrollExtent() ?: 0
        } else {
            recyclerView?.computeHorizontalScrollExtent() ?: 0
        }
        val totalScrollableSize = scrollRange - scrollExtent

        val targetOffset = (totalScrollableSize * proportion).toInt()
        val currentScrollOffset = if (orientation == Orientation.VERTICAL) {
            recyclerView?.computeVerticalScrollOffset() ?: 0
        } else {
            recyclerView?.computeHorizontalScrollOffset() ?: 0
        }

        val scrollByOffset = targetOffset - currentScrollOffset

        recyclerView?.post {
            if (orientation == Orientation.VERTICAL) {
                recyclerView?.scrollBy(0, scrollByOffset)
            } else {
                recyclerView?.scrollBy(scrollByOffset, 0)
            }
        }

        val layoutManager = recyclerView?.layoutManager as? LinearLayoutManager
        val currentPosition = layoutManager?.findFirstVisibleItemPosition() ?: 1

        thumbView?.text = "${currentPosition+1}".numberLocale()
        this.pageInfoCurrent?.text = "${currentPosition+1}".numberLocale()
        this.pageInfoTotal?.text = "${recyclerView?.adapter?.itemCount}".numberLocale()
    }
}
