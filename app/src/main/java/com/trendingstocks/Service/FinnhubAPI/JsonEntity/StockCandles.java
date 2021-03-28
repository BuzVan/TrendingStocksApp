package com.trendingstocks.Service.FinnhubAPI.JsonEntity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StockCandles implements Serializable {
    public List<Float> c;
    public List<Float> h;
    public List<Float> l;
    public List<Float> o;
    public List<Long> t;
    public String s;

    public Date getDate(int i) {
        Date time = new java.util.Date(t.get(i) * 1000);
        return time;
    }

    static final DateFormat df = new SimpleDateFormat("dd MMM YYYY");

    public String getStrDate(int i) {
        return df.format(getDate(i));
    }
}
