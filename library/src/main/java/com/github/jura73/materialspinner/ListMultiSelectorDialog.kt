package com.github.jura73.materialspinner

import android.app.Dialog
import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View

class ListMultiSelectorDialog<T>(context: Context, private val mItemList: List<T>, private val listSelectedPositions: MutableSet<Int>, private val onItemMultiSelectedListener: OnListSelectedPositionsListener) :
        Dialog(context, R.style.Dialog), View.OnClickListener {

    init {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_selectable_list, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAutocompleteSuggestions)
        val floatingDone = view.findViewById<FloatingActionButton>(R.id.floatingDone)
        floatingDone.setOnClickListener(this)
        recyclerView.addItemDecoration(DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL))
        val adapter = SelectableListAdapter(mItemList, listSelectedPositions)
        recyclerView.adapter = adapter
        recyclerView.setOnTouchListener { _, _ ->
            ViewHelper.hideSoftInput(this@ListMultiSelectorDialog)
            false
        }
        setContentView(view)
        ViewHelper.setupToolbar(this, adapter.filter)
    }


    override fun onBackPressed() {
        onItemMultiSelectedListener.onItemsSelected(listSelectedPositions.toList())
        super.onBackPressed()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.floatingDone) {
            onBackPressed()
        }
    }
}