package com.majd_alden.storyviewerlibrary.customview

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.majd_alden.storyviewerlibrary.callback.TouchCallbacks

class PullDismissLayout : ConstraintLayout {
    private var listener: Listener? = null
    private var dragHelper: ViewDragHelper? = null
    private var minFlingVelocity = 0f
    private var verticalTouchSlop = 0f
    private var mTouchCallbacks: TouchCallbacks? = null
    private var animateAlpha = false
    private var disableOnInterceptTouchEventX: Float? = null
    private var disableOnInterceptTouchEventY: Float? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int, defResStyle: Int) : super(
        context,
        attrs,
        defStyle,
        defResStyle
    ) {
        init(context)
    }

    private fun init(context: Context) {
        if (!isInEditMode) {
            val vc = ViewConfiguration.get(context)
            minFlingVelocity = vc.scaledMinimumFlingVelocity.toFloat()
            dragHelper = ViewDragHelper.create(this, ViewDragCallback(this))
        }
        disableOnInterceptTouchEventX = null
        disableOnInterceptTouchEventY = null
    }

    override fun computeScroll() {
        super.computeScroll()
        if (dragHelper != null && dragHelper?.continueSettling(true) == true) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
//        int action = MotionEventCompat.getActionMasked(event);
        val action = event.action
        if (disableOnInterceptTouchEventX != null
            && disableOnInterceptTouchEventY != null
            && event.x >= (disableOnInterceptTouchEventX ?: 0f)
            && event.y >= (disableOnInterceptTouchEventY ?: 0f)
        ) {
            return false
        }
        var pullingDown = false
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                verticalTouchSlop = event.y
                val dy = event.y - verticalTouchSlop
                if (dy > (dragHelper?.touchSlop ?: 0)) {
                    pullingDown = true
                    mTouchCallbacks?.touchPull()
                } else {
                    mTouchCallbacks?.touchDown(event.x, event.y)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = event.y - verticalTouchSlop
                if (dy > (dragHelper?.touchSlop ?: 0)) {
                    pullingDown = true
                    mTouchCallbacks?.touchPull()
                } else {
                    mTouchCallbacks?.touchDown(event.x, event.y)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                verticalTouchSlop = 0.0f
                mTouchCallbacks?.touchUp()
            }
        }
        if (dragHelper?.shouldInterceptTouchEvent(event) != true && pullingDown) {
            if (dragHelper?.viewDragState == ViewDragHelper.STATE_IDLE &&
                dragHelper?.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL) == true
            ) {
                val child = getChildAt(0)
                if (child != null && listener?.onShouldInterceptTouchEvent() != true) {
                    dragHelper?.captureChildView(child, event.getPointerId(0))
                    return dragHelper?.viewDragState == ViewDragHelper.STATE_DRAGGING
                }
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper?.processTouchEvent(event)
        return dragHelper?.capturedView != null
    }

    fun setMinFlingVelocity(velocity: Float) {
        minFlingVelocity = velocity
    }

    fun setAnimateAlpha(b: Boolean) {
        animateAlpha = b
    }

    fun setListener(l: Listener?) {
        listener = l
    }

    fun setmTouchCallbacks(mTouchCallbacks: TouchCallbacks?) {
        this.mTouchCallbacks = mTouchCallbacks
    }

    interface Listener {
        /**
         * Layout is pulled down to dismiss
         * Good time to finish activity, remove fragment or any view
         */
        fun onDismissed() {}

        /**
         * Convenient method to avoid layout_color overriding event
         * If you have a RecyclerView or ScrollerView in our layout_color your can
         * avoid PullDismissLayout to handle event.
         *
         * @return true when ignore pull down event, f
         * false for allow PullDismissLayout handle event
         */
        fun onShouldInterceptTouchEvent(): Boolean = false
    }

    fun getmTouchCallbacks(): TouchCallbacks? {
        return mTouchCallbacks
    }

    private class ViewDragCallback constructor(private val pullDismissLayout: PullDismissLayout) :
        ViewDragHelper.Callback() {
        private var startTop = 0
        private var dragPercent = 0.0f
        private var capturedView: View? = null
        private var dismissed = false
        override fun tryCaptureView(view: View, i: Int): Boolean {
            return capturedView == null
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return if (top < 0) 0 else top
        }

        override fun onViewCaptured(view: View, activePointerId: Int) {
            capturedView = view
            startTop = view.top
            dragPercent = 0.0f
            dismissed = false
        }

        @SuppressLint("NewApi")
        override fun onViewPositionChanged(view: View, left: Int, top: Int, dx: Int, dy: Int) {
            val range = pullDismissLayout.height
            val moved = Math.abs(top - startTop)
            if (range > 0) {
                dragPercent = moved.toFloat() / range.toFloat()
            }
            if (pullDismissLayout.animateAlpha) {
                view.alpha = 1.0f - dragPercent
                pullDismissLayout.invalidate()
            }
        }

        override fun onViewDragStateChanged(state: Int) {
            if (capturedView != null && dismissed && state == ViewDragHelper.STATE_IDLE) {
                pullDismissLayout.removeView(capturedView)
                if (pullDismissLayout.listener != null) {
                    pullDismissLayout.listener?.onDismissed()
                }
            }
        }

        override fun onViewReleased(view: View, xv: Float, yv: Float) {
            dismissed = dragPercent >= 0.50f ||
                    Math.abs(xv) > pullDismissLayout.minFlingVelocity && dragPercent > 0.20f
            val finalTop = if (dismissed) pullDismissLayout.height else startTop
            if (!dismissed) {
                pullDismissLayout.getmTouchCallbacks()?.touchUp()
            }
            pullDismissLayout.dragHelper?.settleCapturedViewAt(0, finalTop)
            pullDismissLayout.invalidate()
        }
    }
}