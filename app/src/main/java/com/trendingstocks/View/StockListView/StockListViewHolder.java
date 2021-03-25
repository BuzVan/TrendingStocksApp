package com.trendingstocks.View.StockListView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.trendingstocks.Entity.Company;
import com.trendingstocks.R;

class StockListViewHolder extends RecyclerView.ViewHolder{
    ImageView companyImageView;
    TextView priceNow;
    TextView priceChange;
    TextView tickerName;
    TextView companyName;
    ImageButton starButton;
    public StockListViewHolder(@NonNull View itemView) {
        super(itemView);

        companyImageView = itemView.findViewById(R.id.company_image_view);
        priceNow = itemView.findViewById(R.id.price_now);
        priceChange = itemView.findViewById(R.id.price_change);
        tickerName = itemView.findViewById(R.id.ticker_name);
        companyName = itemView.findViewById(R.id.company_name);
        starButton = itemView.findViewById(R.id.starButton);
    }

    public void bind(Company company){
        tickerName.setText(company.ticker);

        //установка цвета акциям
        if (company.stock.currentPrice == 0) {
            priceChange.setTextColor(itemView.getResources().getColor(R.color.gray_text));
            priceNow.setTextColor(itemView.getResources().getColor(R.color.gray_text));
        }
        else if (company.stock.getPriceChange()<0){
            priceChange.setTextColor(itemView.getResources().getColor(R.color.red_text));
            priceNow.setTextColor(itemView.getResources().getColor(R.color.black_text));
        }
        else if (company.stock.getPriceChange()>0){
            priceChange.setTextColor(itemView.getResources().getColor(R.color.green_text));
            priceNow.setTextColor(itemView.getResources().getColor(R.color.black_text));
        }
        //установка цвета звезды
        if (company.isFavorite)
            starButton.setColorFilter(itemView.getResources().getColor(R.color.star_yellow));
        else
            starButton.setColorFilter(itemView.getResources().getColor(R.color.gray));

        priceNow.setText(company.getStringStock());
        priceChange.setText(company.getStringChangeStock());


        companyName.setText(company.name);

        if (company.logo == null || company.logo.equals(""))
            companyImageView.setImageResource(R.drawable.err_image);
        else
            try {
                Picasso.with(itemView.getContext())
                        .load(company.logo)
                        .placeholder(R.drawable.load_image)
                        .error(R.drawable.err_image)
                        .fit()
                        .centerInside()
                        .into(companyImageView);
            }
            catch (Exception e){
                companyImageView.setImageResource(R.drawable.err_image);
            }
    }
}
