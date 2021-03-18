package com.trendingstocks.View.StockListView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.R;
import com.trendingstocks.Service.App;

import java.util.ArrayList;

public abstract class CompanyListAdapter extends RecyclerView.Adapter{

    public ArrayList<Company> companyList;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_stock_item, parent, false);
        View constLayout = view.findViewById(R.id.stock_item_layout);

        if (viewType == 0)
            constLayout.setBackgroundResource(R.drawable.gray_card_shape);
        else
            constLayout.setBackgroundResource(R.drawable.white_card_shape);
        return  new StockListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((StockListViewHolder)holder).bind(companyList.get(position));
        //событие нажатия на звезду
        ((StockListViewHolder) holder).starButton.setOnClickListener(v -> {
            Company company = companyList.get(position);

            company.favorite = !company.favorite;
             Thread tr = new Thread(() -> App.getInstance().getDatabase().companyDao().update(company));
             tr.start();
        });
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }
    public int getItemViewType(int index){
        return index%2;
    }
}
