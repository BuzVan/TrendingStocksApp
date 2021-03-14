package com.trendingstocks.Entity;

public class Stock {
    private double OpenPrice;
    private double CurrentPrice;

    public Stock(double openPrice, double currentPrice) {
        OpenPrice = openPrice;
        CurrentPrice = currentPrice;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "OpenPrice=" + OpenPrice +
                ", CurrentPrice=" + CurrentPrice +
                '}';
    }

    public double getOpenPrice() {
        return OpenPrice;
    }

    public void setOpenPrice(double openPrice) {
        OpenPrice = openPrice;
    }

    public double getCurrentPrice() {
        return CurrentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        CurrentPrice = currentPrice;
    }
}
