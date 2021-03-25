package com.trendingstocks.Entity;

import org.decimal4j.util.DoubleRounder;

import java.io.Serializable;

public class Stock implements Serializable {
    public double prevClosePrice = 0;
    public double currentPrice = 0;

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
}
