package com.trendingstocks.Service;

import com.google.gson.Gson;
import com.trendingstocks.Entity.Company;
import com.trendingstocks.Entity.Stock;
import com.trendingstocks.Service.Interface.HttpService;
import com.trendingstocks.Service.Interface.JsonRequest;
import com.trendingstocks.Service.JsonEntity.ConstituentArray;
import com.trendingstocks.Service.JsonEntity.JsonStock;
import com.trendingstocks.Service.JsonEntity.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class JsonRequestImpl implements JsonRequest {

    private static final String BASE_URL = "https://finnhub.io/api/v1/";
    private static final String TOKEN = "c16sucn48v6ppg7f3es0";

    @Override
    public List<Company> getCompanySearch(String find) throws IOException {
        HttpService httpService = new HttpServiceImpl();
        String url = BASE_URL + "search";
        Map<String, String> map = new HashMap<String, String>();
        map.put("q",find);
        map.put("token", TOKEN);
        Request request = httpService.getRequestWithParams(url, map);
        Response response = httpService.getSyncResponse(request);

        Gson gson = new Gson();

        if (response.body() == null)
            throw new IOException("no answer from finnhub");

        String json = response.body().string();
        SearchResult searchResult = gson.fromJson(json, SearchResult.class);

        List<Company> answer = new ArrayList<>();
        for (SearchResult.JsonSymbol item: searchResult.result) {
            if (item.type.equals("Common Stock")){
                Company current = getCompany(item.symbol);
                if (!answer.contains(current))
                    answer.add(current);

            }

        }
        return  answer;
    }

    @Override
    public Company getCompany(String ticker) throws IOException {
        HttpService httpService = new HttpServiceImpl();
        String url = BASE_URL + "stock/profile2";
        Map<String, String> map = new HashMap<>();
        map.put("symbol",ticker);
        map.put("token", TOKEN);
        Request request = httpService.getRequestWithParams(url, map);
        Response response = httpService.getSyncResponse(request);

        Gson gson = new Gson();

        if (response.body() == null)
            throw new IOException("no answer from finnhub");

        String json = response.body().string();
        Company company =  gson.fromJson(json,Company.class);
        company.setStock(getStockByTicker(company.getTicker()));

        return company;
    }

    @Override
    public List<String> getStartTickers() throws IOException {
        HttpService httpService = new HttpServiceImpl();
        String url = BASE_URL + "index/constituents";
        Map<String, String> map = new HashMap<String, String>();
        map.put("symbol","^DJI");
        map.put("token", TOKEN);
        Request request = httpService.getRequestWithParams(url, map);
        Response response = httpService.getSyncResponse(request);

        if (response.body() == null)
            throw new IOException("no answer from finnhub");

        Gson gson = new Gson();
        ConstituentArray constituentArray
                = gson.fromJson(response.body().string(), ConstituentArray.class);
        return  constituentArray.constituents;
    }

    @Override
    public Stock getStockByTicker(String ticker) throws IOException {
        HttpService httpService = new HttpServiceImpl();
        String url = BASE_URL + "quote";

        Map<String, String> map = new HashMap<String, String>();
        map.put("symbol", ticker);
        map.put("token", TOKEN);
        Request request = httpService.getRequestWithParams(url, map);
        Response response = httpService.getSyncResponse(request);


        if (response.body() == null)
            throw new IOException("no answer from finnhub");

        Gson gson = new Gson();
        JsonStock jstock
                = gson.fromJson(response.body().string(), JsonStock.class);

        Stock stock = new Stock(jstock.pc,jstock.c);
        return  stock;
    }
}
