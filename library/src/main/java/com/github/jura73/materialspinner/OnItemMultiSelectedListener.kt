package com.github.jura73.materialspinner

import android.view.View

interface OnItemMultiSelectedListener<T> {
    fun onItemsSelected(items: Collection<T>, view: View?)
}