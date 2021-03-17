package com.trendingstocks;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.Service.ComplexPreferences.ObjectPreference;
import com.trendingstocks.Service.Interface.JsonRequest;
import com.trendingstocks.Service.JsonRequestImpl;
import com.trendingstocks.View.StockListView.CompanyListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    final String START_COMPANY_KEY = "START_COMPANY_KEY";
    final String NEED_UPDATE_STOCKS_KEY = "need_update_stocks";
    RecyclerView companyRecyclerView;
    CompanyListAdapter companyListAdapter;
    JsonRequest jsonRequest;
    ObjectPreference objectPreference;


    CompaniesLoadTask companiesLoadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objectPreference = (ObjectPreference) this.getApplication();


        jsonRequest = new JsonRequestImpl();
        companyRecyclerView =  findViewById(R.id.company_recycler_view);
        companyListAdapter = new CompanyListAdapter();
        companyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        companyRecyclerView.setAdapter(companyListAdapter);


        // ассинхронный поток для скачивания компаний
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

    static class CompaniesLoadTask extends AsyncTask<Void, Void, Void>{
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
        protected Void doInBackground(Void... voids) {
            //получаем данные из SharedPreferences
            Company[] data = activity.objectPreference.getComplexPreference().getCompanies(activity.START_COMPANY_KEY);

            if (data != null){
                activity.companyListAdapter.companyList.addAll(Arrays.asList(data));
                publishProgress();
                for (Company company:data) {
                    try {
                        company.updateStock();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    publishProgress();
                }
            }
            else{
                try {
                    //получаем данные из json файла
                    List<Company> temp =  activity.jsonRequest.getStartCompaniesFromJsonFile(activity.getAssets());
                    if (temp != null) {
                        activity.companyListAdapter.companyList.addAll(temp);
                        publishProgress();
                    }
                    for (Company company:temp) {
                        company.updateStock();
                        publishProgress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //ошибка. пробуем скачать по api
                    downloadStartCompany();
                }
            }
            return null;
        }
        private void downloadStartCompany(){
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
                    downloadStartCompany();
                }
                return;
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
                    activity.companyListAdapter.companyList.add(company);
                    publishProgress();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private List<Company> temp_company = new ArrayList<>();

        @Override
        protected void onProgressUpdate(Void... voids) {
            activity.companyListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        /*
        outState.putSerializable("company_list", companyListAdapter.companyList.toArray());
        super.onSaveInstanceState(outState);
         */
        objectPreference.getComplexPreference().putCompanies(START_COMPANY_KEY, companyListAdapter.companyList.toArray());
        outState.putBoolean(NEED_UPDATE_STOCKS_KEY, false);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Company[] data = objectPreference.getComplexPreference().getCompanies(START_COMPANY_KEY);
        companyListAdapter.companyList.addAll(Arrays.asList(data));
        companyListAdapter.notifyDataSetChanged();
        super.onRestoreInstanceState(savedInstanceState);
    }
}
