package com.trendingstocks.View.StockListView;

import android.annotation.SuppressLint;

import com.trendingstocks.Service.App;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;


public class StockCompanyListAdapter extends CompanyListAdapter {

    @SuppressLint("CheckResult")
    public StockCompanyListAdapter(){
        companyList = new ArrayList<>();
        App
                .getInstance()
                .getDatabase()
                .companyDao()
                .getAllFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(companies -> {
                    companyList = new ArrayList<>(companies);
                    notifyDataSetChanged();
                });
    }
}
