package com.github.jura73.materialspinner

import android.view.View

interface OnItemSelectedListener<T> {
    fun onItemSelected(item: T, view: View, position: Int)
}