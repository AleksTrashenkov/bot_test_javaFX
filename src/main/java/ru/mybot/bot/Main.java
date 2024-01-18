package ru.mybot.bot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("java.awt.headless", "true");
        FXMLLoader fxmlLoaderHead = new FXMLLoader(Bot_console_controller.class.getResource("console_bot.fxml"));
        Scene sceneHead = new Scene(fxmlLoaderHead.load(), 800, 700);
        InputStream iconStream = getClass().getResourceAsStream("bot_icon.png");
        Image image = new Image(iconStream);
        stage.getIcons().add(image);
        stage.setScene(sceneHead);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }}
