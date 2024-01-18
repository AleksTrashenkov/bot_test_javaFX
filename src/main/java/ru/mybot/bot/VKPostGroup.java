package ru.mybot.bot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class VKPostGroup {
    final static String token = "vk1.a.T6WovCu7rioKwnYm7U1tx1g3cSpH_LMVLP-Isy8Ev2hgkBVxzIB5Y4ZcDX3xgXAQs9hHEHBmw22JRUAZz8p5d1OjylNkbI95j5boSwAJGtSkN9s2dINUvxkWko5smWZr31qD0a6Ve_kNK1M1QqFxK3wFnCQx4iHY4hTkxz3Ph9Lsx-1okxjwPPUfvvkDRz-IJAXlknQAE_SRzrlkWmj1pQ";
    final static String owner_id = "-45327067";
    final static String versionVk = "5.131";
    public synchronized static String getUserVK (String userName) throws IOException, InterruptedException {
        String url = "https://api.vk.com/method/users.search";

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("q", userName);
        requestBody.put("count", "5");
        requestBody.put("fields", "domain, city, bdate");
        requestBody.put("v", versionVk);
        requestBody.put("access_token", token);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildQueryParamsUser(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        Gson gson = new Gson();
        String city;
        String bdate;
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        JsonArray positionsArray = jsonObject.getAsJsonObject("response").getAsJsonArray("items");
        HashMap<String, String> users = new HashMap<>();
        for (JsonElement positionElement : positionsArray) {
            JsonObject positionObject = positionElement.getAsJsonObject();
            String domain = positionObject.get("domain").getAsString();
            String first_name = positionObject.get("first_name").getAsString();
            String last_name = positionObject.get("last_name").getAsString();
            if (positionElement.getAsJsonObject().get("bdate") != null) {
                bdate = positionElement.getAsJsonObject().get("bdate").getAsString();
            } else {
                bdate = "Не указано";
            }
            if (positionElement.getAsJsonObject().get("city") instanceof JsonObject) {
                JsonObject positionObjectCity = positionElement.getAsJsonObject().get("city").getAsJsonObject();
                city = positionObjectCity.get("title").getAsString();
            } else {
                city = "Не указано";
            }
            users.put("https://vk.com/"+domain, "Фамилия: "+last_name+"\n"+
                    "Имя: " +first_name + "\n"+
                    "Город: " + city + "\n"+
                    "Дата рождения: " +bdate);
        }
        return users.toString().replace("{","- Ссылка на страницу: ")
                .replace(",","\n- Ссылка на страницу: ")
                .replace("=", "\n")
                .replace("}","");
    }

    private static String buildQueryParamsUser(HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
    public static void setVKPostFilmsPremiere (String posterUrl, String nameFilm, String dateRelise, String genre, String country, String urlTraler) throws IOException, InterruptedException {
        String url = "https://api.vk.com/method/wall.post";
        String post = "Премьера этого месяца:";

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("from_group", "1");
        requestBody.put("owner_id", owner_id);
        requestBody.put("v", versionVk);
        requestBody.put("access_token", token);
        requestBody.put("message", post +"\n"+ "Название: " + nameFilm + "\n"+"Дата релиза: "+ dateRelise+"\n"+
                "Жанр: "+genre+"\n"+"Страна: "+country + "\n" +
                "Трейлер/тизер: " + urlTraler);
        requestBody.put("attachments", posterUrl);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildQueryParamsFilmsPremiere(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println(responseBody);
    }
    private static String buildQueryParamsFilmsPremiere(HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }

    public static void setVKPostFilmsRelise (String posterUrl, String nameFilm, String dateRelise, String genre, String country, String rating) throws IOException, InterruptedException {
        String url = "https://api.vk.com/method/wall.post";
        String post = "Цифровой релиз этого месяца:";

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("from_group", "1");
        requestBody.put("owner_id", owner_id);
        requestBody.put("v", versionVk);
        requestBody.put("access_token", token);
        requestBody.put("message", post +"\n"+ "Название: " + nameFilm + "\n"+"Дата релиза: "+ dateRelise+"\n"+
                "Жанр: "+genre+"\n"+"Страна: "+country + "\n"+
                "Рейтинг: " + rating);
        requestBody.put("attachments", posterUrl);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildQueryParamsFilmsRelise(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println(responseBody);
    }
    private static String buildQueryParamsFilmsRelise(HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
    public static void setVKPostFilmsFind (String posterUrl, String nameFilm,
                                           String dateRelise, String genre,
                                           String country, String description,
                                           String rating, String reg) throws IOException, InterruptedException {
        String url = "https://api.vk.com/method/wall.post";
        String post = "Сейчас ищут:";

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("from_group", "1");
        requestBody.put("owner_id", owner_id);
        requestBody.put("v", versionVk);
        requestBody.put("access_token", token);
        requestBody.put("message", post +"\n"+ "Название: " + nameFilm + "\n"+"Год: "+ dateRelise+"\n"+
                "Жанр: "+genre+"\n"+"Страна: "+country + "\n"+
                "Рейтинг: " + rating + "\n" +
                "Режиссер: " + reg + "\n" +
                "Описание: " + description);
        requestBody.put("attachments", posterUrl);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildQueryParamsFilmsFind(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println(responseBody);
    }

    private static String buildQueryParamsFilmsFind(HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
    public synchronized static void setVKPostFilmsNewsPeoples (String textPost, String urlPost) throws IOException, InterruptedException {
        String url = "https://api.vk.com/method/wall.post";
        if (textPost != null) {
            String post = "Новости мира кино!";

            HashMap<String, String> requestBody = new HashMap<>();
            requestBody.put("from_group", "1");
            requestBody.put("owner_id", owner_id);
            requestBody.put("v", versionVk);
            requestBody.put("access_token", token);
            requestBody.put("message", post + "\n" + textPost);
            requestBody.put("attachments", urlPost);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(buildQueryParamsFilmsNews(requestBody)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(responseBody);
        } else {
            String post = "Может быть интересна информация:";

            HashMap<String, String> requestBody = new HashMap<>();
            requestBody.put("from_group", "1");
            requestBody.put("owner_id", owner_id);
            requestBody.put("v", versionVk);
            requestBody.put("access_token", token);
            requestBody.put("message", post);
            requestBody.put("attachments", urlPost);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(buildQueryParamsFilmsNews(requestBody)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(responseBody);
        }
    }

    private static String buildQueryParamsFilmsNews(HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }

    public synchronized static void setVKPostNewsMail (String textPost, String urlPostPicture, String headPost) throws IOException, InterruptedException {
        String url = "https://api.vk.com/method/wall.post";
        String post = "Новости мира кино!" + "\n\n" + headPost + "\n";

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("from_group", "1");
        requestBody.put("owner_id", owner_id);
        requestBody.put("v", versionVk);
        requestBody.put("access_token", token);
        requestBody.put("message", post + "\n" + textPost);
        requestBody.put("attachments", urlPostPicture);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildQueryParamsNewsMail(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        System.out.println(responseBody);
    }

    private static String buildQueryParamsNewsMail (HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
}