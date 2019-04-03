MaterialSpinner
====================

### Recent changes

```
MaterialSpinner<String> materialSpinner;

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

materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(@NonNull String item, @NonNull View view, int position) {
                materialSpinner.setEnabled(false);
            }
        });
```

![Sample1](https://github.com/jura73/MaterialSpinner/blob/master/img/Sample1.png)
![Sample2](https://github.com/jura73/MaterialSpinner/blob/master/img/Sample2.png)


### Integration

**1)** Add library as a dependency to your project 

```implementation 'com.github.jura73:MaterialSpinner:0.9.1'```
