# Используем официальный образ Java 11
FROM openjdk:16.0.2

# Копируйте JAR-файл вашего приложения в контейнер
COPY bot.jar /home/app/bot.jar
COPY DB_Bot.db /home/app/DB_Bot.db

# Создайте рабочий каталог
WORKDIR /home/app

# Установка переменных среды для указания пути к веб-драйверу
ENV CHROMEDRIVER /app/chromedriver
# Установите переменную среды для подключения к SQLite
ENV JDBC_URL=jdbc:sqlite:/home/app/DB_Bot.db

# Команда для запуска приложения
CMD ["java", "--module-path", "/usr/share/openjfx/lib", "--add-modules", "javafx.controls,javafx.fxml,javafx.base", "-jar", "/home/app/bot.jar"]
