package ru.mybot.bot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Servise {
    private static String limits;
    private static String valuesLimits;

    public static String getLimits() throws IOException {

        URL urlLim = new URL("https://api-developer.tech.yandex.net/projects/code_project/services/pogoda/limits");
        HttpURLConnection conLim = (HttpURLConnection) urlLim.openConnection();
        conLim.setRequestMethod("GET");
        conLim.setRequestProperty("X-Auth-Key", "ypur_key");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(conLim.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            String jsonLim = content.toString();

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonLim);
            JSONArray limitsArray = new JSONArray();
            JSONObject limits1 = (JSONObject) jsonObject.get("limits");
            limitsArray.add(limits1);
            for (int i = 0; i < limitsArray.size(); i++) {
                JSONObject pogoda_hits_daily = (JSONObject) limits1.get("pogoda_hits_daily");
                int idLimit = pogoda_hits_daily.toString().indexOf("limit");
                limits = pogoda_hits_daily.toString().substring(idLimit + 7).split(",")[0];
                int idValue = pogoda_hits_daily.toString().indexOf("value");
                valuesLimits = pogoda_hits_daily.toString().substring(idValue + 7).split("}")[0];
            }
        } catch (final Exception ex) {
            return "Извините, что-то пошло не так.";
        }
        return "Запрошено: " + valuesLimits + " из " + limits;
    }
}
