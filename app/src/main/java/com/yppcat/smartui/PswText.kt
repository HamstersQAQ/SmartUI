package com.yppcat.smartui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import java.util.*


class PswText : View {
    private var input: InputMethodManager? = null//输入法管理
    private var result: ArrayList<Int>? = null//保存当前输入的密码
    private var saveResult: Int = 0//保存按下返回键时输入的密码总数
    private var pswLength: Int = 0//密码长度
    private var borderColor: Int = 0//密码框颜色
    private var borderShadowColor: Int = 0//密码框阴影颜色
    private var pswColor: Int = 0//明文密码颜色
    private var pswTextSize: Int = 0//明文密码字体大小
    private var inputBorderColor: Int = 0//输入时密码边框颜色
    private var borderImg: Int = 0//边框图片
    private var inputBorderImg: Int = 0//输入时边框图片
    private var delayTime: Int = 0//延迟绘制圆点时间，1000 = 1s
    private var isBorderImg: Boolean = false//是否使用图片绘制边框
    private var isShowTextPsw: Boolean = false//是否在按返回键时绘制明文密码
    private var isShowBorderShadow: Boolean = false//是否绘制在输入时，密码框的阴影颜色
    private var clearTextPsw: Boolean = false//是否只绘制明文密码
    private var darkPsw: Boolean = false//是否只绘制圆点密码
    private var isChangeBorder: Boolean = false//是否在输入密码时不更改密码框颜色
    private var pswDotPaint: Paint? = null//密码圆点画笔
    private var pswTextPaint: Paint? = null//明文密码画笔
    private var borderPaint: Paint? = null//边框画笔
    private var inputBorderPaint: Paint? = null//输入时边框画笔
    private var borderRectF: RectF? = null//边框圆角矩形
    private var borderRadius: Int = 0//边框圆角程度
    private var borderWidth: Int = 0//边框宽度
    private var spacingWidth: Int = 0//边框之间的间距宽度
    private var inputCallBack: InputCallBack? = null//输入完成时监听
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    invalidated = true
                    invalidate()
                }
                else -> {
                }
            }
        }
    }

    var isClearTextPsw: Boolean
        get() = clearTextPsw
        set(clearTextPsw) {
            this.clearTextPsw = clearTextPsw
            invalidate()
        }

    var isDarkPsw: Boolean
        get() = darkPsw
        set(darkPsw) {
            this.darkPsw = darkPsw
            invalidate()
        }

    //获取密码
    val psw: String
        get() {
            val sb = StringBuffer()
            for (i in result!!) {
                sb.append(i)
            }
            return sb.toString()
        }

    interface InputCallBack {
        fun onInputFinish(password: String)
    }

    fun setInputCallBack(inputCallBack: InputCallBack) {
        this.inputCallBack = inputCallBack
    }

    fun setPswLength(pswLength: Int) {
        this.pswLength = pswLength
        invalidate()
    }

    fun getPswLength(): Int {
        return pswLength
    }

    fun setBorderColor(borderColor: Int) {
        this.borderColor = borderColor
        borderPaint!!.color = borderColor
        invalidate()
    }

    fun getBorderColor(): Int {
        return borderColor
    }

    fun setPswColor(pswColor: Int) {
        this.pswColor = pswColor
        pswDotPaint!!.color = pswColor
        pswTextPaint!!.color = pswColor
        invalidate()
    }

    fun getPswColor(): Int {
        return pswColor
    }

    fun setInputBorderColor(inputBorderColor: Int) {
        this.inputBorderColor = inputBorderColor
        inputBorderPaint!!.color = inputBorderColor
        invalidate()
    }

    fun getInputBorderColor(): Int {
        return inputBorderColor
    }

    fun setBorderShadowColor(borderShadowColor: Int) {
        this.borderShadowColor = borderShadowColor
        inputBorderPaint!!.setShadowLayer(6f, 0f, 0f, borderShadowColor)
        invalidate()
    }

    fun getBorderShadowColor(): Int {
        return borderShadowColor
    }

    fun setPswTextSize(pswTextSize: Int) {
        this.pswTextSize = pswTextSize
        invalidate()
    }

    fun getPswTextSize(): Int {
        return pswTextSize
    }

    fun setBorderRadius(borderRadius: Int) {
        this.borderRadius = borderRadius
        invalidate()
    }

    fun getBorderRadius(): Int {
        return borderRadius
    }

    fun setIsBorderImg(borderImg: Boolean) {
        isBorderImg = borderImg
        invalidate()
    }

    fun isBorderImg(): Boolean {
        return isBorderImg
    }

    fun setIsShowTextPsw(showTextPsw: Boolean) {
        isShowTextPsw = showTextPsw
        invalidate()
    }

    fun isShowTextPsw(): Boolean {
        return isShowTextPsw
    }

    fun setBorderImg(borderImg: Int) {
        this.borderImg = borderImg
        invalidate()
    }

    fun getBorderImg(): Int {
        return borderImg
    }

    fun setInputBorderImg(inputBorderImg: Int) {
        this.inputBorderImg = inputBorderImg
        invalidate()
    }

    fun getInputBorderImg(): Int {
        return inputBorderImg
    }

    fun setDelayTime(delayTime: Int) {
        this.delayTime = delayTime
        invalidate()
    }

    fun getDelayTime(): Int {
        return delayTime
    }

    fun setIsChangeBorder(changeBorder: Boolean) {
        isChangeBorder = changeBorder
        invalidate()
    }

    fun isChangeBorder(): Boolean {
        return isChangeBorder
    }

    fun setShowBorderShadow(showBorderShadow: Boolean) {
        isShowBorderShadow = showBorderShadow
        invalidate()
    }

    fun isShowBorderShadow(): Boolean {
        return isShowBorderShadow
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    //初始化
    @SuppressLint("Recycle")
    private fun initView(context: Context, attrs: AttributeSet) {
        this.setOnKeyListener(NumKeyListener())
        this.isFocusable = true
        this.isFocusableInTouchMode = true
        input = getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        result = ArrayList()

        val array = context.obtainStyledAttributes(attrs, R.styleable.PswText)
        if (array != null) {
            pswLength = array.getInt(R.styleable.PswText_pswLength, 6)
            pswColor = array.getColor(R.styleable.PswText_pswColor, Color.parseColor("#333333"))
            borderColor = array.getColor(R.styleable.PswText_borderColor, Color.parseColor("#999999"))
            inputBorderColor = array.getColor(R.styleable.PswText_inputBorder_color, Color.parseColor("#E6E6E6"))
            borderShadowColor = array.getColor(R.styleable.PswText_borderShadow_color, Color.parseColor("#3577e2"))
            borderImg = array.getResourceId(R.styleable.PswText_borderImg, R.drawable.pic_dlzc_srk1)
            inputBorderImg = array.getResourceId(R.styleable.PswText_inputBorderImg, R.drawable.pic_dlzc_srk)
            isBorderImg = array.getBoolean(R.styleable.PswText_isDrawBorderImg, false)
            isShowTextPsw = array.getBoolean(R.styleable.PswText_isShowTextPsw, false)
            isShowBorderShadow = array.getBoolean(R.styleable.PswText_isShowBorderShadow, false)
            clearTextPsw = array.getBoolean(R.styleable.PswText_clearTextPsw, false)
            darkPsw = array.getBoolean(R.styleable.PswText_darkPsw, false)
            isChangeBorder = array.getBoolean(R.styleable.PswText_isChangeBorder, false)
            delayTime = array.getInt(R.styleable.PswText_delayTime, 1000)
            pswTextSize = array.getDimension(R.styleable.PswText_psw_textSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, resources.displayMetrics)).toInt()
            borderRadius = array.getDimension(R.styleable.PswText_borderRadius,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)).toInt()
        } else {
            pswLength = 6
            pswColor = Color.parseColor("#3779e3")
            borderColor = Color.parseColor("#999999")
            inputBorderColor = Color.parseColor("#3779e3")
            borderShadowColor = Color.parseColor("#3577e2")
            borderImg = R.drawable.pic_dlzc_srk1
            inputBorderImg = R.drawable.pic_dlzc_srk
            delayTime = 1000
            clearTextPsw = false
            darkPsw = false
            isBorderImg = false
            isShowTextPsw = false
            isShowBorderShadow = false
            isChangeBorder = false
            //明文密码字体大小，初始化18sp
            pswTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, resources.displayMetrics).toInt()
            //边框圆角程度初始化8dp
            borderRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
        }
        //边框宽度初始化47dp
        borderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47f, resources.displayMetrics).toInt()
        //边框之间的间距初始化10dp
        spacingWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, resources.displayMetrics).toInt()
        borderRectF = RectF()
        initPaint()
    }

    //初始化画笔
    private fun initPaint() {
        //密码圆点画笔初始化
        pswDotPaint = Paint()
        pswDotPaint!!.isAntiAlias = true
        pswDotPaint!!.strokeWidth = 3f
        pswDotPaint!!.style = Paint.Style.FILL
        pswDotPaint!!.color = pswColor

        //明文密码画笔初始化
        pswTextPaint = Paint()
        pswTextPaint!!.isAntiAlias = true
        pswTextPaint!!.isFakeBoldText = true
        pswTextPaint!!.color = pswColor

        //边框画笔初始化
        borderPaint = Paint()
        borderPaint!!.isAntiAlias = true
        borderPaint!!.color = borderColor
        borderPaint!!.style = Paint.Style.STROKE
        borderPaint!!.strokeWidth = 3f

        //输入时边框画笔初始化
        inputBorderPaint = Paint()
        inputBorderPaint!!.isAntiAlias = true
        inputBorderPaint!!.color = inputBorderColor
        inputBorderPaint!!.style = Paint.Style.STROKE
        inputBorderPaint!!.strokeWidth = 3f
        //是否绘制边框阴影
        if (isShowBorderShadow) {
            inputBorderPaint!!.setShadowLayer(6f, 0f, 0f, borderShadowColor)
            setLayerType(View.LAYER_TYPE_SOFTWARE, inputBorderPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpec = View.MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val heightSpec = View.MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        if (widthSpec == View.MeasureSpec.AT_MOST) {
            if (heightSpec != View.MeasureSpec.AT_MOST) {//高度已知但宽度未知时
                //				spacingWidth = heightSize / 4;
                spacingWidth = 0
                widthSize = heightSize * pswLength + spacingWidth * pswLength
                borderWidth = heightSize
            } else {//宽高都未知时
                widthSize = borderWidth * pswLength + spacingWidth * pswLength
                heightSize = (borderWidth + borderPaint!!.strokeWidth * 2).toInt()
            }
        } else {
            //宽度已知但高度未知时
            if (heightSpec == View.MeasureSpec.AT_MOST) {
                borderWidth = widthSize * 4 / (5 * pswLength)
                //				spacingWidth = borderWidth / 4;
                spacingWidth = 0
                heightSize = (borderWidth + borderPaint!!.strokeWidth * 2).toInt()
            }
        }
        Companion.height = heightSize
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val dotRadius = borderWidth / 6//密码圆点为边框宽度的六分之一

        /*
		* 如果明文密码字体大小为默认大小，则取边框宽度的八分之一，否则用自定义大小
		* */
        if (pswTextSize == TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, resources.displayMetrics).toInt()) {
            pswTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (borderWidth / 8).toFloat(), resources.displayMetrics).toInt()
        }
        pswTextPaint!!.textSize = pswTextSize.toFloat()

        //绘制密码边框
        drawBorder(canvas, Companion.height)
        if (isChangeBorder) {
            if (clearTextPsw) {
                for (i in result!!.indices) {
                    val num = result!![i].toString() + ""
                    drawText(canvas, num, i)
                }
            } else if (darkPsw) {
                for (i in result!!.indices) {
                    val circleX = ((i * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                    val circleY = (Companion.height / 2).toFloat()
                    canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint!!)
                }
            } else {
                if (invalidated) {
                    drawDelayCircle(canvas, Companion.height, dotRadius)
                    return
                }
                for (i in result!!.indices) {
                    //明文密码
                    val num = result!![i].toString() + ""
                    //圆点坐标
                    val circleX = (((i - 1) * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                    val circleY = (Companion.height / 2).toFloat()
                    //密码框坐标
                    drawText(canvas, num, i)

                    /*
				* 当输入位置 = 输入长度时
				* 即判断当前绘制位置是否等于当前正在输入密码的位置
				* 若是则延迟delayTime时间后绘制为圆点
				* */
                    if (i + 1 == result!!.size) {
                        handler.sendEmptyMessageDelayed(1, delayTime.toLong())
                    }
                    //若按下back键保存的密码 > 输入的密码长度，则只绘制圆点
                    //即按下back键时，不绘制明文密码
                    if (!isShowTextPsw) {
                        if (saveResult > result!!.size) {
                            canvas.drawCircle((i * (borderWidth + spacingWidth) + (borderWidth / 2 + 0.5 * spacingWidth)).toFloat(), circleY, dotRadius.toFloat(), pswDotPaint!!)
                        }
                    }
                    //当输入第二个密码时，才开始从第一个位置绘制圆点
                    if (i >= 1) {
                        canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint!!)
                    }
                }
            }
        } else {
            if (clearTextPsw) {
                for (i in result!!.indices) {
                    val num = result!![i].toString() + ""
                    drawText(canvas, num, i)
                    //计算密码边框坐标
                    val left = (i * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                    val right = (((i + 1) * borderWidth).toDouble() + (i * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()

                    drawBitmapOrBorder(canvas, left, right, Companion.height)
                }
            } else if (darkPsw) {
                for (i in result!!.indices) {
                    val circleX = ((i * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                    val circleY = (Companion.height / 2).toFloat()
                    val left = (i * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                    val right = (((i + 1) * borderWidth).toDouble() + (i * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()
                    drawBitmapOrBorder(canvas, left, right, Companion.height)
                    canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint!!)
                }
            } else {
                if (invalidated) {
                    drawDelayCircle(canvas, Companion.height, dotRadius)
                    return
                }
                for (i in result!!.indices) {
                    //明文密码
                    val num = result!![i].toString() + ""
                    //圆点坐标
                    val circleX = (((i - 1) * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                    val circleY = (Companion.height / 2).toFloat()
                    //密码框坐标
                    val left = (i * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                    val right = (((i + 1) * borderWidth).toDouble() + (i * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()

                    drawBitmapOrBorder(canvas, left, right, Companion.height)

                    drawText(canvas, num, i)

                    /*
				* 当输入位置 = 输入长度时
				* 即判断当前绘制位置是否等于当前正在输入密码的位置
				* 若是则延迟delayTime时间后绘制为圆点
				* */
                    if (i + 1 == result!!.size) {
                        handler.sendEmptyMessageDelayed(1, delayTime.toLong())
                    }
                    //若按下back键保存的密码 > 输入的密码长度，则只绘制圆点
                    //即按下back键时，不绘制明文密码
                    if (!isShowTextPsw) {
                        if (saveResult > result!!.size) {
                            canvas.drawCircle((i * (borderWidth + spacingWidth) + (borderWidth / 2 + 0.5 * spacingWidth)).toFloat(), circleY, dotRadius.toFloat(), pswDotPaint!!)
                        }
                    }
                    //当输入第二个密码时，才开始从第一个位置绘制圆点
                    if (i >= 1) {
                        canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint!!)
                    }
                }
            }
        }

    }

    //绘制明文密码
    private fun drawText(canvas: Canvas, num: String, i: Int) {
        val mTextBound = Rect()
        pswTextPaint!!.getTextBounds(num, 0, num.length, mTextBound)
        val fontMetrics = pswTextPaint!!.fontMetrics
        val textX = ((i * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2 - mTextBound.width() / 2).toDouble() + 0.45 * spacingWidth).toFloat()
        val textY = (Companion.height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
        if (saveResult != 0 || saveResult < result!!.size) {
            canvas.drawText(num, textX, textY, pswTextPaint!!)
        }
    }

    //延迟delay时间后，将当前输入的明文密码绘制为圆点
    private fun drawDelayCircle(canvas: Canvas, height: Int, dotRadius: Int) {
        invalidated = false
        if (isChangeBorder) {
            for (i in result!!.indices) {
                val circleX = (((i - 1) * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                val circleY = (height / 2).toFloat()
                canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint!!)
            }
            canvas.drawCircle((((result!!.size - 1) * (borderWidth + spacingWidth) + borderWidth / 2).toFloat() + 0.5 * spacingWidth).toFloat(),
                    (height / 2).toFloat(), dotRadius.toFloat(), pswDotPaint!!)
        } else {
            for (i in result!!.indices) {
                val circleX = (((i - 1) * (borderWidth + spacingWidth)).toDouble() + (borderWidth / 2).toDouble() + 0.5 * spacingWidth).toFloat()
                val circleY = (height / 2).toFloat()
                val left = (i * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
                val right = (((i + 1) * borderWidth).toDouble() + (i * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()
                canvas.drawCircle(circleX, circleY, dotRadius.toFloat(), pswDotPaint!!)
                drawBitmapOrBorder(canvas, left, right, height)
            }
            canvas.drawCircle((((result!!.size - 1) * (borderWidth + spacingWidth) + borderWidth / 2).toFloat() + 0.5 * spacingWidth).toFloat(),
                    (height / 2).toFloat(), dotRadius.toFloat(), pswDotPaint!!)
        }
        handler.removeMessages(1)
    }

    //绘制初始密码框时判断是否用图片绘制密码框
    private fun drawBorder(canvas: Canvas, height: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, borderImg)
        val src = Rect(0, 0, bitmap.width, bitmap.height)
        for (i in 0 until pswLength) {
            val left = (i * (borderWidth + spacingWidth) + 0.5 * spacingWidth).toInt()
            val right = (((i + 1) * borderWidth).toDouble() + (i * spacingWidth).toDouble() + 0.5 * spacingWidth).toInt()
            if (isBorderImg) {
                val dst = Rect(left, borderPaint!!.strokeWidth.toInt(), right, (height - borderPaint!!.strokeWidth).toInt())
                canvas.drawBitmap(bitmap, src, dst, borderPaint)
            } else {
                borderRectF!!.set(left.toFloat(), borderPaint!!.strokeWidth, right.toFloat(), height - borderPaint!!.strokeWidth)
                canvas.drawRoundRect(borderRectF!!, 0f, 0f, borderPaint!!)
            }
        }
        bitmap.recycle()

    }

    //是否使用图片绘制密码框
    private fun drawBitmapOrBorder(canvas: Canvas, left: Int, right: Int, height: Int) {
        if (isBorderImg) {
            val bitmap = BitmapFactory.decodeResource(resources, inputBorderImg)
            val src = Rect(0, 0, bitmap.width, bitmap.height)
            val dst = Rect(left, (0 + borderPaint!!.strokeWidth).toInt(), right, (height - borderPaint!!.strokeWidth).toInt())
            canvas.drawBitmap(bitmap, src, dst, inputBorderPaint)
            bitmap.recycle()
        } else {
            borderRectF!!.set(left.toFloat(), 0 + borderPaint!!.strokeWidth, right.toFloat(), height - borderPaint!!.strokeWidth)
            canvas.drawRoundRect(borderRectF!!, 0f, 0f, inputBorderPaint!!)
        }
    }

    //清除密码
    fun clearPsw() {
        result!!.clear()
        invalidate()
    }

    //隐藏键盘
    fun hideKeyBord() {
        input!!.hideSoftInputFromWindow(this.windowToken, 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {//点击弹出键盘
            requestFocus()
            input!!.showSoftInput(this, InputMethodManager.SHOW_FORCED)
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus) {
            input!!.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER//只允许输入数字
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE
        return NumInputConnection(this, false)
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    internal inner class NumInputConnection(targetView: View, fullEditor: Boolean) : BaseInputConnection(targetView, fullEditor) {

        override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
            //这里是接收文本的输入法，我们只允许输入数字，则不做任何处理
            return super.commitText(text, newCursorPosition)
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            //屏蔽返回键，发送自己的删除事件
            return if (beforeLength == 1 && afterLength == 0) {
                super.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && super.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            } else super.deleteSurroundingText(beforeLength, afterLength)
        }
    }

    /**
     * 输入监听
     */
    inner class NumKeyListener : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (event.isShiftPressed) {//处理*#等键
                    return false
                }
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {//只处理数字
                    if (result!!.size < pswLength) {
                        result!!.add(keyCode - 7)
                        invalidate()
                        FinishInput()
                    }
                    return true
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (!result!!.isEmpty()) {//不为空时，删除最后一个数字
                        saveResult = result!!.size
                        result!!.removeAt(result!!.size - 1)
                        invalidate()
                    }
                    return true
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    FinishInput()
                    return true
                }
            }
            return false
        }

        /**
         * 输入完成后调用的方法
         */
        private fun FinishInput() {
            if (result!!.size == pswLength && inputCallBack != null) {//输入已完成
                val sb = StringBuffer()
                for (i in result!!) {
                    sb.append(i)
                }
                inputCallBack!!.onInputFinish(sb.toString())
                val imm = this@PswText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(this@PswText.windowToken, 0) //输入完成后隐藏键盘
            }
        }
    }

    companion object {

        private var invalidated = false
        private var height: Int = 0//整个view的高度
    }
}