package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoader.OnDataLoadedListener {

    private static final String TAG = "MainActivity";
    private static final String ECB_URL =
            "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private EditText edtFilter;
    private ListView listViewRates;

    private ArrayAdapter<String> adapter;
    private final List<String> displayItems = new ArrayList<>();
    private List<CurrencyRate> allRates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("MainActivity.onCreate() called");
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtFilter = findViewById(R.id.edtFilter);
        listViewRates = findViewById(R.id.listViewRates);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayItems
        );
        listViewRates.setAdapter(adapter);

        // Filtravimas pagal valiutos kodą
        edtFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("beforeTextChanged() called");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("onTextChanged() called: " + s);
                filterRates(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("afterTextChanged() called");
            }
        });

        // Paleidžiame asinchroninį duomenų užkrovimą
        new DataLoader(this).execute(ECB_URL);
    }

    private void filterRates(String filter) {
        System.out.println("filterRates() called with filter: " + filter);
        displayItems.clear();

        if (allRates == null) {
            adapter.notifyDataSetChanged();
            return;
        }

        String upper = filter.toUpperCase();

        for (CurrencyRate rate : allRates) {
            String code = rate.getCode();
            if (upper.isEmpty() || code.toUpperCase().contains(upper)) {
                displayItems.add(code + " - " + rate.getRate());
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDataLoaded(List<CurrencyRate> rates) {
        System.out.println("onDataLoaded() called, count = " + (rates != null ? rates.size() : 0));
        Log.d(TAG, "onDataLoaded");

        if (rates == null || rates.isEmpty()) {
            Toast.makeText(this, "No data loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        this.allRates = rates;
        filterRates(edtFilter.getText().toString());
    }

    @Override
    public void onDataLoadError(Exception e) {
        System.out.println("onDataLoadError() called: " + e.getMessage());
        Log.e(TAG, "onDataLoadError", e);
        Toast.makeText(this, "Error loading data", Toast.LENGTH_LONG).show();
    }
}