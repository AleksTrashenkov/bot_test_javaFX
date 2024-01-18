package ru.mybot.bot;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ParserWeather {

    public synchronized static String getWeather(String location) throws IOException {

        String urlWeather = null;
        String date = null;
        String temp = null;
        String feels_like_temp = null;
        String season = null;
        String condition = null;
        String temp_min = null;
        String temp_max = null;

        Geocoder geocoder = new Geocoder();

        String longitude;
        String latitude;
        int idLongitude = location.indexOf("longitude");
        longitude = location.substring(idLongitude + 10).split(",")[0];
        int idLatitude = location.indexOf("latitude");
        latitude = location.substring(idLatitude + 9).split(",")[0];

        String url = "https://api.weather.yandex.ru/v2/informers?lat=" + latitude + "&lon=" + longitude + "&lang=ru_RU";
        URL urlConn = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlConn.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-Yandex-API-Key", "cb878fae-386a-45d8-beef-e37ba353c71a");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            String json = content.toString();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);

            JSONArray province = new JSONArray();
            JSONObject forecast = (JSONObject) jsonObject.get("forecast");
            province.add(forecast.get("parts"));
            for (int i = 0; i < province.size(); i++) {
                String res = forecast.get("parts").toString();
                //System.out.println(province.get(i));
                int idTemp_max = res.indexOf("temp_max");
                if (idTemp_max == -1) {
                    temp_max = "Значение не определено";
                } else {
                    temp_max = res.substring(idTemp_max + 10).split(",")[0] + "°C";
                }
                //System.out.println(temp_max);
                int idTemp_min = res.indexOf("temp_min");
                if (idTemp_min == -1) {
                    temp_min = "Значение не определено";
                } else {
                    temp_min = res.substring(idTemp_min + 10).split(",")[0] + "°C";
                }
                //System.out.println(temp_min);
            }
            JSONArray infoWeather = new JSONArray();
            infoWeather.add(jsonObject.get("fact"));
            for (int i = 0; i < infoWeather.size(); i++) {
                String res = jsonObject.get("fact").toString();
                int idTemp = res.indexOf("\"temp\":");
                if (idTemp == -1) {
                    temp = "Не удалось определить значение";
                } else {
                    temp = res.substring(idTemp + 7).split(",")[0] + "°C";
                }
                int idSeason = res.indexOf("season");
                if (idSeason == -1) {
                    season = "Не удалось определить значение";
                } else {
                    season = res.substring(idSeason + 9).split("\"")[0];
                }
                int idCondition = res.indexOf("condition");
                if (idCondition == -1) {
                    condition = "Не удалось определить значение";
                } else {
                    condition = res.substring(idCondition + 12).split("\"")[0];
                }
                int idfeels_like_temp = res.indexOf("feels_like");
                if (idfeels_like_temp == -1) {
                    feels_like_temp = "Не удалось определить значение";
                } else {
                    feels_like_temp = res.substring(idfeels_like_temp + 12).split(",")[0] + "°C";
                }
            }
            JSONArray info = new JSONArray();
            info.add(jsonObject.get("info"));
            for (int i = 0; i < info.size(); i++) {
                JSONObject infoOb = (JSONObject) jsonObject.get("info");
                urlWeather = (String) infoOb.get("url");
            }
        } catch (final Exception ex) {
            return "Простите, похоже превышен лимит запросов к сервису на день. Завтра все снова будет работать.";
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        date = dateTimeFormatter.format(LocalDateTime.now());

        //System.out.println(urlWeather);
        switch (season) {
            case "summer": season = "лето";break;
            case "autumn": season = "осень";break;
            case "winter": season = "зима";break;
            case "spring": season = "весна";break;
        }
        switch (condition) {
            case "clear": condition = "ясно";break;
            case "partly-cloudy": condition = "малооблачно";break;
            case "cloudy": condition = "облачно с прояснениями";break;
            case "overcast": condition = "пасмурно";break;
            case "drizzle": condition = "морось";break;
            case "light-rain": condition = "небольшой дождь";break;
            case "rain": condition = "дождь";break;
            case "moderate-rain": condition = "умеренно сильный дождь";break;
            case "heavy-rain": condition = "сильный дождь";break;
            case "continuous-heavy-rain": condition = "длительный сильный дождь";break;
            case "showers": condition = "ливень";break;
            case "wet-snow": condition = "дождь со снегом";break;
            case "light-snow": condition = "небольшой снег";break;
            case "snow": condition = "снег";break;
            case "snow-showers": condition = "снегопад";break;
            case "hail": condition = "град";break;
            case "thunderstorm": condition = "гроза";break;
            case "thunderstorm-with-rain": condition = "дождь с грозой";break;
            case "thunderstorm-with-hail": condition = "гроза с градом";break;
            default: condition = "Не удалось определить";break;
        }

        return "Сейчас: " + date + " (МСК+1)" + "\n" +
                "Местоположение: " + geocoder.getGeoPosition(location) + "\n" +
                "Минимальная температура: " + temp_min + "\n" +
                "Максимальная температура: " + temp_max + "\n" +
                "Температура сейчас: " + temp + "\n" +
                "По ощущениям: " + feels_like_temp + "\n" +
                "Время года: " + season + "\n" +
                "Погода сейчас: " + condition + "\n" +
                "Подробный прогноз погоды Вы сможете узнать по ссылке: " + "\n" + urlWeather;
    }
    public static String getWeatherCity(String location) throws IOException {

        String city;
        String description;
        String temp;
        String feels_like;
        String temp_min;
        String temp_max;
        String date;
        String condition;
        String speed;
        String sunrise;
        String sunset;

        Geocoder geocoder = new Geocoder();

        String url = "https://api.openweathermap.org/data/2.5/weather?q="+geocoder.getGeoPositionCityName(location).replace("деревня ","").replace("село ","").replace("поселок ","")+"&lang=ru&appid=7b0f3768baff4595f437509ae8db4dac&units=metric";
        URL urlConn = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlConn.openConnection();
        con.setRequestMethod("GET");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            String json = content.toString();
            int idCity = json.indexOf("\"name\":\"");
            if (idCity == -1) {
                city = "Не удалось определить значение";
            } else {
                city = json.substring(idCity + 8).split("\"")[0];
            }
            int idDesc = json.indexOf("description");
            if (idDesc == -1) {
                description = "Не удалось определить значение";
            } else {
                description = json.substring(idDesc + 14).split("\"")[0];
            }
            int idTempNow = json.indexOf("\"temp\":");
            if (idTempNow == -1) {
                temp = "Не удалось определить значение";
            } else {
                temp = json.substring(idTempNow + 7).split(",")[0] + "°C";
            }
            int idFeels = json.indexOf("feels_like");
            if (idFeels == -1) {
                feels_like = "Не удалось определить значение";
            } else {
                feels_like = json.substring(idFeels + 12).split(",")[0] + "°C";
            }
            int idtTempMin = json.indexOf("temp_min");
            if (idtTempMin == -1) {
                temp_min = "Не удалось определить значение";
            } else {
                temp_min = json.substring(idtTempMin + 10).split(",")[0] + "°C";
            }
            int idTempMax = json.indexOf("temp_max");
            if (idTempMax == -1) {
                temp_max = "Не удалось определить значение";
            } else {
                temp_max = json.substring(idTempMax + 10).split(",")[0] + "°C";
            }
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
            //date = dateTimeFormatter.format(LocalDateTime.now());
            int idDate = json.indexOf("\"dt\":");
            String dateNow = json.substring(idDate + 5).split(",")[0];
            Long dateNowLo = Long.parseLong(dateNow);
            Instant instantDate = Instant.ofEpochSecond(dateNowLo);
            LocalDateTime dateNowLoc = LocalDateTime.ofInstant(instantDate, ZoneId.of("Europe/Moscow"));
            date = dateTimeFormatter.format(dateNowLoc);
            int idSpeed = json.indexOf("speed");
            if (idSpeed == -1) {
                speed = "Не удалось определить значение";
            } else {
                speed = json.substring(idSpeed + 7).split(",")[0] + " м/с";
            }
            DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
            int idSunrise = json.indexOf("sunrise");
            if (idSunrise  == -1) {
                sunrise = null;
            } else {
                String sunriseStr = json.substring(idSunrise + 9).split(",")[0];
                Long sunriseLo = Long.parseLong(sunriseStr);
                Instant instant = Instant.ofEpochSecond(sunriseLo);
                LocalTime localTime = LocalTime.ofInstant(instant, ZoneId.systemDefault());
                sunrise = dateTimeFormatterTime.format(localTime);
            }
            int idSunset = json.indexOf("sunset");
            if (idSunset == -1) {
                sunset = null;
            } else {
                String sunsetStr = json.substring(idSunset + 8).split("}")[0];
                Long sunsetLo = Long.parseLong(sunsetStr);
                Instant instantSun = Instant.ofEpochSecond(sunsetLo);
                LocalTime localTimeSun = LocalTime.ofInstant(instantSun, ZoneId.systemDefault());
                sunset = dateTimeFormatterTime.format(localTimeSun);
            }

            switch (description) {
                case "ясно": condition = "\u2600";break;
                case "малооблачно": condition = "\u26C5";break;
                case "облачно с прояснениями": condition = "\u26C5 " + "\u2600";break;
                case "переменная облачность": condition = "\u2600"+"\u26C5 ";break;
                case "небольшая облачность": condition = "\u26C5";break;
                case "пасмурно": condition = "\u2601";break;
                case "морось": condition = "\uD83D\uDCA7";break;
                case "небольшой дождь": condition = "\u2614";break;
                case "дождь": condition = "\uD83D\uDCA7"+"\uD83D\uDCA7";break;
                case "умеренно сильный дождь": condition = "\uD83D\uDCA7"+"\uD83D\uDCA7"+"\uD83D\uDCA7";break;
                case "сильный дождь": condition = "\uD83D\uDCA7"+"\uD83D\uDCA7"+"\uD83D\uDCA7"+"\uD83D\uDCA7";break;
                case "длительный сильный дождь": condition = "\u1F55\u0042"+"\uD83D\uDCA6 "+"\uD83D\uDD5F";break;
                case "ливень": condition = "\uD83D\uDCA6 " + "\uD83D\uDCA6 "+"\uD83D\uDCA6";break;
                case "дождь со снегом": condition = "\uD83D\uDCA6 " + "\u2744";break;
                case "небольшой снег": condition = "\u2744";break;
                case "снег": condition = "\u2744 " + "\u2744";break;
                case "снегопад": condition = "\u2744 " + "\u2744 " + "\u2744";break;
                case "град": condition = "\u26AA";break;
                case "гроза": condition = "\u26A1";break;
                case "дождь с грозой": condition = "\uD83D\uDCA6 " + "\u26A1";break;
                case "гроза с градом": condition = "\u26A1 " + "\u26AA";break;
                default: condition = "\u2753";break;
            }

            return "Полуторачасовая рассылка о погоде в населенном пункте: "+city+"\n"+
                    "Данные актуальны на: " + date + " (МСК)" + "\n" +
                    "Минимальная температура: " + temp_min + "\n" +
                    "Максимальная температура: " + temp_max + "\n" +
                    "Температура сейчас: " + temp + "\n" +
                    "По ощущениям: " + feels_like + "\n" +
                    "Погода сейчас: " +"\n"+ description + " " + condition + "\n"+
                    "Скорость ветра: " + speed + "\n" +
                    "Время восхода солнца: " + sunrise + "\n" +
                    "Время заката: " + sunset + "\n" +
                    "Прогноз погоды по Вашему точному местоположению, можете узнать по кнопке ниже ↓";
        }
    }
}

