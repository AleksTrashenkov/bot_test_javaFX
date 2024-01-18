package ru.mybot.bot;

import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Stations {
    public synchronized static String getStations(String location) throws IOException, ParseException {
        String longitude;
        String latitude;
        DecimalFormat myFormat = new DecimalFormat("#.##"); // округляем до 3-х знаков после запятой
        HashMap<String, String> stations = new HashMap<>();
        int idLongitude = location.indexOf("longitude");
        longitude = location.substring(idLongitude + 10).split(",")[0];
        int idLatitude = location.indexOf("latitude");
        latitude = location.substring(idLatitude + 9).split(",")[0];
        String API = "your_key_api";
        String url = "https://api.rasp.yandex.net/v3.0/nearest_stations/?apikey=" + API + "&distance=5&transport_types=train" + "&lat=" + latitude + "&lng=" + longitude + "&lang=ru_RU";
        URL urlConn = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlConn.openConnection();
        con.setRequestMethod("GET");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String content = in.readLine();
            String name = null;
            String resUrl = null;
            double distance;
            String[] words = content.replace(",\"title\":\"","").split("\"type\":\"station\"");
            ArrayList<String> listStation = new ArrayList<>(Arrays.asList(words));
            for (int i = 1; i < listStation.size(); i++) {
                name = listStation.get(i).split("\",\"")[0];
                int idTrain = listStation.get(i).indexOf("touch_url");
                resUrl = listStation.get(i).substring(idTrain + 12).split("\"}")[0];
                int idDistance = listStation.get(i).indexOf("distance");
                distance = Double.parseDouble(listStation.get(i).substring(idDistance + 10).split(",\"")[0]);
                stations.put("- Название: "+name, "Расстояние до станции: "+myFormat.format(distance)+" км.\n"+
                        "Ссылка для просмотра расписания: " +resUrl);
            }
        }
        return stations.toString().replace("{","").replace("}","")
                .replace(", ","\n").replace("=","\n");
    }
}
