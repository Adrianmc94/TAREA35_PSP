package org.example;

public class CoinData {
    // Estas propiedades coinciden con las claves principales del JSON
    private String id;
    private String symbol;
    private String name;

    // Gson mapea el objeto "market_data" a esta clase
    private MercadoData market_data;

    // --- Getters requeridos por el programa ---
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