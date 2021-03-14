package com.trendingstocks.Entity;

public class Stock {
    private double prevClosePrice;
    private double currentPrice;

    public Stock(double prevClosePrice, double currentPrice) {
        this.prevClosePrice = prevClosePrice;
        this.currentPrice = currentPrice;
    }

    public double getPriceChange(){
        return prevClosePrice - currentPrice;
    }

    public  double getPriceChangePercent(){
        double value = getPriceChange()/prevClosePrice;
        double scale = Math.pow(10,2);
        double result = Math.ceil(value * scale) / scale;
        return  result;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "PrevClosePrice=" + prevClosePrice +
                ", CurrentPrice=" + currentPrice +
                '}';
    }

    public double getPrevClosePrice() {
        return prevClosePrice;
    }

    public void setPrevClosePrice(double prevClosePrice) {
        this.prevClosePrice = prevClosePrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
}
