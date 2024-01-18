package ru.mybot.bot;

import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.tinkoff.piapi.core.InvestApi;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class tinAPI {
    final static String API_KEY = "t.UPOmMX5uv_LubtQYsCOeohJ1qRLY-rDyC0O97_1QG4gHi0v_wqbjhxVXVQBywlkW4KGve7N53jv9G8aLGK65BQ";
    //final static String url = "https://api-invest.tinkoff.ru/openapi/sandbox";//тестовая песочница
    final static String url = "https://api-invest.tinkoff.ru/openapi/portfolio/currencies/"; //получение валютных активов клиента
    final static String urlPort = "https://api-invest.tinkoff.ru/openapi/portfolio/";//получение портфеля
    final static String urlValPar = "https://api-invest.tinkoff.ru/openapi/market/stocks";

    public static String getActionsOld() throws IOException {
        double countPort = 0;
        double countPortMin = 0;
        String currency = null;
        DecimalFormat myFormat = new DecimalFormat("#.##"); // округляем до 3-х знаков после запятой
        HashMap<String, Double> actions = new HashMap<>();
        HashMap<String, Double> valutes = new HashMap<>();
        OkHttpClient client = new OkHttpClient();
        Request requestPort = new Request.Builder()
                .url(urlPort)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();
        Request requestAct = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        Response response = client.newCall(requestPort).execute();
        String responseBodyString = response.body().string();
        //System.out.println(responseBodyString);

        Response responseAct = client.newCall(requestAct).execute();
        String responseBodyStringAct = responseAct.body().string();
        //System.out.println(responseBodyStringAct);

        // создание объекта Gson
        Gson gson = new Gson();
// парсинг JSON-строки в объект JsonObject
        JsonObject jsonObject = gson.fromJson(responseBodyString, JsonObject.class);
        JsonObject jsonObjectAct = gson.fromJson(responseBodyStringAct, JsonObject.class);

// получение массива объектов "positions"
        JsonArray positionsArray = jsonObject.getAsJsonObject("payload").getAsJsonArray("positions");
        JsonArray positionsArrayAct = jsonObjectAct.getAsJsonObject("payload").getAsJsonArray("currencies");
// итерация по массиву объектов "positions"
        double usd = 0;
        for (JsonElement positionElement : positionsArray) {
            // преобразование элемента массива в объект JsonObject
            JsonObject positionObject = positionElement.getAsJsonObject();
            // получение значения поля "averagePositionPrice"
            JsonObject avgPositionPriceObject = positionObject.getAsJsonObject("averagePositionPrice");
            JsonObject expectedYield = positionObject.getAsJsonObject("expectedYield");
            // получение значения поля "balance"
            double balance = positionObject.get("balance").getAsDouble();
            int balanceInt = positionObject.get("balance").getAsInt();
            currency = avgPositionPriceObject.get("currency").getAsString();
            double valueAver = avgPositionPriceObject.get("value").getAsDouble();
            double valueExpe = expectedYield.get("value").getAsDouble();
            countPortMin = countPortMin + valueExpe;
            String name = positionObject.get("name").getAsString() + " ("+balanceInt+" шт.)";
            String ticker = positionObject.get("ticker").toString().replace("\"", "");
            if (ticker.equals("USD000UTSTOM")) {
                usd = (balance * valueAver) + valueExpe;
            } else {
                countPort = countPort + (balance * valueAver);
            }
            actions.put(name, valueExpe);
        }
        double rub = 0;
        for (JsonElement jsonElement : positionsArrayAct) {
            JsonObject positionObject = jsonElement.getAsJsonObject();
            String currenc = positionObject.get("currency").getAsString();
            if (currenc.equals("RUB")) {
                rub = positionObject.get("balance").getAsDouble();
            }
            Double balance = positionObject.get("balance").getAsDouble();
            valutes.put(currenc, balance);
        }
        return "Статистика по Вашим активам:\n" +
                "Было вложено: "+myFormat.format(countPort + usd + rub) + " " + currency +"\n"+
                "Активы просели на: "+ myFormat.format(countPortMin) + " " + currency +"\n"+
                "Текущая стоимость: "+ myFormat.format((countPort + countPortMin + rub + usd)) + " " + currency +"\n"+
                "Данные по Вашим акциям: \n"+
                actions.toString().replace("{", "").replace("}", " руб.").replace(","," руб.\n").replace("=", " : ") +"\n"+
                "Ваши валютные данные: \n"+
                valutes.toString().replace("{", "").replace(",", "\n").replace("}", "").replace("=", " : ");
    }
    public static String getActions() throws IOException, ParseException {
        var api = InvestApi.create(API_KEY);

        var valNow = api.getOperationsService().getPortfolio("2073583573").join().getTotalAmountPortfolio().getValue();
        var orderValMin = api.getOperationsService().getPortfolio("2073583573").join().getPositions();
        var orderValMon = api.getOperationsService().getWithdrawLimits("2073583573").join();
        var orderInstrumentsService = api.getInstrumentsService();
        String currency;
        String currencyRub = null;
        String currencyUSD = null;
        String currencuOtherStr;
        HashMap<String, LocalDate> divNow = new HashMap<>();
        HashMap<String, LocalDate> bondsCupNow = new HashMap<>();
        double usdVal = 0;
        double rubVal = 0;
        double otherVal;
        HashMap<String, Double> currencyOther = new HashMap<>();
        DecimalFormat myFormat = new DecimalFormat("#.##"); // округляем до 3-х знаков после запятой
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date startDate = dateFormat.parse(LocalDateTime.now().minusMonths(5).toString());
        Date toDate = dateFormat.parse(LocalDateTime.now().plusDays(30).toString());
        for (var orderVal : api.getOperationsService().getPortfolio("2073583573").join().getPositions()) {
            if (orderVal.getInstrumentType().equals("share")) {
                for (var orderShare : orderInstrumentsService.getDividends(orderVal.getFigi(), startDate.toInstant(), toDate.toInstant()).join()) {
                    Instant instant = Instant.ofEpochSecond(orderShare.getLastBuyDate().getSeconds());
                    LocalDate localTime = LocalDate.ofInstant(instant, ZoneId.systemDefault());
                    if (ChronoUnit.DAYS.between(localTime,LocalDate.now()) < 0) {
                        divNow.put(orderInstrumentsService.getShareByFigi(orderVal.getFigi()).join().getName(), localTime);
                    }}}else if (orderVal.getInstrumentType().equals("bond")) {
                for (var orderBond : orderInstrumentsService.getBondCoupons(orderVal.getFigi(), startDate.toInstant(), toDate.toInstant()).join()) {
                    Instant instant = Instant.ofEpochSecond(orderBond.getCouponDate().getSeconds());
                    LocalDate localTime = LocalDate.ofInstant(instant, ZoneId.systemDefault());
                    if (ChronoUnit.DAYS.between(localTime,LocalDate.now()) < 0) {
                        bondsCupNow.put(api.getInstrumentsService().getBondByFigi(orderVal.getFigi()).join().getName(), localTime);
                    }}}}
        for (var orderVal : orderValMon.getMoney()) {
            currency = orderVal.getCurrency();
            switch (currency) {
                case "rub" : rubVal = Double.parseDouble(orderVal.getValue().toString());
                    currencyRub = currency;
                    break;
                case "usd" : usdVal = Double.parseDouble(orderVal.getValue().toString());
                    currencyUSD = currency;
                    break;
                default: otherVal = Double.parseDouble(orderVal.getValue().toString());
                    currencyOther.put(orderVal.getCurrency(),otherVal);
                    break;
            }}
        double valMin = 0;
        for (var orderValPos : orderValMin) {
            valMin += Double.parseDouble(orderValPos.getExpectedYield().toString());
        }
        if (currencyOther == null || currencyOther.isEmpty()) {
            currencuOtherStr = "Пока нет, но скоро Вы их купите";
        }else {
            currencuOtherStr = currencyOther.toString().replace("{", "").replace("}", "").replace("=", " : ").replace(",", "\n");
        }

        if (divNow.isEmpty()){
            divNow.put("На сегодня такие отсутствуют.", null);
        }
        if (bondsCupNow.isEmpty()){
            bondsCupNow.put("На сегодня такие отсутствуют.", null);
        }
        return "Статистика Ваших активов: "+"\n"+
                "Портфель просел/подрос: "+myFormat.format(valMin) + "\n" +
                "Текущая стоимость портфеля: "+myFormat.format(valNow) + "\n" +
                "Было вложено в портфель: " + myFormat.format(- valMin + Double.parseDouble(valNow.toString())) + "\n" +
                "Валюты: " + "\n" + myFormat.format(usdVal) + " " + currencyUSD + "\n" + myFormat.format(rubVal) + " " + currencyRub + "\n" +
                "Другие валюты: " + "\n" + currencuOtherStr + "\n" +
                /*"Ближайшая дата выплаты дивидендов: " + "\n" + divNow.toString().replace("{","").replace("}","").replace("=",": ").replace(",","\n").replace(": null","") + "\n"+
                "Дата ближайшей выплаты купонов: " + "\n" + bondsCupNow.toString().replace("{", "").replace("}","")
                .replace(",","\n").replace("=",": ").replace(": null",""+ "\n"+*/
                        "Счет от Линетского: ".toUpperCase() +"\n"+
                        getActiveLitn();
    }
    public static String getActiveLitn() throws ParseException {
        var api = InvestApi.create(API_KEY);

        var valNow = api.getOperationsService().getPortfolio("2062013289").join().getTotalAmountPortfolio().getValue();
        var orderValMin = api.getOperationsService().getPortfolio("2062013289").join().getPositions();
        var orderValMon = api.getOperationsService().getWithdrawLimits("2062013289").join();
        var orderInstrumentsService = api.getInstrumentsService();
        String currency;
        String currencyRub = null;
        String currencyUSD = null;
        String currencuOtherStr;
        HashMap<String, LocalDate> divNow = new HashMap<>();
        HashMap<String, LocalDate> bondsCupNow = new HashMap<>();
        double usdVal = 0;
        double rubVal = 0;
        double otherVal;
        HashMap<String, Double> currencyOther = new HashMap<>();
        DecimalFormat myFormat = new DecimalFormat("#.##"); // округляем до 3-х знаков после запятой
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date startDate = dateFormat.parse(LocalDateTime.now().minusMonths(5).toString());
        Date toDate = dateFormat.parse(LocalDateTime.now().plusDays(30).toString());
        for (var orderVal : api.getOperationsService().getPortfolio("2073583573").join().getPositions()) {
            if (orderVal.getInstrumentType().equals("share")) {
                for (var orderShare : orderInstrumentsService.getDividends(orderVal.getFigi(), startDate.toInstant(), toDate.toInstant()).join()) {
                    Instant instant = Instant.ofEpochSecond(orderShare.getLastBuyDate().getSeconds());
                    LocalDate localTime = LocalDate.ofInstant(instant, ZoneId.systemDefault());
                    if (ChronoUnit.DAYS.between(localTime,LocalDate.now()) < 0) {
                        divNow.put(orderInstrumentsService.getShareByFigi(orderVal.getFigi()).join().getName(), localTime);
                    }}}else if (orderVal.getInstrumentType().equals("bond")) {
                for (var orderBond : orderInstrumentsService.getBondCoupons(orderVal.getFigi(), startDate.toInstant(), toDate.toInstant()).join()) {
                    Instant instant = Instant.ofEpochSecond(orderBond.getCouponDate().getSeconds());
                    LocalDate localTime = LocalDate.ofInstant(instant, ZoneId.systemDefault());
                    if (ChronoUnit.DAYS.between(localTime,LocalDate.now()) < 0) {
                        bondsCupNow.put(api.getInstrumentsService().getBondByFigi(orderVal.getFigi()).join().getName(), localTime);
                    }}}}
        for (var orderVal : orderValMon.getMoney()) {
            currency = orderVal.getCurrency();
            switch (currency) {
                case "rub" : rubVal = Double.parseDouble(orderVal.getValue().toString());
                    currencyRub = currency;
                    break;
                case "usd" : usdVal = Double.parseDouble(orderVal.getValue().toString());
                    currencyUSD = currency;
                    break;
                default: otherVal = Double.parseDouble(orderVal.getValue().toString());
                    currencyOther.put(orderVal.getCurrency(),otherVal);
                    break;
            }}
        double valMin = 0;
        for (var orderValPos : orderValMin) {
            valMin += Double.parseDouble(orderValPos.getExpectedYield().toString());
        }
        if (currencyOther == null || currencyOther.isEmpty()) {
            currencuOtherStr = "Пока нет, но скоро Вы их купите";
        }else {
            currencuOtherStr = currencyOther.toString().replace("{", "").replace("}", "").replace("=", " : ").replace(",", "\n");
        }
        if (usdVal == 0) {
            currencyUSD = "usd";
        }

        if (divNow.isEmpty()){
            divNow.put("На сегодня такие отсутствуют.", null);
        }
        if (bondsCupNow.isEmpty()){
            bondsCupNow.put("На сегодня такие отсутствуют.", null);
        }
        return "Статистика Ваших активов: "+"\n"+
                "Портфель просел/подрос: "+myFormat.format(valMin) + "\n" +
                "Текущая стоимость портфеля: "+myFormat.format(valNow) + "\n" +
                "Было вложено в портфель: " + myFormat.format(- valMin + Double.parseDouble(valNow.toString())) + "\n" +
                "Валюты: " + "\n" + myFormat.format(usdVal) + " " + currencyUSD + "\n" + myFormat.format(rubVal) + " " + currencyRub + "\n" +
                "Другие валюты: " + "\n" + currencuOtherStr  + "\n" + "______________________" + "\n" +
                "Ближайшая дата выплаты дивидендов: " + "\n" + divNow.toString().replace("{","").replace("}","").replace("=",": ").replace(",","\n").replace(": null","") + "\n"+
                "Дата ближайшей выплаты купонов: " + "\n" + bondsCupNow.toString().replace("{", "").replace("}","")
                .replace(",","\n").replace("=",": ").replace(": null","");
    }
}
