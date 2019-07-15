package com.github.jura73.materialspinner

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View

import java.util.LinkedHashSet

class ListMultiSelectorView<T> : ListSelectorView<T> {

    private var mOnItemMultiSelectedListener: OnItemMultiSelectedListener<T>? = null
    private var linkedHashSet: LinkedHashSet<T>? = null
    private var savedState: SavedState? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun showSpinnerListDialog() {
        itemList?.let {
            ListMultiSelectorDialog(context, it, linkedHashSet, object : OnItemMultiSelectedListener<T> {
                override fun onItemsSelected(items: LinkedHashSet<T>, view: View?) {
                    setSelectionList(items)
                    if (mOnItemMultiSelectedListener != null) {
                        mOnItemMultiSelectedListener!!.onItemsSelected(items, this@ListMultiSelectorView)
                    }
                }
            }).show()
        }
    }

    override fun setSelectionItem(item: T?) {
        val set = LinkedHashSet<T>()
        set.add(item!!)
        setSelectionList(set)
    }

    fun setOnItemMultiSelectedListener(onItemMultiSelectedListener: OnItemMultiSelectedListener<T>) {
        mOnItemMultiSelectedListener = onItemMultiSelectedListener
    }

    fun setSelectionList(items: LinkedHashSet<T>) {
        linkedHashSet = items
        if (!items.isEmpty()) {
            val sb = StringBuilder()
            var isNotFirst = false
            for (t in items) {
                if (isNotFirst) {
                    sb.append(", ")
                }
                sb.append(t.toString())
                isNotFirst = true
            }
            setText(sb.toString())
        } else {
            setText(null)
        }
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
            setSelectionList(set)
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        if (linkedHashSet != null && itemList != null) {
            val positions = IntArray(linkedHashSet!!.size)
            var i = 0
            for (t in linkedHashSet!!) {
                positions[i] = itemList!!.indexOf(t)
                i++
            }
            val savedState = SavedState(positions)
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