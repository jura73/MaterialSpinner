package com.github.jura73.materialspinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

abstract class ListSelectorView<T> extends View {

    public static final int ALPHA = 85;

    private boolean isShowChoiceAfterFilling;
    protected List<T> mArrayList;
    protected T mDefaultItem;
    private OnClickListener mOnLazyLoading;

    public ListSelectorView(Context context) {
        this(context, null);
    }

    int spaceSize;
    TextPaint labelTextPaint;
    TextPaint valueTextPaint;
    @Nullable
    String textLabel;
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
        drawText(canvas, textLabel, availableWidth, labelTextPaint, false);
        int widthValueTextWithSpace = getWidthValueTextWithSpace();
        canvas.translate(widthValueTextWithSpace, 0);
        availableWidth -= widthValueTextWithSpace;
        // Draw Value
        drawText(canvas, valueText, availableWidth, valueTextPaint, true);
    }

    private int getWidthValueTextWithSpace() {
        int widthValueText = 0;
        if (textLabel != null) {
            widthValueText = Math.round(labelTextPaint.measureText(textLabel));
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
        labelTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        labelTextPaint.setStyle(Paint.Style.STROKE);

        valueTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        valueTextPaint.setStyle(Paint.Style.STROKE);
        post(new Runnable() {
            @Override
            public void run() {
                restoreState();
            }
        });
        TypedArray typedArrayListSelectorView = context.obtainStyledAttributes(attrs, R.styleable.ListSelectorView);

        textLabel = typedArrayListSelectorView.getString(R.styleable.ListSelectorView_android_hint);
        valueText = typedArrayListSelectorView.getString(R.styleable.ListSelectorView_lsw_value);

        mDrawable = typedArrayListSelectorView.getDrawable(R.styleable.ListSelectorView_android_drawableEnd);
        if (mDrawable == null) {
            mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_right, null);
        }
        int scaledSizeInPixels = getResources().getDimensionPixelSize(R.dimen.fontSize);
        int textSize = typedArrayListSelectorView.getDimensionPixelSize(R.styleable.ListSelectorView_android_textSize, scaledSizeInPixels);
        labelTextPaint.setTextSize(textSize);
        valueTextPaint.setTextSize(textSize - 1);
        valueTextPaint.setAlpha(ALPHA);
        spaceSize = typedArrayListSelectorView.getDimensionPixelSize(R.styleable.ListSelectorView_lsw_spaceSize, scaledSizeInPixels / 2);

        setEnabled(typedArrayListSelectorView.getBoolean(R.styleable.MaterialSpinner_android_enabled, true));
        typedArrayListSelectorView.recycle();
    }

    public final void setList(@Nullable List<T> arrayList) {
        setClickable(true);
        mArrayList = arrayList;
        if (this.isShowChoiceAfterFilling) {
            this.showSpinnerListDialog();
            this.isShowChoiceAfterFilling = false;
        }
        restoreState();
    }

    public final void setListWithAutoSelect(@Nullable List<T> arrayList) {
        mArrayList = arrayList;
        if (arrayList != null) {
            if (mArrayList.size() == 1) {
                setSelectionItem(arrayList.get(0));
            } else if (this.isShowChoiceAfterFilling) {
                this.showSpinnerListDialog();
                this.isShowChoiceAfterFilling = false;
            }
            restoreState();
        }
    }

    public abstract void setSelectionItem(T item);

    protected void restoreState() {
    }

    public void clear() {
        this.mArrayList = null;
        this.cleanSelected();
    }

    public void cleanSelected() {
        setText("");
    }

    public final void setDefaultItem(@Nullable T item) {
        mDefaultItem = item;
        if (item != null) {
            setText(String.valueOf(item));
        } else {
            setText("");
        }
    }

    public final void setLazyLoading(@Nullable OnClickListener onClickListener) {
        this.mOnLazyLoading = onClickListener;
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
            height = Math.round(labelTextPaint.getTextSize() + labelTextPaint.getFontMetrics().bottom * getResources().getDisplayMetrics().density) + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }
}