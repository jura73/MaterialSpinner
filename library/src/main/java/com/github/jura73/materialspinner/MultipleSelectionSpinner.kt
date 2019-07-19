package com.github.jura73.materialspinner

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet

class MultipleSelectionSpinner<T> : ListSelectorView<SelectableWrapper<T>> {
    private var mOnItemMultiSelectedListener: OnItemMultiSelectedListener<T>? = null
    private var savedState: SavedState? = null

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun showSpinnerListDialog() {
        itemList?.let { list ->
            ListMultiSelectorDialog<T>(context, list, object : OnListSelectedPositionsListener {
                override fun onItemsSelectedChanged() {
                    onSelectedChange()
                    mOnItemMultiSelectedListener?.onItemsSelected(itemList?.filter { it.isSelected }?.map { it.item }, this@MultipleSelectionSpinner)
                }
            }).show()
        }
    }

    fun setList(arrayList: Collection<T>) {
        setList(arrayList.map { SelectableWrapper(it) })
    }

    override fun setSelectedPosition(positions: Int) {
        itemList?.get(positions)?.isSelected = true
        onSelectedChange()
    }

    override fun cleanSelected() {
        itemList?.forEach { it.isSelected = false }
        onSelectedChange()
    }

    private fun onSelectedChange() {
        setText(itemList?.filter { it.isSelected }?.joinToString())
    }

    fun setOnItemMultiSelectedListener(onItemMultiSelectedListener: OnItemMultiSelectedListener<T>) {
        mOnItemMultiSelectedListener = onItemMultiSelectedListener
    }

    fun setSelectedPositions(items: Collection<Int>) {
        items.forEach { itemList?.get(it)?.isSelected = true }
        onSelectedChange()
    }

    override fun restoreState() {
        savedState?.positions?.let { savedPositions ->
            setSelectedPositions(savedPositions.asList())
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val selectedPositions = itemList?.filter { it.isSelected }?.mapIndexed { index, _ -> index }
        if (selectedPositions?.isNotEmpty() == true) {
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