package com.github.jura73.materialspinner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.IntRange
import android.support.v4.content.res.ResourcesCompat
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View

abstract class ListSelectorView<T> constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    private var isShowChoiceAfterFilling: Boolean = false

    protected var itemList: List<T>? = null
    private var mOnLazyLoading: OnClickListener? = null

    private var spaceSize: Int = 0
    private var hintTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var valueTextPaint: TextPaint
    private var hint: String? = null
    private var valueText: String? = null
    private val mDrawable: Drawable

    init {
        hintTextPaint.style = Paint.Style.FILL_AND_STROKE

        valueTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        valueTextPaint.style = Paint.Style.FILL_AND_STROKE
        post { restoreState() }

        val typedArrayListSelectorView = context.obtainStyledAttributes(attrs, R.styleable.ListSelectorView)

        hint = typedArrayListSelectorView.getString(R.styleable.ListSelectorView_android_hint)
        valueText = typedArrayListSelectorView.getString(R.styleable.ListSelectorView_lsw_value)
        val textColor = typedArrayListSelectorView.getColor(R.styleable.ListSelectorView_android_textColor, getContext().resources.getColor(R.color.primary_text))
        hintTextPaint.color = textColor
        valueTextPaint.color = textColor

        mDrawable = typedArrayListSelectorView.getDrawable(R.styleable.ListSelectorView_android_drawableEnd)
                ?: ResourcesCompat.getDrawable(resources, R.drawable.ic_keyboard_arrow_right, null)!!

        val scaledSizeInPixels = resources.getDimensionPixelSize(R.dimen.fontSize)
        val textSize = typedArrayListSelectorView.getDimensionPixelSize(R.styleable.ListSelectorView_android_textSize, scaledSizeInPixels)
        hintTextPaint.textSize = textSize.toFloat()
        valueTextPaint.textSize = (textSize - 1).toFloat()
        spaceSize = typedArrayListSelectorView.getDimensionPixelSize(R.styleable.ListSelectorView_lsw_spaceSize, scaledSizeInPixels / 2)
        val alphaValue = typedArrayListSelectorView.getInt(R.styleable.ListSelectorView_lsw_alpha_value, ALPHA)
        valueTextPaint.alpha = alphaValue
        isEnabled = typedArrayListSelectorView.getBoolean(R.styleable.ListSelectorView_android_enabled, true)
        typedArrayListSelectorView.recycle()
    }

    protected abstract fun showSpinnerListDialog()

    protected fun setText(text: String?) {
        valueText = text
        invalidate()
    }

    override fun setEnabled(enabled: Boolean) {
        if (enabled == isEnabled) return
        if (enabled) {
            setAlpha(1f)
        } else {
            setAlpha(0.3f)
        }
        super.setEnabled(enabled)
    }

    fun setList(arrayList: List<T>?) {
        setInnerList(arrayList)
        showDialogAfterFillingIfNeed()
    }

    fun setHint(hint: String) {
        this.hint = hint
        invalidate()
    }

    fun setAlphaValue(@IntRange(from = 0, to = 255) alpha: Int) {
        valueTextPaint.alpha = alpha
    }

    fun setColor(@ColorInt color: Int) {
        hintTextPaint.color = color
        valueTextPaint.color = color
    }

    fun setListWithAutoSelect(arrayList: List<T>?) {
        setInnerList(arrayList)
        if (arrayList != null) {
            if (arrayList.size == 1) {
                setSelectedPosition(0)
            } else
                showDialogAfterFillingIfNeed()
        }
    }

    private fun showDialogAfterFillingIfNeed() {
        if (this.isShowChoiceAfterFilling) {
            this.showSpinnerListDialog()
            this.isShowChoiceAfterFilling = false
        }
    }

    private fun setInnerList(arrayList: List<T>?) {
        isClickable = true
        itemList = arrayList
        restoreState()
    }

    abstract fun setSelectedPosition(positions: Int)

    protected open fun restoreState() {}

    fun clear() {
        this.itemList = null
        this.cleanSelected()
    }

    abstract fun cleanSelected()

    fun setLazyLoading(onClickListener: OnClickListener?) {
        this.mOnLazyLoading = onClickListener
        isClickable = true
    }

    override fun performClick(): Boolean {
        if (this.itemList != null) {
            this.showSpinnerListDialog()
        } else {
            mOnLazyLoading?.let {
                this.isShowChoiceAfterFilling = true
                it.onClick(this)
            }
        }
        return super.performClick()
    }

    private fun widthValueTextWithSpace(): Int {
        hint.let {
            return if (it != null) {
                Math.round(hintTextPaint.measureText(hint)) + spaceSize
            } else {
                spaceSize
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        var availableWidth = width - paddingRight - paddingLeft
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        val height = height - paddingTop - paddingBottom
        val topDrawable = (height - mDrawable.intrinsicHeight) / 2

        mDrawable.setBounds(availableWidth - mDrawable.intrinsicWidth, topDrawable, availableWidth, topDrawable + mDrawable.intrinsicHeight)
        mDrawable.draw(canvas)

        availableWidth -= mDrawable.intrinsicWidth

        canvas.translate(0f, (height shr 1).toFloat())
        // Draw Label
        drawText(canvas, hint, availableWidth, hintTextPaint, false)
        val widthValueTextWithSpace = widthValueTextWithSpace()
        canvas.translate(widthValueTextWithSpace.toFloat(), 0f)
        availableWidth -= widthValueTextWithSpace
        // Draw Value
        drawText(canvas, valueText, availableWidth, valueTextPaint, true)
    }

    private fun drawText(canvas: Canvas, text: String?, availableWidth: Int, textPaint: TextPaint, textToRight: Boolean) {
        if (text != null && availableWidth > 0) {
            val yPos = -Math.round((textPaint.descent() + textPaint.ascent()) / 2)
            val widthValueText = Math.round(textPaint.measureText(text))
            if (widthValueText > availableWidth) {
                val ellipsizeValueText = TextUtils.ellipsize(text, textPaint, availableWidth.toFloat(), TextUtils.TruncateAt.END)
                canvas.drawText(ellipsizeValueText.toString(), 0f, yPos.toFloat(), textPaint)
            } else {
                var xPos = 0
                if (textToRight) {
                    xPos = availableWidth - widthValueText
                }
                canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), textPaint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var height = heightSize

        if (heightMode != MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = Math.round(hintTextPaint.textSize + hintTextPaint.fontMetrics.bottom * resources.displayMetrics.density) + paddingTop + paddingBottom
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize)
            }
        }
        setMeasuredDimension(widthSize, height)
    }

    companion object {
        const val ALPHA = 158
    }
}