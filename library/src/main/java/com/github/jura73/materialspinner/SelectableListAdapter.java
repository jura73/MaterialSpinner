package com.github.jura73.materialspinner;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

class SelectableListAdapter<T> extends RecyclerView.Adapter<SelectableListAdapter.ViewHolderItem<T>>
        implements Filterable {
    private View.OnClickListener onClickListener;
    private List<T> mAdapterItems;
    private List<T> sourceItems;
    private final LinkedHashSet<T> linkedHashSet;
    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<T> filteredList = new ArrayList<>();
            for (T initialListItem : sourceItems) {
                if (initialListItem.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredList.add(initialListItem);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAdapterItems = (List<T>) results.values;
            notifyDataSetChanged();
        }
    };

    SelectableListAdapter(List<T> adapterItems, LinkedHashSet<T> selectedItems, View.OnClickListener onClickListener) {
        sourceItems = adapterItems;
        mAdapterItems = adapterItems;
        this.onClickListener = onClickListener;
        linkedHashSet = selectedItems;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selectable_item_list, parent, false);
        itemView.setOnClickListener(onClickListener);
        return new ViewHolderItem(itemView);
    }


    public void onBindViewHolder(@NonNull ViewHolderItem<T> viewHolderItem, int position) {
        T item = mAdapterItems.get(position);
        viewHolderItem.onBind(item, linkedHashSet.contains(item));
    }

    @Override
    public int getItemCount() {
        return mAdapterItems.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    static class ViewHolderItem<T> extends RecyclerView.ViewHolder {

        private final AppCompatCheckBox checkBox;

        ViewHolderItem(View itemView) {
            super(itemView);
            checkBox = (AppCompatCheckBox) itemView;
        }

        void onBind(T item, boolean isSelected) {
            checkBox.setTag(item);
            checkBox.setText(item.toString());
            checkBox.setChecked(isSelected);
        }
    }
}