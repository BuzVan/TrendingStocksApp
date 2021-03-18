package com.trendingstocks.Entity;

import com.trendingstocks.Service.Interface.JsonRequest;
import com.trendingstocks.Service.JsonRequestImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class Company implements Serializable {
    private String name;
    private String country;
    private String currency;
    private String ticker;
    private String weburl;
    private Boolean favorite = false;
    private String logo;
    private Stock stock;

    public void updateStock() throws IOException {
        JsonRequest jsonRequest = new JsonRequestImpl();
        this.stock =  jsonRequest.getStockByTicker(ticker);
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", currency='" + currency + '\'' +
                ", ticker='" + ticker + '\'' +
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

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTicker() {
        return ticker;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }

    public String getLogoUri() {
        return logo;
    }

    public void setLogoUri(String logo) {
        this.logo = logo;
    }

    public Stock getStock() {
        if (stock == null)
            stock = new Stock();
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getStringStock(){
        return getStock().getCurrentPrice()+" " + getCurrency();
    }
    public String getStringChangeStock(){
        String res = String.format("%s %s (%s%%)", getStock().getPriceChange(), getCurrency(), getStock().getPriceChangePercent());
        if (getStock().getPriceChange()>0)
            res = "+" + res;
        return  res;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
