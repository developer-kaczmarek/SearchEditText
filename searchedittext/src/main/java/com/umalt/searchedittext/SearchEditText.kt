package com.umalt.searchedittext

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat


/**
 * Created by Umalt on 26.08.2019.
 */
class SearchEditText : AppCompatEditText {

    private var drawableEndAlpha = 0

    private var isDrawableEndVisible = false
        set(value) {
            field = value
            requestLayout()
        }

    var onDrawableStartTouch: OnDrawableStartTouchListener? = null

    private var drawableEnd: Drawable? = null
        set(value) {
            field = value
            requestLayout()
        }

    private var drawableStart: Drawable? = null
        set(value) {
            field = value
            requestLayout()
        }

    private val View.density
        get() = context.resources.displayMetrics.density

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        setPaddings()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setDrawableEndBounds()
        setDrawableStartBounds()

        drawableStart?.draw(canvas)

        if (isDrawableEndVisible) {
            drawableEnd?.apply {
                alpha = drawableEndAlpha
                draw(canvas)
            }
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        val textLength = text?.length ?: 0
        isDrawableEndVisible = textLength > 0

        if (textLength > 0 && drawableEndAlpha == 0) {
            startDrawableEndAnimation()
        }

        if (textLength == 0) {
            drawableEnd?.alpha = 0
            drawableEndAlpha = 0
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val isTouchInDrawableEndBounds = drawableEnd?.isTouchInIconBounds(event) ?: false
        val isTouchInDrawableStartBounds = drawableStart?.isTouchInIconBounds(event) ?: false

        if (isTouchInDrawableEndBounds) {
            touchDrawableEnd(event)
        } else if (isTouchInDrawableStartBounds) {
            touchDrawableStart(event)
        }

        return super.onTouchEvent(event)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SearchEditText, defStyle, 0)

        val bgColor = typedArray.getColor(
            R.styleable.SearchEditText_set_background_color,
            ContextCompat.getColor(context, R.color.white_61c)
        )

        drawableStart = when {
            typedArray.hasValue(R.styleable.SearchEditText_drawable_start) ->
                typedArray.getDrawable(R.styleable.SearchEditText_drawable_start)
            else -> ContextCompat.getDrawable(context, R.drawable.ic_vector_search)
        }

        drawableEnd = when {
            typedArray.hasValue(R.styleable.SearchEditText_drawable_end) ->
                typedArray.getDrawable(R.styleable.SearchEditText_drawable_end)
            else -> ContextCompat.getDrawable(context, R.drawable.ic_vector_clear)
        }?.apply {
            alpha = drawableEndAlpha
        }

        typedArray.recycle()

        background =
            ContextCompat.getDrawable(context, R.drawable.shape_solid_white61c_grey_rounded_48dp)
                ?.apply {
                    DrawableCompat.setTint(DrawableCompat.wrap(this), bgColor)
                }

        setPaddings()
    }

    private fun touchDrawableEnd(event: MotionEvent): Boolean {
        drawableEnd?.let {
            if (isDrawableEndVisible) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    return true
                } else if (event.action == MotionEvent.ACTION_UP) {
                    text = null
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun touchDrawableStart(event: MotionEvent): Boolean {
        drawableStart?.let {
            if (event.action == MotionEvent.ACTION_DOWN) {
                return true
            } else if (event.action == MotionEvent.ACTION_UP) {
                onDrawableStartTouch?.onStartIconClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun Drawable.isTouchInIconBounds(event: MotionEvent): Boolean {
        val rectIconWithPaddings = Rect(bounds).apply {
            val inset = -(VERTICAL_PADDING_DP * density).toInt()
            inset(inset, inset)
        }
        return rectIconWithPaddings.contains(event.x.toInt() + scrollX, event.y.toInt())
    }

    private fun setDrawableStartBounds() {
        drawableStart?.let {
            val top = height / 2 - ICON_SIZE_DP / 2 * density
            val left = ICON_HORIZONTAL_PADDING_DP * density + scrollX
            val right = left + ICON_SIZE_DP * density
            val bottom = height / 2 + ICON_SIZE_DP / 2 * density
            it.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }
    }

    private fun setDrawableEndBounds() {
        drawableEnd?.let {
            val top = height / 2 - ICON_SIZE_DP / 2 * density
            val right = width - ICON_HORIZONTAL_PADDING_DP * density + scrollX
            val left = right - ICON_SIZE_DP * density
            val bottom = height / 2 + ICON_SIZE_DP / 2 * density
            it.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }
    }

    private fun setPaddings() {
        val drawableEndWidth = when {
            drawableEnd != null && isDrawableEndVisible -> ICON_SIZE_DP * density
            else -> 0f
        }
        val drawableStartWidth = drawableStart?.let { ICON_SIZE_DP * density } ?: 0f
        val left = ICON_SIZE_DP * density + drawableStartWidth
        val right = ICON_SIZE_DP * density + drawableEndWidth
        val verticalPadding = VERTICAL_PADDING_DP * density

        setPadding(
            left.toInt(),
            verticalPadding.toInt(),
            right.toInt(),
            verticalPadding.toInt()
        )
    }

    private fun startDrawableEndAnimation() {
        ValueAnimator.ofInt(0, 255).apply {
            duration = 1000
            addUpdateListener {
                drawableEndAlpha = it.animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    companion object {
        private const val ICON_SIZE_DP = 24
        private const val ICON_HORIZONTAL_PADDING_DP = 16
        private const val VERTICAL_PADDING_DP = 8
    }
}