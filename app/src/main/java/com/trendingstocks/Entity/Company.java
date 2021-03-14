package com.trendingstocks.Entity;

import java.util.Objects;

public class Company {
    private String name;
    private String country;
    private String currency;
    private String ticker;
    private String weburl;
    private String logo;

    private Stock stock;

    public  Company(String name, String ticker, String currency){
        this.name = name;
        this.ticker = ticker;
        this.currency = currency;
    }
    public Company(String name, String country, String currency, String ticker, String weburl, String logo, Stock stock) {
        this.name = name;
        this.country = country;
        this.currency = currency;
        this.ticker = ticker;
        this.weburl = weburl;
        this.logo = logo;
        this.stock = stock;
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
