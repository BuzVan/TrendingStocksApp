package com.trendingstocks;

import com.trendingstocks.Entity.Company;
import com.trendingstocks.Entity.Stock;
import com.trendingstocks.Service.FinnhubAPI.JsonRequestImpl;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class JsonRequestUnitTest {
    @Test
    public void GetStartCompany_ReturnResult() throws Exception {
        JsonRequestImpl jsonRequest = new JsonRequestImpl();
        List<Company> companies = jsonRequest.getStartCompany();
        Assert.assertNotEquals(null, companies);
        for( Company company: companies){
            System.out.println(company);
        }
    }

    @Test
    public void SearchResult_ReturnGoodAnswer() throws Exception {
        JsonRequestImpl jsonRequest = new JsonRequestImpl();
        List<Company> companies = jsonRequest.getCompanySearch("yandex");
        Assert.assertNotEquals(null, companies);
        for( Company company: companies){
            System.out.println(company);
        }
    }

    @Test
    public void CompanyStock_ReturnGoodAnswer() throws Exception {
        JsonRequestImpl jsonRequest = new JsonRequestImpl();
        Stock stock = jsonRequest.getStockByTicker("YNDX");
        Assert.assertNotEquals(null, stock);
        System.out.println(stock);
    }
}