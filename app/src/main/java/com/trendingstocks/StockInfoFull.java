package com.trendingstocks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.trendingstocks.Entity.Company;
import com.trendingstocks.Service.App;
import com.trendingstocks.Service.FinnhubAPI.Interface.JsonRequest;
import com.trendingstocks.Service.FinnhubAPI.JsonEntity.StockCandles;
import com.trendingstocks.Service.FinnhubAPI.JsonRequestImpl;

import org.decimal4j.util.DoubleRounder;

import java.io.IOException;
import java.util.ArrayList;

public class StockInfoFull extends AppCompatActivity {

    private Company company;
    private JsonRequest jsonRequest;
    private StockCandles stockCandles;

    private RadioGroup radioGroup;
    private CandleStickChart chart;
    private ProgressBar progressBar;
    private Thread runThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_full_info);
        company = (Company) getIntent().getSerializableExtra("company");

        jsonRequest = new JsonRequestImpl();

        initViews();

        if (savedInstanceState != null)
            stockCandles = (StockCandles) savedInstanceState.getSerializable("stock");
        if (stockCandles == null)
            ((RadioButton) findViewById(R.id.lastM_btn)).setChecked(true);
        else
            printChart();
    }

    private void initViews() {


        TextView tittle = findViewById(R.id.companyInfoTicker);
        tittle.setText(company.ticker);

        TextView subtitle = findViewById(R.id.companyInfoName);
        subtitle.setText(company.name);

        TextView priceNow = findViewById(R.id.companyInfoPriceNow);
        TextView priceChange = findViewById(R.id.companyInfoPriceChange);

        //установка значений акций
        if (company.stock.currentPrice == 0) {
            priceChange.setTextColor(getResources().getColor(R.color.gray_text));
            priceNow.setTextColor(getResources().getColor(R.color.gray_text));
        } else if (company.stock.getPriceChange() < 0) {
            priceChange.setTextColor(getResources().getColor(R.color.red_text));
            priceNow.setTextColor(getResources().getColor(R.color.black_text));
        } else if (company.stock.getPriceChange() > 0) {
            priceChange.setTextColor(getResources().getColor(R.color.green_text));
            priceNow.setTextColor(getResources().getColor(R.color.black_text));
        }
        priceNow.setText(company.getStringStock());
        priceChange.setText(company.getStringChangeStock());


        ImageButton backButton = findViewById(R.id.companyInfoBackButton);
        backButton.setOnClickListener(v -> finish());

        ImageButton starButton = findViewById(R.id.companyInfoStarButton);
        if (company.isFavorite)
            starButton.setColorFilter(getResources().getColor(R.color.star_yellow));
        else
            starButton.setColorFilter(getResources().getColor(R.color.gray));

        progressBar = findViewById(R.id.chart_progress_bar);
        chart = findViewById(R.id.chartView);


        starButton.setOnClickListener(v -> {
            company.isFavorite = !company.isFavorite;

            new Thread(() -> App.getInstance().getDatabase().companyDao().update(company)).start();

            if (company.isFavorite)
                starButton.setColorFilter(getResources().getColor(R.color.star_yellow));
            else
                starButton.setColorFilter(getResources().getColor(R.color.gray));
        });

        radioGroup = findViewById(R.id.radio);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (chart != null)
                chart.clear();

            switch (checkedId) {
                case R.id.lastM_btn:
                    downloadChart(ChartType.LAST_MONTH);
                    break;
                case R.id.lastY_btn:
                    downloadChart(ChartType.LAST_YEAR);
                    break;
                case R.id.all_btn:
                    downloadChart(ChartType.ALL_TIME);
                    break;
            }
        });
    }

    private enum ChartType {
        ALL_TIME,
        LAST_YEAR,
        LAST_MONTH
    }

    private void downloadChart(ChartType type) {
        if (runThread != null)
            runThread.interrupt();

        chart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        runThread = new Thread(() -> {

            try {
                switch (type) {
                    case ALL_TIME:
                        stockCandles = jsonRequest.getStockCandlesAllTimeByMonth(company.ticker);
                        break;
                    case LAST_YEAR:
                        stockCandles = jsonRequest.getStockCandlesLastYearByWeek(company.ticker);
                        break;
                    case LAST_MONTH:
                        stockCandles = jsonRequest.getStockCandlesLastMonthByDays(company.ticker);
                        break;
                }

            } catch (IOException e) {
                if (Thread.interrupted())
                    return;
                StockInfoFull.this.runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    chart.setVisibility(View.VISIBLE);
                });
                e.printStackTrace();
                return;
            }
            if (stockCandles.s == null || !stockCandles.s.equals("ok")) {
                StockInfoFull.this.runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    chart.setVisibility(View.VISIBLE);
                });
                return;
            }
            if (Thread.interrupted())
                return;
            StockInfoFull.this.runOnUiThread(StockInfoFull.this::printChart);
        });
        runThread.start();

    }

    private void printChart() {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        for (int i = 0; i < stockCandles.o.size(); i++) {
            if (Thread.interrupted())
                return;

            CandleEntry cur = new CandleEntry(
                    i,
                    stockCandles.h.get(i),
                    stockCandles.l.get(i),
                    stockCandles.o.get(i),
                    stockCandles.c.get(i));
            entries.add(cur);
        }

        CandleDataSet set1 = new CandleDataSet(entries, "Stock");

        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(getResources().getColor(R.color.gray));
        set1.setShadowWidth(0.8f);
        set1.setDecreasingColor(getResources().getColor(R.color.red_text));
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(getResources().getColor(R.color.green_text));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(false);
        //TODO может оставить крест индикатора
        set1.setDrawHorizontalHighlightIndicator(false);

        chart.setHighlightPerDragEnabled(true);

        chart.setBackgroundColor(getResources().getColor(R.color.black));

        chart.setDrawBorders(false);

        YAxis yAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);

        XAxis xAxis = chart.getXAxis();

        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        rightAxis.setTextColor(Color.WHITE);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        chart.getLegend().setEnabled(false);

        chart.setScaleYEnabled(false);

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);

        chart.animateX(1000);

        chart.resetZoom();

        CandleData candleData = new CandleData(set1);
        chart.setData(candleData);

        IMarker chartMarkerView = new ChartMarkerView(getBaseContext(), R.layout.marker_view);
        chart.setMarker(chartMarkerView);

        progressBar.setVisibility(View.GONE);
        chart.setVisibility(View.VISIBLE);


        chart.invalidate();
    }

    @Override
    protected void onStop() {
        if (runThread != null)
            runThread.interrupt();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("stock", stockCandles);

    }

    class ChartMarkerView extends MarkerView {

        private final TextView stockTV;
        private final TextView dateTV;

        /**
         * Constructor. Sets up the MarkerView with a custom layout resource.
         *
         * @param context
         * @param layoutResource the layout resource to use for the MarkerView
         */
        public ChartMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            stockTV = findViewById(R.id.marker_stock);
            dateTV = findViewById(R.id.marker_date);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            // set the entry-value as the display text

            stockTV.setText(DoubleRounder.round(((CandleEntry) e).getClose(), 2) + " " + company.currency);
            dateTV.setText(stockCandles.getStrDate((int) e.getX()));

        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if (mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-getWidth(), -getHeight());
            }

            return mOffset;
        }
    }
}
