package com.github.jura73.materialspinner

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View

class ListDialog<T>(context: Context, private val mItemList: List<T>, private val mSelectedListener: OnItemSelectedListener<T>) : Dialog(context, R.style.Dialog), View.OnClickListener {

    init {
        val inflater = LayoutInflater.from(context)
        //no root to pass here
        val view = inflater.inflate(R.layout.dialog_list, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAutocompleteSuggestions)
        recyclerView.addItemDecoration(DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL))
        val adapter = ListAdapter(mItemList, this)
        recyclerView.adapter = adapter
        recyclerView.setOnTouchListener { v, event ->
            ViewHelper.hideSoftInput(this@ListDialog)
            false
        }
        setContentView(view)
        ViewHelper.setupToolbar(this, adapter.filter)
    }

    override fun onClick(v: View) {
        val itemSelected = v.tag as T
        val selectedPosition = mItemList.indexOf(itemSelected)
        mSelectedListener.onItemSelected(itemSelected, v, selectedPosition)
        dismiss()
    }
}