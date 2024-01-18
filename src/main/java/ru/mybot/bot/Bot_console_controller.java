package ru.mybot.bot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Bot_console_controller {
    BotSettings botSettings;

    @FXML
    private Button clearConsole;

    @FXML
    private TextArea log_text;

    @FXML
    private Button restart;

    @FXML
    private Button settings;

    @FXML
    private Button diagnosticDB;

    @FXML
    private Button stopBot;

    @FXML
    private Label welcomeText;

    @FXML
    void onHelloButtonClick(MouseEvent event) {

    }

    @FXML
    void initialize() throws SQLException, TelegramApiException {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setAllowedUpdates(List.of("message", "callback_query"));
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botSettings.setBotSession(telegramBotsApi.registerBot(new BotJarvisTelegramm(botOptions)));
        } catch (TelegramApiException | SQLException e) {
            e.printStackTrace();
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        log_text.setText(dateTimeFormatter.format(LocalDateTime.now()) + " : " + "Бот запущен" + "\n");
        botSettings = new BotSettings();
        System.setOut(new PrintStream(System.out) {
            public void println(String s) {
                log_text.appendText(dateTimeFormatter.format(LocalDateTime.now()) + " : " + s + "\n");
                //super.println(s);
            }
        });
        stopBot.setOnAction(event -> {
            botSettings.getBotSession().stop();
            stopBot.getScene().getWindow().hide();
        });
        clearConsole.setOnAction(event -> log_text.clear());
        settings.setOnAction(event -> {
            FXMLLoader fxmlLoaderTwo = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            try {
                fxmlLoaderTwo.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = fxmlLoaderTwo.getRoot();
            Stage stageTwo = new Stage();
            stageTwo.setScene(new Scene(root, 500, 500));
            stageTwo.setTitle("Бот телеграмм");
            InputStream iconStream = getClass().getResourceAsStream("bot_icon.png");
            Image image = new Image(iconStream);
            stageTwo.getIcons().add(image);
            stageTwo.showAndWait();
        });
        diagnosticDB.setOnAction(event -> {
            //String connectionString = "jdbc:sqlite::resource:DB_Bot.db";
            //String connectionString = "jdbc:sqlite:D:\\projects\\DB_Bot.db";
            String connectionString = "jdbc:sqlite:DB_Bot.db";
            String sqlReadTokNam = "SELECT rowid, token, name, driver_path FROM table_paths where rowid = 1;";
            String sqlTablWeath = "SELECT chatID, location, Subscr, last_activ, name, zodiacSignSubs, zodiacSign, sendDate, city FROM table_weather";
            String sqlTabFilmWatch = "SELECT chatID, name, filmName FROM table_want_watch";
            Connection connection;
            try {
                connection = DriverManager.getConnection(connectionString);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlReadTokNam);
                System.out.println("Информация о системных путях Бота: ");
                while (resultSet.next()) {
                    System.out.println("TOKEN: "+resultSet.getString("token"));
                    System.out.println("BOT_NAME: " + resultSet.getString("name"));
                    System.out.println("DRIVER_PATH: " + resultSet.getString("driver_path"));
                }
                System.out.println("___________________________________________________________________");
                ResultSet resultSet1 = statement.executeQuery(sqlTablWeath);
                System.out.println("Информация о пользователях Бота: ");
                while (resultSet1.next()) {
                    System.out.println("Номер активного чата: "+resultSet1.getString("chatID"));
                    System.out.println("Местоположение пользователя: " + resultSet1.getString("location"));
                    System.out.println("Наименование локации: " + resultSet1.getString("city"));
                    System.out.println("Подписка на рассылку погоды: " + resultSet1.getString("Subscr"));
                    System.out.println("Последняя активность: "+resultSet1.getString("last_activ"));
                    System.out.println("Имя: " + resultSet1.getString("name"));
                    System.out.println("Подписка на рассылку гороскопа: " + resultSet1.getString("zodiacSignSubs"));
                    System.out.println("Знак зодиака: " + resultSet1.getString("zodiacSign"));
                    System.out.println("Дата последней рассылки: " + resultSet1.getString("sendDate"));
                    System.out.println("___________________________________________________________________");
                }
                System.out.println("Информация о фильмах, которые хотят посмотреть: ");
                ResultSet resultSet2 = statement.executeQuery(sqlTabFilmWatch);
                while (resultSet2.next()){
                    System.out.println("Имя желающего: " + resultSet2.getString("name"));
                    System.out.println("Номер активного чата желающего: " + resultSet2.getString("chatId"));
                    System.out.println("Название ожидаемого фильма: " + resultSet2.getString("filmName"));
                    System.out.println("___________________________________________________________________");
                }

                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace();
                System.out.println(e);
            }
        });
/*        restart.setOnAction(event -> {
            // Получаем ссылку на текущий Stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Закрываем текущий Stage
            stage.close();
            // Вызываем статический метод restart класса Platform
            Platform.runLater(() -> {
                try {
                    // Получаем ссылку на класс, содержащий метод main()
                    Class<?> clazz = Class.forName("ru.mybot.bot.Main");
                    // Получаем метод main()
                    Method main = clazz.getMethod("main", String[].class);
                    // Вызываем метод main() с пустым массивом аргументов
                    main.invoke(null, (Object) new String[] {});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });*/
    }
}
