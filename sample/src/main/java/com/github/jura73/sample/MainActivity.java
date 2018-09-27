package com.github.jura73.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.github.jura73.materialspinner.MaterialSpinner;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MaterialSpinner<String> materialSpinner1;
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

        progressBar = findViewById(R.id.progressSpiner2);

        materialSpinner1 = findViewById(R.id.materialSpinner1);
        materialSpinner2 = findViewById(R.id.materialSpinner2);
        materialSpinner2.setDefaultItem("Milan");

        materialSpinner2.setLazyLoading(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSpinner2.setList(list_of_cities);
                        progressBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });

        Button buttonClear = findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSpinner2.clear();
            }
        });

        materialSpinner3 = findViewById(R.id.materialSpinner3);
        materialSpinner3.setList(list_of_cities);
    }
}