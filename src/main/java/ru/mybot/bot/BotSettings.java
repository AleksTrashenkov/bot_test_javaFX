package ru.mybot.bot;

import javafx.scene.control.TextArea;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.sql.Connection;
import java.sql.Statement;

public class BotSettings {
    private String location;
    private static BotSession botSession;
    private Connection connectMysql;
    private Statement statement;

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public Connection getConnectMysql() {
        return connectMysql;
    }

    public void setConnectMysql(Connection connectMysql) {
        this.connectMysql = connectMysql;
    }

    public static BotSession getBotSession() {
        return botSession;
    }

    public static void setBotSession(BotSession botSession) {
        BotSettings.botSession = botSession;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "BotSettings{" +
                "location='" + location + '\'' +
                '}';
    }

}
