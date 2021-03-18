package com.trendingstocks.Service.JsonEntity;

import java.util.List;

public  class SearchResult{
    public List<JsonSymbol> result;
    public static class JsonSymbol {
        public String symbol;
        public String type;
    }
}




