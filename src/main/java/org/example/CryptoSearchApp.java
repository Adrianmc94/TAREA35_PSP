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

    // URL base de la API
    private static final String API_BASE_URL = "https://api.coingecko.com/api/v3/";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Buscador de Criptomonedas (CoinGecko API)");
        System.out.print("Introduce el nombre o símbolo de la criptomoneda: ");
        // Lee la entrada del usuario
        String query = scanner.nextLine();

        try {
            // Llama a la función principal para buscar y mostrar la información
            searchAndDisplayCrypto(query);
        } catch (Exception e) {
            System.err.println("\nERROR al procesar la solicitud: " + e.getMessage());
        } finally {
            // Asegura que el scanner se cierre
            scanner.close();
        }
    }

    /**
     * Busca el ID de la moneda y luego obtiene y muestra sus datos.
     * @param query Nombre o símbolo de la criptomoneda.
     */
    private static void searchAndDisplayCrypto(String query) throws Exception {
        // 1. Obtener el ID de CoinGecko para la búsqueda del usuario
        String coinId = getCoinId(query);

        if (coinId == null) {
            System.out.println("\nMoneda no encontrada.");
            return;
        }

        // URL para obtener los datos detallados usando el ID
        String detailUrl = API_BASE_URL + "coins/" + coinId +
                "?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false";

        // Realiza la petición GET para obtener los detalles
        String jsonDetail = sendGetRequest(detailUrl);

        // Parsear el JSON de detalle a un objeto CoinData usando Gson
        Gson gson = new Gson();
        CoinData data = gson.fromJson(jsonDetail, CoinData.class);

        // Mostrar la información formateada
        displayCoinInfo(data);
    }

    /**
     * Realiza una búsqueda inicial para obtener el ID único de la criptomoneda.
     */
    private static String getCoinId(String query) throws Exception {
        // URL para la búsqueda
        String searchUrl = API_BASE_URL + "search?query=" + query;
        String jsonResponse = sendGetRequest(searchUrl);

        // Parsear la respuesta JSON de la búsqueda
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray coins = jsonObject.getAsJsonArray("coins"); // El resultado está en el array "coins"

        if (coins.size() > 0) {
            // Devuelve el ID de la primera coincidencia
            return coins.get(0).getAsJsonObject().get("id").getAsString();
        }
        return null; // Moneda no encontrada
    }

    /**
     * Ejecuta una petición HTTP GET a la URL especificada.
     */
    private static String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configuración de la conexión
        connection.setRequestMethod("GET");
        connection.setRequestProperty("accept", "application/json");

        int responseCode = connection.getResponseCode();

        // Verifica que la petición haya sido ok
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Fallo en la petición HTTP. Código: " + responseCode);
        }

        // Lee el contenido de la respuesta
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     * Muestra la información de la criptomoneda de forma formateada.
     */
    private static void displayCoinInfo(CoinData data) {
        String name = data.getName();
        // El símbolo se convierte a mayúsculas
        String symbol = data.getSymbol().toUpperCase();

        MercadoData marketData = data.getMarketData();

        if (marketData == null || marketData.getCurrentPriceMap() == null) {
            System.out.println("\nNo se encontraron datos de mercado para esta moneda.");
            return;
        }

        // Obtiene los datos clave
        double priceUsd = marketData.getCurrentPriceMap().getUsd();
        int rank = marketData.getMarketCapRank();
        double change24h = marketData.getPriceChangePercentage24h();

        // Determina el color y el signo para la variación 24h
        String color = (change24h >= 0) ? ANSI_GREEN : ANSI_RED;
        String sign = (change24h >= 0) ? "+" : "";


        System.out.printf("Nombre y Símbolo: %s (%s)\n", name, symbol);
        // Formateo del precio a dos decimales y con separadores de miles
        System.out.printf("Precio en USD: $%,.2f\n", priceUsd);
        System.out.printf("Ranking: #%d\n", rank);

        // Muestra la variación con color y el signo (+/-)
        System.out.printf("Variación 24h: %s%s%.2f%%%s\n",
                color, sign, change24h, ANSI_RESET);
    }
}