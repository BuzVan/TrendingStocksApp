package com.trendingstocks;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
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

class CompaniesLoadTask {
    static ComTask comTask;

    public static void deleteLink(){
        comTask.activity = null;
    }

    public static void destroyTask(){
        if (comTask!= null){
            deleteLink();
            comTask.cancel(true);
            comTask = null;
        }
    }

    public static void startTask(MainActivity activity){
        if (comTask == null){
            comTask = new ComTask(activity, ComTask.LaunchType.CREATE);
            comTask.execute();
        }
        else if (activity.companyListAdapter.getItemCount()==0){
            destroyTask();
            comTask = new ComTask(activity, ComTask.LaunchType.CREATE);
            comTask.execute();
        }
        else if (comTask.isCancelled()){
            comTask = new ComTask(activity, ComTask.LaunchType.UPDATE);
        }
        else if (comTask.activity != activity)
            comTask.activity = activity;
    }

    static class ComTask extends AsyncTask<Void, Pair<Integer, Company>, Void> {
        enum LaunchType {
            CREATE,
            UPDATE
        }

        private MainActivity activity;
        private final LaunchType type;
        private int restarts = 0;

        // получаем ссылку на MainActivity
        void link(MainActivity act) {
            activity = act;
        }

        // обнуляем ссылку
        void unLink() {
            activity = null;
        }

        ComTask(MainActivity activity, LaunchType type) {
            this.activity = activity;
            this.type = type;

        }

        private MainActivity getActivity() {
            while (activity == null) {
                if (isCancelled())
                    return null;
                try {
                    Thread.sleep(500);
                    Log.e("CompaniesLoadTask", "activity is null...wait");
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Main activity is Null!!");
                }
            }
            return activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.w("CompaniesLoadTask", "start");
            switch (type) {
                case CREATE:
                    downloadCompanies();
                    break;
                case UPDATE:
                    updateCompanies();
                    break;
            }
            Log.w("CompaniesLoadTask", "finished");
            return null;
        }

        protected void updateCompanies() {
            int max = getActivity().companyListAdapter.getItemCount();
            Log.i("CompaniesLoadTask", "max = " + max);
            for (int i = 0; i < max; i++) {
                try {
                    if (isCancelled())
                        return;
                    Log.i("CompaniesLoadTask", "i = " + i);
                    Company company = getActivity().companyListAdapter.companyList.get(i);
                    company.updateStock();
                    publishProgress(new Pair<>(i, company));
                } catch (IOException e) {
                    e.printStackTrace();
                    i--;
                }
            }
        }

        protected void downloadCompanies() {
            //получаем данные из getActivity
            List<Company> data = new ArrayList<>();

            Company[] temp = getActivity().objectPreference.getComplexPreference().getCompanies(getActivity().START_COMPANY_KEY);
            if (temp != null && temp.length > 0)
                data.addAll(Arrays.asList(temp));
                //не успешно
            else {

                try {
                    //получаем данные из json файла
                    data = getActivity().jsonRequest.getStartCompaniesFromJsonFile(getActivity().getAssets());
                } catch (Exception e) {
                    e.printStackTrace();
                    //ошибка. скачиваем по api
                    downloadStartCompany();
                    return;
                }
            }
            //обновляем цены на акции
            if (data.size() == 0)
                return;
            if (isCancelled())
                return;
            getActivity().companyListAdapter.companyList = new ArrayList<>(data);
            getActivity().runOnUiThread(() -> getActivity().companyListAdapter.notifyDataSetChanged());

            updateCompanies();
        }

        private void downloadStartCompany() {

            List<String> tickers = new ArrayList<>();
            try {
                tickers = getActivity().jsonRequest.getStartTickers();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                //возможно превышен лимит обращений к api
                if (restarts < 3) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    restarts++;
                    downloadStartCompany();
                }
                return;
            }
            if (isCancelled())
                return;
            getActivity().companyListAdapter.companyList.clear();
            for (String ticker : tickers) {
                if (isCancelled())
                    return;
                Company company = null;
                try {
                    company = getActivity().jsonRequest.getCompany(ticker);

                    this.publishProgress(new Pair<Integer, Company>(getActivity().companyListAdapter.getItemCount() - 1,company));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private final List<Pair<Integer, Company>> temp_company = new ArrayList<>();


        @Override
        protected void onProgressUpdate(Pair<Integer, Company>... companies) {
            if (activity != null) {
                activity.companyListAdapter.companyList.set(companies[0].first, companies[0].second);
                activity.companyListAdapter.notifyDataSetChanged();
                Log.i("CompaniesLoadTask", "progressUpdate = " + companies[0].first);
                if (temp_company.size() > 0) {
                    for (Pair<Integer, Company> pair : temp_company) {
                        activity.companyListAdapter.companyList.set(pair.first, pair.second);
                        Log.i("CompaniesLoadTask", "progressUpdate = " + pair.first);
                    }
                    temp_company.clear();
                    Log.i("CompaniesLoadTask", "progressUpdate!");
                    activity.companyListAdapter.notifyDataSetChanged();
                }
            }
            else {
                temp_company.add(companies[0]);
            }

            super.onProgressUpdate(companies);
        }

    }

}
public class MainActivity extends AppCompatActivity {

    final String START_COMPANY_KEY = "START_COMPANY_KEY";
    final String NEED_UPDATE_STOCKS_KEY = "need_update_stocks";
    RecyclerView companyRecyclerView;
    CompanyListAdapter companyListAdapter;
    JsonRequest jsonRequest;
    ObjectPreference objectPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objectPreference = (ObjectPreference) this.getApplication();


        jsonRequest = new JsonRequestImpl();
        companyRecyclerView = findViewById(R.id.company_recycler_view);
        companyListAdapter = new CompanyListAdapter();
        companyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        companyRecyclerView.setAdapter(companyListAdapter);

        //восстановление данных
        Company[] data = objectPreference.getComplexPreference().getCompanies(START_COMPANY_KEY);
        if (data != null && data.length > 0) {
            companyListAdapter.companyList = new ArrayList<>(Arrays.asList(data));
            companyListAdapter.notifyDataSetChanged();
        }


        // ассинхронный поток для скачивания/обновления компаний
        com.trendingstocks.CompaniesLoadTask.startTask(this);
    }


    @Override
    protected void onStop() {
        com.trendingstocks.CompaniesLoadTask.deleteLink();
        super.onStop();
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

}
