package com.github.jura73.materialspinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

abstract class ListSelectorView<T> extends View {

    public static final int ALPHA = 158;

    private boolean isShowChoiceAfterFilling;

    @Nullable
    protected List<T> mArrayList;
    private OnClickListener mOnLazyLoading;

    public ListSelectorView(Context context) {
        this(context, null);
    }

    int spaceSize;
    TextPaint hintTextPaint;
    TextPaint valueTextPaint;
    @Nullable
    String hint;
    @Nullable
    String valueText;
    Drawable mDrawable;

    public ListSelectorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setText(@Nullable String text) {
        valueText = text;
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) return;
        if (enabled) {
            setAlpha(1f);
        } else {
            setAlpha(0.2f);
        }
        super.setEnabled(enabled);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        final int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int topDrawable = (height - mDrawable.getIntrinsicHeight()) / 2;

        mDrawable.setBounds(availableWidth - mDrawable.getIntrinsicWidth(), topDrawable, availableWidth, topDrawable + mDrawable.getIntrinsicHeight());
        mDrawable.draw(canvas);

        availableWidth -= mDrawable.getIntrinsicWidth();

        canvas.translate(0, height >> 1);
        // Draw Label
        drawText(canvas, hint, availableWidth, hintTextPaint, false);
        int widthValueTextWithSpace = getWidthValueTextWithSpace();
        canvas.translate(widthValueTextWithSpace, 0);
        availableWidth -= widthValueTextWithSpace;
        // Draw Value
        drawText(canvas, valueText, availableWidth, valueTextPaint, true);
    }

    private int getWidthValueTextWithSpace() {
        int widthValueText = 0;
        if (hint != null) {
            widthValueText = Math.round(hintTextPaint.measureText(hint));
        }
        return widthValueText + spaceSize;
    }

    protected void drawText(Canvas canvas, String text, int availableWidth, TextPaint textPaint, boolean textToRight) {
        if (text != null && availableWidth > 0) {
            int yPos = Math.round(textPaint.getFontMetrics().descent);
            int widthValueText = Math.round(textPaint.measureText(text));
            if (widthValueText > availableWidth) {
                CharSequence ellipsizeValueText = TextUtils.ellipsize(text, textPaint, availableWidth, TextUtils.TruncateAt.END);
                canvas.drawText(ellipsizeValueText.toString(), 0, yPos, textPaint);
            } else {
                int xPos = 0;
                if (textToRight) {
                    xPos = availableWidth - widthValueText;
                }
                canvas.drawText(text, xPos, yPos, textPaint);
            }
        }
    }

    public ListSelectorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hintTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        hintTextPaint.setStyle(Paint.Style.STROKE);

        valueTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        valueTextPaint.setStyle(Paint.Style.STROKE);
        post(new Runnable() {
            @Override
            public void run() {
                restoreState();
            }
        });

        TypedArray typedArrayListSelectorView = context.obtainStyledAttributes(attrs, R.styleable.ListSelectorView);

        hint = typedArrayListSelectorView.getString(R.styleable.ListSelectorView_android_hint);
        valueText = typedArrayListSelectorView.getString(R.styleable.ListSelectorView_lsw_value);
        int textColor = typedArrayListSelectorView.getColor(R.styleable.ListSelectorView_android_textColor, getContext().getResources().getColor(R.color.primary_text));
        hintTextPaint.setColor(textColor);
        valueTextPaint.setColor(textColor);

        mDrawable = typedArrayListSelectorView.getDrawable(R.styleable.ListSelectorView_android_drawableEnd);
        if (mDrawable == null) {
            mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_right, null);
        }
        int scaledSizeInPixels = getResources().getDimensionPixelSize(R.dimen.fontSize);
        int textSize = typedArrayListSelectorView.getDimensionPixelSize(R.styleable.ListSelectorView_android_textSize, scaledSizeInPixels);
        hintTextPaint.setTextSize(textSize);
        valueTextPaint.setTextSize(textSize - 1);
        spaceSize = typedArrayListSelectorView.getDimensionPixelSize(R.styleable.ListSelectorView_lsw_spaceSize, scaledSizeInPixels / 2);
        int alphaValue = typedArrayListSelectorView.getInt(R.styleable.ListSelectorView_lsw_alpha_value, ALPHA);
        valueTextPaint.setAlpha(alphaValue);
        setEnabled(typedArrayListSelectorView.getBoolean(R.styleable.ListSelectorView_android_enabled, true));
        typedArrayListSelectorView.recycle();
    }

    public final void setList(@Nullable List<T> arrayList) {
        setInnerList(arrayList);
        showDialogAfterFillingIfNeed();
    }

    public void setHint(String hint) {
        this.hint = hint;
        invalidate();
    }

    public void setAlphaValue(@IntRange(from = 0, to = 255) int alpha) {
        valueTextPaint.setAlpha(alpha);
    }

    public void setColor(@ColorInt int color) {
        hintTextPaint.setColor(color);
        valueTextPaint.setColor(color);
    }

    public final void setListWithAutoSelect(@Nullable List<T> arrayList) {
        setInnerList(arrayList);
        if (arrayList != null) {
            if (arrayList.size() == 1) {
                setSelectionItem(arrayList.get(0));
            } else
                showDialogAfterFillingIfNeed();
        }
    }

    private void showDialogAfterFillingIfNeed() {
        if (this.isShowChoiceAfterFilling) {
            this.showSpinnerListDialog();
            this.isShowChoiceAfterFilling = false;
        }
    }

    private void setInnerList(@Nullable List<T> arrayList) {
        setClickable(true);
        mArrayList = arrayList;
        restoreState();
    }

    public abstract void setSelectionItem(@Nullable T item);

    protected void restoreState() {
    }

    public void clear() {
        this.mArrayList = null;
        this.cleanSelected();
    }

    public void cleanSelected() {
        setSelectionItem(null);
    }

    public final void setLazyLoading(@Nullable OnClickListener onClickListener) {
        this.mOnLazyLoading = onClickListener;
        setClickable(true);
    }

    @Override
    public boolean performClick() {
        if (this.mArrayList != null) {
            this.showSpinnerListDialog();
        } else {
            OnClickListener lazyLoading = this.mOnLazyLoading;
            if (lazyLoading != null) {
                this.isShowChoiceAfterFilling = true;
                lazyLoading.onClick(this);
            }
        }
        return super.performClick();
    }

    protected abstract void showSpinnerListDialog();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSize;
        int height = heightSize;

        if (heightMode != MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = Math.round(hintTextPaint.getTextSize() + hintTextPaint.getFontMetrics().bottom * getResources().getDisplayMetrics().density) + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }
}