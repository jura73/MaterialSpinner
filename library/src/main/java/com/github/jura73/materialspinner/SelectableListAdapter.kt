package com.github.jura73.materialspinner

import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Filter
import android.widget.Filterable
import java.util.*

internal class SelectableListAdapter(private val sourceItems: List<SelectableWrapper<*>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable, View.OnClickListener {
    private var mAdapterItems: List<SelectableWrapper<*>> = sourceItems
    private val mFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = ArrayList<SelectableWrapper<*>>()
            for (initialListItem in sourceItems) {
                if (initialListItem.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredList.add(initialListItem)
                }
            }

            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            mAdapterItems = results.values as List<SelectableWrapper<*>>
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_HEADER) {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.selectable_header_list, parent, false)
            itemView.setOnClickListener(this)
            return ViewHolderHeader(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.selectable_item_list, parent, false)
            itemView.setOnClickListener(this)
            return ViewHolderItem(itemView)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            (viewHolder as ViewHolderHeader).onBind(sourceItems.all { it.isSelected })
        } else {
            val positionInList = position - 1
            val item = mAdapterItems[positionInList]
            (viewHolder as ViewHolderItem).onBind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return mAdapterItems.size + 1
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    override fun onClick(v: View) {
        if (v is CompoundButton) {
            if (v.getId() == R.id.checkboxHeader) {
                sourceItems.forEach { it.isSelected = v.isChecked }
                notifyDataSetChanged()
            } else {
                val selectedPosition = v.getTag() as SelectableWrapper<*>
                selectedPosition.isSelected = v.isChecked
                notifyItemChanged(0)
            }
        }
    }

    internal class ViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val checkBox: AppCompatCheckBox = itemView as AppCompatCheckBox

        fun onBind(isSelected: Boolean) {
            checkBox.isChecked = isSelected
        }
    }

    internal class ViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val checkBox: AppCompatCheckBox = itemView as AppCompatCheckBox

        fun onBind(item: SelectableWrapper<*>) {
            checkBox.tag = item
            checkBox.text = item.toString()
            checkBox.isChecked = item.isSelected
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}