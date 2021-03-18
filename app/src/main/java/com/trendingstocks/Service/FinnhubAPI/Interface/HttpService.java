package com.trendingstocks.Service.FinnhubAPI.Interface;



import java.io.IOException;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public interface HttpService {
    /**
     * Метод формирует простой GET-запрос
     * @param url - адрес страницы
     * @return - сформированный запрос
     */
    Request getSimpleRequest(String url);
    /**
     * Метод формирует GET-запрос с параметрами
     * @param url - адрес страницы
     * @param params - список параметров
     * @return - сформированный запрос
     */
    Request getRequestWithParams(String url, Map<String, String>
            params);
    /**
     * Метод формирует ответ на GET-запрос
     * @param request
     * @return - ответ на запрос, в случае положительного исхода
     * @throws IOException - исключение, в случае если ответ не
    был получен
     */
    Response getSyncResponse(Request request) throws IOException;
    
    
}
