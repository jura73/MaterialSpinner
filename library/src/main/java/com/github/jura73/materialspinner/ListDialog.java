package com.github.jura73.materialspinner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListDialog<T> extends AlertDialog {
    private final List<T> mItemsList;

    private final MaterialSpinner.OnItemSelectedListener<T> mSelectedListener;
    private AutocompleteSuggestionsAdapter mSuggestionsAdapter;

    public ListDialog(@NonNull Context context, List<T> list, final MaterialSpinner.OnItemSelectedListener<T> selectedListener) {
        super(context);
        mItemsList = list;
        mSelectedListener = selectedListener;
        setView(createView(context));
    }

    private View createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //no root to pass here
        View view = inflater.inflate(R.layout.dialog_list, null);
        RecyclerView rvAutocompleteSuggestions = view.findViewById(R.id.rvAutocompleteSuggestions);
        EditText edtTxtFilter = view.findViewById(R.id.etFilter);
        rvAutocompleteSuggestions.setLayoutManager(new LinearLayoutManager(context));
        mSuggestionsAdapter = new AutocompleteSuggestionsAdapter(mItemsList);
        rvAutocompleteSuggestions.setAdapter(mSuggestionsAdapter);
        edtTxtFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                List<T> filteredList = getFilteredList(mItemsList, charSequence.toString());
                mSuggestionsAdapter.assignNewDataSource(filteredList);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return view;
    }

    private List<T> getFilteredList(List<T> initialList, String filterQuery) {
        ArrayList<T> filteredList = new ArrayList<>();
        for (T initialListItem : initialList) {
            if (initialListItem.toString().toLowerCase().contains(filterQuery.toLowerCase())) {
                filteredList.add(initialListItem);
            }
        }
        return filteredList;
    }

    class AutocompleteSuggestionsAdapter extends RecyclerView.Adapter<ViewHolderSuggestion>
            implements View.OnClickListener {

        private List<T> mAdapterItems;

        private AutocompleteSuggestionsAdapter(List<T> adapterItems) {
            mAdapterItems = adapterItems;
        }

        private void assignNewDataSource(List<T> newItemsList) {
            mAdapterItems = newItemsList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolderSuggestion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new ViewHolderSuggestion(
                    layoutInflater.inflate(R.layout.item_autocomplete_list_activity_suggestion, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderSuggestion holder, int position) {
            holder.mTxtVwItemName.setText(mAdapterItems.get(position).toString());
            holder.mTxtVwItemName.setTag(holder);
            holder.mTxtVwItemName.setOnClickListener(this);

        }

        @Override
        public int getItemCount() {
            return mAdapterItems.size();
        }

        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
            int selectedAdapterPosition = mAdapterItems.indexOf(mAdapterItems.get(holder.getAdapterPosition()));
            mSelectedListener.onItemSelected(mAdapterItems.get(holder.getAdapterPosition()), view, selectedAdapterPosition);
            ListDialog.this.dismiss();
        }
    }

    class ViewHolderSuggestion extends RecyclerView.ViewHolder {

        private final TextView mTxtVwItemName;

        ViewHolderSuggestion(View itemView) {
            super(itemView);
            mTxtVwItemName = (TextView) itemView;
        }
    }
}