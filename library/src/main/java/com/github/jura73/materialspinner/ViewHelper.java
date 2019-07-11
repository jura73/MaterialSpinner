package com.github.jura73.materialspinner;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;

class ViewHelper {
    static void setupToolbar(final Dialog dialog, final Filter filter) {
        Toolbar toolbar = dialog.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitle(R.string.action_search);
        toolbar.setNavigationIcon(dialog.getContext().getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.onBackPressed();
            }
        });

        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search)
                .getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(dialog.getContext().getString(R.string.action_search));
        // expands SearchView
        searchView.setIconified(false);
        searchView.clearFocus();
        // not closes the SearchView
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filter.filter(query);
                return false;
            }
        });
    }

    static void hideSoftInput(final Dialog dialog){
        View view = dialog.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}