package org.example;

public class MercadoData {

    private int market_cap_rank;

    private double price_change_percentage_24h;

    private CurrentPriceMap current_price;

    // Getters requeridos
    public int getMarketCapRank() {
        return market_cap_rank;
    }

    public double getPriceChangePercentage24h() {
        return price_change_percentage_24h;
    }

    // Retorna el objeto que contiene el precio actual
    public CurrentPriceMap getCurrentPriceMap() {
        return current_price;
    }

    /**
     * Clase interna POJO para obtener el precio en USD.
     * Mapea el objeto JSON "current_price" que contiene el valor "usd".
     */
    public class CurrentPriceMap {
        // Mapea la clave "usd"
        private double usd;

        public double getUsd() {
            return usd;
        }
    }
}