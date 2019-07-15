package com.github.jura73.materialspinner

import android.app.Dialog
import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import java.util.*

class ListMultiSelectorDialog<T>(context: Context, private val mItemList: List<T>, selectedItems: LinkedHashSet<T>?, private val onItemMultiSelectedListener: OnItemMultiSelectedListener<T>) :
        Dialog(context, R.style.Dialog), View.OnClickListener {

    private val linkedHashSet: LinkedHashSet<T>
    private var adapter: SelectableListAdapter<T>? = null

    init {
        if (selectedItems != null) {
            this.linkedHashSet = selectedItems
        } else {
            linkedHashSet = LinkedHashSet()
        }
        setContentView(createView(context))
        ViewHelper.setupToolbar(this, adapter!!.filter)
    }

    private fun createView(context: Context): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_selectable_list, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAutocompleteSuggestions)
        val floatingDone = view.findViewById<FloatingActionButton>(R.id.floatingDone)
        floatingDone.setOnClickListener(this)
        recyclerView.addItemDecoration(DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL))
        adapter = SelectableListAdapter(mItemList, linkedHashSet)
        recyclerView.adapter = adapter
        recyclerView.setOnTouchListener { _, _ ->
            ViewHelper.hideSoftInput(this@ListMultiSelectorDialog)
            false
        }
        return view
    }

    override fun onBackPressed() {
        onItemMultiSelectedListener.onItemsSelected(linkedHashSet, null)
        super.onBackPressed()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.floatingDone) {
            onBackPressed()
        }
    }
}