package com.mks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CollectPayment {
    private static final String CONFIG_FILE = "config.json";
    private static final String[] REQUIRED_FIELDS = {"tokenid", "apikey", "service"};
    private static Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();

    private static JsonObject makePostRequest(String url, Map<String, Object> data, String token) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            formBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + token)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorMessage = response.body().string();
                throw new IOException("Erreur : " + errorMessage);
            }

            String responseData = response.body().string();
            return JsonParser.parseString(responseData).getAsJsonObject();
        }
    }

    private static JsonObject PostRequest(String url, Map<String, String> data) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            formBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorMessage = response.body().string();
                throw new IOException("Erreur : " + errorMessage);
            }

            String responseData = response.body().string();
            return JsonParser.parseString(responseData).getAsJsonObject();
        }
    }

    private static JsonObject makeGetRequest(String url, String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorMessage = response.body().string();
                throw new IOException("Erreur : " + errorMessage);
            }

            String responseData = response.body().string();
            return JsonParser.parseString(responseData).getAsJsonObject();
        }
    }

    public static JsonObject initialize(int amount, String customData) throws IOException {
        if (!Files.exists(Paths.get(CONFIG_FILE))) {
            throw new IOException("Le fichier 'config.json' de configuration est manquant. Consulter la documentation afin d'obtenir ce fichier.");
        }

        Gson gson = new Gson();
        JsonObject config = gson.fromJson(new FileReader(CONFIG_FILE), JsonObject.class);

        for (String field : REQUIRED_FIELDS) {
            if (!config.has(field)) {
                throw new IOException("Le champ '" + field + "' est manquant dans le fichier de configuration.");
            }
        }

        String url = "https://kaliapay.com/api/generate-mobpay-qrcode/";

        // Construction des données au format x-www-form-urlencoded
        Map<String, Object> data = new HashMap<>();
        data.put("apikey", config.get("apikey").getAsString());
        data.put("service", config.get("service").getAsString());
        data.put("amount", amount);
        data.put("custom_data", customData);

        return makePostRequest(url, data, config.get("tokenid").getAsString());
    }

    public static JsonObject getTransactionStatus(String reference) throws IOException {
        if (!Files.exists(Paths.get(CONFIG_FILE))) {
            throw new IOException("Le fichier 'config.json' de configuration est manquant. Consulter la documentation afin d'obtenir ce fichier.");
        }

        Gson gson = new Gson();
        JsonObject config = gson.fromJson(new FileReader(CONFIG_FILE), JsonObject.class);

        for (String field : REQUIRED_FIELDS) {
            if (!config.has(field)) {
                throw new IOException("Le champ '" + field + "' est manquant dans le fichier de configuration.");
            }
        }

        String url = "https://kaliapay.com/api/get-express-transaction-details/" + reference + "/";
        return makeGetRequest(url, config.get("tokenid").getAsString());
    }

    public static JsonObject getConfigFile(String username, String password) throws IOException {
        String url = "https://kaliapay.com/api/signin-users/";

        // Construction des données au format x-www-form-urlencoded
        Map<String, String> data = new HashMap<>();
        data.put("user", username);
        data.put("password", password);

        try {
            JsonObject response = PostRequest(url, data);
            if (response.has("result") && response.get("result").getAsJsonObject().has("tid")) {
                String tid = response.get("result").getAsJsonObject().get("tid").getAsString();
                String apiKey = response.get("result").getAsJsonObject().get("apikey").getAsString();

                JsonObject configData = new JsonObject();
                configData.addProperty("tokenid", tid);
                configData.addProperty("apikey", apiKey);
                configData.addProperty("service", "");

                Files.write(Paths.get(CONFIG_FILE), gson.toJson(configData).getBytes());

                return response;
            } else {
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("Erreur", "Vérifiez vos accès !");
                return errorResponse;
            }
        } catch (IOException e) {
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("Erreur", "Vérifiez votre connexion internet");
            return errorResponse;
        }
    }
}
