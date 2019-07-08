package com.github.jura73.materialspinner;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

class SelectableListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable, View.OnClickListener {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
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

    SelectableListAdapter(@NonNull List<T> adapterItems, @NonNull LinkedHashSet<T> selectedItems, @NonNull View.OnClickListener onClickListener) {
        sourceItems = adapterItems;
        mAdapterItems = adapterItems;
        this.onClickListener = onClickListener;
        linkedHashSet = selectedItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selectable_header_list, parent, false);
            itemView.setOnClickListener(this);
            return new ViewHolderHeader(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selectable_item_list, parent, false);
            itemView.setOnClickListener(this);
            return new ViewHolderItem(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ((ViewHolderHeader) viewHolder).onBind(linkedHashSet.containsAll(sourceItems));
        } else {
            T item = mAdapterItems.get(position - 1);
            ((ViewHolderItem) viewHolder).onBind(item, linkedHashSet.contains(item));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mAdapterItems.size() + 1;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public void onClick(View v) {
        if(v instanceof CompoundButton){
            if(v.getId() == R.id.checkboxHeader) {
                CompoundButton compoundButton = (CompoundButton) v;
                if (compoundButton.isChecked()) {
                    linkedHashSet.addAll(sourceItems);
                } else {
                    linkedHashSet.clear();
                }
                compoundButton.setChecked(!compoundButton.isChecked());
                notifyDataSetChanged();
            }
            else {
                onClickListener.onClick(v);
                notifyItemChanged(0);
            }
        }
    }

    static class ViewHolderHeader extends RecyclerView.ViewHolder {

        private final AppCompatCheckBox checkBox;

        ViewHolderHeader(View itemView) {
            super(itemView);
            checkBox = (AppCompatCheckBox) itemView;
        }

        void onBind(boolean isSelected) {
            checkBox.setChecked(isSelected);
        }
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