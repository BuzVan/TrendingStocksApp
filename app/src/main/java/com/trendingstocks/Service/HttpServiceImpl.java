package com.trendingstocks.Service;

import com.trendingstocks.Service.Interface.HttpService;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpServiceImpl implements HttpService {
    @Override
    public Request getSimpleRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    @Override
    public Request getRequestWithParams(String url, Map<String, String> params) {
        HttpUrl.Builder urlBuilder = HttpUrl
                .parse(url)
                .newBuilder();
        for (Map.Entry<String, String> entry: params.entrySet()){
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        String params_url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(params_url)
                .build();
    }

    @Override
    public Response getSyncResponse(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        return  client.newCall(request).execute();
    }
}
