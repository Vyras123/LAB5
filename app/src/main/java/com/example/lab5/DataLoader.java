package com.example.lab5;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DataLoader extends AsyncTask<String, Void, List<CurrencyRate>> {

    public interface OnDataLoadedListener {
        void onDataLoaded(List<CurrencyRate> rates);
        void onDataLoadError(Exception e);
    }

    private static final String TAG = "DataLoader";

    private final OnDataLoadedListener listener;
    private Exception error;

    public DataLoader(OnDataLoadedListener listener) {
        System.out.println("DataLoader constructor called");
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        System.out.println("DataLoader.onPreExecute() called");
        Log.d(TAG, "onPreExecute()");
    }

    @Override
    protected List<CurrencyRate> doInBackground(String... urls) {
        System.out.println("DataLoader.doInBackground() called");
        Log.d(TAG, "doInBackground() start");

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            String urlString = urls[0];
            System.out.println("DataLoader.doInBackground() URL = " + urlString);
            URL url = new URL(urlString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            inputStream = connection.getInputStream();

            // Kviečiame Parser
            return Parser.parseEcbXml(inputStream);

        } catch (Exception e) {
            error = e;
            System.out.println("DataLoader.doInBackground() error: " + e.getMessage());
            Log.e(TAG, "Error loading data", e);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    System.out.println("InputStream closed");
                }
            } catch (Exception e) {
                System.out.println("InputStream close error: " + e.getMessage());
                Log.e(TAG, "Error closing stream", e);
            }

            if (connection != null) {
                connection.disconnect();
                System.out.println("HttpURLConnection disconnected");
            }
        }
    }

    @Override
    protected void onPostExecute(List<CurrencyRate> currencyRates) {
        System.out.println("DataLoader.onPostExecute() called");
        Log.d(TAG, "onPostExecute()");

        if (listener == null) {
            return;
        }

        if (error != null) {
            listener.onDataLoadError(error);
        } else {
            listener.onDataLoaded(currencyRates);
        }
    }
}