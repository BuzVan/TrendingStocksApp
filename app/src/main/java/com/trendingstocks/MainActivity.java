package com.trendingstocks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Entity.Stock;
import com.trendingstocks.View.StockListView.CompanyListAdapter;
import com.trendingstocks.Entity.Company;
import com.trendingstocks.Service.Interface.JsonRequest;
import com.trendingstocks.Service.JsonRequestImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView companyRecyclerView;
    CompanyListAdapter companyListAdapter;
    JsonRequest jsonRequest;

    CompaniesLoadTask companiesLoadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonRequest = new JsonRequestImpl();
        companyRecyclerView =  findViewById(R.id.company_recycler_view);


        companyListAdapter = new CompanyListAdapter();
        companyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        companyRecyclerView.setAdapter(companyListAdapter);

        if (savedInstanceState == null){

            companiesLoadTask = new CompaniesLoadTask(this);
            companiesLoadTask.execute();

        }
    }

    @Override
    protected void onStop() {
        if(companiesLoadTask!=null && !companiesLoadTask.isCancelled())
            companiesLoadTask.cancel(false);
        super.onStop();
    }

    class CompaniesLoadTask extends AsyncTask<Integer, Integer, Void>{
        private  MainActivity activity;
        private  int restarts = 0;
        CompaniesLoadTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Integer... values) {
            List<String> tickers = new ArrayList<>();
            try {
                tickers = jsonRequest.getStartTickers();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                if (restarts<3){
                    restarts++;
                    doInBackground(values);
                    return null;
                }
                else
                    return null;
            }
            for (String ticker : tickers) {
                if (isCancelled())
                    break;

                Company company = null;
                try {
                    company = jsonRequest.getCompany(ticker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (isCancelled())
                    break;
                companyListAdapter.companyList.add(company);
                publishProgress(companyListAdapter.getItemCount() - 1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            activity.companyListAdapter.notifyItemInserted(values[0]);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("company_list", companyListAdapter.companyList.toArray());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Object[] companies  = (Object[]) savedInstanceState.getSerializable("company_list");
        for (Object item: companies){
            Company company = (Company) item;
            companyListAdapter.companyList.add(company);
        }
        companyListAdapter.notifyDataSetChanged();

        class PriceLoadTask extends AsyncTask<Void, Integer, Void>{
            private  MainActivity activity;

            PriceLoadTask(MainActivity activity) {
                this.activity = activity;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                for (int i=0;i<companyListAdapter.getItemCount();i++) {
                    Company company = companyListAdapter.companyList.get(i);
                    try {
                        company.updateStock();
                    } catch (IOException e) {
                        company.setStock(new Stock());
                    }
                    publishProgress(i);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                companyListAdapter.notifyItemChanged(values[0]);
            }
        }
        PriceLoadTask task = new PriceLoadTask(this);
        task.execute();
    }

}
