package com.trendingstocks;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.trendingstocks.Entity.Company;
import com.trendingstocks.Service.App;
import com.trendingstocks.Service.FinnhubAPI.Interface.JsonRequest;
import com.trendingstocks.Service.FinnhubAPI.JsonRequestImpl;
import com.trendingstocks.View.TabView.FavoriteListPage;
import com.trendingstocks.View.TabView.StockListPage;
import com.trendingstocks.View.TabView.ViewPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    JsonRequest jsonRequest;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jsonRequest = new JsonRequestImpl();

        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        CompaniesLoadTask.startTask(this);


        //удаляем найденные поля, не добавленные в избранное
        new Thread(() -> {
            App.getInstance().getDatabase().companyDao().saveFavoriteSearch();
            App.getInstance().getDatabase().companyDao().deleteNonFavoriteSearch();
        }).start();

        searchView = findViewById(R.id.search_view);

        searchView.setOnClickListener(v -> {
            Intent searchAct = new Intent(MainActivity.this, SearchActivity.class);
            searchView.onActionViewCollapsed();
            startActivity(searchAct);
        });
        searchView.setOnSearchClickListener(v -> {
            Intent searchAct = new Intent(MainActivity.this, SearchActivity.class);
            searchView.onActionViewCollapsed();
            startActivity(searchAct);
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StockListPage(), "Stocks");
        adapter.addFragment(new FavoriteListPage(), "Favourite");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        CompaniesLoadTask.deleteLink();
        super.onStop();
    }

    private static class CompaniesLoadTask {
        private static UpdateCompanyTask comTask;

        public static void deleteLink() {
            comTask.activity = null;
        }

        public static void destroyTask() {
            if (comTask != null) {
                deleteLink();
                comTask.cancel(true);
                comTask = null;
            }
        }

        public static void startTask(MainActivity activity) {
            if (comTask == null) {
                comTask = new UpdateCompanyTask(activity);
                comTask.execute();
            } else if (comTask.isCancelled()) {
                comTask = new UpdateCompanyTask(activity);
            } else if (comTask.activity != activity)
                comTask.activity = activity;
        }

        static class UpdateCompanyTask extends AsyncTask<Void, Pair<Integer, Company>, Void> {

            private MainActivity activity;
            private final int restarts = 0;

            UpdateCompanyTask(MainActivity activity) {
                this.activity = activity;
            }

            final String LOG_TAG = "StartCompLoad";

            private MainActivity getActivity(){
                while (activity == null) {
                    if (isCancelled())
                        return null;
                    try {
                        Thread.sleep(500);
                        Log.e(LOG_TAG, "activity is null...wait");
                    } catch (InterruptedException e) {
                        throw new IllegalStateException("Main activity is Null!!");
                    }
                }
                return activity;
            }
            @Override
            protected Void doInBackground(Void... voids) {

                Log.i(LOG_TAG, "start");
                App.getInstance().getDatabase().companyDao().saveFavoriteSearch();
                App.getInstance().getDatabase().companyDao().deleteNonFavoriteSearch();

                updateStocks();
                downloadStartCompany();

                Log.w(LOG_TAG, "finished");
                return null;
            }

            private void downloadStartCompany() {
                List<String> tickers = new ArrayList<>();
                try {
                    tickers = getActivity().jsonRequest.getStartTickers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (tickers == null)
                    return;

                for (String ticker: tickers
                     ) {
                    if (isCancelled())
                        return;
                    Company company = App.getInstance().getDatabase().companyDao().getByTicker(ticker);
                    if (company == null) {
                        try {
                            company = getActivity().jsonRequest.getCompany(ticker);
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }
                        Company company1 = App.getInstance().getDatabase().companyDao().getByTicker(company.ticker);
                        if (company1 == null)
                            App.getInstance().getDatabase().companyDao().insert(company);
                        else
                            App.getInstance().getDatabase().companyDao().setStock(company.ticker, company.stock.prevClosePrice, company.stock.currentPrice);
                    }
                }
            }

            private void updateStocks() {
                List<Company> companies = App.getInstance().getDatabase().companyDao().getAll();
                Log.i(LOG_TAG, "count Company = " + companies.size());

                for (Company company:
                        companies) {
                    try {
                        if (isCancelled())
                            return;
                        company.updateStock();
                        Log.i(LOG_TAG, "update Company = " + company);
                        App.getInstance().getDatabase().companyDao().setStock(company.ticker, company.stock.prevClosePrice, company.stock.currentPrice);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }



}
