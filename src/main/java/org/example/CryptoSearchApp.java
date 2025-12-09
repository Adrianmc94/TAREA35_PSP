package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CryptoSearchApp {

    private static final String API_BASE_URL = "https://api.coingecko.com/api/v3/";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Buscador de Criptomonedas (CoinGecko API)");
        System.out.print("Introduce el nombre o símbolo de la criptomoneda: ");
        String query = scanner.nextLine();

        try {
            searchAndDisplayCrypto(query);
        } catch (Exception e) {
            System.err.println("\nERROR al procesar la solicitud: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void searchAndDisplayCrypto(String query) throws Exception {
        String coinId = getCoinId(query);

        if (coinId == null) {
            System.out.println("\nMoneda no encontrada.");
            return;
        }

        // URL para obtener los datos detallados
        String detailUrl = API_BASE_URL + "coins/" + coinId +
                "?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false";

        String jsonDetail = sendGetRequest(detailUrl);

        Gson gson = new Gson();
        CoinData data = gson.fromJson(jsonDetail, CoinData.class);

        displayCoinInfo(data);
    }

    private static String getCoinId(String query) throws Exception {
        String searchUrl = API_BASE_URL + "search?query=" + query;
        String jsonResponse = sendGetRequest(searchUrl);

        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray coins = jsonObject.getAsJsonArray("coins");

        if (coins.size() > 0) {
            return coins.get(0).getAsJsonObject().get("id").getAsString();
        }
        return null;
    }

    private static String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("accept", "application/json");

        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Fallo en la petición HTTP. Código: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static void displayCoinInfo(CoinData data) {
        String name = data.getName();
        String symbol = data.getSymbol().toUpperCase();

        MercadoData marketData = data.getMarketData();

        if (marketData == null || marketData.getCurrentPriceMap() == null) {
            System.out.println("\nNo se encontraron datos de mercado para esta moneda.");
            return;
        }

        double priceUsd = marketData.getCurrentPriceMap().getUsd();
        int rank = marketData.getMarketCapRank();
        double change24h = marketData.getPriceChangePercentage24h();

        String color = (change24h >= 0) ? ANSI_GREEN : ANSI_RED;
        String sign = (change24h >= 0) ? "+" : "";

        System.out.println("\n--- Información de Criptomoneda ---");
        System.out.printf("Nombre y Símbolo: %s (%s)\n", name, symbol);
        System.out.printf("Precio en USD: $%,.2f\n", priceUsd);
        System.out.printf("Ranking: #%d\n", rank);

        // Muestra la variación con color y el signo (+/-)
        System.out.printf("Variación 24h: %s%s%.2f%%%s\n",
                color, sign, change24h, ANSI_RESET);
        System.out.println("------------------------------------");
    }
}