package ru.mybot.bot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Geocoder {
    private static String oblName;
    private static String city;

    public String getGeoPosition(String location) throws IOException {
        String longitude;
        String latitude;
        int idLongitude = location.indexOf("longitude");
        longitude = location.substring(idLongitude + 10).split(",")[0];
        int idLatitude = location.indexOf("latitude");
        latitude = location.substring(idLatitude + 9).split(",")[0];
        String API = "your_api_key";
        String url = "https://geocode-maps.yandex.ru/1.x/?apikey=" + API + "&geocode=" + longitude + "," + latitude + "&format=json";
        URL urlConn = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlConn.openConnection();
        con.setRequestMethod("GET");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            //System.out.println(content);
            String json = content.toString();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
            JSONObject response = (JSONObject) jsonObject.get("response");

            String res = response.toString();

            int idObl = res.indexOf("other");
            oblName = res.substring(idObl + 15).split("\"")[0];
        } catch (ParseException e) {
            return "Превышен лимит запросов, попробуйте позже.";
        }
        return oblName;
    }

    public String getGeoPositionCityName(String location) throws IOException {
        String longitude;
        String latitude;
        int idLongitude = location.indexOf("longitude");
        longitude = location.substring(idLongitude + 10).split(",")[0];
        int idLatitude = location.indexOf("latitude");
        latitude = location.substring(idLatitude + 9).split(",")[0];
        String API = "your_api_key";
        String url = "https://geocode-maps.yandex.ru/1.x/?apikey=" + API + "&geocode=" + longitude + "," + latitude + "&format=json";
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
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
            JSONObject response = (JSONObject) jsonObject.get("response");

            String res = response.toString();

            int idObl = res.indexOf("\"kind\":\"locality\",\"name\":\"");
            if (idObl != -1) {
                /*if (res.substring(idObl + 26).split("\"")[0].startsWith("деревня") ||
                        res.substring(idObl + 26).split("\"")[0].startsWith("поселок") ||
                        res.substring(idObl + 26).split("\"")[0].startsWith("село")) {
                    int idObl2 = res.indexOf("\"AdministrativeAreaName\"");
                    city = res.substring(idObl2 + 26).split("\"")[0];
                }else {*/
                    city = res.substring(idObl + 26).split("\"")[0];
                //}
            }else {
                int idObl2 = res.indexOf("\"AdministrativeAreaName\"");
                city = res.substring(idObl2 + 26).split("\"")[0];
            }
        }catch (ParseException e) {
            return "Превышен лимит запросов, попробуйте позже.";
        }
        return city;
    }
}
