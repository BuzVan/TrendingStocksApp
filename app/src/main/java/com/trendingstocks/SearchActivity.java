package com.trendingstocks;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.Service.App;
import com.trendingstocks.Service.FinnhubAPI.Interface.JsonRequest;
import com.trendingstocks.Service.FinnhubAPI.JsonRequestImpl;
import com.trendingstocks.View.StockListView.SearchCompanyListAdapter;

import java.io.IOException;
import java.util.List;





public class SearchActivity extends AppCompatActivity {

    SearchCompanyListAdapter searchCompanyListAdapter;
    SearchView searchView;
    ProgressBar searchProgressBar;
    JsonRequest jsonRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        jsonRequest = new JsonRequestImpl();

        RecyclerView searchRecyclerView = findViewById(R.id.search_recycler_view);
        searchCompanyListAdapter = new SearchCompanyListAdapter();
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchCompanyListAdapter);

        searchView = findViewById(R.id.search_view);
        searchProgressBar = findViewById(R.id.search_progress_bar);
        searchProgressBar.setVisibility(View.GONE);


        //новое окно, активируем строку поиска
        if (savedInstanceState == null)
            searchView.onActionViewExpanded();

        /*
        //добавление данных в recyclerView
        new Thread(() -> searchCompanyListAdapter.companyList = new ArrayList<>(App.getInstance().getDatabase().companyDao().getAllSearch())).start();
*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //добавление данных в recyclerView
                new Thread(() -> searchComp(query)).start();
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


            private void searchComp(String query) {
                runOnUiThread(()->searchProgressBar.setVisibility(View.VISIBLE));
                Log.i("SearchComp", "start");

                App.getInstance().getDatabase().companyDao().saveFavoriteSearch();
                App.getInstance().getDatabase().companyDao().deleteNonFavoriteSearch();


                List<String> tickers;
                try {
                    tickers = jsonRequest.getTickersCompanySearch(query);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                Log.i("SearchComp", "count Company = " + tickers.size());
                if (tickers.size() > 0) {
                    for (int i = 0; i < tickers.size(); i++) {
                        Log.i("SearchComp", "ticker " + i + " = " + tickers.get(i));
                        try {
                            if (isFinishing())
                                break;
                            Company company = App.getInstance().getDatabase().companyDao().getByTicker(tickers.get(i));
                            //нет в бд
                            if (company == null) {
                                company = jsonRequest.getCompany(tickers.get(i));
                                if (company.ticker == null)
                                    continue;
                            }
                            Company company1 = App.getInstance().getDatabase().companyDao().getByTicker(company.ticker);
                            if (company1 == null){
                                company.isSearchResult = true;
                                App.getInstance().getDatabase().companyDao().insert(company);
                            }
                            else{
                                company1.isSearchResult =true;
                                App.getInstance().getDatabase().companyDao().update(company1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.i("SearchComp", "finish");
                runOnUiThread(()->searchProgressBar.setVisibility(View.GONE));
            }

        });

    }

}