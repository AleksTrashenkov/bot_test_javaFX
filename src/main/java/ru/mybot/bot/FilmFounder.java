package ru.mybot.bot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.IOException;

public class FilmFounder {
    public static String getFilm(String passWord, String dr_path) throws IOException, InterruptedException {
            int str = 0;
            int number = 0;
            String result;
            String request = null;
/*        System.setProperty("webdriver.chrome.driver", dr_path);
        WebDriver webDriver = new ChromeDriver();*/
        System.setProperty("webdriver.edge.driver", dr_path);
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--remote-allow-origins=*");
            WebDriver webDriver = new EdgeDriver(options);
            Document document;
            for (int i = 0; i < 80; i++) {
                result = null;
                webDriver.get("https://torrent.aqproject.ru/torrent/browse?offset=" + number);
                document = Jsoup.parse(webDriver.getPageSource());
                Elements elements = document.getElementsByClass("torrent-browse-name");

                for (Element element : elements) {
                    int idWord = element.text().indexOf("Прикреплен:");
                    if (element.text().replace("«","").replace("»","").replace("ё", "е").replace("Ё", "Е")
                            .replace(": "," ").replace("-"," ").replace(", ", " ").replace("!", "").replace(". ", " ").toLowerCase().startsWith(passWord.toLowerCase())
                            || element.text().replace("«","").replace("»","").replace("ё", "е").replace("Ё", "Е")
                            .replace(": "," ").replace("-"," ").replace(", ", " ").replace("!", "")
                            .replace(". ", " ").substring(idWord + 12).toLowerCase().startsWith(passWord.toLowerCase())) {
                        result = element.text() + " " + "https://torrent.aqproject.ru" + element.attr("href");
                        str = i;
                        request = "Страница: " + str + "\n" + result;
                    }
                }
                if (result != null) {
                    webDriver.quit();
                    break;
                } else {
                    number += 20;
                }
            }
            webDriver.quit();
            return request;
    }
}
