package com.github.jura73.materialspinner;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class ListDialog<T> extends Dialog {
    private final List<T> mItemsList;

    private final MaterialSpinner.OnItemSelectedListener<T> mSelectedListener;
    private SearchView mSearchView;
    private ListDialogAdapter mSuggestionsAdapter;

    ListDialog(@NonNull Context context, List<T> list, final MaterialSpinner.OnItemSelectedListener<T> selectedListener) {
        super(context, R.style.Dialog);
        mItemsList = list;
        mSelectedListener = selectedListener;
        setContentView(createView(context));
    }

    private View createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //no root to pass here
        View view = inflater.inflate(R.layout.dialog_list, null);
        setupToolbar(view);
        RecyclerView rvAutocompleteSuggestions = view.findViewById(R.id.rvAutocompleteSuggestions);
        rvAutocompleteSuggestions.setLayoutManager(new LinearLayoutManager(context));
        mSuggestionsAdapter = new ListDialogAdapter(mItemsList);
        rvAutocompleteSuggestions.setAdapter(mSuggestionsAdapter);
        rvAutocompleteSuggestions.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchView.clearFocus();
                return false;
            }
        });
        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitle(R.string.action_search);
        toolbar.setNavigationIcon(getContext().getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSearchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search)
                .getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setQueryHint(getContext().getString(R.string.action_search));
        // expands SearchView
        mSearchView.setIconified(false);
        // not closes the SearchView
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
        // listening to search query text change
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSuggestionsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mSuggestionsAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    class ListDialogAdapter extends RecyclerView.Adapter<ListDialogAdapter.ViewHolderItem>
            implements Filterable {

        private List<T> mAdapterItems;
        Filter mFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<T> filteredList = new ArrayList<>();
                for (T initialListItem : mItemsList) {
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

        private ListDialogAdapter(List<T> adapterItems) {
            mAdapterItems = adapterItems;
        }

        @NonNull
        @Override
        public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ViewHolderItem(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderItem holder, int position) {
            holder.mTxtVwItemName.setText(mAdapterItems.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return mAdapterItems.size();
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        class ViewHolderItem extends RecyclerView.ViewHolder {

            private final TextView mTxtVwItemName;

            ViewHolderItem(View itemView) {
                super(itemView);
                mTxtVwItemName = (TextView) itemView;


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        T itemSelected = mAdapterItems.get(getAdapterPosition());
                        int selectedPosition = mItemsList.indexOf(itemSelected);
                        mSelectedListener.onItemSelected(itemSelected, v, selectedPosition);
                        ListDialog.this.dismiss();
                    }
                });
            }
        }
    }
}