package com.github.jura73.materialspinner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public final class ListSelectorWidget<T> extends View {
    public static final int INVALID_POSITION = -1;
    public static final int ALPHA = 85;

    private boolean isShowChoiceAfterFilling;
    private List<T> mArrayList;
    private T mDefaultItem;
    private OnClickListener mOnLazyLoading;
    private OnItemSelectedListener<T> mOnItemSelectedListener;
    private int mSelectedPosition = INVALID_POSITION;

    public ListSelectorWidget(Context context) {
        this(context, null);
    }

    int spase = 10;
    TextPaint textPaint;
    String hint = "cities in Italy";
    String valueText = "";

    public ListSelectorWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        //  this(context, attrs, R.attr.listSelectorWidgetStyle);//R.attr.listSelectorStyle
        this(context, attrs, R.style.ListSelectorWidget);//R.attr.listSelectorStyle
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        float scaledSizeInPixels = getResources().getDimensionPixelSize(R.dimen.fontSize);
        textPaint.setTextSize(scaledSizeInPixels);
    }

    public void setText(String text) {
        valueText = text;
        invalidate();
    }

    //
    //   @Override

    @Override
    protected void onDraw(Canvas canvas) {
        int yPos = -Math.round(textPaint.getFontMetrics().top);

        // Draw Label
        String textLabel = hint;
        // ширина текста

        int widthLabelText = Math.round(textPaint.measureText(textLabel));
        int paddingLeft = getPaddingLeft();
        canvas.drawText(textLabel, paddingLeft, yPos, textPaint);
        //
        // Draw Value
        String textValue = valueText;
        int widthValueText = Math.round(textPaint.measureText(textValue));
        int compoundPaddingLeftValueText = paddingLeft + widthLabelText + spase;
        int maxWidthValueText = getWidth() - compoundPaddingLeftValueText;
        if (widthValueText > maxWidthValueText) {
            CharSequence ellipsizeValueText = TextUtils.ellipsize(textValue, textPaint, maxWidthValueText, TextUtils.TruncateAt.END);
            canvas.drawText(ellipsizeValueText.toString(), compoundPaddingLeftValueText, yPos, textPaint);
        } else {
            int xPos = getWidth() - widthValueText;
            canvas.drawText(textValue, xPos, yPos, textPaint);
        }
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
            height = Math.round(textPaint.getTextSize() + textPaint.getFontMetrics().bottom * getResources().getDisplayMetrics().density);// 12 TODO текс ровно посередине но не так как в TextView
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }


    public ListSelectorWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        post(new Runnable() {
            @Override
            public void run() {
                restorePosition();
            }
        });
        //   TypedArray typedArrayMaterialSpinner = context.obtainStyledAttributes(attrs, R.styleable.MaterialSpinner);
        //  Drawable[] drawables = getCompoundDrawables();
        // if (drawables[2] == null) {
        //      Drawable arrowDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_right, null);
        // setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], arrowDrawable, drawables[3]);
        //  }


//
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
//        typedArraySpinner.recycle();
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
                        mOnItemSelectedListener.onItemSelected(item, ListSelectorWidget.this, position);
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