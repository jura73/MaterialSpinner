package com.github.jura73.materialspinner

fun List<SelectableWrapper<*>>.setSelectByPositions(listSelectedPositions: MutableSet<Int>) {
    listSelectedPositions.forEach { this[it].isSelected = true }
}

class SelectableWrapper<T>(val item: T, var isSelected: Boolean = false) {
    override fun toString(): String {
        return item.toString()
    }
}