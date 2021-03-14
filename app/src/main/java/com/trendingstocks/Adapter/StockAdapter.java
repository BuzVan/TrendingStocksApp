package com.trendingstocks.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.trendingstocks.Entity.Company;
import com.trendingstocks.R;

import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    private ArrayList<Company> stockList;

    public StockAdapter(ArrayList<Company> stockList) {
        this.stockList = stockList;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_item, parent, false);
        return  new StockAdapter.StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        holder.bind(stockList.get(position));
    }

    @Override
    public int getItemCount() {
       return stockList.size();
    }

    class StockViewHolder extends RecyclerView.ViewHolder{
        ImageView stockImageView;
        TextView priceNow;
        TextView priceChange;
        TextView tickerName;
        TextView companyName;
        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            stockImageView = itemView.findViewById(R.id.stock_image_view);
            priceNow = itemView.findViewById(R.id.price_now);
            priceChange = itemView.findViewById(R.id.price_change);
            tickerName = itemView.findViewById(R.id.ticker_name);
            companyName = itemView.findViewById(R.id.company_name);

        }
        public void bind(Company company){
            tickerName.setText(company.getTicker());
            priceNow.setText(company.getStock().getCurrentPrice()+" " + company.getCurrency());
            priceChange.setText(company.getStock().getPriceChange() +" "+ company.getCurrency());
            companyName.setText(company.getName());

            try{
                if (company.getLogo() != null)
                    Picasso.with(itemView.getContext())
                            .load(company.getLogo())
                            .into(stockImageView);
            }
            catch (Exception e){
                Log.e("STOCK_ADAPTER", "error in image on company " + company);
            }

        }
    }
}
