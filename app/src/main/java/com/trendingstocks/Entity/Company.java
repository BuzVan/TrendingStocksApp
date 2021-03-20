package com.trendingstocks.Entity;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.trendingstocks.Service.FinnhubAPI.Interface.JsonRequest;
import com.trendingstocks.Service.FinnhubAPI.JsonRequestImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Company implements Serializable {
    @NonNull
    @PrimaryKey
    public String ticker;

    public String name;
    public String country;
    public String currency;
    public String weburl;
    public Boolean isFavorite = false;
    public String logo;
    public Boolean isSearchResult = false;

    @Embedded
    public Stock stock;
    public Company(){
        stock = new Stock();
    }

    public void updateStock() throws IOException {
        JsonRequest jsonRequest = new JsonRequestImpl();
        this.stock =  jsonRequest.getStockByTicker(ticker);
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", ticker='" + ticker + '\'' +
                ", country='" + country + '\'' +
                ", currency='" + currency + '\'' +
                ", favorite='" + isFavorite + '\'' +
                ", weburl='" + weburl + '\'' +
                ", logo='" + logo + '\'' +
                ", stock=" + stock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return ticker.equals(company.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, currency, ticker, weburl, logo, stock);
    }
    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getStringStock(){
        return stock.currentPrice+" " + currency;
    }
    public String getStringChangeStock(){
        String res = String.format("%s %s (%s%%)", stock.getPriceChange(), currency, stock.getPriceChangePercent());
        if (stock.getPriceChange()>0)
            res = "+" + res;
        return  res;
    }
}
