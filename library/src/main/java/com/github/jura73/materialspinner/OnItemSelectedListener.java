package com.github.jura73.materialspinner;

import android.support.annotation.NonNull;
import android.view.View;

public interface OnItemSelectedListener<T> {
    void onItemSelected(@NonNull T item, @NonNull View view, int position);
}