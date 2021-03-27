package com.trendingstocks.View.StockListView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.R;
import com.trendingstocks.Service.App;
import com.trendingstocks.StockInfoFull;

import java.util.ArrayList;

public abstract class CompanyListAdapter extends RecyclerView.Adapter{

    private Context context;
    public ArrayList<Company> companyList = new ArrayList<>();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
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
        ((StockListViewHolder) holder).bind(companyList.get(position));
        //событие нажатия на звезду
        ((StockListViewHolder) holder).starButton.setOnClickListener(v -> {
            Company company = companyList.get(position);

            company.isFavorite = !company.isFavorite;
            new Thread(() -> App.getInstance().getDatabase().companyDao().update(company)).start();
        });
        //событие нажатия на остальную часть
        ((StockListViewHolder) holder).itemView.setOnClickListener(v -> {
            Intent searchAct = new Intent(context, StockInfoFull.class);
            searchAct.putExtra("company", companyList.get(position));
            context.startActivity(searchAct);
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
