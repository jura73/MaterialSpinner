package com.github.jura73.materialspinner

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View

class MaterialSpinner<T> : ListSelectorView<T> {
    private var mRestorePosition = INVALID_POSITION
    private var mOnItemSelectedListener: OnItemSelectedListener<T>? = null
    var selectedItem: T? = null
        private set

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener<T>) {
        mOnItemSelectedListener = onItemSelectedListener
    }

    override fun showSpinnerListDialog() {
        itemList?.let {
            ListDialog(context, it, object : OnItemSelectedListener<T> {
                override fun onItemSelected(item: T, view: View, position: Int) {
                    setSelectionItem(item)
                    mOnItemSelectedListener?.onItemSelected(item, this@MaterialSpinner, position)
                }
            }).show()
        }
    }

    override fun setSelectedPosition(positions: Int) {
        itemList.let {
            if (it != null && positions >= 0) {
                setSelectionItem(it[positions])
            } else {
                setSelectionItem(null)
            }
        }
    }

    fun setSelectionItem(item: T?) {
        selectedItem = item
        setText(item?.toString())
    }

    override fun cleanSelected() {
        setSelectionItem(null)
    }

    override fun restoreState() {
        itemList?.let {
            if (mRestorePosition != INVALID_POSITION && it.size > mRestorePosition) {
                setSelectionItem(it[mRestorePosition])
            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        super.onSaveInstanceState()
        val ss = SavedState()
        selectedItem.let {
            if (it != null) {
                ss.stateToSave = itemList?.indexOf(it) ?: INVALID_POSITION
            } else {
                ss.stateToSave = INVALID_POSITION
            }
        }
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(null)
            mRestorePosition = state.stateToSave
        }
        super.onRestoreInstanceState(null)
    }

    internal class SavedState : Parcelable {
        var stateToSave: Int = 0

        constructor() {}

        private constructor(`in`: Parcel) {
            stateToSave = `in`.readInt()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            out.writeInt(stateToSave)
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

    companion object {
        const val INVALID_POSITION = -1
    }
}