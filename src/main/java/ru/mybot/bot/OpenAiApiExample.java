package ru.mybot.bot;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;

public class OpenAiApiExample {
    private static final String API_KEY = "your_key";

    // URL для отправки запросов к OpenAI
    private static final String OPENAI_URL = "https://api.openai.com/v1/engines/text-davinci-003/completions";
    //https://api.openai.com/v1/models/text-davinci-003

    public synchronized static String getResponse (String answer) throws IOException, InterruptedException {
        String Result = null;
        String apiKey = "Bearer "+API_KEY;

        // Темы для генерации ответов
        String[] topics = {answer};

        // Настройки для генерации ответов
        int maxTokens = 4000;
        double temperature = 0.5;

        // Создаем объект HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Отправляем запросы на генерацию ответов для каждой темы
        for (String topic : topics) {
            String requestBody = "{\"prompt\": \"" + topic + "\", \"temperature\": " + temperature + ", \"max_tokens\": " + maxTokens + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL/*"https://api.openai.com/v1/models/text-davinci-003/completions"*/))
                    .header("Content-Type", "application/json")
                    .header("Authorization", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

/*            System.out.println("Question: " + topic);
            System.out.println("Answer: " + parseAnswer(responseBody));
            System.out.println();*/
            int endIndex = responseBody.indexOf(":[{\"text\":\"");
            Result = responseBody.substring(endIndex + 10).replace("\"\\n\\n","").replace("\n","\n").replace("\"?\\n\\n","").split("\",\"index\"")[0];
        }
        return Result;
    }
}
