package ru.otus.rzdtelegrambot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBotInitializer;

@SpringBootApplication
public class RZDTelegrambotApplication implements CommandLineRunner {

    @Autowired
    private RZDTelegramBotInitializer rzdTelegramBotInitializer;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(RZDTelegrambotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        rzdTelegramBotInitializer.initBot();
    }
}
