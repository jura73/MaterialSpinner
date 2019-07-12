package com.github.jura73.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.jura73.materialspinner.ListMultiSelectorView;
import com.github.jura73.materialspinner.MaterialSpinner;
import com.github.jura73.materialspinner.OnItemSelectedListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MaterialSpinner<String> materialSpinner;
    MaterialSpinner<String> materialSpinner2;
    MaterialSpinner<String> materialSpinner3;
    ProgressBar progressBar;
    List<String> list_of_cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] stringArray = getResources().getStringArray(R.array.list_of_cities_in_Italy);
        list_of_cities = Arrays.asList(stringArray);

        final ListMultiSelectorView<String> listSelector = findViewById(R.id.listSelector);
        listSelector.setList(list_of_cities);

        materialSpinner = findViewById(R.id.materialSpinner1);
        progressBar = findViewById(R.id.progressSpiner2);
        materialSpinner.setLazyLoading(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSpinner.setList(list_of_cities);
                        progressBar.setVisibility(View.GONE);

                    }
                }, 1000);
            }
        });
        materialSpinner.setOnItemSelectedListener(new OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(@NonNull String item, @NonNull View view, int position) {
                materialSpinner.setEnabled(false);
            }
        });

        ImageView buttonClear = findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSpinner.setEnabled(true);
                materialSpinner.setSelectionItem(null);
            }
        });

        materialSpinner2 = findViewById(R.id.materialSpinner2);
        materialSpinner2.setList(list_of_cities);
        materialSpinner3 = findViewById(R.id.materialSpinner3);
        materialSpinner3.setList(list_of_cities);
        materialSpinner3.setSelectionItem("Milan");
    }
}