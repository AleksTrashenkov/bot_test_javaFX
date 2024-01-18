package ru.mybot.bot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class ZodiacInform {
    private static String zodiac;
    public String getZodiac (String zodiacSign) throws IOException {

        switch (zodiacSign) {
            case "Овен" : zodiac = "aries"; break;
            case "Телец" : zodiac = "taurus"; break;
            case "Близнецы" : zodiac = "gemini"; break;
            case "Рак" : zodiac = "cancer"; break;
            case "Лев" : zodiac = "leo"; break;
            case "Дева" : zodiac = "virgo"; break;
            case "Весы" : zodiac = "libra"; break;
            case "Скорпион" : zodiac = "scorpio"; break;
            case "Стрелец" : zodiac = "sagittarius"; break;
            case "Козерог" : zodiac = "capricorn"; break;
            case "Водолей" : zodiac = "aquarius"; break;
            case "Рыбы" : zodiac = "pisces"; break;
        }
        Document document = Jsoup.connect("https://horo.mail.ru/prediction/"+zodiac+"/today/")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        String element = document.getElementsByClass("article__item article__item_alignment_left article__item_html").text();
        //System.out.println("Гороскоп на сегодня для: " + zodiacSign + "\n" +element);
        return "Гороскоп на сегодня для: " + zodiacSign + "\n" + element;
    }
    public HashMap<Integer, String> getInfoDay () throws IOException {
        int count = 1;
        HashMap<Integer, String> dayNow = new HashMap<>();
        Document documentFact = Jsoup.connect("https://www.calend.ru/events/")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elements = documentFact.getElementsByClass("three-three");
        for (Element elementFact : elements) {
            String url = elementFact.getAllElements().attr("href");
            String year = elementFact.getElementsByClass("year_on_img ").text();
            String yearSil = elementFact.getElementsByClass("year_on_img silver").text();
            String elementStr = elementFact.getElementsByClass("title").text();
            dayNow.put(count, year + yearSil + " г. " + elementStr + "\n"+ url);
            count++;
        }return dayNow;
    }}
