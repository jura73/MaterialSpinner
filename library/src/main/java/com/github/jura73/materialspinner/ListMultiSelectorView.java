package com.github.jura73.materialspinner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class ListMultiSelectorView<T> extends ListSelectorView<T> {

    private OnItemMultiSelectedListener<T> mOnItemMultiSelectedListener;
    private LinkedHashSet<T> linkedHashSet;

    private List<Integer> mSelectedPositionList = new ArrayList<>();

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
                    linkedHashSet = items;
                    setSelectionListPosition(items);
                    if(mOnItemMultiSelectedListener!= null) {
                        mOnItemMultiSelectedListener.onItemsSelected(items, ListMultiSelectorView.this);
                    }
                }
            }).show();
        }
    }

    public void setSelectionListPosition(@NonNull Collection<T> items) {
        if (!items.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean isNotFirst = false;
            for(T t : items){
                if(isNotFirst){
                    sb.append(", ");
                }
                sb.append(t.toString());
                isNotFirst = true;
            }
            setText(sb.toString());
        } else {
            setText(null);
        }
        T item = this.getSelectedItem();
        if (item != null) {
            setText(item.toString());
        }
    }
}