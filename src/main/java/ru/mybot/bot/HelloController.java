package ru.mybot.bot;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class HelloController {
    BotSettings botSettings;
    @FXML
    private VBox window1;

    @FXML
    private TextField BOT_NAME;

    @FXML
    private TextField BOT_TOKEN;

    @FXML
    private TextField DRAYVER_PATH;

    @FXML
    private Button writedb;

    @FXML
    private Button writedbdDEF;

    @FXML
    void onHelloButtonClick(MouseEvent event) {
    }

    @FXML
    void initialize() throws TelegramApiException {
        botSettings = new BotSettings();

        String connectionStringStart = "jdbc:sqlite:D:\\projects\\DB_Bot.db";
        String sqlReadTokNamStart = "SELECT rowid, token, name, driver_path FROM table_paths where rowid = 1;";
        Connection connectionStart;
        try {
            connectionStart = DriverManager.getConnection(connectionStringStart);
            Statement statement = connectionStart.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlReadTokNamStart);
            while (resultSet.next()) {
                BOT_TOKEN.setText(resultSet.getString("token"));
                BOT_NAME.setText(resultSet.getString("name"));
                DRAYVER_PATH.setText(resultSet.getString("driver_path"));
            }connectionStart.close();
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e);
        }

        writedb.setOnAction(event -> {
            //String connectionString = "jdbc:sqlite::resource:DB_Bot.db";
            String connectionString = "jdbc:sqlite:D:\\projects\\DB_Bot.db";
            String sqlPastTokNam = "UPDATE table_paths SET token = '"+BOT_TOKEN.getText()+"', name = '"+BOT_NAME.getText()+"', driver_path = '"+DRAYVER_PATH.getText()+"' where rowid = 1";
            Connection connection;
            try {
                connection = DriverManager.getConnection(connectionString);
                PreparedStatement prst = connection.prepareStatement(sqlPastTokNam);
                prst.executeUpdate();
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace();
                System.out.println(e);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Бот телеграмм");
            alert.setHeaderText(null);
            alert.setContentText("Настройки сохранены, пожалуйста, перезапустите БОТ чтобы они применились.");
// Получаем поток для загрузки изображения
            InputStream iconStream = getClass().getResourceAsStream("bot_icon.png");
// Создаем объект Image из потока
            Image iconImage = new Image(iconStream);
// Устанавливаем изображение в качестве иконки окна
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(iconImage);

            alert.showAndWait();
        });

        writedbdDEF.setOnAction(event -> {
            String sqlDef = "UPDATE table_paths SET token='Здесь нужно ввести токен', name='Здесь нужно ввести имя бота', driver_path='Здесь нужно ввести путь к драйверу' WHERE rowid = 1";
            //String connectionString = "jdbc:sqlite::resource:DB_Bot.db";
            String connectionString = "jdbc:sqlite:D:\\projects\\DB_Bot.db";
            Connection connection;
            try {
                connection = DriverManager.getConnection(connectionString);
                Statement statement = connection.createStatement();
                statement.execute(sqlDef);
                connection.close();
            } catch (SQLException e) {
                //e.printStackTrace();
                System.out.println(e);
            }
        });
    }
}
