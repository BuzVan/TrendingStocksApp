package com.trendingstocks.Service.Interface;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.Entity.Stock;

import java.io.IOException;
import java.util.List;

public interface JsonRequest {
    /**
     * Результаты поиска
     * @param find строка поиска
     * @return список акицй Stock
     * @throws IOException ошибка, если ответ не получен
     */
    public List<Company> getCompanySearch(String find) throws IOException;

    /**
     * получить акцию по её тикеру
     * @param ticker Тикер акции
     * @return информация об акции
     * @throws IOException ошибка, если ответ не получен
     */
    public Company getCompany(String ticker) throws  IOException;

    public  List<Company> getStartCompany() throws  IOException;
}
