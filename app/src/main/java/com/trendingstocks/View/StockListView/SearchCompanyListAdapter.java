package com.trendingstocks.View.StockListView;

import com.trendingstocks.Service.App;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class SearchCompanyListAdapter extends CompanyListAdapter {
    public SearchCompanyListAdapter() {
        companyList = new ArrayList<>();
        App
                .getInstance()
                .getDatabase()
                .companyDao()
                .getAllSearchFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(companies -> {
                    companyList = new ArrayList<>(companies);
                    notifyDataSetChanged();
                });
    }
}
