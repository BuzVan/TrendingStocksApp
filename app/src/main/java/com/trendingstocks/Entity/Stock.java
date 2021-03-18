package com.trendingstocks.Entity;

import org.decimal4j.util.DoubleRounder;

import java.io.Serializable;

public class Stock implements Serializable {
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
        return DoubleRounder.round(Math.abs(getPriceChange()/prevClosePrice),3);
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

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setPrevClosePrice(double prevClosePrice) {
        this.prevClosePrice = prevClosePrice;
    }
}
