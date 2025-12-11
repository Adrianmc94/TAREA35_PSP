package org.example;

public class CoinData {
    private String id;
    private String symbol;
    private String name;

    private MercadoData market_data;

    // Getters requeridos
    public String getId() {
        return id;
    }
    public String getSymbol() {
        return symbol;
    }
    public String getName() {
        return name;
    }
    public MercadoData getMarketData() {
        return market_data;
    }
}