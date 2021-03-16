package com.trendingstocks.Entity;

import org.decimal4j.util.DoubleRounder;

public class Stock {
    private double prevClosePrice = 0;
    private double currentPrice = 0;

    public Stock(){}
    public Stock(double prevClosePrice, double currentPrice) {
        this.prevClosePrice = DoubleRounder.round(prevClosePrice,2);
        this.currentPrice =  DoubleRounder.round(currentPrice,2);
    }

    public double getPriceChange(){
        return  DoubleRounder.round(prevClosePrice - currentPrice, 2);
    }

    public  double getPriceChangePercent(){
        return DoubleRounder.round(getPriceChange()/prevClosePrice,2);
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

    public double getCurrentPrice() {
        return currentPrice;
    }

}
