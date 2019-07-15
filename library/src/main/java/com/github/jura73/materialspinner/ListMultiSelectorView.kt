package com.github.jura73.materialspinner

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import java.util.*

class ListMultiSelectorView<T> : ListSelectorView<T> {
    private var mOnItemMultiSelectedListener: OnItemMultiSelectedListener<T>? = null
    private var selectedPositions: MutableSet<Int> = mutableSetOf()
    private var savedState: SavedState? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun showSpinnerListDialog() {
        itemList?.let { list ->
            ListMultiSelectorDialog(context, list, selectedPositions, object : OnListSelectedPositionsListener {
                override fun onItemsSelected(items: Collection<Int>) {
                    setSelectedPositions(items)
                    mOnItemMultiSelectedListener?.onItemsSelected(items.map { list[it] }, this@ListMultiSelectorView)

                }
            })
                    .show()
        }
    }

    override fun setSelectedPosition(positions: Int) {
        selectedPositions.clear()
        selectedPositions.add(positions)
        onSelectedChange()
    }

    override fun cleanSelected() {
        selectedPositions.clear()
        onSelectedChange()
    }

    private fun onSelectedChange() {
        itemList.let {
            if (selectedPositions.isNotEmpty() && it != null) {
                val sb = StringBuilder()
                var isNotFirst = false
                for (t in selectedPositions) {
                    if (isNotFirst) {
                        sb.append(", ")
                    }
                    sb.append(it[t].toString())
                    isNotFirst = true
                }
                setText(sb.toString())
            } else {
                setText(null)
            }
        }
    }

    fun setOnItemMultiSelectedListener(onItemMultiSelectedListener: OnItemMultiSelectedListener<T>) {
        mOnItemMultiSelectedListener = onItemMultiSelectedListener
    }

    fun setSelectedPositions(items: Collection<Int>) {
        selectedPositions = items.toMutableSet()
        onSelectedChange()
    }

    override fun restoreState() {
        if (savedState != null && savedState!!.positions != null && itemList != null) {
            val set = LinkedHashSet<T>()
            for (i in savedState!!.positions!!) {
                if (i >= itemList!!.size && i > 0) {
                    return  // List was change
                }
                set.add(itemList!![i])
            }
            setSelectedPositions(savedState!!.positions!!.asList())
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        if (selectedPositions.isNotEmpty()) {
            val savedState = SavedState(selectedPositions.toIntArray())
            super.onSaveInstanceState()
            return savedState
        } else {
            return super.onSaveInstanceState()
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(null)
            savedState = state
        }
        super.onRestoreInstanceState(null)
    }

    internal class SavedState : Parcelable {

        var positions: IntArray? = null

        constructor(positions: IntArray) {
            this.positions = positions
        }

        constructor(`in`: Parcel) {
            positions = `in`.createIntArray()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeIntArray(positions)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}