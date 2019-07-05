package com.github.jura73.materialspinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public final class ListSelectorView<T> extends View {
    public static final int INVALID_POSITION = -1;
    public static final int ALPHA = 85;

    private boolean isShowChoiceAfterFilling;
    private List<T> mArrayList;
    private T mDefaultItem;
    private OnClickListener mOnLazyLoading;
    private OnItemSelectedListener<T> mOnItemSelectedListener;
    private int mSelectedPosition = INVALID_POSITION;

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
        //  this(context, attrs, R.attr.listSelectorWidgetStyle);//R.attr.listSelectorStyle
        this(context, attrs, 0);//R.attr.listSelectorStyle
    }

    public void setText(String text) {
        valueText = text;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        canvas.translate(getPaddingLeft(), 0);

        final int height = getHeight();
        int topDrawable = (height - mDrawable.getIntrinsicHeight()) / 2;

        mDrawable.setBounds(availableWidth - mDrawable.getIntrinsicWidth(), topDrawable, availableWidth, topDrawable + mDrawable.getIntrinsicHeight());
        mDrawable.draw(canvas);

        availableWidth -= mDrawable.getIntrinsicWidth();

        // Draw Label
        drawText(canvas, textLabel, availableWidth, labelTextPaint, false);
        int widthValueText = Math.round(labelTextPaint.measureText(textLabel));
        canvas.translate(widthValueText + spaceSize, 0);
        availableWidth -= widthValueText + spaceSize;
        // Draw Value

        drawText(canvas, valueText, availableWidth, valueTextPaint, true);
    }

    protected void drawText(Canvas canvas, String text, int availableWidth, TextPaint textPaint, boolean textToRight) {
        if (text != null && availableWidth > 0) {
            int yPos = -Math.round(textPaint.getFontMetrics().top);
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
                restorePosition();
            }
        });
        TypedArray typedArrayListSelectorView = context.obtainStyledAttributes(attrs, R.styleable.ListSelectorView);

        textLabel = typedArrayListSelectorView.getNonResourceString(R.styleable.ListSelectorView_lsw_label);
        valueText = typedArrayListSelectorView.getNonResourceString(R.styleable.ListSelectorView_lsw_value);

        mDrawable = typedArrayListSelectorView.getDrawable(R.styleable.ListSelectorView_android_drawableEnd);
        if (mDrawable == null) {
            mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_right, null);
        }
        int scaledSizeInPixels = getResources().getDimensionPixelSize(R.dimen.fontSize);
        int textSize = typedArrayListSelectorView.getDimensionPixelSize(R.styleable.ListSelectorView_android_textSize, scaledSizeInPixels);
        labelTextPaint.setTextSize(textSize);
        valueTextPaint.setTextSize(textSize);
        valueTextPaint.setAlpha(ALPHA);
        spaceSize = typedArrayListSelectorView.getDimensionPixelSize(R.styleable.ListSelectorView_lsw_spaceSize, scaledSizeInPixels / 2);

//        ColorStateList colors = typedArrayMaterialSpinner.getColorStateList(R.styleable.MaterialSpinner_android_textColor);
//        if (colors != null) {
//           // setTextColor(colors);
//        }
//
//        setEnabled(typedArrayMaterialSpinner.getBoolean(R.styleable.MaterialSpinner_android_enabled, true));
//        typedArrayMaterialSpinner.recycle();
//
//        TypedArray typedArraySpinner = getContext().obtainStyledAttributes(attrs, android.support.design.R.styleable.Spinner);
//        int entriesResourceId = typedArraySpinner.getResourceId(0, -1);
//        if (entriesResourceId > 0) {
//            String[] res = getContext().getResources().getStringArray(entriesResourceId);
//            mArrayList = (List<T>) Arrays.asList(res);
//        }
        typedArrayListSelectorView.recycle();
    }

    public final void setList(@Nullable List<T> arrayList) {
        setClickable(true);
        mArrayList = arrayList;
        if (this.isShowChoiceAfterFilling) {
            this.showSpinnerListDialog();
            this.isShowChoiceAfterFilling = false;
        }
        restorePosition();
    }

    public final void setListWithAutoSelect(@Nullable List<T> arrayList) {
        mArrayList = arrayList;
        if (arrayList != null) {
            if (mArrayList.size() == 1) {
                this.setSelectionPosition(0);
            } else if (this.isShowChoiceAfterFilling) {
                this.showSpinnerListDialog();
                this.isShowChoiceAfterFilling = false;
            }
            restorePosition();
        }
    }

    public final void restorePosition() {
        if (mSelectedPosition != INVALID_POSITION) {
            setSelectionPosition(mSelectedPosition);
        }
    }

    public final void clear() {
        this.mArrayList = null;
        this.resetPosition();
    }

    public final void resetPosition() {
        setText("");
        this.mSelectedPosition = -1;
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

    public final void setSelectionPosition(int position) {
        this.mSelectedPosition = position;
        T item = this.getSelectedItem();
        if (item != null) {
            setText(item.toString());
        }
    }

    @Nullable
    public T getSelectedItem() {
        if (mArrayList != null && mArrayList.size() > mSelectedPosition && mSelectedPosition >= 0) {
            return mArrayList.get(mSelectedPosition);
        }
        return mDefaultItem;
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
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

    private void showSpinnerListDialog() {
        if (mArrayList != null) {
            ListDialog<T> dialog = new ListDialog<>(getContext(), mArrayList, new OnItemSelectedListener<T>() {
                @Override
                public void onItemSelected(@NonNull T item, @NonNull View view, int position) {
                    setSelectionPosition(position);
                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onItemSelected(item, ListSelectorView.this, position);
                    }

                }
            });
            dialog.show();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        SavedState ss = new SavedState();
        ss.stateToSave = mSelectedPosition;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(null);
            mSelectedPosition = ss.stateToSave;
        }
        super.onRestoreInstanceState(null);
    }

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
            height = Math.round(labelTextPaint.getTextSize() + labelTextPaint.getFontMetrics().bottom * getResources().getDisplayMetrics().density);// 12 TODO текс ровно посередине но не так как в TextView
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    static class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        int stateToSave;

        SavedState() {
        }

        private SavedState(Parcel in) {
            stateToSave = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(stateToSave);
        }
    }
}