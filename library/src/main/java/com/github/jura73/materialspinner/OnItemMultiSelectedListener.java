package com.github.jura73.materialspinner;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.LinkedHashSet;

public interface OnItemMultiSelectedListener<T> {
    void onItemsSelected(@NonNull LinkedHashSet<T> items, @NonNull View view);
}