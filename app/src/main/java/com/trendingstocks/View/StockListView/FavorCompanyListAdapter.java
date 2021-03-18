package com.trendingstocks.View.StockListView;

import android.annotation.SuppressLint;

import com.trendingstocks.Service.App;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class FavorCompanyListAdapter extends CompanyListAdapter{

    @SuppressLint("CheckResult")
    public  FavorCompanyListAdapter(){
        companyList = new ArrayList<>();
        App
                .getInstance()
                .getDatabase()
                .companyDao()
                .getAllFlowable(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(companies -> {
                    companyList = new ArrayList<>(companies);
                    notifyDataSetChanged();
                });
    }
}
