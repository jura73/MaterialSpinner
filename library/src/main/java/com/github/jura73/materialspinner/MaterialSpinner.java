package com.github.jura73.materialspinner;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public final class MaterialSpinner<T> extends ListSelectorView<T> {
    public static final int INVALID_POSITION = -1;
    private int mRestorePosition = INVALID_POSITION;
    @Nullable
    private OnItemSelectedListener<T> mOnItemSelectedListener;
    @Nullable
    private T selectedItem = null;

    public MaterialSpinner(Context context) {
        super(context);
    }

    public MaterialSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    @Override
    protected void showSpinnerListDialog() {
        if (mArrayList != null) {
            ListDialog<T> dialog = new ListDialog<>(getContext(), mArrayList, new OnItemSelectedListener<T>() {
                @Override
                public void onItemSelected(@NonNull T item, @NonNull View view, int position) {
                    setSelectionItem(item);
                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onItemSelected(item, MaterialSpinner.this, position);
                    }

                }
            });
            dialog.show();
        }
    }

    public void setSelectionItem(T item) {
        selectedItem = item;
        if (item != null) {
            setText(item.toString());
        } else {
            setText(null);
        }
    }

    @Nullable
    public T getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void cleanSelected() {
        super.cleanSelected();
        selectedItem = null;
    }

    protected void restoreState() {
        if (mRestorePosition != INVALID_POSITION && mArrayList != null && mArrayList.size() > mRestorePosition) {
            setSelectionItem(mArrayList.get(mRestorePosition));
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        SavedState ss = new SavedState();
        if (selectedItem != null && mArrayList != null) {
            ss.stateToSave = mArrayList.indexOf(selectedItem);
        } else {
            ss.stateToSave = INVALID_POSITION;
        }

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(null);
            mRestorePosition = ss.stateToSave;
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