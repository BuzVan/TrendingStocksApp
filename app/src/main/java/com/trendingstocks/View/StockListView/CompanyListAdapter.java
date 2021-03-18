package com.trendingstocks.View.StockListView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.R;

import java.util.ArrayList;

public class CompanyListAdapter extends RecyclerView.Adapter {

    public ArrayList<Company> companyList = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gray_stock_item, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.white_stock_item, parent, false);
        return  new CompanyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position>=0 && companyList.size()>= position)
            ((CompanyListViewHolder)holder).bind(companyList.get(position));
        else
            ((CompanyListViewHolder)holder).bind(companyList.get(companyList.size() - 1));
    }

    @Override
    public int getItemCount() {
       return companyList.size();
    }
    public int getItemViewType(int index){
       return index%2;
    }
}
