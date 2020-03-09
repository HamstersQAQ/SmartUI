package com.yppcat.smartui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout

/**
 * Created by ypp0623 on 2018/8/28.
 * 用于多选view操作的矩形
 */

class RectView : FrameLayout {
    private val startP = PointF()
    private val mPaint = Paint()
    var mFrameLayout: FrameLayout? = null
    private var mParams: FrameLayout.LayoutParams? = null
    private val mCenterPoint = PointF()
    /**
     * 边框宽度
     */
    private val strokeWidth = 5
    private var endListener: EndListener? = null
    //移动时起始坐标
    private var mStartX: Float = 0.toFloat()
    private var mStartY: Float = 0.toFloat()
    /**
     * 移动距离
     */
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()
    //是否移动状态
    private var isSwipe: Boolean = false

    private var mDeleteDrawable: Drawable? = null
    private var mDeleteTip: Button? = null

    private var mDelParams: FrameLayout.LayoutParams? = null


    fun setEndListener(endListener: EndListener) {
        this.endListener = endListener
    }

    fun setSwipe(swipe: Boolean) {
        isSwipe = swipe
        invalidate()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //重绘选中框
    @SuppressLint("ResourceAsColor")
    fun initParams() {
        mParams!!.width = 0
        mParams!!.height = 0
        mParams!!.setMargins(0, 0, 0, 0)
        mFrameLayout!!.layoutParams = mParams
        this.mFrameLayout!!.setBackgroundColor(context.resources.getColor(R.color.all_select_bg))
        mFrameLayout!!.removeAllViews()
        mDeleteTip = null
        invalidate()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE) {
            init()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.setBackgroundResource(R.drawable.round_null)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = strokeWidth.toFloat()
        mPaint.alpha = 20
        mPaint.color = context.resources.getColor(R.color.all_select_bg)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (!isSwipe) {
            //画矩形状态
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startP.set(x.toFloat(), y.toFloat())
                    if (x > startP.x) {
                        mParams!!.leftMargin = startP.x.toInt()
                    } else {
                        mParams!!.leftMargin = x
                    }
                    if (y > startP.y) {
                        mParams!!.topMargin = startP.y.toInt()
                    } else {
                        mParams!!.topMargin = y
                    }
                    mParams!!.width = Math.abs(x - startP.x).toInt()
                    mParams!!.height = Math.abs(y - startP.y).toInt()
                    this.mFrameLayout!!.layoutParams = mParams
                    this.mFrameLayout!!.setBackgroundResource(R.drawable.all_select_bg)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (x > startP.x) {
                        mParams!!.leftMargin = startP.x.toInt()
                    } else {
                        mParams!!.leftMargin = x
                    }
                    if (y > startP.y) {
                        mParams!!.topMargin = startP.y.toInt()
                    } else {
                        mParams!!.topMargin = y
                    }
                    mParams!!.width = Math.abs(x - startP.x).toInt()
                    mParams!!.height = Math.abs(y - startP.y).toInt()
                    this.mFrameLayout!!.layoutParams = mParams
                    this.mFrameLayout!!.setBackgroundResource(R.drawable.all_select_bg)
                }
                MotionEvent.ACTION_UP -> {
                    mFrameLayout!!.setBackgroundResource(R.drawable.text_bg_border)
                    invalidate()
                    if (endListener != null) {
                        endListener!!.finishSwipe(mFrameLayout!!)
                    }
                    addControlBtn()
                }
                else -> {
                }
            }
        } else {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mStartX = event.x
                    mStartY = event.y
                    lastX = event.x
                    lastY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastX
                    val dy = event.y - lastY

                    //移动边界问题处理
                    val lm = (Math.abs(dx + mParams!!.leftMargin) + 0.5f).toInt()
                    val tm = (Math.abs(dy + mParams!!.topMargin) + 0.5f).toInt()

                    mParams!!.leftMargin = if (dx + mParams!!.leftMargin < 0) -lm else lm
                    mParams!!.topMargin = if (dy + mParams!!.topMargin < 0) -tm else tm


                    mFrameLayout!!.layoutParams = mParams
                    if (endListener != null) {
                        endListener!!.drag(dx, dy)
                    }
                    lastY = event.y
                    lastX = event.x
                }
                MotionEvent.ACTION_UP -> if (endListener != null) {
                    val rect = Rect(mFrameLayout!!.left, mFrameLayout!!.top, mFrameLayout!!.right, mFrameLayout!!.bottom)
                    //点击空白处消失
                    if ((event.x - mStartX).toDouble() == 0.0 && (event.y - mStartY).toDouble() == 0.0) {
                        if (!rect.contains(event.x.toInt(), event.y.toInt())) {
                            this.visibility = View.GONE
                            endListener!!.clear()
                        }
                    } else {
                        endListener!!.dragEnd(event.x - mStartX, event.y - mStartY)
                    }
                }
                else -> {
                }
            }
        }
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }


    interface EndListener {
        /**
         * 画矩形结束
         *
         * @param mFrameLayout FrameLayout
         */
        fun finishSwipe(mFrameLayout: FrameLayout)

        /**
         * 移动时
         *
         * @param dx float
         * @param dy float
         */
        fun drag(dx: Float, dy: Float)

        /**
         * 移动结束
         *
         * @param totalX float
         * @param totalY float
         */
        fun dragEnd(totalX: Float, totalY: Float)

        /**
         * 点击空白处取消时
         */
        fun clear()

        /**
         * 删除选中的所有子view
         */
        fun delete()

    }

    @SuppressLint("ResourceAsColor")
    private fun init() {
        if (this.mFrameLayout == null) {
            this.mFrameLayout = FrameLayout(context)
            this.mFrameLayout!!.setBackgroundColor(R.color.all_select_bg)
            this.mFrameLayout!!.isFocusable = true
            this.mFrameLayout!!.isFocusableInTouchMode = true
            this.mParams = FrameLayout.LayoutParams(0, 0)
            this.addView(this.mFrameLayout, this.mParams)
        }
    }

    //增加删除按钮
    private fun addControlBtn() {
        try {
            if (mDeleteTip == null) {
                mDeleteDrawable = context.resources.getDrawable(R.mipmap.ic_f_delete_normal)
                mDelParams = FrameLayout.LayoutParams(mDeleteDrawable!!.intrinsicWidth, mDeleteDrawable!!.intrinsicHeight)
                mDelParams!!.gravity = Gravity.TOP
                mDeleteTip = Button(context)
                mDeleteTip!!.setBackgroundResource(R.mipmap.ic_f_delete_normal)
                this.mFrameLayout!!.addView(mDeleteTip, mDelParams)

                mDeleteTip!!.setOnClickListener {
                    if (endListener != null) {
                        this.visibility = View.GONE
                        endListener!!.delete()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
