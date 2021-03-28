package com.trendingstocks.Service.FinnhubAPI.Interface;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.Entity.Stock;
import com.trendingstocks.Service.FinnhubAPI.JsonEntity.StockCandles;

import java.io.IOException;
import java.util.List;

public interface JsonRequest {
    /**
     * Результаты поиска
     * @param find строка поиска
     * @return список компаний Company
     * @throws IOException ошибка, если ответ не получен
     */
    List<String> getTickersCompanySearch(String find) throws IOException;

    /**
     * получить компанию по её тикеру
     * @param ticker Тикер компании
     * @return информация об компании
     * @throws IOException ошибка, если ответ не получен
     */
    Company getCompany(String ticker) throws  IOException;

    /**
     *
     * @return получить стартовые компании
     * @throws IOException ошибка, если ответ не получен
     */
    List<String> getStartTickers() throws IOException;

    /**
     * Получить цену акции с API finnhub по тикеру
     *
     * @param ticker Тикер акции
     * @return информация об акции Stock
     * @throws IOException ошибка, если ответ не получен
     */
    Stock getStockByTicker(String ticker) throws IOException;

    /**
     * Получить цену акций (цена открытия,закрытия, макс, мин) для каждого месяца за всё время
     *
     * @param ticker Тикер акции
     * @return цена акций
     * @throws IOException ошибка, если ответ не получен
     */
    StockCandles getStockCandlesAllTimeByMonth(String ticker) throws IOException;

    StockCandles getStockCandlesLastYearByWeek(String ticker) throws IOException;

    StockCandles getStockCandlesLastMonthByDays(String ticker) throws IOException;
}
