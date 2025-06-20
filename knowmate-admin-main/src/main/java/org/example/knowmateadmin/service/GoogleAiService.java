package org.example.knowmateadmin.service;

import okhttp3.*;
import org.example.knowmateadmin.config.AiConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Optional;

@Service
public class GoogleAiService {

    @Autowired
    private AiConfig config;

    public String chat(String prompt) {
        String apiKey = config.getGoogleKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: API Key is not configured. Please set ai.googleKey in application.properties or application.yml";
        }
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key="
                + apiKey;

        JSONObject body = new JSONObject();
        JSONArray parts = new JSONArray().put(new JSONObject().put("text", prompt));
        JSONArray contents = new JSONArray().put(new JSONObject().put("parts", parts));
        body.put("contents", contents);

        System.out.println("Google AI API Request URL: " + apiUrl);
        System.out.println("Google AI API Request Body: " + body.toString());

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS);

        if (config.isProxyEnabled() && config.getProxyHost() != null && config.getProxyPort() != 0) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(config.getProxyHost(), config.getProxyPort()));
            clientBuilder.proxy(proxy);
            System.out.println("Using proxy: " + config.getProxyHost() + ":" + config.getProxyPort());
        } else {
            System.out.println("Not using proxy.");
        }

        OkHttpClient client = clientBuilder.build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = Optional.ofNullable(response.body()).map(r -> {
                try {
                    String bodyStr = r.string();
                    System.out.println("Google AI API Raw Response: " + bodyStr);
                    return bodyStr;
                } catch (IOException e) {
                    return null;
                }
            }).orElse("");

            if (!response.isSuccessful()) {
                System.err.println(
                        "Google AI API call failed. Status code: " + response.code() + ", Response: " + responseBody);
                return "Call failed: " + response.code() + " - " + responseBody;
            }
            JSONObject jsonResponse = new JSONObject(responseBody);
            if (jsonResponse.has("candidates") && !jsonResponse.getJSONArray("candidates").isEmpty()) {
                return jsonResponse
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");
            } else if (jsonResponse.has("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                return "AI Service returned an error: " + error.optString("message", "Unknown error");
            } else {
                return "AI Service returned unknown response format.";
            }
        } catch (Exception e) {
            System.err.println("Error calling Google AI API: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
    }
}