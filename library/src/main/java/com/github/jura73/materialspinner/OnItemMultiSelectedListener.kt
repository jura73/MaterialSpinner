package com.github.jura73.materialspinner

import android.view.View

import java.util.LinkedHashSet

interface OnItemMultiSelectedListener<T> {
    fun onItemsSelected(items: LinkedHashSet<T>, view: View?)
}