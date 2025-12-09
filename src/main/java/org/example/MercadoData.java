package org.example;

public class MercadoData {

    // 'market_cap_rank' (ranking)
    private int market_cap_rank;

    // 'price_change_percentage_24h' (variación 24h)
    private double price_change_percentage_24h;

    // 'current_price' (objeto anidado para el precio)
    private CurrentPriceMap current_price;

    // --- Getters requeridos por el programa ---
    public int getMarketCapRank() {
        return market_cap_rank;
    }

    public double getPriceChangePercentage24h() {
        return price_change_percentage_24h;
    }

    public CurrentPriceMap getCurrentPriceMap() {
        return current_price;
    }

    /**
     * Clase interna POJO para obtener el precio en USD.
     */
    public class CurrentPriceMap {
        private double usd;

        public double getUsd() {
            return usd;
        }
    }
}