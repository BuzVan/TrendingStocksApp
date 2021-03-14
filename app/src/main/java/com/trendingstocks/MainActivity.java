package com.trendingstocks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Adapter.StockAdapter;
import com.trendingstocks.Entity.Company;
import com.trendingstocks.Service.Interface.JsonRequest;
import com.trendingstocks.Service.JsonRequestImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView stocksRecyclerView;
    StockAdapter stocksAdapter;
    ArrayList<Company> companiesStock;
    JsonRequest jsonRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonRequest = new JsonRequestImpl();
        companiesStock = new ArrayList<>();

        stocksRecyclerView =  findViewById(R.id.stocks_recycler_view);
        StocksTask task = new StocksTask(this);
        task.execute();
    }

    class StocksTask extends AsyncTask<Void, Void, Void>{

        private MainActivity  activity;
        StocksTask(MainActivity mainActivity) {
            activity = mainActivity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //TODO метод обновления RecyclerView какой-то другой
            stocksAdapter = new StockAdapter(companiesStock);
            stocksRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            stocksRecyclerView.setAdapter(stocksAdapter);

            try {
                ArrayList<String> tickers = new ArrayList<>(jsonRequest.getStartTickers());
                for(String ticker: tickers)
                    companiesStock.add(jsonRequest.getCompany(ticker));

                Log.i("JSON_REQUEST", "start company on MainActivity count = " + companiesStock.size());
            }
            catch (IOException e){
                Log.e("JSON_REQUEST","start company on MainActivity doesn't worked!");
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected  void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

        }
    }
}