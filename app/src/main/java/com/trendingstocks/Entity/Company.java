package com.trendingstocks.Entity;

import android.graphics.Bitmap;

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
    private String logo;
    private transient Stock stock = new Stock();


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
        return name.equals(company.name) &&
                Objects.equals(country, company.country) &&
                Objects.equals(currency, company.currency) &&
                ticker.equals(company.ticker) &&
                Objects.equals(weburl, company.weburl) &&
                Objects.equals(logo, company.logo) &&
                Objects.equals(stock, company.stock);
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

}
