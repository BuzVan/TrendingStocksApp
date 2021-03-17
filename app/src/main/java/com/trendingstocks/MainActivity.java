package com.trendingstocks;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.Entity.Stock;
import com.trendingstocks.Service.Interface.JsonRequest;
import com.trendingstocks.Service.JsonRequestImpl;
import com.trendingstocks.View.StockListView.CompanyListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        companiesLoadTask = (CompaniesLoadTask) getLastCustomNonConfigurationInstance();
        if (companiesLoadTask == null){
            companiesLoadTask = new CompaniesLoadTask(this);
            companiesLoadTask.execute();
        }
        else
            companiesLoadTask.link(this);

    }

    @Override
    protected void onStop() {
        onRetainCustomNonConfigurationInstance();
        super.onStop();
    }

    @Nullable
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (companiesLoadTask!=null)
            companiesLoadTask.unLink();
        return companiesLoadTask;
    }

    static class CompaniesLoadTask extends AsyncTask<Integer, Company, Void>{
        private  MainActivity activity;
        private  int restarts = 0;

        // получаем ссылку на MainActivity
        void link(MainActivity act) {
            activity = act;
        }
        // обнуляем ссылку
        void unLink() {
            activity = null;
        }

        CompaniesLoadTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Integer... values) {
            List<String> tickers = new ArrayList<>();
            try {
                tickers = activity.jsonRequest.getStartTickers();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                if (restarts<3){
                    restarts++;
                    //возможно настиг лимит на 60 обращений в секунду.
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    doInBackground(values);
                }
                return null;
            }
            for (String ticker : tickers) {
                //возможно ссылка на MainActivity ещё не восстановлена
                while (activity == null) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Company company = null;
                try {
                    company = activity.jsonRequest.getCompany(ticker);
                    publishProgress(company);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private List<Company> temp_company = new ArrayList<>();
        @Override
        protected void onProgressUpdate(Company... companies) {
            if (activity == null){
                temp_company.add(companies[0]);
            }
            else if (temp_company.size() >0){
                activity.companyListAdapter.companyList.addAll(temp_company);
                temp_company.clear();
                activity.companyListAdapter.companyList.add(companies[0]);
            }
            else {
                activity.companyListAdapter.companyList.add(companies[0]);
                activity.companyListAdapter.notifyDataSetChanged();
            }
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

    }

}
