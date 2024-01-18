module ru.mybot.bot {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires telegrambots.meta;
    requires telegrambots;
    requires java.sql;
    requires java.desktop;
    requires json.simple;
    requires org.jsoup;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.chrome_driver;
    requires java.net.http;
    requires bluetooth.utils;
    requires org.seleniumhq.selenium.edge_driver;
    requires com.google.gson;
    requires okhttp3;
    requires java.sdk.core;
    requires java.sdk.grpc.contract;
    requires com.google.protobuf;

    opens ru.mybot.bot to javafx.fxml;
    exports ru.mybot.bot;
}