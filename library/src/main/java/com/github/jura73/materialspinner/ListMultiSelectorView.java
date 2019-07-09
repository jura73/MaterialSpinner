package com.github.jura73.materialspinner;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedHashSet;

public class ListMultiSelectorView<T> extends ListSelectorView<T> {

    @Nullable
    private OnItemMultiSelectedListener<T> mOnItemMultiSelectedListener;
    @Nullable
    private LinkedHashSet<T> linkedHashSet;
    SavedState savedState = null;

    public ListMultiSelectorView(Context context) {
        super(context);
    }

    public ListMultiSelectorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ListMultiSelectorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void showSpinnerListDialog() {
        if (mArrayList != null) {
            new ListMultiSelectorDialog<>(getContext(), mArrayList, linkedHashSet, new OnItemMultiSelectedListener<T>() {
                @Override
                public void onItemsSelected(@NonNull LinkedHashSet<T> items, @NonNull View view) {
                    setSelectionList(items);
                    if (mOnItemMultiSelectedListener != null) {
                        mOnItemMultiSelectedListener.onItemsSelected(items, ListMultiSelectorView.this);
                    }
                }
            }).show();
        }
    }

    @Override
    public void setSelectionItem(T item) {
        LinkedHashSet<T> set = new LinkedHashSet<>();
        set.add(item);
        setSelectionList(set);
    }

    public final void setOnItemMultiSelectedListener(OnItemMultiSelectedListener<T> onItemMultiSelectedListener) {
        mOnItemMultiSelectedListener = onItemMultiSelectedListener;
    }

    public void setSelectionList(@NonNull LinkedHashSet<T> items) {
        linkedHashSet = items;
        if (!items.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean isNotFirst = false;
            for (T t : items) {
                if (isNotFirst) {
                    sb.append(", ");
                }
                sb.append(t.toString());
                isNotFirst = true;
            }
            setText(sb.toString());
        } else {
            if (mDefaultItem != null) {
                setText(mDefaultItem.toString());
            } else {
                setText(null);
            }
        }
    }

    protected void restoreState() {
        if (savedState != null && savedState.positions != null) {
            LinkedHashSet<T> set = new LinkedHashSet<>();
            for (int i : savedState.positions) {
                set.add(mArrayList.get(i));
            }
            setSelectionList(set);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        SavedState ss = new SavedState();
        if (linkedHashSet != null && mArrayList != null) {
            int[] positions = new int[linkedHashSet.size()];
            int i = 0;
            for (T t : linkedHashSet) {
                positions[i] = mArrayList.indexOf(t);
                i++;
            }
            ss.positions = positions;
        } else {
            ss.positions = null;
        }

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(null);
            savedState = ss;
        }
        super.onRestoreInstanceState(null);
    }

    static class SavedState implements Parcelable {

        private int[] positions;

        SavedState() {
        }

        protected SavedState(Parcel in) {
            positions = in.createIntArray();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeIntArray(positions);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}