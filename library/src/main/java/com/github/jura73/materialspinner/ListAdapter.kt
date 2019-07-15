package com.github.jura73.materialspinner

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

import java.util.ArrayList

internal class ListAdapter<T>(private val sourceItems: List<T>, private val onClickListener: View.OnClickListener) : RecyclerView.Adapter<ListAdapter.ViewHolderItem<T>>(), Filterable {
    private var mAdapterItems: List<T>? = null
    private val mFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
            val filteredList = ArrayList<T>()
            for (initialListItem in sourceItems) {
                if (initialListItem.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredList.add(initialListItem)
                }
            }

            val filterResults = Filter.FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            mAdapterItems = results.values as List<T>
            notifyDataSetChanged()
        }
    }

    init {
        mAdapterItems = sourceItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem<T> {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list, parent, false)
        itemView.setOnClickListener(onClickListener)
        return ViewHolderItem(itemView)
    }


    override fun onBindViewHolder(viewHolderItem: ViewHolderItem<T>, position: Int) {
        viewHolderItem.onBind(mAdapterItems!![position])
    }

    override fun getItemCount(): Int {
        return mAdapterItems!!.size
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    internal class ViewHolderItem<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mTxtVwItemName: TextView

        init {
            mTxtVwItemName = itemView as TextView
        }

        fun onBind(item: T) {
            mTxtVwItemName.tag = item
            mTxtVwItemName.text = item.toString()
        }
    }
}