package com.trendingstocks.View.StockListView;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trendingstocks.Entity.Company;
import com.trendingstocks.R;

class CompanyListViewHolder extends RecyclerView.ViewHolder{
    ImageView companyImageView;
    TextView priceNow;
    TextView priceChange;
    TextView tickerName;
    TextView companyName;
    public CompanyListViewHolder(@NonNull View itemView) {
        super(itemView);

        companyImageView = itemView.findViewById(R.id.company_image_view);
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

        if (company.getLogoUri() == null || company.getLogoUri().equals(""))
            companyImageView.setImageResource(R.drawable.err_image);
        else
            try {
                Picasso.with(itemView.getContext())
                        .load(company.getLogoUri())
                        .placeholder(R.drawable.load_image)
                        .error(R.drawable.err_image)
                        .fit()
                        .into(companyImageView);
            }
            catch (Exception e){
                companyImageView.setImageResource(R.drawable.err_image);
            }
    }
}
