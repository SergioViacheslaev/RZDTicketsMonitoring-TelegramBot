package ru.otus.rzdtelegrambot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class RZDTelegramBotInitializer {
    private RZDTelegramBot rzdTelegramBot;

    public RZDTelegramBotInitializer(RZDTelegramBot rzdTelegramBot) {
        this.rzdTelegramBot = rzdTelegramBot;
    }


    public void initBot() {
//        ApiContextInitializer.init();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(rzdTelegramBot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
