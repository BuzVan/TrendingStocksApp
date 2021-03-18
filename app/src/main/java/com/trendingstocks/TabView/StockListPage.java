package com.trendingstocks.TabView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.R;
import com.trendingstocks.Service.ComplexPreferences.ObjectPreference;
import com.trendingstocks.Service.Interface.JsonRequest;
import com.trendingstocks.Service.JsonRequestImpl;
import com.trendingstocks.View.StockListView.CompanyListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class StartCompaniesLoadTask {
    static ComTask comTask;

    public static void deleteLink(){
        comTask.stockListPage = null;
    }

    public static void destroyTask(){
        if (comTask!= null){
            deleteLink();
            comTask.cancel(true);
            comTask = null;
        }
    }

    public static void startTask(StockListPage activity){
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
        else if (comTask.stockListPage != activity)
            comTask.stockListPage = activity;
    }

    static class ComTask extends AsyncTask<Void, Pair<Integer, Company>, Void> {
        enum LaunchType {
            CREATE,
            UPDATE
        }

        private StockListPage stockListPage;
        private final LaunchType type;
        private int restarts = 0;

        // получаем ссылку на MainActivity
        void link(StockListPage act) {
            stockListPage = act;
        }

        // обнуляем ссылку
        void unLink() {
            stockListPage = null;
        }

        ComTask(StockListPage favoriteListPage, LaunchType type) {
            this.stockListPage = favoriteListPage;
            this.type = type;

        }
        final String LOG_TAG = "StartCompLoad";
        private StockListPage getStockListPage(){
            while (stockListPage == null) {
                if (isCancelled())
                    return null;
                try {
                    Thread.sleep(500);
                    Log.e(LOG_TAG, "activity is null...wait");
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Main activity is Null!!");
                }
            }
            return stockListPage;
        }
        private FragmentActivity getMainActivity() {
            while (stockListPage.getActivity() == null) {
                if (isCancelled())
                    return null;
                try {
                    Thread.sleep(500);
                    Log.e(LOG_TAG, "activity is null...wait");
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Main activity is Null!!");
                }
            }
            return stockListPage.getActivity();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.w(LOG_TAG, "start");
            switch (type) {
                case CREATE:
                    downloadCompanies();
                    break;
                case UPDATE:
                    updateCompanies();
                    break;
            }
            Log.w(LOG_TAG, "finished");
            return null;
        }

        protected void updateCompanies() {
            int max = getStockListPage().companyListAdapter.getItemCount();
            Log.i(LOG_TAG, "max = " + max);
            for (int i = 0; i < max; i++) {
                try {
                    if (isCancelled())
                        return;
                    Log.i(LOG_TAG, "i = " + i);
                    Company company = getStockListPage().companyListAdapter.companyList.get(i);
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

            Company[] temp = getStockListPage().objectPreference.getComplexPreference().getCompanies(getStockListPage().START_COMPANY_KEY);
            if (temp != null && temp.length > 0)
                data.addAll(Arrays.asList(temp));
                //не успешно
            else {

                try {
                    //получаем данные из json файла
                    data = getStockListPage().jsonRequest.getStartCompaniesFromJsonFile(getMainActivity().getAssets());
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
            getStockListPage().companyListAdapter.companyList = new ArrayList<>(data);
            getMainActivity().runOnUiThread(() -> getStockListPage().companyListAdapter.notifyDataSetChanged());

            updateCompanies();
        }

        private void downloadStartCompany() {

            List<String> tickers = new ArrayList<>();
            try {
                tickers = getStockListPage().jsonRequest.getStartTickers();
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
            getStockListPage().companyListAdapter.companyList.clear();
            for (String ticker : tickers) {
                if (isCancelled())
                    return;
                Company company = null;
                try {
                    company = getStockListPage().jsonRequest.getCompany(ticker);

                    this.publishProgress(new Pair<Integer, Company>(getStockListPage().companyListAdapter.getItemCount() - 1,company));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private final List<Pair<Integer, Company>> temp_company = new ArrayList<>();


        @Override
        protected void onProgressUpdate(Pair<Integer, Company>... companies) {
            if (stockListPage != null && stockListPage.companyListAdapter != null) {
                stockListPage.companyListAdapter.companyList.set(companies[0].first, companies[0].second);
                stockListPage.companyListAdapter.notifyDataSetChanged();
                Log.i(LOG_TAG, "progressUpdate = " + companies[0].first);
                if (temp_company.size() > 0) {
                    for (Pair<Integer, Company> pair : temp_company) {
                        stockListPage.companyListAdapter.companyList.set(pair.first, pair.second);
                        Log.i(LOG_TAG, "progressUpdate = " + pair.first);
                    }
                    temp_company.clear();
                    Log.i(LOG_TAG, "progressUpdate!");
                    stockListPage.companyListAdapter.notifyDataSetChanged();
                }
            }
            else {
                temp_company.add(companies[0]);
            }

            super.onProgressUpdate(companies);
        }

    }

}


public class StockListPage extends Fragment {

    public static final int LAYOUT = R.layout.stocklist_view;

    final String START_COMPANY_KEY = "START_COMPANY_KEY";
    RecyclerView companyRecyclerView;
    CompanyListAdapter companyListAdapter;
    JsonRequest jsonRequest;
    ObjectPreference objectPreference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stocklist_view, container,false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        objectPreference = (ObjectPreference) this.getActivity().getApplication();
        jsonRequest = new JsonRequestImpl();
        companyRecyclerView = getActivity().findViewById(R.id.company_recycler_view);
        companyListAdapter = new CompanyListAdapter();
        companyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyRecyclerView.setAdapter(companyListAdapter);

        Company[] data = objectPreference.getComplexPreference().getCompanies(START_COMPANY_KEY);
        if (data != null && data.length > 0) {
            companyListAdapter.companyList = new ArrayList<>(Arrays.asList(data));
            companyListAdapter.notifyDataSetChanged();
        }

        // ассинхронный поток для скачивания/обновления компаний
        StartCompaniesLoadTask.startTask(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStop() {
        StartCompaniesLoadTask.deleteLink();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        objectPreference.getComplexPreference().putCompanies(START_COMPANY_KEY, companyListAdapter.companyList.toArray());
        super.onSaveInstanceState(outState);
    }
}
