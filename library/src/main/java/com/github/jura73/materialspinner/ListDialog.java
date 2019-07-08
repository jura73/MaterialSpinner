package com.github.jura73.materialspinner;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class ListDialog<T> extends Dialog implements View.OnClickListener {
    private final List<T> mItemsList;

    private final OnItemSelectedListener<T> mSelectedListener;
    private ListDialogAdapter<T> mSuggestionsAdapter;

    public ListDialog(@NonNull Context context, List<T> list, final OnItemSelectedListener<T> selectedListener) {
        super(context, R.style.Dialog);
        mItemsList = list;
        mSelectedListener = selectedListener;
        setContentView(createView(context));
        ViewHelper.setupToolbar(this, mSuggestionsAdapter.getFilter());
    }

    private View createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //no root to pass here
        View view = inflater.inflate(R.layout.dialog_list, null);

        RecyclerView recyclerView = view.findViewById(R.id.rvAutocompleteSuggestions);
        mSuggestionsAdapter = new ListDialogAdapter<>(mItemsList, this);
        recyclerView.setAdapter(mSuggestionsAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewHelper.hideSoftInput(ListDialog.this);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        T itemSelected = (T) v.getTag();
        int selectedPosition = mItemsList.indexOf(itemSelected);
        mSelectedListener.onItemSelected(itemSelected, v, selectedPosition);
        dismiss();
    }
}