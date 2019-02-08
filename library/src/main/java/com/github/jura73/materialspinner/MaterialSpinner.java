package com.github.jura73.materialspinner;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

public final class MaterialSpinner<T> extends TextInputLayout implements OnClickListener {
    public static final int INVALID_POSITION = -1;
    public static final int ALPHA = 85;
    private final EditText mEditText;
    private final ColorStateList mColorsTint;
    private boolean isShowChoiceAfterFilling;
    private List<T> mArrayList;
    private T mDefaultItem;
    private OnClickListener mOnLazyLoading;
    private OnItemSelectedListener<T> mOnItemSelectedListener;
    private int mSelectedPosition = INVALID_POSITION;

    public MaterialSpinner(Context context) {
        this(context, null);
    }

    public MaterialSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.material_loading, this);
        mEditText = findViewById(R.id.edtTxtSpinner);
        mEditText.setOnClickListener((new OnClickListener() {
            public final void onClick(View v) {
                MaterialSpinner.this.onClick(v);
            }
        }));

        post(new Runnable() {
            @Override
            public void run() {
                restorePosition();
            }
        });
        TypedArray typedArrayMaterialSpinner = context.obtainStyledAttributes(attrs, R.styleable.MaterialSpinner);

        ColorStateList colors = typedArrayMaterialSpinner.getColorStateList(R.styleable.MaterialSpinner_android_textColor);
        if (colors != null) {
            mEditText.setTextColor(colors);
        }

        mColorsTint = typedArrayMaterialSpinner.getColorStateList(R.styleable.MaterialSpinner_colorAccent);

        setEnabled(typedArrayMaterialSpinner.getBoolean(R.styleable.MaterialSpinner_android_enabled, true));
        typedArrayMaterialSpinner.recycle();

        TypedArray typedArraySpinner = getContext().obtainStyledAttributes(attrs, android.support.design.R.styleable.Spinner);
        int entriesResourceId = typedArraySpinner.getResourceId(0, -1);
        if (entriesResourceId > 0) {
            String[] res = getContext().getResources().getStringArray(entriesResourceId);
            mArrayList = (List<T>) Arrays.asList(res);
        }
        typedArraySpinner.recycle();
    }

    @Override
    public void setEnabled(boolean enabled) {
        setTintColor(enabled);
        super.setEnabled(enabled);
    }

    private void setTintColor(boolean enabled) {
        if (mColorsTint != null) {
            if (enabled) {
                ViewCompat.setBackgroundTintList(mEditText, mColorsTint);
            } else {
                ViewCompat.setBackgroundTintList(mEditText, mColorsTint.withAlpha(ALPHA));
            }
        }
    }

    public final void setList(@Nullable List<T> arrayList) {
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
        this.mEditText.setText("");
        this.mSelectedPosition = -1;
    }

    public final void setDefaultItem(@Nullable T item) {
        mDefaultItem = item;
        if (item != null) {
            mEditText.setText(String.valueOf(item));
        } else {
            mEditText.setText("");
        }
    }

    public final void setLazyLoading(@Nullable OnClickListener onClickListener) {
        this.mOnLazyLoading = onClickListener;
    }

    public final void setSelectionPosition(int position) {
        this.mSelectedPosition = position;
        T item = this.getSelectedItem();
        if (item != null) {
            mEditText.setText(item.toString());
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

    public void onClick(@NonNull View v) {
        if (this.mArrayList != null) {
            this.showSpinnerListDialog();
        } else {
            OnClickListener lazyLoading = this.mOnLazyLoading;
            if (lazyLoading != null) {
                this.isShowChoiceAfterFilling = true;
                lazyLoading.onClick(v);
            }
        }
    }

    private void showSpinnerListDialog() {
        if (mArrayList != null) {
            ListDialog<T> dialog = new ListDialog<>(getContext(), mArrayList, new OnItemSelectedListener<T>() {
                @Override
                public void onItemSelected(@NonNull T item, @NonNull View view, int position) {
                    MaterialSpinner.this.setSelectionPosition(position);
                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onItemSelected(item, MaterialSpinner.this, position);
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
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(null);
            mSelectedPosition = ss.stateToSave;
        }
        super.onRestoreInstanceState(null);
    }

    public interface OnItemSelectedListener<T> {
        void onItemSelected(@NonNull T item, @NonNull View view, int position);
    }

    static class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
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