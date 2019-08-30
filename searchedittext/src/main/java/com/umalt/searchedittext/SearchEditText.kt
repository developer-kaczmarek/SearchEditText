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

    private var clearIconAlpha = 0

    private var iconTouchedDown: Boolean = false

    private var isClearIconVisible = false
        set(value) {
            field = value
            requestLayout()
        }

    private var clearIconDrawable: Drawable? = null
        set(value) {
            field = value
            requestLayout()
        }

    private var searchIconDrawable: Drawable? = null
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

        setClearIconBounds()
        setSearchIconBounds()

        searchIconDrawable?.draw(canvas)

        if (isClearIconVisible) {
            clearIconDrawable?.apply {
                alpha = clearIconAlpha
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
        isClearIconVisible = textLength > 0

        if (textLength > 0 && clearIconAlpha == 0) {
            startClearIconAnimation()
        }

        if (textLength == 0) {
            clearIconDrawable?.alpha = 0
            clearIconAlpha = 0
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        clearIconDrawable?.let {
            if (isClearIconVisible) {
                val rectIconWithPaddings = Rect(it.bounds).apply {
                    val inset = -(VERTICAL_PADDING_DP * density).toInt()
                    inset(inset, inset)
                }
                val isTouchInIconBounds =
                    rectIconWithPaddings.contains(event.x.toInt() + scrollX, event.y.toInt())
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (isTouchInIconBounds) {
                        iconTouchedDown = true
                        return true
                    }
                    iconTouchedDown = false
                } else if (event.action == MotionEvent.ACTION_UP) {
                    if (isTouchInIconBounds) {
                        text = null
                        return true
                    }
                    iconTouchedDown = false
                }
            }
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

        searchIconDrawable = when {
            typedArray.hasValue(R.styleable.SearchEditText_set_icon_search) ->
                typedArray.getDrawable(R.styleable.SearchEditText_set_icon_search)
            else -> ContextCompat.getDrawable(context, R.drawable.ic_vector_search)
        }

        clearIconDrawable = when {
            typedArray.hasValue(R.styleable.SearchEditText_set_icon_clear) ->
                typedArray.getDrawable(R.styleable.SearchEditText_set_icon_clear)
            else -> ContextCompat.getDrawable(context, R.drawable.ic_vector_clear)
        }?.apply {
            alpha = clearIconAlpha
        }

        typedArray.recycle()

        background =
            ContextCompat.getDrawable(context, R.drawable.shape_solid_white61c_grey_rounded_48dp)
                ?.apply {
                    DrawableCompat.setTint(DrawableCompat.wrap(this), bgColor)
                }

        setPaddings()
    }

    private fun setSearchIconBounds() {
        searchIconDrawable?.let {
            val top = height / 2 - ICON_SIZE_DP / 2 * density
            val left = ICON_HORIZONTAL_PADDING_DP * density + scrollX
            val right = left + ICON_SIZE_DP * density
            val bottom = height / 2 + ICON_SIZE_DP / 2 * density
            it.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }
    }

    private fun setClearIconBounds() {
        clearIconDrawable?.let {
            val top = height / 2 - ICON_SIZE_DP / 2 * density
            val right = width - ICON_HORIZONTAL_PADDING_DP * density + scrollX
            val left = right - ICON_SIZE_DP * density
            val bottom = height / 2 + ICON_SIZE_DP / 2 * density
            it.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }
    }

    private fun setPaddings() {
        val clearIconWidth = when {
            clearIconDrawable != null && isClearIconVisible -> ICON_SIZE_DP * density
            else -> 0f
        }
        val searchIconWidth = searchIconDrawable?.let { ICON_SIZE_DP * density } ?: 0f
        val left = ICON_SIZE_DP * density + searchIconWidth
        val right = ICON_SIZE_DP * density + clearIconWidth
        val verticalPadding = VERTICAL_PADDING_DP * density

        setPadding(
            left.toInt(),
            verticalPadding.toInt(),
            right.toInt(),
            verticalPadding.toInt()
        )
    }

    private fun startClearIconAnimation() {
        ValueAnimator.ofInt(0, 255).apply {
            duration = 1000
            addUpdateListener {
                clearIconAlpha = it.animatedValue as Int
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