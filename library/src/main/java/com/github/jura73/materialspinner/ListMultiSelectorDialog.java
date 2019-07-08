package com.github.jura73.materialspinner;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedHashSet;
import java.util.List;

public class ListMultiSelectorDialog<T> extends Dialog implements View.OnClickListener {
    private final List<T> mItemList;

    private final LinkedHashSet<T> linkedHashSet;

    private final OnItemMultiSelectedListener<T> onItemMultiSelectedListener;
    private SelectableListAdapter<T> adapter;

    public ListMultiSelectorDialog(@NonNull Context context, List<T> list, LinkedHashSet<T> selectedItems, final OnItemMultiSelectedListener<T> selectedListener) {
        super(context, R.style.Dialog);
        mItemList = list;
        if(selectedItems != null) {
            this.linkedHashSet = selectedItems;
        }
        else {
            linkedHashSet = new LinkedHashSet<>();
        }
        onItemMultiSelectedListener = selectedListener;
        setContentView(createView(context));
        ViewHelper.setupToolbar(this, adapter.getFilter());
    }

    private View createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_list, null);

        RecyclerView recyclerView = view.findViewById(R.id.rvAutocompleteSuggestions);
        adapter = new SelectableListAdapter<>(mItemList, linkedHashSet, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewHelper.hideSoftInput(ListMultiSelectorDialog.this);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onBackPressed() {
        onItemMultiSelectedListener.onItemsSelected(linkedHashSet, null);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        T itemSelected = (T) v.getTag();
        if (linkedHashSet.contains(itemSelected)) {
            linkedHashSet.remove(itemSelected);
        } else {
            linkedHashSet.add(itemSelected);
        }
    }
}