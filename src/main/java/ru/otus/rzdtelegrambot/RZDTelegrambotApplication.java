package ru.otus.rzdtelegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBotInitializer;
import ru.otus.rzdtelegrambot.model.CarType;
import ru.otus.rzdtelegrambot.service.CarTypeLocalizationService;

@SpringBootApplication
public class RZDTelegrambotApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(RZDTelegrambotApplication.class);

    @Autowired
    private RZDTelegramBotInitializer rzdTelegramBotInitializer;

    @Autowired
    private CarTypeLocalizationService carTypeLocalizationService;


    public static void main(String[] args) {
        RZDTelegramBotInitializer.initTelegramBotApiContext();
        SpringApplication.run(RZDTelegrambotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        for (CarType carType : CarType.values()) {
            System.out.println(carTypeLocalizationService.getLocalizedCarTypeName(carType));
        }


        rzdTelegramBotInitializer.initBot();

    }
}
