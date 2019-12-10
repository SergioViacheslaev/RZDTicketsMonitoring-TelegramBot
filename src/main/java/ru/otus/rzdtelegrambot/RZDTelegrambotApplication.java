package ru.otus.rzdtelegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
public class RZDTelegrambotApplication {
    private static final Logger logger = LoggerFactory.getLogger(RZDTelegrambotApplication.class);


    public static void main(String[] args) {
        ApiContextInitializer.init();

        SpringApplication.run(RZDTelegrambotApplication.class, args);
    }


}
