package com.yppcat.smartui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import com.yppcat.smartui.util.DensityUtil


class RoundImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : android.support.v7.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private var width: Float = 0.toFloat()
    private var height: Float = 0.toFloat()

    private var roundWidth: Int = 0

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0) {
        initView(context, attrs)
    }

    init {
        initView(context, attrs)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
    }

    private fun initView(context: Context, attr: AttributeSet?) {
        val array = context.obtainStyledAttributes(attr, R.styleable.RoundImageView)
        roundWidth = array.getInt(R.styleable.RoundImageView_round_width, 10)
        array.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        if (width > DensityUtil.dip2px(context,roundWidth) && height > DensityUtil.dip2px(context,roundWidth)) {
            val path = Path()
            path.moveTo(DensityUtil.dip2px(context,roundWidth).toFloat(), 0f)
            path.lineTo(width - DensityUtil.dip2px(context,roundWidth), 0f)
            path.quadTo(width, 0f, width, DensityUtil.dip2px(context,roundWidth).toFloat())
            path.lineTo(width, height - DensityUtil.dip2px(context,roundWidth))
            path.quadTo(width, height, width - DensityUtil.dip2px(context,roundWidth), height)
            path.lineTo(DensityUtil.dip2px(context,roundWidth).toFloat(), height)
            path.quadTo(0f, height, 0f, height - DensityUtil.dip2px(context,roundWidth))
            path.lineTo(0f, DensityUtil.dip2px(context,roundWidth).toFloat())
            path.quadTo(0f, 0f, DensityUtil.dip2px(context,roundWidth).toFloat(), 0f)
            canvas.clipPath(path)
        }

        super.onDraw(canvas)
    }
}
