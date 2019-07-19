package com.github.jura73.materialspinner

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.inputmethod.InputMethodManager
import android.widget.Filter

internal object ViewHelper {
    fun setupToolbar(dialog: Dialog, filter: Filter) {
        val toolbar = dialog.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setNavigationOnClickListener { dialog.onBackPressed() }

        val searchView = toolbar.menu.findItem(R.id.action_search).actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        // expands SearchView
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = dialog.context.getString(R.string.action_search)
        // not closes the SearchView
        searchView.setOnCloseListener { true }
        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                filter.filter(query)
                return false
            }
        })
    }

    fun hideSoftInput(dialog: Dialog) {
        val view = dialog.currentFocus
        if (view != null) {
            val imm = dialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}