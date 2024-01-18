package ru.mybot.bot;

import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tinkoff.piapi.contract.v1.OptionPaymentType;

import java.io.IOException;
import java.lang.invoke.StringConcatException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotJarvisTelegramm extends TelegramLongPollingBot {

    private String BOT_TOKEN;
    private String BOT_NAME;
    private String DRAYVER_PATH;

    ParserWeather parserWeather;
    BotSettings botSettings;
    FilmInformer filmInformer;
    FilmFounder filmFounder;
    Servise servise;
    ZodiacInform zodiacInform;
    Stations stations;
    Geocoder geocoder;
    OpenAiApiExample openAiApiExample;
    tinAPI tinAPI;
    VKPostGroup vkPostGroup;

    private static final String INFO_BUTTON_TEXT = "Информация о погоде";
    private static final String SERVICE_BUTTON_TEXT = "Раздел Админа";
    private static final String SERVICES_BUTTON_TEXT = "Сервисы";
    private static final String BACK_BUTTON_IN_HEAD_MENU = "Назад";
    private static final String COME_BACK_BUTT = "Главное меню ↓";
    private static final String LIMITS_BUTTON = "Лимиты";
    private static final String STATISTICS_BUTTON = "Выгрузить пользователей";
    private static final String LIST_FILMS_AT_DAY = "Выгрузить фильмы за день";
    private static final String TRUE_BUTTON = "Разрешить";
    private static final String FALSE_BUTTON = "Запретить";
    private static final String SEND_BUTTON = "Рассылка всем";
    private static final String SEND_LABEL = "Введите текст для рассылки:";
    private static final String SUBSCR_BUTTON_WEATHER = "Подписаться на рассылку о погоде";
    private static final String SUBSCR_BUTTON_WEATHERNO = "Отписаться от рассылки о погоде";
    private static final String LOCATION_BUTTON = "Мое местоположение";
    private static final String WEATHER_BUTTON_INF = "Узнать погоду";
    private static final String BUTTON_USER_DEL = "Удалить?";
    private static final String TORRENT_BUTTON_TEXT = "Торрент-трекер";
    private static final String TORRENT_INFO_BUTTON = "Поиск по торрент-трекеру";
    private static final String FILM_BUTTON_TEXT = "Инфо по фильмам";
    private static final String FILMS_FOR_NAME = "Фильмография по имени";
    private static final String PEOPLE_NAME = "Введите имя актера-режиссера:";
    private static final String HOROSCOPE_BUTTON_TEXT = "Хочу гороскоп";
    private static final String HOROSCOPE_BUTTON_TEXTNO = "Не хочу гороскоп";
    private static final String STATIONS_BUTTON_TEXT = "Станции ЖД";
    private static final String WANT_WATCH_BUTTON = "Хочу посмотреть";
    private static final String LIST_WATCH_BUTTON = "Список фильмов";
    private static final String CLEAR_LIST_FILMS_BUTTON = "Очистить мой список ожидания";
    private static final String CLEAR_LIST_FILM_NAME_BUTTON = "Удалить фильм из списка";
    private static final String TORRENT_PROMPT_TEXT = "Введите название торрента:";
    private static final String FILM_PROMPT_TEXT = "Введите название фильма или сериала:";
    private static final String HOROSCOPE_PROMPT_TEXT = "Выберите Ваш знак зодиака из списка:";
    private static final String WANT_FILM_PROMPT_BUTT_TEXT = "Введите название ожидаемого фильма или сериала:";
    private static final String CLEAR_FILM_PROM_TEXT = "Введите название фильма для удаления:";
    private static final String BUTT_BACK = "Вернуться";
    private static final String BUTT_BACK_MENU = "Отобразить меню";
    private static final String FIND_USERS_VK = "Поиск пользователя в ВК";
    private static final String FIO_USER_FIND = "Введите Имя и Фамилию человека:";

    BotJarvisTelegramm(DefaultBotOptions botOptions) throws SQLException {
        super(botOptions);
        parserWeather = new ParserWeather(); //создаем экземпляр класса, который пришлет результат запрошенной погоды по твоей геолокации
        botSettings = new BotSettings(); //создаем экземпляр класса с геттерами и сеттерами для бота, в которые будем записывать ответы пользователям
        filmInformer = new FilmInformer();
        filmFounder = new FilmFounder();
        servise = new Servise();
        zodiacInform = new ZodiacInform();
        stations = new Stations();
        geocoder = new Geocoder();
        openAiApiExample = new OpenAiApiExample();
        tinAPI = new tinAPI();
        vkPostGroup = new VKPostGroup();

        //String connectionString = "jdbc:sqlite::resource:DB_Bot.db"; //так только если запустить jar и более не трогать БД и сам код
        /*String connectionString = "jdbc:sqlite:D:\\projects\\DB_Bot.db";
        botSettings.setConnectMysql(DriverManager.getConnection(connectionString));
        botSettings.setStatement(botSettings.getConnectMysql().createStatement());*/
        String connectionString = "jdbc:sqlite:DB_Bot.db";
        botSettings.setConnectMysql(DriverManager.getConnection(connectionString));
        botSettings.setStatement(botSettings.getConnectMysql().createStatement());

        String sqlReadTokNam = "SELECT rowid, token, name, driver_path FROM table_paths where rowid = 1;";
        try {
            ResultSet resultSet = botSettings.getStatement().executeQuery(sqlReadTokNam);
            while (resultSet.next()) {
                BOT_TOKEN = resultSet.getString("token");
                BOT_NAME = resultSet.getString("name");
                DRAYVER_PATH = resultSet.getString("driver_path");
            }resultSet.close();
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e);
        }
        class MyTimerTaskSendWeather extends TimerTask { //через данный класс, раз в 5 часов запускается рассылка о погоде
            public void run() {
                System.out.println("Начал выполнять проверку и рассылку погоды по подписке.");
                //Этот метод будет выполняться с нужным нам периодом
                SendMessage sendMessage = new SendMessage();
                String chatIDErr = null;
                String nameErr = null;
                Map<String, String> weatherDict = new HashMap<>();
                String sqlDuplicates = "SELECT city, chatID, location, name, Subscr FROM table_weather WHERE city IN " +
                        "(SELECT city FROM table_weather GROUP BY city HAVING COUNT(*) > 1)";
                String sqlNonDuplicates = "SELECT rowid, city, chatID, location, name, Subscr FROM table_weather WHERE city NOT IN " +
                        "(SELECT city FROM table_weather GROUP BY city HAVING COUNT(*) > 1)";
                try {
                    ResultSet Duplicates = botSettings.getStatement().executeQuery(sqlDuplicates);
                    Map<String, List<Map<String, String>>> duplicates = new HashMap<>();
                    while (Duplicates.next()) {
                        if (Duplicates.getInt("Subscr") == 1 && (!Duplicates.getString("location").equals("(null)") || Duplicates.getString("location") != null || !Duplicates.getString("location").isEmpty())) {
                            String city = Duplicates.getString("city");
                            String chatId = Duplicates.getString("chatID");
                            String location = Duplicates.getString("location");
                            String name = Duplicates.getString("name");

                            Map<String, String> data = new HashMap<>();
                            data.put("chatID", chatId);
                            data.put("location", location);
                            data.put("name", name);
                            data.put("city", city);
                            if (!duplicates.containsKey(city)) {
                                duplicates.put(city, new ArrayList<>());
                            }
                            duplicates.get(city).add(data);
                            // Проверяем, есть ли уже информация о погоде для данному городу в словаре `weatherDict`.
                            // Если информация о погоде еще не была получена, то получаем ее и сохраняем в словарь `weatherDict`.
                            if (!weatherDict.containsKey(city)) {
                                try {
                                    String weatherInfo = ParserWeather.getWeatherCity(location);
                                    weatherDict.put(city, weatherInfo);
                                }catch (IOException ex) {
                                    System.out.println(ex);
                                }
                            }}else {
                            System.out.println("Игнорим тех кто не подписан: " + Duplicates.getString("chatID"));
                        }}Duplicates.close();
                    for (List<Map<String, String>> list : duplicates.values()) {
                        list.sort((a, b) -> a.get("city").compareTo(b.get("city")));
                        String city = list.get(0).get("city"); // выбираем случайную локацию из отсортированных записей по полю "city"
                        String weatherInfo = weatherDict.get(city); // получаем информацию о погоде для этого города из словаря `weatherDict`
                        for (Map<String, String> data : list) {
                            String chatId = data.get("chatID");
                            String name = data.get("name");
                            chatIDErr = chatId;
                            nameErr = name;
                            sendMessage.setChatId(chatId);
                            sendMessage.setText(name + ",\n" + weatherInfo);
                            sendMessage.setReplyMarkup(getInlKeyboardWeather());
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException ex) {
                                System.out.println(ex);
                                sendMessage.setText("Ошибка рассылки для: \n" + chatIDErr + "\nИмя: " +nameErr);
                                sendMessage.setReplyMarkup(getInlKeyboardUserDel(chatIDErr, nameErr));
                                sendMessage.setChatId("523626416");
                                try {
                                    execute(sendMessage);
                                }catch (TelegramApiException fdf){System.out.println(fdf);}
                            }
                            System.out.println("Рассылка о погоде по подписке для: " + chatId);
                        }}
                    ResultSet noDuplicates = botSettings.getStatement().executeQuery(sqlNonDuplicates);
                    // Добавить остальные ID в коллекцию
                    while (noDuplicates.next()) {
                        if (noDuplicates.getInt("Subscr") == 1 && (!noDuplicates.getString("location").equals("(null)") || noDuplicates.getString("location") != null || !noDuplicates.getString("location").isEmpty())) {
                            chatIDErr = noDuplicates.getString("chatId");
                            nameErr = noDuplicates.getString("name");
                            sendMessage.setChatId(noDuplicates.getString("chatID"));
                            try {
                                sendMessage.setText(noDuplicates.getString("name") + ",\n" + parserWeather.getWeatherCity(noDuplicates.getString("Location")));
                                sendMessage.setReplyMarkup(getInlKeyboardWeather());
                            }catch (IOException ex) {
                                System.out.println(ex);
                            }
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException ex) {
                                sendMessage.setText("Ошибка рассылки для: \n" + chatIDErr + "\nИмя: " +nameErr);
                                sendMessage.setReplyMarkup(getInlKeyboardUserDel(chatIDErr, nameErr));
                                sendMessage.setChatId("your_id_telergamm_user");
                                try {
                                    execute(sendMessage);
                                }catch (TelegramApiException fdf){System.out.println(fdf);}}
                            System.out.println("Рассылка о погоде по подписке для: " + noDuplicates.getString("chatID"));
                        }else {
                            System.out.println("Игнорим тех кто не подписан: " + noDuplicates.getString("chatID"));
                        }}
                    noDuplicates.close();
                    weatherDict.clear();
                    duplicates.clear();
                } catch (SQLException ex) {
                    System.out.println(ex + ": Ошибка SQL рассылки погоды по подписке");
                }}}
        class MyTimerTaskZod extends TimerTask { //через данный класс, раз в 5 часов запускается рассылка о погоде
            public void run() {
                System.out.println("Начал выполнять проверку и рассылку гороскопа.");
                //Этот метод будет выполняться с нужным нам периодом
                SendMessage sendMessage = new SendMessage();
                String chatID = null;
                String name = null;
                HashMap<Integer, String> dayNow = null;
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                LocalDateTime specificTime = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0); //записываем новую дату с определенным заданным временем
                String dateNow = dateTimeFormatter.format(specificTime);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                LocalDateTime endDate = LocalDateTime.now();
                LocalDateTime StartDate;
                try {
                    String sqlZodiac = "SELECT chatID, name, zodiacSign, zodiacSignSubs, sendDate FROM table_weather WHERE zodiacSignSubs = 1;";
                    ResultSet resultSet = botSettings.getConnectMysql().prepareStatement(sqlZodiac).executeQuery();
                    while (resultSet.next()) {
                        StartDate = LocalDateTime.parse(resultSet.getString("sendDate"), formatter);
                        if (ChronoUnit.DAYS.between(StartDate,endDate) >= 1) {
                            if (dayNow == null){
                                try {
                                    dayNow = zodiacInform.getInfoDay();
                                } catch (IOException e) {
                                    dayNow.put(0, "Возникла какая-то ошибка, скоро все снова заработает.").replace("0=","");
                                }}
                            chatID = resultSet.getString("chatID");
                            name = resultSet.getString("name");
                            sendMessage.setChatId(chatID);
                            sendMessage.setText(name + ", " + "\n" + zodiacInform.getZodiac(resultSet.getString("zodiacSign")) +"\n"+
                                    "\n" +
                                    "Несколько исторических фактов о сегодняшнем дне: \n" +
                                    dayNow.toString().replace(",","\n").replace("{","").replace("}","").replace("=",". "));
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException e){
                                sendMessage.setText("Ошибка рассылки для: \n" + chatID + "\nИмя: " +name);
                                sendMessage.setReplyMarkup(getInlKeyboardUserDel(chatID, name));
                                sendMessage.setChatId("your_id_telergamm_user");
                                try {
                                    execute(sendMessage);
                                }catch (TelegramApiException fdf){System.out.println(fdf);}
                            }
                            System.out.println("Рассылка гороскопа по подписке для: " + resultSet.getString("chatID"));
                            String sqlZodUpdate = "UPDATE table_weather SET sendDate = ? WHERE chatID = ?;";
                            PreparedStatement prstDel = botSettings.getConnectMysql().prepareStatement(sqlZodUpdate);
                            prstDel.setString(1, dateNow);
                            prstDel.setString(2, chatID);
                            prstDel.executeUpdate();//для обновления даты без прерывания цикла
                            prstDel.close();
                        }}resultSet.close();
                } catch (SQLException | IOException ex) {
                    System.out.println(ex + ": Ошибка рассылки гороскопа по подписке");
                    sendMessage.setChatId(chatID);
                    sendMessage.setText(name + ", " + "\n" + "Чтобы рассылка работала корректно, " +
                            "Вам нужно указать Ваш знак зодиака, в разделе 'Сервисы'");
                    try {
                        execute(sendMessage);
                    }catch (TelegramApiException e){
                        sendMessage.setText("Ошибка рассылки для: \n" + chatID + "\nИмя: " + name);
                        sendMessage.setReplyMarkup(getInlKeyboardUserDel(chatID, name));
                        sendMessage.setChatId("523626416");
                        try {
                            execute(sendMessage);
                        }catch (TelegramApiException fdf){System.out.println(fdf);}
                    }}dayNow.clear();}}
        class MyTimerTaskParsFilmTorr extends TimerTask {
            public void run() {
                System.out.println("Начал выполнять проверку и запись новых фильмов за день");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String dateNow = dateTimeFormatter.format(LocalDateTime.now());
                /*System.setProperty("webdriver.chrome.driver", DRAYVER_PATH);
                WebDriver webDriver = new ChromeDriver();*/
                //try {
                System.setProperty("webdriver.edge.driver", DRAYVER_PATH);
                //System.setProperty("webdriver.http.factory", "jdk-http-client");
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--remote-allow-origins=*");
                    WebDriver webDriver = new EdgeDriver(options);
                    webDriver.get("https://torrent.aqproject.ru/torrent/browse?offset=0");
                    Document document = Jsoup.parse(webDriver.getPageSource());
                    Elements elements = document.getElementsByClass("col-auto torrent-browse-list-name");
                    try {
                        for (Element element : elements) {
                            String name;
                            String url;
                            String str = element.getElementsByClass("torrent-browse-name").attr("href");
                            name = element.text();
                            url = "https://torrent.aqproject.ru" + str;
                            if (name.startsWith("Прикреплён:")) {
                                System.out.println("Игнорю прикрепленное поле: "+name);
                                Thread.sleep(1000);
                            } else {
                                String sqlInsert = "INSERT INTO table_films (name, url, day) VALUES (?, ?, ?);";
                                PreparedStatement stmt = botSettings.getStatement().getConnection().prepareStatement(sqlInsert);
                                // Check if the URL already exists in the database
                                Set<String> existingNames = getExistingNames();
                                if (existingNames.contains(name)) {
                                    System.out.println("Уже есть в базе: " + name);
                                    Thread.sleep(1000);
                                    webDriver.quit();
                                    return;
                                }
                                // Insert the new data into the database
                                stmt.setString(1, name);
                                stmt.setString(2, url);
                                stmt.setString(3, dateNow);
                                stmt.executeUpdate();
                                stmt.close();
                                //vkPostGroup.setVKPost(url);
                                System.out.println("Пишем данные: " + name);
                                Thread.sleep(1000);
                        /*System.out.println("Пишем данные: " + name);
                        botSettings.getStatement().execute(sqlInsert);*/
                            }}elements.clear();}
                    catch (SQLException | InterruptedException e) {
                        System.out.println(e);
                    }finally {
                        if (webDriver != null) {
                            webDriver.quit();
                        }
                    }}
            private Set<String> getExistingNames() throws SQLException {
                Set<String> names = new HashSet<>();
                try (PreparedStatement stmt = botSettings.getStatement().getConnection().prepareStatement("SELECT name FROM table_films");
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        names.add(rs.getString("name"));
                    }stmt.close();}
                return names;
            }}
        class MyTimerTaskDelOldFolm extends TimerTask {
            public void run(){
                System.out.println("Начал выполнять удаление старых фильмов.");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                LocalDateTime endDate = LocalDateTime.now();
                LocalDateTime StartDate;
                int id;
                String sql1 = "SELECT rowid, name, day FROM table_films;";
                try {
                    ResultSet resultSet = botSettings.getStatement().executeQuery(sql1);
                    while (resultSet.next()) {
                        StartDate = LocalDateTime.parse(resultSet.getString("day"), formatter);
                        if (ChronoUnit.DAYS.between(StartDate,endDate) >= 150) {
                            id = resultSet.getInt("rowid");
                            String sqlDelete = "DELETE FROM table_films WHERE rowid= ?";
                            PreparedStatement prstDel = botSettings.getConnectMysql().prepareStatement(sqlDelete);
                            prstDel.setInt(1, id);
                            prstDel.executeUpdate(); //для удаления множества не прерывая цикл
                            prstDel.close();
                        }}resultSet.close();
                } catch (SQLException e) {
                    System.out.println("Ошибка удаления: " + e);}}}
        class MyTimerTaskSend extends TimerTask {
            public void run(){
                System.out.println("Начал выполнять: MyTimerTaskSend");
                SendMessage sendMessage = new SendMessage();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
                String date = dateTimeFormatter.format(LocalDateTime.now());
                sendMessage.setChatId("523626416");
                sendMessage.setText("Алексей" + ",\nПосле неожиданного отключения, я снова запустился. Дата моего запуска: " + date);
                String sqlInsert = "INSERT INTO table_reboots (text, date) VALUES (?, ?);";
                try {
                    execute(sendMessage);
                    PreparedStatement stmt = botSettings.getStatement().getConnection().prepareStatement(sqlInsert);
                    // Insert the new data into the database
                    stmt.setString(1, "Перезагрузка бота.");
                    stmt.setString(2, date);
                    stmt.executeUpdate();
                    stmt.close();
                }catch (TelegramApiException e){
                    System.out.println("Ошибка: " +e);
                } catch (SQLException e) {
                    System.out.println("SQL errors: "+e);
                }}}
        class MyTimerTaskFilmWant extends TimerTask {
            public void run(){
                System.out.println("Начал выполнять проверку наличия ожидаемых фильмов.");
                SendMessage sendMessage = new SendMessage();
                try {
                    String queryWatch = "SELECT chatID, name, filmName FROM table_want_watch WHERE yes = 0";
                    PreparedStatement pstmt1 = botSettings.getConnectMysql().prepareStatement(queryWatch);
                    ResultSet rs1 = pstmt1.executeQuery();

                    String queryFilms = "SELECT name, url FROM table_films";
                    PreparedStatement pstmt2 = botSettings.getConnectMysql().prepareStatement(queryFilms);
                    ResultSet rs2 = pstmt2.executeQuery();

                    List<String> filmNames = new ArrayList<>();
                    while (rs2.next()) {
                        String filmName = rs2.getString("name").split(":")[0]
                                .split(" \\(")[0]
                                .replace("Ё", "Е")
                                .replace("ё", "е")
                                .replace(": "," ")
                                .replace("«", "")
                                .replace("»","")
                                .replace("\"","")
                                .replace("!", "")
                                .replace("-"," ")
                                .replace(", ", " ")
                                .replace(". ", " ")
                                .split(" /")[0];
                        String url = rs2.getString("url");
                        filmNames.add(filmName + "|"+ url);
                    }rs2.close();
                    pstmt2.close();
                    while (rs1.next()) {
                        String filmName = rs1.getString("filmName");
                        for (int i = 0; i < filmNames.size(); i++) {
                            String film = filmNames.get(i);
                            String[] parts = film.split("\\|"); // исправление разделителя
                            String filmName2 = parts[0];
                            String url = parts[1];
                            if (filmName.replace("ё", "е")
                                    .split(":")[0]
                                    .replace("Ё", "Е")
                                    .replace(": "," ")
                                    .replace("\"","")
                                    .replace("!", "")
                                    .replace("-", " ")
                                    .replace(". ", " ")
                                    .replace(", ", " ")
                                    .replace("«", "")
                                    .replace("»","")
                                    .equalsIgnoreCase(filmName2)) {
                                sendMessage.setText(rs1.getString("name")+",\nнаконец, Ваш ожидаемый фильм, доступен на скачивание по ссылке: \n" + url);
                                sendMessage.setChatId(rs1.getString("chatID"));
                                try {
                                    execute(sendMessage);
                                } catch (TelegramApiException e) {
                                    System.out.println(e);
                                }
                                String deleteWatch = "UPDATE table_want_watch SET yes = '1' WHERE filmName = ? AND chatID = ?";
                                PreparedStatement pstmtDelete1 = botSettings.getConnectMysql().prepareStatement(deleteWatch);
                                pstmtDelete1.setString(1, filmName);
                                pstmtDelete1.setString(2, rs1.getString("chatID"));
                                pstmtDelete1.executeUpdate();
                                pstmtDelete1.close();
                                filmNames.remove(i); //пометка фильма как найденного
                                break;
                            }}}rs1.close();
                    pstmt1.close();
                    filmNames.clear();
                }catch (SQLException e) {
                    System.out.println(e);
                }}}
        class MyTimerTaskInfoActions extends TimerTask {
            public void run(){
                System.out.println("Начал выполнять сбор и отправку статистики по акциям.");
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId("your_id_telergamm_user");
                try {
                    sendMessage.setText(tinAPI.getActions());
                    execute(sendMessage);
                } catch (IOException | java.text.ParseException e) {
                    System.out.println(e);
                } catch (TelegramApiException e) {
                    System.out.println(e);
                }}}
        class MyTimerTaskFilmsPremiere extends TimerTask {
            public void run(){
                LocalDateTime currentDateTime = LocalDateTime.now();
                int currentMonth = currentDateTime.getMonthValue();
                int currentYear = currentDateTime.getYear();
                String queryMonthNum = "SELECT monthNum, year FROM table_month WHERE rowid = 1";
                try {
                    ResultSet resultSet = botSettings.getStatement().executeQuery(queryMonthNum);
                    if (currentMonth - resultSet.getInt("monthNum") >= 1) {
                        if (currentYear - resultSet.getInt("year") >= 1) {
                            filmInformer.setPremierPostVK(currentYear, currentMonth);
                            String deleteWatch = "UPDATE table_month SET monthNum = ?, year = ? WHERE rowid = 1";
                            PreparedStatement pstmtDelete1 = botSettings.getConnectMysql().prepareStatement(deleteWatch);
                            pstmtDelete1.setInt(1, currentMonth);
                            pstmtDelete1.setInt(2, currentYear);
                            pstmtDelete1.executeUpdate();
                            pstmtDelete1.close();
                        }else {
                            filmInformer.setPremierPostVK(currentYear, currentMonth);
                            String deleteWatch = "UPDATE table_month SET monthNum = ? WHERE rowid = 1";
                            PreparedStatement pstmtDelete1 = botSettings.getConnectMysql().prepareStatement(deleteWatch);
                            pstmtDelete1.setInt(1, currentMonth);
                            pstmtDelete1.executeUpdate();
                            pstmtDelete1.close();
                        }}}catch (SQLException ex) {
                    System.out.println(ex);
                } catch (IOException e) {
                    System.out.println(e);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }}}
        class MyTimerTaskFilmsRelise extends TimerTask {
            public void run(){
                LocalDateTime currentDateTime = LocalDateTime.now();
                int currentMonth = currentDateTime.getMonthValue();
                int currentYear = currentDateTime.getYear();
                String queryMonthNum = "SELECT monthNum, year FROM table_month_rel WHERE rowid = 1";
                try {
                    ResultSet resultSet = botSettings.getStatement().executeQuery(queryMonthNum);
                    if (currentMonth - resultSet.getInt("monthNum") >= 1) {
                        if (currentYear - resultSet.getInt("year") >= 1) {
                            filmInformer.setRelisePostVK(currentYear, currentMonth);
                            String deleteWatch = "UPDATE table_month_rel SET monthNum = ?, year = ? WHERE rowid = 1";
                            PreparedStatement pstmtDelete1 = botSettings.getConnectMysql().prepareStatement(deleteWatch);
                            pstmtDelete1.setInt(1, currentMonth);
                            pstmtDelete1.setInt(2, currentYear);
                            pstmtDelete1.executeUpdate();
                            pstmtDelete1.close();
                        }else {
                            filmInformer.setRelisePostVK(currentYear, currentMonth);
                            String deleteWatch = "UPDATE table_month_rel SET monthNum = ? WHERE rowid = 1";
                            PreparedStatement pstmtDelete1 = botSettings.getConnectMysql().prepareStatement(deleteWatch);
                            pstmtDelete1.setInt(1, currentMonth);
                            pstmtDelete1.executeUpdate();
                            pstmtDelete1.close();
                        }}}catch (SQLException ex) {
                    System.out.println(ex);
                } catch (IOException e) {
                    System.out.println(e);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }}}
        class MyTimerTaskFilmsNewsDay extends TimerTask {
            public void run(){
                try {
                    filmInformer.setNewsFilmDay();
                }catch (IOException | InterruptedException e) {
                    System.out.println(e);
                }}}
        class MyTimerTaskFilmsNewsMail extends TimerTask {
            public void run(){
                try {
                    filmInformer.filmNewsMail();
                }catch (IOException | InterruptedException e) {
                    System.out.println(e);
                }}}
        class MyTimerTaskMyBirthDay extends TimerTask {
            public void run(){
                LocalDateTime currentDateTime = LocalDateTime.now();
                int currentMonth = currentDateTime.getMonthValue();
                int currentDay = currentDateTime.getDayOfMonth();
                SendMessage sendMessage = new SendMessage();
                if (currentDay == 8 && currentMonth == 11){
                    String sqlChatID = "SELECT chatID, name FROM table_weather";
                    try {
                        ResultSet resultSetChatID = botSettings.getStatement().executeQuery(sqlChatID);
                        while (resultSetChatID.next()) {
                            sendMessage.setChatId(resultSetChatID.getString("chatID"));
                            sendMessage.setText(resultSetChatID.getString("name") + ", \n" +
                                    "Именно в этот день - 8 ноября 2022 года, я был создан и запущен, для помощи моему создателю, следовательно, " +
                                    "можете мысленно поздравить меня с днем рождения! " +
                                    "Надеюсь что я также полезен для Вас как и для него, особенно в наше непростое время!" +
                                    "Да прибудет с Вами сила и терпение, а я буду и дальше продолжать совершенствоваться!");
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException e) {}
                        }}catch (SQLException ex){}}}}
        ScheduledExecutorService scheduler1 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler2 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler3 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler4 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler5 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler6 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler7 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler8 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler9 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler10 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler11 = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler12 = Executors.newSingleThreadScheduledExecutor();
        LocalTime[] targetTimesWeather = { LocalTime.of(6, 0), LocalTime.of(7, 30),
                LocalTime.of(9, 4), LocalTime.of(10, 30),
                LocalTime.of(12, 0), LocalTime.of(13, 30),
                LocalTime.of(15, 0), LocalTime.of(16, 30),
                LocalTime.of(18, 0), LocalTime.of(19, 30),
                LocalTime.of(21, 3), LocalTime.of(22, 30), LocalTime.of(0, 0) };
        LocalTime[] targetTimesZod = { LocalTime.of(9, 1), LocalTime.of(12, 6), LocalTime.of(16, 0), LocalTime.of(20, 0), LocalTime.of(23, 58) };
        LocalTime[] targetTimesParsFilmTorr = { LocalTime.of(0, 3), LocalTime.of(1, 33),
                LocalTime.of(3, 3), LocalTime.of(4, 33),
                LocalTime.of(6, 3), LocalTime.of(7, 33),
                LocalTime.of(9, 6), LocalTime.of(10, 33),
                LocalTime.of(12, 3), LocalTime.of(13, 33),
                LocalTime.of(15, 3), LocalTime.of(16, 33),
                LocalTime.of(17, 3), LocalTime.of(18, 33),
                LocalTime.of(20, 3), LocalTime.of(21, 33),
                LocalTime.of(23, 3) };
        LocalTime[] targetTimesDelOldFilm = { LocalTime.of(5, 33), LocalTime.of(9, 33),
                LocalTime.of(13, 39), LocalTime.of(17, 5),
                LocalTime.of(21, 9), LocalTime.of(23, 9) };
        LocalTime[] targetTimesWantFilm = { LocalTime.of(9, 8), LocalTime.of(9, 19),
                LocalTime.of(10, 6), LocalTime.of(10, 16),
                LocalTime.of(9, 31), LocalTime.of(9, 42),
                LocalTime.of(9, 53), LocalTime.of(10, 4),
                LocalTime.of(10, 15), LocalTime.of(10, 26),
                LocalTime.of(10, 37), LocalTime.of(10, 48),
                LocalTime.of(10, 59), LocalTime.of(11, 10),
                LocalTime.of(12, 21), LocalTime.of(12, 32),
                LocalTime.of(12, 43), LocalTime.of(12, 54),
                LocalTime.of(13, 5), LocalTime.of(13, 16),
                LocalTime.of(13, 27), LocalTime.of(13, 38),
                LocalTime.of(13, 49), LocalTime.of(14, 1),
                LocalTime.of(14, 12), LocalTime.of(14, 23),
                LocalTime.of(14, 34), LocalTime.of(14, 45),
                LocalTime.of(14, 56), LocalTime.of(15, 7),
                LocalTime.of(15, 18), LocalTime.of(15, 29),
                LocalTime.of(15, 40), LocalTime.of(15, 51),
                LocalTime.of(16, 2), LocalTime.of(16, 13),
                LocalTime.of(16, 24), LocalTime.of(16, 35),
                LocalTime.of(16, 46), LocalTime.of(16, 57),
                LocalTime.of(17, 8), LocalTime.of(17, 19),
                LocalTime.of(17, 30), LocalTime.of(17, 41),
                LocalTime.of(17, 52), LocalTime.of(18, 3),
                LocalTime.of(18, 14), LocalTime.of(18, 25),
                LocalTime.of(18, 36), LocalTime.of(18, 47),
                LocalTime.of(18, 58), LocalTime.of(19, 9),
                LocalTime.of(19, 20), LocalTime.of(19, 32),
                LocalTime.of(19, 43), LocalTime.of(19, 54),
                LocalTime.of(20, 5), LocalTime.of(20, 16),
                LocalTime.of(20, 27), LocalTime.of(20, 38),
                LocalTime.of(20, 49), LocalTime.of(21, 7),
                LocalTime.of(21, 19), LocalTime.of(21, 30),
                LocalTime.of(21, 41), LocalTime.of(21, 52),
                LocalTime.of(22, 3), LocalTime.of(22, 14),
                LocalTime.of(22, 25), LocalTime.of(22, 36),
                LocalTime.of(22, 47), LocalTime.of(22, 58),
                LocalTime.of(23, 12), LocalTime.of(23, 23),
                LocalTime.of(23, 34), LocalTime.of(23, 45),
                LocalTime.of(23, 56) };
        LocalTime[] targetTimesInfoActions = { LocalTime.of(8, 33),
                LocalTime.of(13, 3), LocalTime.of(17, 8), LocalTime.of(21, 0) };
        LocalTime[] targetTimesFilmsPremiere = { LocalTime.of(3, 33) };
        LocalTime[] targetTimesFilmsRelise = { LocalTime.of(3, 37) };
        LocalTime[] targetTimesFilmsNews = { LocalTime.of(23, 48) };
        LocalTime[] targetTimesParsNewsFilmsMail = { LocalTime.of(0, 13), LocalTime.of(1, 43),
                LocalTime.of(3, 13), LocalTime.of(4, 43),
                LocalTime.of(6, 13), LocalTime.of(7, 43),
                LocalTime.of(9, 15), LocalTime.of(10, 43),
                LocalTime.of(12, 13), LocalTime.of(13, 43),
                LocalTime.of(15, 13), LocalTime.of(16, 42),
                LocalTime.of(17, 13), LocalTime.of(18, 43),
                LocalTime.of(20, 13), LocalTime.of(21, 44),
                LocalTime.of(23, 16) };
        LocalTime[] targetTimesBirthDay = { LocalTime.of(12, 38)};

        scheduler1.scheduleAtFixedRate(new MyTimerTaskSend(), 0, 10000, TimeUnit.DAYS);

        for (LocalTime targetTime : targetTimesWeather) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler2.scheduleAtFixedRate(() -> {
                new MyTimerTaskSendWeather().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesZod) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler3.scheduleAtFixedRate(() -> {
                new MyTimerTaskZod().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesParsFilmTorr) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler4.scheduleAtFixedRate(() -> {
                new MyTimerTaskParsFilmTorr().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesDelOldFilm) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler5.scheduleAtFixedRate(() -> {
                new MyTimerTaskDelOldFolm().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesWantFilm) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler6.scheduleAtFixedRate(() -> {
                new MyTimerTaskFilmWant().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesInfoActions) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler7.scheduleAtFixedRate(() -> {
                new MyTimerTaskInfoActions().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesFilmsPremiere) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler8.scheduleAtFixedRate(() -> {
                new MyTimerTaskFilmsPremiere().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesFilmsRelise) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler9.scheduleAtFixedRate(() -> {
                new MyTimerTaskFilmsRelise().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesFilmsNews) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler10.scheduleAtFixedRate(() -> {
                new MyTimerTaskFilmsNewsDay().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesParsNewsFilmsMail) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler11.scheduleAtFixedRate(() -> {
                new MyTimerTaskFilmsNewsMail().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }
        for (LocalTime targetTime : targetTimesBirthDay) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime targetDateTime = now.with(targetTime);
            if (now.compareTo(targetDateTime) > 0) {
                targetDateTime = targetDateTime.plusDays(1);
            }
            Duration duration = Duration.between(now, targetDateTime);
            long initialDelay = duration.getSeconds();
            scheduler12.scheduleAtFixedRate(() -> {
                new MyTimerTaskMyBirthDay().run();
            }, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
        }}

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdateReceived(Update update) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        Message message = update.getMessage();
        int userSecur = 0;
        if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            String data = query.getData();
            try {
                PreparedStatement securityUsers = botSettings.getStatement().getConnection().prepareStatement("SELECT permission FROM table_weather WHERE chatID = ?");
                securityUsers.setString(1, query.getMessage().getChatId().toString());
                ResultSet rs = securityUsers.executeQuery() ;
                while (rs.next()) {
                    userSecur = rs.getInt("permission");
                }rs.close();
                securityUsers.close();
            }catch (SQLException ex) {
                System.out.println(ex);
            }
            if (userSecur != 0) {
                if (data.equals("torrent")) {
                    EditMessageText editMarkup = new EditMessageText();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlineKeyboardTorr());
                    editMarkup.setText("Выберите нужное действие:");
                    try {
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        //e.printStackTrace();
                        System.out.println(e);
                    }
                }else if (data.equals("torrent_info")) {
                    SendMessage smessage = new SendMessage();
                    smessage.setChatId(query.getMessage().getChatId().toString());
                    smessage.setText(TORRENT_PROMPT_TEXT);
                    ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
                    forceReply.setForceReply(true);
                    forceReply.setSelective(true);
                    smessage.setReplyMarkup(forceReply);
                    try {
                        execute(smessage);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        //e.printStackTrace();
                        System.out.println(e);
                    }
                } else if (data.equals("movie")) {
                    SendMessage smessage = new SendMessage();
                    smessage.setChatId(query.getMessage().getChatId().toString());
                    smessage.setText(FILM_PROMPT_TEXT);
                    ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
                    forceReply.setForceReply(true);
                    forceReply.setSelective(true);
                    smessage.setReplyMarkup(forceReply);
                    try {
                        execute(smessage);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        SendMessage errorMessage = new SendMessage();
                        errorMessage.setChatId(query.getMessage().getChatId().toString());
                        errorMessage.setText("Произошла ошибка при выполнении запроса");
                        try {
                            execute(errorMessage);
                        } catch (TelegramApiException ex) {
                            System.out.println(ex);
                        }}
                } else if (data.equals("horoscopeYes")) {
                    // Update zodiac sign subscription in database
                    DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String dateNow = dateTimeFormatter1.format(LocalDateTime.now());
                    String sqlZodUpdateDate = "UPDATE table_weather SET zodiacSignSubs = '0', zodiacSign = 'NO', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "', sendDate = '" + dateNow + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    try {
                        botSettings.getStatement().executeUpdate(sqlZodUpdateDate);
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorNo());
                    try {
                        execute(editMarkup);
                    }catch (TelegramApiException e) {
                        System.out.println(e);
                    }} else if (data.equals("horoscopeNo")) {
                    EditMessageText editMarkup = new EditMessageText();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorZod());
                    editMarkup.setText(HOROSCOPE_PROMPT_TEXT);
                    try {
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                } else if (data.equals("aries")) {
                    String zod = "Овен";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                }else if (data.equals("taurus")) {
                    String zod = "Телец";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                }else if (data.equals("gemini")) {
                    String zod = "Близнецы";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                }else if (data.equals("cancer")) {
                    String zod = "Рак";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(message.getChatId() + ": Записал свой зодиак");
                }else if (data.equals("leo")) {
                    String zod = "Лев";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                }else if (data.equals("virgo")) {
                    String zod = "Дева";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                } else if (data.equals("libra")) {
                    String zod = "Весы";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                } else if (data.equals("scorpio")) {
                    String zod = "Скорпион";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                }else if (data.equals("sagittarius")) {
                    String zod = "Стрелец";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                } else if (data.equals("capricorn")) {
                    String zod = "Козерог";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                } else if (data.equals("aquarius")) {
                    String zod = "Водолей";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                }else if (data.equals("pisces")) {
                    String zod = "Рыбы";
                    String sqlZod4 = "UPDATE table_weather SET zodiacSignSubs = '1', zodiacSign = '" + zod + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                    editMarkup.setChatId(query.getMessage().getChatId().toString());
                    editMarkup.setMessageId(query.getMessage().getMessageId());
                    editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(query.getMessage().getChatId().toString());
                    sendMessage.setText(query.getFrom().getFirstName() + ", записал Ваш знак зодиака как: " + zod);
                    try {
                        execute(sendMessage);
                        execute(editMarkup);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        botSettings.getStatement().execute(sqlZod4);
                    } catch (SQLException e) {
                        System.out.println(e);
                    } catch (TelegramApiException e) {
                        System.out.println(e);
                    }
                    System.out.println(query.getMessage().getChatId().toString() + ": Записал свой зодиак");
                }  else if (data.equals("station")) {
                    String sqlLoc = "SELECT location FROM table_weather WHERE chatID = " + query.getMessage().getChatId().toString();
                    try {
                        ResultSet resultSet3 = botSettings.getStatement().executeQuery(sqlLoc);
                        if (!resultSet3.getString("location").isEmpty() || !resultSet3.getString("location").equals("(null)")
                                || resultSet3.getString("location") != null){
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(query.getMessage().getChatId().toString());
                            sendMessage.setText(query.getFrom().getFirstName() + ", \nСписок станций ниже:\n" +
                                    Stations.getStations(resultSet3.getString("location")));
                            sendMessage.setReplyMarkup(getInlKeyboardBack());
                            execute(sendMessage);
                            System.out.println(query.getMessage().getChatId() + ": Нажал 'Станции РЖД'");
                            resultSet3.close();
                            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                            answerCallbackQuery.setCallbackQueryId(query.getId());
                            execute(answerCallbackQuery);} else {
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(query.getMessage().getChatId().toString());
                            sendMessage.setText("Нужно предоставить мне данные о Вашем местоположении, нажав на кнопку ниже ↓");
                            sendMessage.setReplyMarkup(getLocationButt());
                            execute(sendMessage);
                        }
                    }  catch (IOException | TelegramApiException | SQLException | ParseException ioException) {
                        System.out.println(ioException);
                    }
                } else if (data.equals("want_watch")) {
                    SendMessage smessage = new SendMessage();
                    smessage.setChatId(query.getMessage().getChatId().toString());
                    smessage.setText(WANT_FILM_PROMPT_BUTT_TEXT);
                    ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
                    forceReply.setForceReply(true);
                    forceReply.setSelective(true);
                    smessage.setReplyMarkup(forceReply);
                    try {
                        execute(smessage);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        //e.printStackTrace();
                        System.out.println(e);
                    }}else if (data.equals("list_watch")) {
                    String sqlListFilm = "SELECT filmName FROM table_want_watch WHERE chatID = ? AND (SELECT COUNT(*) FROM table_want_watch WHERE chatID = ?) > 0 AND yes = 0;";
                    try {
                        PreparedStatement statement = botSettings.getConnectMysql().prepareStatement(sqlListFilm);
                        statement.setString(1, query.getMessage().getChatId().toString());
                        statement.setString(2, query.getMessage().getChatId().toString());
                        ResultSet resultSet = statement.executeQuery();
                        if (!resultSet.isBeforeFirst()) {
                            SendMessage smessage = new SendMessage();
                            smessage.setChatId(query.getMessage().getChatId().toString());
                            smessage.setText(query.getFrom().getFirstName() + ", \nВы еще не добавили ни одного ожидаемого фильма в список ожидания, либо ранее добавленные Вами фильм уже были найдены и отправлены Вам.");
                            try {
                                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                                answerCallbackQuery.setCallbackQueryId(query.getId());
                                execute(answerCallbackQuery);
                                execute(smessage);
                            }catch (TelegramApiException ex) {
                                System.out.println(ex);
                            }
                        } else {
                            // ResultSet содержит хотя бы одну запись
                            StringBuilder films = new StringBuilder();
                            int count = 1;
                            while (resultSet.next()) {
                                // здесь можно обработать каждое значение filmName
                                films.append(count+". "+resultSet.getString("filmName")).append("\n");
                                count++;
                            }
                            SendMessage smessage = new SendMessage();
                            smessage.setChatId(query.getMessage().getChatId().toString());
                            smessage.setText(query.getFrom().getFirstName() + ", \nВот Ваши ожидаемые фильмы: \n" + films.toString());
                            try {
                                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                                answerCallbackQuery.setCallbackQueryId(query.getId());
                                execute(answerCallbackQuery);
                                execute(smessage);
                            }catch (TelegramApiException e) {
                                System.out.println(e);
                            }}
                        resultSet.close();}catch (SQLException e) {
                        System.out.println(e);
                    }}else if (data.equals("clear_list")){
                    try {
                        String sqlDelete = "DELETE FROM table_want_watch WHERE chatID= ?";
                        PreparedStatement prstDel = botSettings.getConnectMysql().prepareStatement(sqlDelete);
                        prstDel.setString(1, query.getMessage().getChatId().toString());
                        prstDel.executeUpdate();
                        prstDel.close();
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(query.getMessage().getChatId().toString());
                        sendMessage.setText("Хорошо, очистил Ваш лист ожидания");
                        sendMessage.setReplyMarkup(getInlineKeyboardTorr());
                        execute(sendMessage);
                        // Отвечаем на callbackQuery
                        AnswerCallbackQuery answer = new AnswerCallbackQuery();
                        answer.setCallbackQueryId(query.getId());
                        execute(answer);
                    }catch (SQLException | TelegramApiException e) {
                        System.out.println(e);
                    }
                } else if (data.equals("clear_film_name")) {
                    SendMessage smessage = new SendMessage();
                    smessage.setChatId(query.getMessage().getChatId().toString());
                    smessage.setText(CLEAR_FILM_PROM_TEXT);
                    ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
                    forceReply.setForceReply(true);
                    forceReply.setSelective(true);
                    smessage.setReplyMarkup(forceReply);
                    try {
                        execute(smessage);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }}else if (data.startsWith("TRUE_")) {
                    String chatID = data.split("_")[1].split(":")[0]; // Получаем chatID из callback data
                    int getMessageId = Integer.parseInt(data.split(":")[1].split(">")[0]);
                    String name = data.split(">")[1];
                    try {
                        // Обновляем разрешение пользователя в базе данных
                        PreparedStatement updateStatement = botSettings.getConnectMysql().prepareStatement("INSERT INTO table_weather (chatID, permission, Subscr, name, zodiacSign, zodiacSignSubs) VALUES (?, 1, 0, '" + name + "', ?, 0);");
                        updateStatement.setString(1, chatID);
                        updateStatement.setString(2, "NO");
                        updateStatement.executeUpdate();
                        updateStatement.close();
                        String sqlAccess = "UPDATE table_access_user SET permission = '1' WHERE chatID = ?";
                        PreparedStatement updateAccess = botSettings.getConnectMysql().prepareStatement(sqlAccess);
                        updateAccess.setString(1, chatID);
                        updateAccess.executeUpdate();
                        updateAccess.close();
                        sendMsgHEADEROtherReg(message,  name + ", Приветствую!\nЯ умею рассказывать о погоде и фильмах. \n" +
                                "Если интересно получать регулярную информацию о погоде по Вашему местоположению, подпишитесь на рассылку в разделе 'Информация о погоде'. \n" +
                                "Я умею искать информацию о фильмах в торрент трекере. \n" +
                                "Если хотите получать рассылку гороскопа, подпишитесь на нее в разделе 'Сервисы'" + "\n" +
                                "Ожидаете какой-то фильм, но кинокомпании нас покинули, не беда, добавьте наименование ожидаемого Вами фильма в список ожидаемых" +
                                "и я пришлю Вам уведомление, когда данный фильм можно будет скачать с торрента, приложив ссылку для скачивания." + "\n"+
                                "Если бот завис или не отображается клавиатура, просто напишите любую букву или фразу и она появится.", chatID, getMessageId);
                        // Отвечаем на callbackQuery
                        AnswerCallbackQuery answer = new AnswerCallbackQuery();
                        answer.setCallbackQueryId(query.getId());
                        execute(answer);
                        DeleteMessage deleteMessage = new DeleteMessage();
                        deleteMessage.setChatId(query.getMessage().getChatId().toString());
                        deleteMessage.setMessageId(query.getMessage().getMessageId());
                        try {
                            execute(deleteMessage);
                        }catch (TelegramApiException ex) {}
                    }catch (SQLException | TelegramApiException ex) {
                        System.out.println(ex);
                    }}else if (data.startsWith("FALSE_")) {
                    String chatID = data.split("_")[1].split(":")[0]; // Получаем chatID из callback data
                    Long userID = Long.parseLong(data.split(":")[1]);// Получаем userID из callback data
                    try {
                        // Блокируем пользователя и отправляем сообщение об отказе
                        SendMessage denyMessage = new SendMessage(chatID, "Ваш запрос на доступ был отклонен администратором.");
                        execute(denyMessage);
                        // Обновляем разрешение пользователя в базе данных
                        PreparedStatement updateStatement = botSettings.getConnectMysql().prepareStatement("UPDATE table_access_user SET permission = '0' WHERE chatID= ?");
                        updateStatement.setString(1, chatID);
                        updateStatement.executeUpdate();
                        updateStatement.close();
                        // Отвечаем на callbackQuery
                        AnswerCallbackQuery answer = new AnswerCallbackQuery();
                        answer.setCallbackQueryId(query.getId());
                        execute(answer);
                        DeleteMessage deleteMessage = new DeleteMessage();
                        deleteMessage.setChatId(query.getMessage().getChatId().toString());
                        deleteMessage.setMessageId(query.getMessage().getMessageId());
                        try {
                            execute(deleteMessage);
                        }catch (TelegramApiException ex) {}
                    }catch (SQLException | TelegramApiException ex) {
                        System.out.println(ex);
                    }}else if (data.equals("come_back")) {
                    SendMessage sendMessage = new SendMessage();
                    if (query.getMessage().getChatId().toString().equals("523626416")){
                        sendMessage.setChatId("your_id_telergamm_user");
                        sendMessage.setReplyMarkup(getReplyKeybAdm());
                        sendMessage.setText("Вернулись на главную");
                        try {
                            execute(sendMessage);
                        }catch (TelegramApiException ex) {
                            System.out.println(ex);
                        }}else {
                        sendMessage.setChatId(query.getMessage().getChatId().toString());
                        sendMessage.setReplyMarkup(getReplyKeyboOther());
                        sendMessage.setText("Вернулись на главную");
                        try {
                            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                            answerCallbackQuery.setCallbackQueryId(query.getId());
                            execute(answerCallbackQuery);
                            execute(sendMessage);
                        }catch (TelegramApiException ex) {
                            System.out.println(ex);
                        }}}else if (data.equals("films_people")) {
                    SendMessage smessage = new SendMessage();
                    smessage.setChatId(query.getMessage().getChatId().toString());
                    smessage.setText(PEOPLE_NAME);
                    ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
                    forceReply.setForceReply(true);
                    forceReply.setSelective(true);
                    smessage.setReplyMarkup(forceReply);
                    try {
                        execute(smessage);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        //e.printStackTrace();
                        System.out.println(e);
                    }}else  if (data.equals("back")) {
                    String sqlLoc = "SELECT location, zodiacSignSubs FROM table_weather WHERE chatID = " + query.getMessage().getChatId().toString();
                    try {
                        ResultSet resultSet3 = botSettings.getStatement().executeQuery(sqlLoc);
                        if (resultSet3.getInt("zodiacSignSubs") == 1) {
                            EditMessageText editMarkup = new EditMessageText();
                            editMarkup.setChatId(query.getMessage().getChatId().toString());
                            editMarkup.setMessageId(query.getMessage().getMessageId());
                            editMarkup.setReplyMarkup(getInlKeyboardHorYes());
                            editMarkup.setText("Выберите сервис:");
                            try {
                                execute(editMarkup);
                                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                                answerCallbackQuery.setCallbackQueryId(query.getId());
                                execute(answerCallbackQuery);
                            } catch (TelegramApiException e) {
                                //e.printStackTrace();
                                System.out.println(e);
                            }}else {
                            EditMessageText editMarkup = new EditMessageText();
                            editMarkup.setChatId(query.getMessage().getChatId().toString());
                            editMarkup.setMessageId(query.getMessage().getMessageId());
                            editMarkup.setReplyMarkup(getInlKeyboardHorNo());
                            editMarkup.setText("Выберите сервис:");
                            try {
                                execute(editMarkup);
                                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                                answerCallbackQuery.setCallbackQueryId(query.getId());
                                execute(answerCallbackQuery);
                            } catch (TelegramApiException e) {
                                //e.printStackTrace();
                                System.out.println(e);
                            }}}catch (SQLException ex) {
                        System.out.println(ex);
                    }}else if (data.equals("weather")){
                    SendMessage sendMessage = new SendMessage();
                    ResultSet resultSet23;
                    String sqlWeather = "SELECT name, location FROM table_weather WHERE chatID = " + query.getMessage().getChatId().toString() + ";";
                    try {
                        resultSet23 = botSettings.getStatement().executeQuery(sqlWeather);
                        sendMessage.setChatId(query.getMessage().getChatId().toString());
                        sendMessage.setText(resultSet23.getString("name") + ", Информация о погоде по Вашему точному местоположению ниже: \n" + parserWeather.getWeather(resultSet23.getString("location")));
                        sendMessage.setReplyMarkup(getInlKeyboardBack());
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                        execute(sendMessage);
                        resultSet23.close();
                        System.out.println(query.getMessage().getChatId().toString() + "Запросил информацию о погоде по точному местоположению.");
                    } catch (TelegramApiException | IOException | SQLException e) {
                        System.out.println(e);
                    }}else if (data.equals("find_users_vk")){
                    SendMessage smessage = new SendMessage();
                    smessage.setChatId(query.getMessage().getChatId().toString());
                    smessage.setText(FIO_USER_FIND);
                    ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
                    forceReply.setForceReply(true);
                    forceReply.setSelective(true);
                    smessage.setReplyMarkup(forceReply);
                    try {
                        execute(smessage);
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                        answerCallbackQuery.setCallbackQueryId(query.getId());
                        execute(answerCallbackQuery);
                    } catch (TelegramApiException e) {
                        //e.printStackTrace();
                        System.out.println(e);
                    }} else if (data.startsWith("delete_")) {
                    String chatID = data.split("_")[1].split(":")[0]; // Получаем chatID из callback data
                    String sqlDelete = "DELETE FROM table_weather WHERE chatID = ?";
                                try{
                                    PreparedStatement prstDel = botSettings.getConnectMysql().prepareStatement(sqlDelete);
                                    prstDel.setString(1, chatID);
                                    prstDel.executeUpdate();//для удаления множества не прерывая цикл чистим базу от тех кто заблокировал бота
                                    prstDel.close();
                                }catch (SQLException e) {
                                    System.out.println(e);
                                }
                    DeleteMessage deleteMessage = new DeleteMessage();
                                deleteMessage.setChatId(query.getMessage().getChatId().toString());
                                deleteMessage.setMessageId(query.getMessage().getMessageId());
                                try {
                                    execute(deleteMessage);
                                }catch (TelegramApiException ex) {}
                }}}

        try {
            PreparedStatement securityUsers = botSettings.getStatement().getConnection().prepareStatement("SELECT permission FROM table_weather WHERE chatID = ?");
            securityUsers.setString(1, message.getChatId().toString());
            ResultSet rs = securityUsers.executeQuery() ;
            while (rs.next()) {
                userSecur = rs.getInt("permission");
            }rs.close();
            securityUsers.close();
        }catch (SQLException ex) {
            System.out.println(ex);
        }
        if(update.hasMessage()&&update.getMessage().hasText()){ //для проверки уровня доступа создана отдельная update проверка
            switch (message.getText()) {
                case "/start":
                    if (message.getChatId().toString().equals("523626416")) {
                        sendMsgHEADERAdm(message, "Здравствуйте, " + message.getFrom().getFirstName() + "! " + "Вы перешли на главную" + "\n" +
                                "где Вам доступны доп функции, т.к. Вы админ!");
                    }else {
                        try {
                            PreparedStatement statement = botSettings.getConnectMysql().prepareStatement("SELECT permission FROM table_weather WHERE chatID = ?");
                            statement.setString(1, message.getChatId().toString());
                            ResultSet result = statement.executeQuery();
                            if (result.next()) {
                                // Запись уже есть в базе данных
                                int permission = result.getInt("permission");
                                if (permission == 1) {
                                    // Разрешаем пользоваться функциями бота
                                    // Ничего не делаем, так как пользователь уже имеет разрешение
                                    sendMsgHEADEROther(message, message.getFrom().getFirstName() + ", приветствую!\nЯ умею рассказывать о погоде и фильмах. \n" +
                                            "Если интересно получать регулярную информацию о погоде по Вашему местоположению, подпишитесь на рассылку в разделе 'Информация о погоде'. \n" +
                                            "Я умею искать информацию о фильмах в торрент трекере. \n" +
                                            "Если хотите получать рассылку гороскопа, подпишитесь на нее в разделе 'Сервисы'" + "\n" +
                                            "Ожидаете какой-то фильм, но кинокомпании нас покинули, не беда, добавьте наименование ожидаемого Вами фильма в список ожидаемых" +
                                            "и я пришлю Вам уведомление, когда данный фильм можно будет скачать с торрента, приложив ссылку для скачивания." + "\n" +
                                            "Если бот завис или не отображается клавиатура, просто напишите любую букву или фразу и она появится.");
                                    System.out.println(message.getChatId() + ": Стартанул бота");
                                }
                            } else {
                                // Записи в базе данных для текущего пользователя нет
                                // Добавляем пользователя в базу данных со значением разрешения null
                                PreparedStatement insertStatement = botSettings.getConnectMysql().prepareStatement("INSERT INTO table_access_user (chatID, permission, name) VALUES (?, NULL, '" + message.getFrom().getFirstName().replace("'", "") + "');");
                                insertStatement.setString(1, message.getChatId().toString());
                                insertStatement.executeUpdate();
                                insertStatement.close();
                                // Отправляем сообщение администратору
                                SendMessage startMessage = new SendMessage(message.getChatId().toString(), "Запрос на доступ отправлен администратору. " +
                                        "Если хотите увеличить шансы получить доступ, пишите письмо на почту - trashenkov-aleks@mail.ru " +
                                        "с указанием Вашего имени и логина в телеграмм.");
                                try {
                                    execute(startMessage);
                                } catch (TelegramApiException ex) {
                                    System.out.println(ex);
                                }
                                sendToAdmin(startMessage, message.getFrom().getFirstName(), message.getChatId().toString(), message.getFrom().getId().toString(), message.getMessageId());
                            }statement.close();
                            result.close();
                        } catch (SQLException ex) {
                            System.out.println(ex);
                        }}break;}}
        if(update.getMessage().hasLocation() && userSecur != 0){
            System.out.println("Запись местоположения для: " + message.getChatId().toString());
            String sqlLocSave = null;
            try {
                sqlLocSave = "UPDATE table_weather SET location = '" + update.getMessage().getLocation().toString() + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "', name = '" + message.getFrom().getFirstName().replace("'", "") + "', city = '" + geocoder.getGeoPositionCityName(update.getMessage().getLocation().toString()) + "' WHERE chatID = " + message.getChatId().toString() + ";";
                // Отправка уведомления о выполнении предыдущего запроса
                SendChatAction chatAction = new SendChatAction();
                chatAction.setChatId(message.getChatId().toString());
                chatAction.setAction(ActionType.FINDLOCATION); // "typing" - действие ожидания отправки сообщения
                try {
                    execute(chatAction);
                } catch (TelegramApiException e) {
                    System.out.println(e);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            try {
                botSettings.getStatement().execute(sqlLocSave); //записываем местоположение в БД
            } catch (SQLException e) {
                System.out.println(e);
            }}
        else if(update.hasMessage()&&update.getMessage().hasText() && userSecur != 0){
            // Отправка уведомления о выполнении предыдущего запроса
            SendChatAction chatAction = new SendChatAction();
            chatAction.setChatId(message.getChatId().toString());
            chatAction.setAction(ActionType.TYPING); // "typing" - действие ожидания отправки сообщения
            ResultSet resultSet3;
            String sqlWeather = "SELECT Subscr, location, permission FROM table_weather WHERE chatID = " + message.getChatId().toString() + ";";
            try {
                resultSet3 = botSettings.getStatement().executeQuery(sqlWeather);
                resultSet3.close();
            } catch (SQLException e) {
                System.out.println(e);
            }
            switch (message.getText()) {
                case "Сервисы":
                    String sqlZod6 = "SELECT zodiacSignSubs FROM table_weather WHERE chatID = " + message.getChatId().toString() + ";";
                    try {
                        ResultSet resultSet34 = botSettings.getStatement().executeQuery(sqlZod6);
                        try {execute(chatAction); //Отправка уведомления о ответе Бота
                        } catch (TelegramApiException e) {
                            System.out.println(e);}
                        if (resultSet34.getInt("zodiacSignSubs") == 1) {
                            sendMsYes(message, "Выберите сервис: ");
                        } else {
                            sendMsNo(message, "Выберите сервис: ");
                        }resultSet34.close();
                        System.out.println(message.getChatId() + ": Открыл раздел Сервисов");
                    } catch (SQLException e) {
                        System.out.println(e);
                    }break;
                case "Информация о погоде":
                    try {
                        ResultSet resultSet34 = botSettings.getStatement().executeQuery(sqlWeather);
                        try {execute(chatAction); //Отправка уведомления о ответе Бота
                        } catch (TelegramApiException e) {
                            System.out.println(e);}
                        if (resultSet34.getInt("Subscr") == 0) {
                            sendMsgWEATHERSubYes(message, message.getFrom().getFirstName() + ", Вы перешли в раздел о погоде");
                            System.out.println(message.getChatId() + ": Перешел в раздел 'О погоде'");
                        } else {
                            sendMsgWEATHERSubNo(message, message.getFrom().getFirstName() + ", Вы перешли в раздел о погоде");
                            System.out.println(message.getChatId() + ": Перешел в раздел 'О погоде'");
                        }resultSet34.close();} catch (SQLException e) {
                        //e.printStackTrace();
                        System.out.println(e);
                    }break;
                case "Узнать погоду":
                    try {
                        ResultSet resultSet34 = botSettings.getStatement().executeQuery(sqlWeather);
                        String location = resultSet34.getString("location");
                        if (location == null || location.isEmpty() || location.equals("(null)")) {
                            try {
                                try {execute(chatAction); //Отправка уведомления о ответе Бота
                                } catch (TelegramApiException e) {
                                    System.out.println(e);}
                                if (resultSet34.getInt("Subscr") == 0) {
                                    sendMsgWEATHERSubYes(message, message.getFrom().getFirstName() + ", пожалуйста, сначала предоставьте мне данные о Вашей геопозиции");
                                    System.out.println(message.getChatId() + ": Не определил свое местоположение");
                                } else {
                                    sendMsgWEATHERSubNo(message, message.getFrom().getFirstName() + ", пожалуйста, сначала предоставьте мне данные о Вашей геопозиции");
                                    System.out.println(message.getChatId() + ": Не определил свое местоположение");
                                }resultSet34.close();} catch (SQLException e) {
                                //e.printStackTrace();
                                System.out.println(e);
                            }} else {
                            try {
                                ResultSet resultSet23 = botSettings.getStatement().executeQuery(sqlWeather);
                                try {execute(chatAction); //Отправка уведомления о ответе Бота
                                } catch (TelegramApiException e) {
                                    System.out.println(e);}
                                if (resultSet23.getInt("Subscr") == 0) {
                                    sendMsgWEATHERSubYes(message, message.getFrom().getFirstName() + ", Информация о погоде для Вас ниже: \n" + parserWeather.getWeather(resultSet23.getString("location")));
                                } else {
                                    sendMsgWEATHERSubNo(message, message.getFrom().getFirstName() + ", Информация о погоде для Вас ниже: \n" + parserWeather.getWeather(resultSet23.getString("location")));
                                }resultSet23.close();} catch (IOException | SQLException e) {
                                //e.printStackTrace();
                                System.out.println(e);
                                try {
                                    ResultSet resultSet2 = botSettings.getStatement().executeQuery(sqlWeather);
                                    try {execute(chatAction); //Отправка уведомления о ответе Бота
                                    } catch (TelegramApiException ex) {
                                        System.out.println(ex);}
                                    if (resultSet2.getInt("Subscr") == 0) {
                                        sendMsgWEATHERSubYes(message, message.getFrom().getFirstName() + ", Сервис временно недоступен. Попробуйте запросить позднее");
                                        System.out.println("Ошибка запроса погоды");
                                    } else {
                                        sendMsgWEATHERSubNo(message, message.getFrom().getFirstName() + ", Сервис временно недоступен. Попробуйте запросить позднее");
                                        System.out.println("Ошибка запроса погоды");
                                    }resultSet2.close();} catch (SQLException ex) {
                                    //ex.printStackTrace();
                                    System.out.println("Ошибка SQL " + ex);
                                }}
                            System.out.println(message.getChatId() + ": Запросил информацию о погоде");
                            //botSettings.setLocation(null); //после отправки запроса информации о погоде, обнуляем геопозицию, чтобы она не была доступна другим пользователям
                        }} catch (SQLException e) {
                        System.out.println("Ошибка SQL " + e);
                    }break;
                case "Подписаться на рассылку о погоде":
                    String sqlWeather1 = "UPDATE table_weather SET Subscr = '" + "1" + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + message.getChatId().toString() + ";";
                    try {
                        try {execute(chatAction); //Отправка уведомления о ответе Бота
                        } catch (TelegramApiException e) {
                            System.out.println(e);}
                        botSettings.getStatement().execute(sqlWeather1);
                    } catch (SQLException e) {
                        e.printStackTrace();}
                    sendMsgWEATHERSubNo(message, message.getFrom().getFirstName() + ", Вы подписались на рассылку о погоде");
                    System.out.println(message.getChatId() + ": Подписался на рассылку о погоде");
                    break;
                case "Отписаться от рассылки о погоде":
                    String sqlWeather2 = "UPDATE table_weather SET Subscr = '" + "0" + "', last_activ = '" + dateTimeFormatter.format(LocalDateTime.now()) + "' WHERE chatID = " + message.getChatId().toString() + ";";
                    try {
                        try {execute(chatAction); //Отправка уведомления о ответе Бота
                        } catch (TelegramApiException e) {
                            System.out.println(e);}
                        botSettings.getStatement().execute(sqlWeather2);
                    } catch (SQLException e) {
                        System.out.println(e);}
                    sendMsgWEATHERSubYes(message, message.getFrom().getFirstName() + ", Вы отписались от рассылки о погоде");
                    System.out.println(message.getChatId() + ": Отписался от рассылки о погоде");
                    break;
                case "Раздел Админа":
                    try {execute(chatAction); //Отправка уведомления о ответе Бота
                    } catch (TelegramApiException e) {
                        System.out.println(e);}
                    if (message.getChatId().toString().equals("523626416")) {
                        sendMsgAdm(message, message.getFrom().getFirstName() + ", Вы перешли в раздел администратора");
                    }break;
                case "Лимиты" :
                    if (message.getChatId().toString().equals("523626416")) {
                        try {execute(chatAction); //Отправка уведомления о ответе Бота
                        } catch (TelegramApiException e) {
                            System.out.println(e);}
                        try {try {execute(chatAction); //Отправка уведомления о ответе Бота
                        } catch (TelegramApiException e) {
                            System.out.println(e);}
                            sendMsgAdm(message, message.getFrom().getFirstName() + ", запрошенная Вами информация ниже: \n" + servise.getLimits());
                            System.out.println(message.getChatId() + ": Запросил сервисную информацию.");
                        } catch (IOException e) {
                            //e.printStackTrace();
                            System.out.println("Ошибка запроса статистики.");
                            sendMsgAdm(message, message.getFrom().getFirstName() + ", \n К сожалению, что-то пошло не так, либо сервис недоступен. Скоро он снова будет работать!");
                        }}break;
                case "Выгрузить пользователей" :
                    if (message.getChatId().toString().equals("523626416")) {
                        try {execute(chatAction); //Отправка уведомления о ответе Бота
                        } catch (TelegramApiException e) {
                            System.out.println(e);}
                        String sqlUserList = "SELECT chatID, name, last_activ, zodiacSign, city, permission FROM table_weather";
                        try{
                            ResultSet resultSet = botSettings.getStatement().executeQuery(sqlUserList);
                            while (resultSet.next()) {
                                String security;
                                switch (resultSet.getInt("permission")) {
                                    case 1 : security = "Имеет доступ к сервису";break;
                                    case 0 : security = "Заблокирован";break;
                                    default: security = "На рассмотрении у админа";break;
                                }
                                sendMsgAdm(message, "Номер активного чата: "+resultSet.getString("chatID")+"\n"+
                                        "Имя пользователя: "+resultSet.getString("name")+"\n"+
                                        "Дата последней активности: "+resultSet.getString("last_activ")+"\n"+
                                        "Знак зодиака: "+resultSet.getString("zodiacSign")+"\n"+
                                        "Город: "+resultSet.getString("city")+"\n"+
                                        "Доступ: " + security);
                                Thread.sleep(2000);
                            }resultSet.close();}catch (SQLException ex) {
                            System.out.println(ex);
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }
                    }break;
                case "Выгрузить фильмы за день" :
                    if (message.getChatId().toString().equals("523626416")) {
                        try {execute(chatAction); //Отправка уведомления о ответе Бота
                        } catch (TelegramApiException e) {
                            System.out.println(e);}
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        LocalDateTime endDate = LocalDateTime.now();
                        LocalDateTime StartDate;
                        String sqlListFilmsDay = "SELECT url, day FROM table_films";
                        try{
                            ResultSet resultSet = botSettings.getStatement().executeQuery(sqlListFilmsDay);
                            while (resultSet.next()){
                                StartDate = LocalDateTime.parse(resultSet.getString("day"), formatter);
                                if (ChronoUnit.DAYS.between(StartDate,endDate) == 0) {
                                    sendMsgAdm(message, resultSet.getString("url"));
                                    Thread.sleep(2000);
                                }}resultSet.close();}catch (SQLException | InterruptedException es){
                            System.out.println(es);}}break;
                case "Назад":
                    try {execute(chatAction); //Отправка уведомления о ответе Бота
                    } catch (TelegramApiException e) {
                        System.out.println(e);}
                    if (message.getChatId().toString().equals("523626416")) {
                        sendMsgHEADERAdm(message, message.getFrom().getFirstName() + ", вернулись на главную");
                    } else {
                        sendMsgHEADEROther(message, message.getFrom().getFirstName() + ", вернулись на главную");
                    }System.out.println(message.getChatId() + ": Нажал 'Назад'");
                    break;
                case "Рассылка всем" :
                    SendMessage smessage = new SendMessage();
                    smessage.setChatId(message.getChatId().toString());
                    smessage.setText(SEND_LABEL);
                    ForceReplyKeyboard forceReply = new ForceReplyKeyboard();
                    forceReply.setForceReply(true);
                    forceReply.setSelective(true);
                    smessage.setReplyMarkup(forceReply);
                    try {
                        execute(smessage);
                    }catch (TelegramApiException ex) {
                        System.out.println(ex);
                    }
                    break;
                default:
                    SendMessage sendMessage = new SendMessage();
                    String userInput = message.getText().substring(0,1).toUpperCase() + message.getText().substring(1);
                    if (message.getText().startsWith("Бот,") || message.getText().startsWith("бот,") || message.getText().startsWith("Бот") || message.getText().startsWith("бот")) {
                        int idBot = message.getText().indexOf("Бот,");
                        try {
                            try {execute(chatAction); //Отправка уведомления о ответе Бота
                            } catch (TelegramApiException e) {
                                System.out.println(e);}
                            if (message.getChatId().toString().equals("523626416")) {
                                sendMsgHEADERAdm(message, message.getFrom().getFirstName() + ", " + openAiApiExample.getResponse(message.getText().substring(idBot + 5)));
                            } else {
                                sendMsgHEADEROther(message, message.getFrom().getFirstName() + ", " + openAiApiExample.getResponse(message.getText().substring(idBot + 5)));
                            }} catch (IOException | InterruptedException ioException) {
                            try {execute(chatAction); //Отправка уведомления о ответе Бота
                            } catch (TelegramApiException e) {
                                System.out.println(e);}
                            if (message.getChatId().toString().equals("523626416")) {
                                sendMsgHEADERAdm(message, message.getFrom().getFirstName() + ",\nПростите что-то пошло не так... Попробуйте позже.");
                            } else {
                                sendMsgHEADEROther(message, message.getFrom().getFirstName() + ",\nПростите что-то пошло не так... Попробуйте позже.");
                            }}System.out.println(message.getChatId() + ": Общается с ИИ: " + message.getText());
                    }
                    // Обработка ответа на запрос названия торрента
                    else if (message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals("Введите название торрента:")) {
                        // Добавьте ваш код для обработки названия торрента здесь
                        try {
                            try {execute(chatAction); //Отправка уведомления о ответе Бота
                            } catch (TelegramApiException e) {
                                System.out.println(e);}

                                HashMap<String, ArrayList<String>> names = new HashMap<>();
                                ArrayList<String> urls = new ArrayList<>();
                                try (PreparedStatement stmt = botSettings.getStatement().getConnection().prepareStatement("SELECT name, url FROM table_films");
                                     ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        /*if (rs.getString("name").toLowerCase().replace("«","").replace("»","").replace("ё", "е").replace("Ё", "Е")
                                                .replace(":","").replace("-"," ").split(" /")[0]
                                                .equals(userInput.toLowerCase().replace("«","").replace("»","").replace("ё", "е").replace("Ё", "Е")
                                                        .replace(":","").replace("-"," "))){*/
                                        int idFindWords = rs.getString("name").toLowerCase().replace("«","").replace("»","").replace("ё", "е").replace("Ё", "Е")
                                                .replace(": "," ").replace("-"," ").replace(", ", " ").replace("!", "").replace(". "," ").indexOf(userInput.toLowerCase().replace("«","").replace("»","").replace("ё", "е").replace("Ё", "Е")
                                                        .replace(": "," ").replace("-"," ").replace(", ", " ").replace("!", "").replace(". "," "));
                                        if (!(idFindWords < 0)) {
                                            urls.add(rs.getString("url"));
                                        }}stmt.close();} catch (SQLException e) {
                                    System.out.println(e);}
                                    names.put(userInput, urls);
                                if (!urls.isEmpty()) {
                                    sendMessage.setChatId(message.getChatId().toString());
                                sendMessage.setText(message.getFrom().getFirstName() + ", нашел в базе следующее: "+"\n" + "Введенное название: "+names.toString().replace("{", "")
                                        .replace("]}", "").replace("=[", "\n").replace(",", "\n"));
                                sendMessage.setReplyMarkup(getInlineKeyboardTorr());
                                    try {
                                        execute(sendMessage);
                                    }catch (TelegramApiException ex) {
                                        System.out.println(ex);
                                    }} else { String res = filmFounder.getFilm(userInput, DRAYVER_PATH);
                                 if (res != null) {
                                sendMessage.setChatId(message.getChatId().toString());
                                sendMessage.setText(message.getFrom().getFirstName() + ",\nНашел первое совпадение на торрент сайте: \n" + res);
                                sendMessage.setReplyMarkup(getInlineKeyboardTorr());
                                     try {
                                         execute(sendMessage);
                                     }catch (TelegramApiException ex) {
                                         System.out.println(ex);
                                     }
                                } else  {sendMessage.setChatId(message.getChatId().toString());
                                    sendMessage.setText(message.getFrom().getFirstName() + ", к сожалению, ни на торрент сайте, ни в базе, ничего не найдено.");
                                    sendMessage.setReplyMarkup(getInlineKeyboardTorr());
                                     try {
                                         execute(sendMessage);
                                     }catch (TelegramApiException ex) {
                                         System.out.println(ex);
                                     }}
                                names.clear(); urls.clear();}System.out.println(message.getChatId() + ": Запросил торрент по названию: " + userInput);
                        } catch (IOException | InterruptedException e) {
                            sendMessage.setChatId(message.getChatId().toString());
                            sendMessage.setText(message.getFrom().getFirstName() + ", \n К сожалению, что-то пошло не так, либо сервис недоступен. Скоро он снова будет работать!");
                            sendMessage.setReplyMarkup(getInlineKeyboardTorr());
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException ex) {
                                System.out.println(ex);
                            }}}
                    // Обработка ответа на запрос названия фильма
                    else if (message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals("Введите название фильма или сериала:")) {
                        // Добавьте ваш код для обработки названия фильма здесь
                        try {
                            try {execute(chatAction); //Отправка уведомления о ответе Бота
                            } catch (TelegramApiException e) {
                                System.out.println(e);}
                            sendMessage.setChatId(message.getChatId().toString());
                            sendMessage.setText(message.getFrom().getFirstName() + ", вот что я нашел: \n" +filmInformer.getFilmInfo(userInput));
                            sendMessage.setReplyMarkup(getInlKeyboardBack());
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException ex) {
                                System.out.println(ex);
                            }
                            System.out.println(message.getChatId() + ": Запросил фильм по названию: " + userInput);
                        } catch (IOException e) {
                            sendMsNo(message, message.getFrom().getFirstName() +", \n К сожалению, что-то пошло не так, либо сервис недоступен. Скоро он снова будет работать!");
                        } catch (InterruptedException e) {
                           System.out.println(e);
                        }
                    }else if (message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals("Введите название ожидаемого фильма или сериала:")) {
                        try {
                            try {execute(chatAction); //Отправка уведомления о ответе Бота
                            } catch (TelegramApiException e) {
                                System.out.println(e);}
                            String sqlWantWatch = "INSERT INTO table_want_watch (filmName, chatID, name, yes) VALUES ('"+userInput+"', '"+message.getChatId() +"', '"+message.getFrom().getFirstName()+"', '0')";
                            botSettings.getStatement().execute(sqlWantWatch);
                            sendMessage.setChatId(message.getChatId().toString());
                            sendMessage.setText(message.getFrom().getFirstName() + ", я записал фильм, который Вы желаете посмотреть: " + userInput+"\n" +
                                    "Как только данный торрент появится в моей базе, я вышлю Вам уведомление.");
                            sendMessage.setReplyMarkup(getInlineKeyboardTorr());
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException ex) {
                                System.out.println(ex);
                            }System.out.println(message.getChatId()+ ": Записал фильм, который хочет посмотреть");
                        }catch (SQLException e) {
                            System.out.println(e);
                        }}else if (message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals("Введите название фильма для удаления:")) {
                        try {
                            try {execute(chatAction); //Отправка уведомления о ответе Бота
                            } catch (TelegramApiException e) {
                                System.out.println(e);}
                            String sqlDelete = "DELETE FROM table_want_watch WHERE chatID= ? AND filmName = ?";
                            PreparedStatement prstDel = botSettings.getConnectMysql().prepareStatement(sqlDelete);
                            prstDel.setString(1, message.getChatId().toString());
                            prstDel.setString(2, userInput);
                            int resultDel = prstDel.executeUpdate();
                            if (resultDel > 0){
                                sendMessage.setChatId(message.getChatId().toString());
                                sendMessage.setText(message.getFrom().getFirstName() + ",\nфильм: "+userInput+" успешно удален из списка ожидаемых.");
                                sendMessage.setReplyMarkup(getInlineKeyboardTorr());
                                try {
                                    execute(sendMessage);
                                }catch (TelegramApiException ex) {
                                    System.out.println(ex);
                                }}else {
                                sendMessage.setChatId(message.getChatId().toString());
                                sendMessage.setText(message.getFrom().getFirstName() + ",\nк сожалению, фильм: "+userInput+" не найден в списке, либо был удален ранее.");
                                sendMessage.setReplyMarkup(getInlineKeyboardTorr());
                                try {
                                    execute(sendMessage);
                                }catch (TelegramApiException ex) {
                                    System.out.println(ex);
                                }}prstDel.close();
                            System.out.println(message.getChatId().toString() +": Удалил фильм из своего списка ожидания.");
                        }catch (SQLException ex) {
                            System.out.println(ex);
                        }} else if (message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals("Введите текст для рассылки:")) {
                        String sqlSender = "SELECT chatID, name FROM table_weather";
                        String chatID = null;
                        String userName = null;
                        try {
                            ResultSet resultSetSender = botSettings.getStatement().executeQuery(sqlSender);
                            while (resultSetSender.next()) {
                                if (!resultSetSender.getString("chatID").equals("523626416")) {
                                    chatID = resultSetSender.getString("chatID");
                                    userName = resultSetSender.getString("name");
                                    sendMessage.setText(userInput);
                                    sendMessage.setChatId(chatID);
                                    execute(sendMessage);
                                }else {
                                    sendMsgAdm(message, "Рассылка произведена!");
                                }}}catch (SQLException ex) {
                            System.out.println(ex);
                        } catch (TelegramApiException e) {
                            System.out.println(e);
                            sendMessage.setText("Ошибка рассылки для: \n" + chatID + "\nИмя: " +userName);
                            sendMessage.setReplyMarkup(getInlKeyboardUserDel(chatID, userName));
                            sendMessage.setChatId("523626416");
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException ex) {
                                System.out.println(ex);
                            }}} else if (message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals("Введите имя актера-режиссера:")) {
                        try {
                            sendMessage.setChatId(message.getChatId().toString());
                            sendMessage.setText(filmInformer.getPeoples(userInput));
                            sendMessage.setReplyMarkup(getInlKeyboardBack());
                            execute(sendMessage);
                        }catch (TelegramApiException | IOException ex) {
                            System.out.println(ex);
                            sendMessage.setChatId(message.getChatId().toString());
                            sendMessage.setText("Ничего не найдено.");
                            sendMessage.setReplyMarkup(getInlKeyboardCallBack());
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException es) {
                                System.out.println(es);
                            }}catch (InterruptedException e){
                            System.out.println(e);
                        }}else if (message.getReplyToMessage() != null && message.getReplyToMessage().getText().equals("Введите Имя и Фамилию человека:")) {
                        try {
                            sendMessage.setChatId(message.getChatId().toString());
                            sendMessage.setText(vkPostGroup.getUserVK(userInput));
                            sendMessage.setReplyMarkup(getInlKeyboardBack());
                            execute(sendMessage);
                        }catch (TelegramApiException | IOException ex) {
                            System.out.println(ex);
                            sendMessage.setChatId(message.getChatId().toString());
                            sendMessage.setText("Ничего не найдено.");
                            sendMessage.setReplyMarkup(getInlKeyboardCallBack());
                            try {
                                execute(sendMessage);
                            }catch (TelegramApiException es) {
                                System.out.println(es);
                            }} catch (InterruptedException e) {
                            System.out.println(e);
                        }} else {
                        if (message.getChatId().toString().equals("523626416")) {
                            //sendMsgHEADERAdm(message, message.getFrom().getFirstName() + ", " + message.getText() + " - Неизвестная команда");
                            SendInvoice sendInvoice = new SendInvoice();
                            sendInvoice.setChatId("523626416");
                            sendInvoice.setProviderToken("PAYMENT_PROVIDER_TOKEN");
                            sendInvoice.setTitle("Оплата за продукт или услугу");
                            sendInvoice.setDescription("Описание продукта или услуги");
                            sendInvoice.setCurrency("RUB"); // Валюта платежа
                            sendInvoice.setPrices(Arrays.asList(new LabeledPrice("Сумма оплаты", 1000))); // Сумма в центах (например, $10)
                            sendInvoice.setStartParameter("invoice-" + System.currentTimeMillis()); // Уникальный параметр начала оплаты

                            try {
                                execute(sendInvoice);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        } else {
                            sendMsgHEADEROther(message, message.getFrom().getFirstName() + ", " + message.getText() + " - Неизвестная команда");
                        }
                        System.out.println(message.getChatId() + ": Ввел неизвестную команду: " + message.getText());
                    }break;}}}
    private void sendToAdmin(SendMessage message, String name, String chatId, String userID, int getMessageId) {
        message.setChatId("523626416");
        message.setText("Поступил запрос на доступ для:\n" +"Имя: "+name + "\n"+"ChatID: "+chatId);
        message.enableMarkdown(true);

        // Создаем клавиатуру
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        message.setReplyMarkup(inlineKeyboardMarkup);

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаем первую строку кнопок
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(TRUE_BUTTON);
        button1.setCallbackData("TRUE_"+chatId+":"+getMessageId+">"+name);
        row1.add(button1);
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(FALSE_BUTTON);
        button2.setCallbackData("FALSE_"+chatId+":"+userID);
        row1.add(button2);
        keyboard.add(row1);

        inlineKeyboardMarkup.setKeyboard(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMsgAdm(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);
        KeyboardButton keyboardButton1 = new KeyboardButton(STATISTICS_BUTTON);
        keyboardRow1.add(keyboardButton1);
        KeyboardButton keyboardButton3 = new KeyboardButton(LIST_FILMS_AT_DAY);
        keyboardRow1.add(keyboardButton3);

        KeyboardRow keyboardRow2 = new KeyboardRow(); //второй ряд главных кнопок
        keyboardRows.add(keyboardRow2);
        KeyboardButton keyboardButton = new KeyboardButton(LIMITS_BUTTON);
        keyboardRow2.add(keyboardButton);
        KeyboardButton keyboardButton4 = new KeyboardButton(SEND_BUTTON);
        keyboardRow2.add(keyboardButton4);
        KeyboardButton keyboardButton2 = new KeyboardButton(BACK_BUTTON_IN_HEAD_MENU);
        keyboardRow2.add(keyboardButton2);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }

    public InlineKeyboardMarkup getInlKeyboardHorYes () {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаем первую строку кнопок
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(TORRENT_BUTTON_TEXT);
        button1.setCallbackData("torrent");
        row1.add(button1);
        keyboard.add(row1);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(FILM_BUTTON_TEXT);
        button2.setCallbackData("movie");
        row3.add(button2);
        keyboard.add(row3);
        // Создаем вторую строку кнопок
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText(HOROSCOPE_BUTTON_TEXTNO);
        button3.setCallbackData("horoscopeYes");
        row2.add(button3);
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText(STATIONS_BUTTON_TEXT);
        button4.setCallbackData("station");
        row2.add(button4);
        keyboard.add(row2);

        List<InlineKeyboardButton> row7 = new ArrayList<>();
        InlineKeyboardButton button10 = new InlineKeyboardButton();
        button10.setText(FILMS_FOR_NAME);
        button10.setCallbackData("films_people");
        row7.add(button10);
        keyboard.add(row7);
        List<InlineKeyboardButton> row8 = new ArrayList<>();
        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText(FIND_USERS_VK);
        button11.setCallbackData("find_users_vk");
        row8.add(button11);
        keyboard.add(row8);
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText(COME_BACK_BUTT);
        button9.setCallbackData("come_back");
        row6.add(button9);
        keyboard.add(row6);

        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboardTorr () {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(TORRENT_INFO_BUTTON);
        button.setCallbackData("torrent_info");
        row.add(button);
        keyboard.add(row);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(WANT_WATCH_BUTTON);
        button2.setCallbackData("want_watch");
        row2.add(button2);
        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText(LIST_WATCH_BUTTON);
        button6.setCallbackData("list_watch");
        row2.add(button6);
        keyboard.add(row2);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText(CLEAR_LIST_FILMS_BUTTON);
        button3.setCallbackData("clear_list");
        row3.add(button3);
        keyboard.add(row3);
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText(CLEAR_LIST_FILM_NAME_BUTTON);
        button4.setCallbackData("clear_film_name");
        row4.add(button4);
        keyboard.add(row4);
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText(BUTT_BACK);
        button5.setCallbackData("back");
        row5.add(button5);
        keyboard.add(row5);

        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlKeyboardBack () {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(BUTT_BACK_MENU);
        button.setCallbackData("back");
        row.add(button);
        keyboard.add(row);
        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getInlKeyboardCallBack () {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(COME_BACK_BUTT);
        button.setCallbackData("come_back");
        row.add(button);
        keyboard.add(row);
        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlKeyboardWeather () {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(WEATHER_BUTTON_INF);
        button.setCallbackData("weather");
        row.add(button);
        keyboard.add(row);
        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getInlKeyboardUserDel (String errChatID, String errUserName) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(BUTTON_USER_DEL);
        button.setCallbackData("delete_"+errChatID);
        row.add(button);
        keyboard.add(row);
        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getLocationButt () {
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);

        KeyboardButton keyboardButton1 = new KeyboardButton(LOCATION_BUTTON);
        keyboardButton1.setRequestLocation(true);
        keyboardRow1.add(keyboardButton1);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
    public InlineKeyboardMarkup getInlKeyboardHorZod () {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        // Создаем первую строку кнопок
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Овен");
        button1.setCallbackData("aries");
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Телец");
        button2.setCallbackData("taurus");
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Близнецы");
        button3.setCallbackData("gemini");
        row1.add(button1);
        row1.add(button2);
        row1.add(button3);
        keyboard.add(row1);
        // Создаем вторую строку кнопок
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("Рак");
        button4.setCallbackData("cancer");
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("Лев");
        button5.setCallbackData("leo");
        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("Дева");
        button6.setCallbackData("virgo");
        row2.add(button4);
        row2.add(button5);
        row2.add(button6);
        keyboard.add(row2);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton button7 = new InlineKeyboardButton();
        button7.setText("Весы");
        button7.setCallbackData("libra");
        InlineKeyboardButton button8 = new InlineKeyboardButton();
        button8.setText("Скорпион");
        button8.setCallbackData("scorpio");
        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText("Стрелец");
        button9.setCallbackData("sagittarius");
        row3.add(button8);
        row3.add(button7);
        row3.add(button9);
        keyboard.add(row3);
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton button10 = new InlineKeyboardButton();
        button10.setText("Козерог");
        button10.setCallbackData("capricorn");
        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText("Водолей");
        button11.setCallbackData("aquarius");
        InlineKeyboardButton button12 = new InlineKeyboardButton();
        button12.setText("Рыбы");
        button12.setCallbackData("pisces");
        row4.add(button10);
        row4.add(button11);
        row4.add(button12);
        keyboard.add(row4);
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        InlineKeyboardButton button13 = new InlineKeyboardButton();
        button13.setText(BUTT_BACK);
        button13.setCallbackData("back");
        row5.add(button13);
        keyboard.add(row5);
        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup getInlKeyboardHorNo () {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаем первую строку кнопок
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(TORRENT_BUTTON_TEXT);
        button1.setCallbackData("torrent");
        row1.add(button1);
        keyboard.add(row1);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(FILM_BUTTON_TEXT);
        button2.setCallbackData("movie");
        row3.add(button2);
        keyboard.add(row3);
        // Создаем вторую строку кнопок
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText(HOROSCOPE_BUTTON_TEXT);
        button3.setCallbackData("horoscopeNo");
        row2.add(button3);
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText(STATIONS_BUTTON_TEXT);
        button4.setCallbackData("station");
        row2.add(button4);
        keyboard.add(row2);
        List<InlineKeyboardButton> row7 = new ArrayList<>();
        InlineKeyboardButton button10 = new InlineKeyboardButton();
        button10.setText(FILMS_FOR_NAME);
        button10.setCallbackData("films_people");
        row7.add(button10);
        keyboard.add(row7);
        List<InlineKeyboardButton> row8 = new ArrayList<>();
        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText(FIND_USERS_VK);
        button11.setCallbackData("find_users_vk");
        row8.add(button11);
        keyboard.add(row8);
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText(COME_BACK_BUTT);
        button9.setCallbackData("come_back");
        row6.add(button9);
        keyboard.add(row6);

        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
    public ReplyKeyboardMarkup getReplyKeybAdm () {
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);
        KeyboardButton keyboardButton = new KeyboardButton(INFO_BUTTON_TEXT);
        keyboardRow1.add(keyboardButton);

        KeyboardRow keyboardRow2 = new KeyboardRow(); //второй ряд главных кнопок
        keyboardRows.add(keyboardRow2);
        KeyboardButton keyboardButton1 = new KeyboardButton(SERVICE_BUTTON_TEXT);
        keyboardRow2.add(keyboardButton1);
        KeyboardButton keyboardButton2 = new KeyboardButton(SERVICES_BUTTON_TEXT);
        keyboardRow2.add(keyboardButton2);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
    public ReplyKeyboardMarkup getReplyKeyboOther () {
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);
        KeyboardButton keyboardButton = new KeyboardButton(INFO_BUTTON_TEXT);
        keyboardRow1.add(keyboardButton);

        KeyboardRow keyboardRow2 = new KeyboardRow(); //второй ряд главных кнопок
        keyboardRows.add(keyboardRow2);
        KeyboardButton keyboardButton2 = new KeyboardButton(SERVICES_BUTTON_TEXT);
        keyboardRow2.add(keyboardButton2);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
    public void sendMsgHEADERAdm(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);
        KeyboardButton keyboardButton = new KeyboardButton(INFO_BUTTON_TEXT);
        keyboardRow1.add(keyboardButton);

        KeyboardRow keyboardRow2 = new KeyboardRow(); //второй ряд главных кнопок
        keyboardRows.add(keyboardRow2);
        KeyboardButton keyboardButton1 = new KeyboardButton(SERVICE_BUTTON_TEXT);
        keyboardRow2.add(keyboardButton1);
        KeyboardButton keyboardButton2 = new KeyboardButton(SERVICES_BUTTON_TEXT);
        keyboardRow2.add(keyboardButton2);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }
    public void sendMsgHEADEROtherReg(Message message, String text, String chatID, int getMessageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);
        KeyboardButton keyboardButton = new KeyboardButton(INFO_BUTTON_TEXT);
        keyboardRow1.add(keyboardButton);

        KeyboardRow keyboardRow2 = new KeyboardRow(); //второй ряд главных кнопок
        keyboardRows.add(keyboardRow2);
        KeyboardButton keyboardButton2 = new KeyboardButton(SERVICES_BUTTON_TEXT);
        keyboardRow2.add(keyboardButton2);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setChatId(chatID);
        sendMessage.setReplyToMessageId(getMessageId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }

    public void sendMsgHEADEROther(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);
        KeyboardButton keyboardButton = new KeyboardButton(INFO_BUTTON_TEXT);
        keyboardRow1.add(keyboardButton);

        KeyboardRow keyboardRow2 = new KeyboardRow(); //второй ряд главных кнопок
        keyboardRows.add(keyboardRow2);
        KeyboardButton keyboardButton2 = new KeyboardButton(SERVICES_BUTTON_TEXT);
        keyboardRow2.add(keyboardButton2);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }

    public void sendMsgWEATHERSubYes(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);

        KeyboardButton keyboardButton = new KeyboardButton(SUBSCR_BUTTON_WEATHER);
        keyboardRow1.add(keyboardButton);
        KeyboardButton keyboardButton1 = new KeyboardButton(LOCATION_BUTTON);
        keyboardButton1.setRequestLocation(true);
        keyboardRow1.add(keyboardButton1);

        KeyboardRow keyboardRow2 = new KeyboardRow(); //второй ряд главных кнопок
        keyboardRows.add(keyboardRow2);
        KeyboardButton keyboardButton3 = new KeyboardButton(WEATHER_BUTTON_INF);
        keyboardRow2.add(keyboardButton3);

        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRows.add(keyboardRow3);
        KeyboardButton keyboardButton4 = new KeyboardButton(BACK_BUTTON_IN_HEAD_MENU);
        keyboardRow2.add(keyboardButton4);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }

    public void sendMsgWEATHERSubNo(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        //replyKeyboardMarkup.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow(); //первый ряд главных кнопок
        keyboardRows.add(keyboardRow1);

        KeyboardButton keyboardButton = new KeyboardButton(SUBSCR_BUTTON_WEATHERNO);
        keyboardRow1.add(keyboardButton);
        KeyboardButton keyboardButton1 = new KeyboardButton(LOCATION_BUTTON);
        keyboardButton1.setRequestLocation(true);
        keyboardRow1.add(keyboardButton1);

        KeyboardRow keyboardRow2 = new KeyboardRow(); //второй ряд главных кнопок
        keyboardRows.add(keyboardRow2);
        KeyboardButton keyboardButton3 = new KeyboardButton(WEATHER_BUTTON_INF);
        keyboardRow2.add(keyboardButton3);

        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRows.add(keyboardRow3);
        KeyboardButton keyboardButton4 = new KeyboardButton(BACK_BUTTON_IN_HEAD_MENU);
        keyboardRow2.add(keyboardButton4);

        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }
    public void sendMsNo(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаем первую строку кнопок
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(TORRENT_BUTTON_TEXT);
        button1.setCallbackData("torrent");
        row1.add(button1);
        keyboard.add(row1);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(FILM_BUTTON_TEXT);
        button2.setCallbackData("movie");
        row3.add(button2);
        keyboard.add(row3);

        // Создаем вторую строку кнопок
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText(HOROSCOPE_BUTTON_TEXT);
        button3.setCallbackData("horoscopeNo");
        row2.add(button3);
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText(STATIONS_BUTTON_TEXT);
        button4.setCallbackData("station");
        row2.add(button4);
        keyboard.add(row2);

        List<InlineKeyboardButton> row7 = new ArrayList<>();
        InlineKeyboardButton button10 = new InlineKeyboardButton();
        button10.setText(FILMS_FOR_NAME);
        button10.setCallbackData("films_people");
        row7.add(button10);
        keyboard.add(row7);
        List<InlineKeyboardButton> row8 = new ArrayList<>();
        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText(FIND_USERS_VK);
        button11.setCallbackData("find_users_vk");
        row8.add(button11);
        keyboard.add(row8);
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText(COME_BACK_BUTT);
        button9.setCallbackData("come_back");
        row6.add(button9);
        keyboard.add(row6);

        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);
        /*sendMessage.setText("Выберите сервис: ");
        sendMessage.setChatId(chatID);*/
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }

    public void sendMsYes(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        // Создаем список кнопок
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаем первую строку кнопок
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(TORRENT_BUTTON_TEXT);
        button1.setCallbackData("torrent");
        row1.add(button1);
        keyboard.add(row1);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(FILM_BUTTON_TEXT);
        button2.setCallbackData("movie");
        row3.add(button2);
        keyboard.add(row3);

        // Создаем вторую строку кнопок
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText(HOROSCOPE_BUTTON_TEXTNO);
        button3.setCallbackData("horoscopeYes");
        row2.add(button3);
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText(STATIONS_BUTTON_TEXT);
        button4.setCallbackData("station");
        row2.add(button4);
        keyboard.add(row2);

        List<InlineKeyboardButton> row7 = new ArrayList<>();
        InlineKeyboardButton button10 = new InlineKeyboardButton();
        button10.setText(FILMS_FOR_NAME);
        button10.setCallbackData("films_people");
        row7.add(button10);
        keyboard.add(row7);
        List<InlineKeyboardButton> row8 = new ArrayList<>();
        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText(FIND_USERS_VK);
        button11.setCallbackData("find_users_vk");
        row8.add(button11);
        keyboard.add(row8);
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText(COME_BACK_BUTT);
        button9.setCallbackData("come_back");
        row6.add(button9);
        keyboard.add(row6);

        // Устанавливаем список кнопок на клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }
    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
