package ru.mybot.bot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FilmInformer {
    static VKPostGroup vkPostGroup = new VKPostGroup();
    public synchronized static String getFilmInfo(String filmName) throws IOException, InterruptedException {

        String encoded = URLEncoder.encode(filmName, "UTF-8");
        ArrayList<String> resultListFilms = new ArrayList<>();
        URL url = new URL("https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword?keyword=" + encoded);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-API-KEY", "your_api_key");
        con.setRequestProperty("Content-Type", "application/json");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String jsonLim = in.readLine();

            String[] films = jsonLim.split("\"filmId\":");
            ArrayList<String> listFilms = new ArrayList<>(Arrays.asList(films));
            int count;
            if (listFilms.size() <= 3) {
                count = listFilms.size();
            }else  {
                count = 4;
            }
            String name;
            String description;
            String reg;
            String filmID;
            for (int i = 1; i < count; i++) {
                int idNameRu = listFilms.get(i).indexOf("nameRu");
                if (idNameRu != -1) {
                    name = listFilms.get(i).substring(idNameRu + 9).split("\",\"")[0];
                }else {
                    int idNameEn = listFilms.get(i).indexOf("nameEn");
                    name = listFilms.get(i).substring(idNameEn + 9).split("\",\"")[0];
                }
                int idYear = listFilms.get(i).indexOf("year");
                String year = listFilms.get(i).substring(idYear + 7).split("\",\"")[0];
                int idDescription = listFilms.get(i).indexOf("description");
                if (idDescription != -1) {
                    description = listFilms.get(i).substring(idDescription + 14).replace("u\":\"", "").split("\",\"")[0];
                } else {
                    description = "Описания пока нет.";
                }
                int idCountry = listFilms.get(i).indexOf("countries\":[{\"country\":\"");
                String country = listFilms.get(i).substring(idCountry + 24).split("\"},")[0].split("\"}]")[0];
                int idGenre = listFilms.get(i).indexOf("genres\":[{\"genre\":\"");
                String genre = listFilms.get(i).substring(idGenre + 19).split("\"")[0];
                int idRating = listFilms.get(i).indexOf("\"rating\"");
                String rating = listFilms.get(i).substring(idRating + 10).split("\",")[0];
                int idPosterUrl = listFilms.get(i).indexOf("\"posterUrl\":\"");
                String posterUrl = listFilms.get(i).substring(idPosterUrl + 13).split("\",")[0];

                filmID = listFilms.get(i).split(",")[0];
                //System.out.println(filmID);
                URL urlFilmID = new URL("https://kinopoiskapiunofficial.tech/api/v1/staff?filmId="+filmID);
                HttpURLConnection conFilmId = (HttpURLConnection) urlFilmID.openConnection();
                conFilmId.setRequestMethod("GET");
                conFilmId.setRequestProperty("X-API-KEY", "your_api_key");
                conFilmId.setRequestProperty("Content-Type", "application/json");
                try (final BufferedReader inFilmID = new BufferedReader(new InputStreamReader(conFilmId.getInputStream()))) {
                    String jsonLimFilmID = inFilmID.readLine();
                    int idNameReg = jsonLimFilmID.indexOf("nameRu");
                    if (idNameReg == -1) {
                        reg = "Неизвестен.";
                    } else {
                        reg = jsonLimFilmID.substring(idNameReg + 9).split("\"")[0];
                        if (reg.isEmpty()) {
                            int idNameRegEng = jsonLimFilmID.indexOf("nameEn");
                            reg = jsonLimFilmID.substring(idNameRegEng + 9).split("\"")[0];
                        }
                    }
                }

                resultListFilms.add(i + ". Название: " + name + "\n" +
                        "Год: " + year + "\n" +
                        "Описание: " + description + "\n" +
                        "Страна: " + country + "\n" +
                        "Жанр: " + genre + "\n" +
                        "Рейтинг: " + rating + "\n"+
                        "Режиссер: "+reg + "\n");
                vkPostGroup.setVKPostFilmsFind(posterUrl, name, year, genre, country, description, rating, reg);
            }
            return resultListFilms.toString().replace("[", "").replace("]", "").replace(",", "");
        }}
    public synchronized static String getPeoples (String name) throws IOException, InterruptedException {
        String encoded = URLEncoder.encode(name, "UTF-8");
        HashMap<String, String> listPeople = new HashMap<>();
        URL url = new URL("https://kinopoiskapiunofficial.tech/api/v1/persons?name=" + encoded);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-API-KEY", "your_api_key");
        con.setRequestProperty("Content-Type", "application/json");
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String jsonLim = in.readLine();
            if (jsonLim.equals("{\"total\":0,\"items\":[]}")) {
                listPeople.put("Не найдено","Не найдено");
            } else {
                int idName = jsonLim.indexOf("nameRu");
                String namePeople = jsonLim.substring(idName + 9).split("\",")[0];
                int idUrl = jsonLim.indexOf("webUrl");
                String urls = jsonLim.substring(idUrl + 9).split("\",")[0];
                listPeople.put(namePeople, urls);
                vkPostGroup.setVKPostFilmsNewsPeoples(null, urls);
            }
        }
        return listPeople.toString().replace("{","Имя: ").replace("}","").replace("=","\nСсылка с информацией: \n");
    }
    public static void setPremierPostVK (int year, int monthInt) throws IOException, InterruptedException {
        String month = null;
        switch (monthInt) {
            case 1:
                month = "JANUARY";
                break;
            case 2:
                month = "FEBRUARY";
                break;
            case 3:
                month = "MARCH";
                break;
            case 4:
                month = "APRIL";
                break;
            case 5:
                month = "MAY";
                break;
            case 6:
                month = "JUNE";
                break;
            case 7:
                month = "JULY";
                break;
            case 8:
                month = "AUGUST";
                break;
            case 9:
                month = "SEPTEMBER";
                break;
            case 10:
                month = "OCTOBER";
                break;
            case 11:
                month = "NOVEMBER";
                break;
            case 12:
                month = "DECEMBER";
                break;
        }
        URL url = new URL("https://kinopoiskapiunofficial.tech/api/v2.2/films/premieres?year=" + year + "&month=" + month);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-API-KEY", "your_api_key");
        con.setRequestProperty("Content-Type", "application/json");
        String jsonLim;
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            jsonLim = in.readLine();
        }
        String[] films = jsonLim.split("\"kinopoiskId\":");
        ArrayList<String> listFilms = new ArrayList<>(Arrays.asList(films));
        for (int i = 1; i < listFilms.size(); i++) {
            int idNameRu = listFilms.get(i).indexOf("nameRu");
            int idPosterUrl = listFilms.get(i).indexOf("posterUrl");
            int idPremiereRu = listFilms.get(i).indexOf("premiereRu");
            int idGenre = listFilms.get(i).indexOf("[{\"genre");
            int idCountry = listFilms.get(i).indexOf("[{\"country\":\"");
            String filmID = listFilms.get(i).split(",")[0];
            String nameRu = listFilms.get(i).substring(idNameRu + 9).split("\",\"nameEn\"")[0];
            String posterUrl = listFilms.get(i).substring(idPosterUrl + 12).split("\",\"posterUrlPreview\"")[0];
            String premiereRu = listFilms.get(i).substring(idPremiereRu + 13).split("\"}")[0];
            String genre = listFilms.get(i).substring(idGenre + 11).split("\"},")[0].split("\"}],")[0];
            String country = listFilms.get(i).substring(idCountry + 13).split("\"},")[0].split("\"}]")[0];
            URL urlFilms = new URL("https://kinopoiskapiunofficial.tech/api/v2.2/films/" + filmID + "/videos");
            HttpURLConnection conFilm = (HttpURLConnection) urlFilms.openConnection();
            conFilm.setRequestMethod("GET");
            conFilm.setRequestProperty("X-API-KEY", "your_api_key");
            conFilm.setRequestProperty("Content-Type", "application/json");
            try (final BufferedReader inFilm = new BufferedReader(new InputStreamReader(conFilm.getInputStream()))) {
                String jsonLimFilmID = inFilm.readLine();
                int urlID = jsonLimFilmID.indexOf("url");
                String urlTraler;
                if (urlID == -1) {
                    urlTraler = "Трейлера нет";
                } else {
                    urlTraler = jsonLimFilmID.substring(urlID + 6).split("\",")[0];
                }
                vkPostGroup.setVKPostFilmsPremiere(posterUrl, nameRu, premiereRu, genre, country, urlTraler);
                Thread.sleep(1000);
            }
            listFilms.clear();
        }
    }
    public static void setRelisePostVK (int year, int monthInt) throws IOException, InterruptedException {
        String month = null;
        switch (monthInt) {
            case 1 : month = "JANUARY";
                break;
            case 2 : month = "FEBRUARY";
                break;
            case 3 : month = "MARCH";
                break;
            case 4 : month = "APRIL";
                break;
            case 5 : month = "MAY";
                break;
            case 6 : month = "JUNE";
                break;
            case 7 : month = "JULY";
                break;
            case 8 : month = "AUGUST";
                break;
            case 9 : month = "SEPTEMBER";
                break;
            case 10 : month = "OCTOBER";
                break;
            case 11 : month = "NOVEMBER";
                break;
            case 12 : month = "DECEMBER";
                break;
        }
        URL url = new URL("https://kinopoiskapiunofficial.tech/api/v2.1/films/releases?year="+year+"&month="+month);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-API-KEY", "your_api_key");
        con.setRequestProperty("Content-Type", "application/json");
        String jsonLim;
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            jsonLim = in.readLine();
        }
        String [] films = jsonLim.split("\"filmId\":");
        ArrayList<String> listFilms = new ArrayList<>(Arrays.asList(films));
        for (int i = 1; i < listFilms.size(); i++) {
            int idNameRu = listFilms.get(i).indexOf("nameRu");
            int idPosterUrl = listFilms.get(i).indexOf("posterUrl");
            int idReleaseDate = listFilms.get(i).indexOf("releaseDate");
            int idGenre = listFilms.get(i).indexOf("[{\"genre");
            int idCountry = listFilms.get(i).indexOf("[{\"country\":\"");
            int idRating = listFilms.get(i).indexOf("\"rating\":");
            String nameRu = listFilms.get(i).substring(idNameRu + 9).split("\",\"nameEn\"")[0];
            String posterUrl = listFilms.get(i).substring(idPosterUrl + 12).split("\",\"posterUrlPreview\"")[0];
            String releaseDate = listFilms.get(i).substring(idReleaseDate + 14).split("\"}")[0];
            String genre = listFilms.get(i).substring(idGenre + 11).split("\"},")[0].split("\"}],")[0];
            String country = listFilms.get(i).substring(idCountry + 13).split("\"},")[0].split("\"}]")[0];
            String rating = listFilms.get(i).substring(idRating + 9).split(",\"")[0];
            vkPostGroup.setVKPostFilmsRelise(posterUrl, nameRu, releaseDate, genre, country , rating);
            Thread.sleep(1000);
        }
        listFilms.clear();
    }
    public static void setNewsFilmDay () throws IOException, InterruptedException{
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dateNow = LocalDate.parse(LocalDate.now().toString());
        Document documentNews = Jsoup.connect("https://www.film.ru/topic/news")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elementsNews = documentNews.getElementsByClass("redesign_topic");
        for (Element elementNews : elementsNews) {
            //System.out.println(elementNews.text().replace("Новости","\n"));
            //System.out.println("https://www.film.ru"+elementNews.attr("onclick").replace("location.href='","").split("';")[0]);
            String url = "https://www.film.ru"+elementNews.attr("onclick").replace("location.href='","").split("';")[0];
            getInfo(url);
                /*Elements elementsPost = documentPost.getElementsByClass("content_div text title");
                for (Element elementPost : elementsPost) {
                    LocalDate StartDate = LocalDate.parse(elementPost.getElementsByClass("author").text(), dateTimeFormatter);
                    //System.out.println(StartDate);
                    if (ChronoUnit.DAYS.between(StartDate,dateNow) == 0) {
                        *//*System.out.println(url.text());
                        System.out.println(elementPost.getElementsByClass("text").text());
                        System.out.println(elementPost.getElementsByClass("author").text());
                        System.out.println("https://www.film.ru"+url.attr("href"));*//*
                        vkPostGroup.setVKPostFilmsNews(elementPost.getElementsByClass("text").text(), "https://www.film.ru"+url.attr("href"));
                    } else {return;}
                }*/}}
    public static void getInfo (String url) throws IOException, InterruptedException {
        Document documentPost = Jsoup.connect(url)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        if (documentPost.getElementsByClass("wrapper_articles_info").text().equals("Сегодня")) {
            //System.out.println(documentPost.getElementsByClass("wrapper_articles_text").text());
            //System.out.println(documentPost.getElementsByClass("wrapper_articles_info").text());
            String text = documentPost.getElementsByClass("wrapper_articles_text").text();
            String urlPict = "https://www.film.ru" + documentPost.getElementsByClass("wrapper_articles_background_block").attr("src");
            vkPostGroup.setVKPostFilmsNewsPeoples(text, urlPict);
        }
    }
    public static void filmNewsMail () throws IOException, InterruptedException {
        Document documentNews = Jsoup.connect("https://kino.mail.ru/news/")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elementsNews = documentNews.getElementsByClass("newsitem");
        String textPost;
        String urlPost;
        String urlPostPicture;
        String headPost;
        for (Element element : elementsNews) {
            urlPost = "https://kino.mail.ru"+element.getElementsByClass("newsitem__title link-holder").attr("href");
            if (!urlPost.equals("https://kino.mail.ru")) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime date = LocalDateTime.parse(element.getElementsByClass("newsitem__param js-ago").attr("datetime").replace("+03:00",""), dateTimeFormatter);
                if (ChronoUnit.DAYS.between(date, LocalDateTime.now()) == 0) {
                    if (ChronoUnit.MINUTES.between(date, LocalDateTime.now()) <= 149) {
                        Document documentNewsPosts = Jsoup.connect("https://kino.mail.ru"+element.getElementsByClass("newsitem__title link-holder").attr("href"))
                                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                                .referrer("http://www.google.com")
                                .get();
                        Elements elementsNewsPosts = documentNewsPosts.getElementsByClass("layout layout_afisha js-adv-marker");
                        for (Element elementNewPost : elementsNewsPosts) {
                            headPost = elementNewPost.getElementsByClass("hdr__inner").first().text().toUpperCase()+"!";;
                            textPost = elementNewPost.getElementsByClass("article__item article__item_alignment_left article__item_html").text() + "\n" +
                                    "Источник: https://kino.mail.ru/news/";
                            urlPostPicture = elementNewPost.getElementsByClass("picture__image picture__image_cover picture__image_center photo__pic").attr("src");
                            if (urlPostPicture.length() == 0) {
                                urlPostPicture = elementNewPost.getElementsByClass("photo__pic photo__pic_lazy").attr("src");
                            }vkPostGroup.setVKPostNewsMail(textPost, urlPostPicture, headPost);
                        }}}}}}}