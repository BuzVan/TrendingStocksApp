package com.trendingstocks.Service.Interface;

import android.content.res.AssetManager;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.Entity.Stock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface JsonRequest {
    /**
     * Результаты поиска
     * @param find строка поиска
     * @return список компаний Company
     * @throws IOException ошибка, если ответ не получен
     */
    public List<Company> getCompanySearch(String find) throws IOException;

    /**
     * получить компанию по её тикеру
     * @param ticker Тикер компании
     * @return информация об компании
     * @throws IOException ошибка, если ответ не получен
     */
    public Company getCompany(String ticker) throws  IOException;

    /**
     *
     * @return получить стартовые компании
     * @throws IOException ошибка, если ответ не получен
     */
    public  List<String> getStartTickers() throws  IOException;

    /**
     * Получить цену акции с API finnhub по тикеру
     * @param ticker Тикер акции
     * @return информация об акции Stock
     * @throws IOException ошибка, если ответ не получен
     */
    public Stock getStockByTicker(String ticker) throws IOException;

    /**
     * Открыть стартовые компании из JSON файла проекта
     * @param assetManager ссылка на файл
     * @return созданные компании
     * @throws FileNotFoundException файл не найден
     * @throws IllegalArgumentException файл существует, но получить список компаний не получилось
     */
    public List<Company> getStartCompaniesFromJsonFile(AssetManager assetManager) throws FileNotFoundException, IllegalArgumentException;
}
