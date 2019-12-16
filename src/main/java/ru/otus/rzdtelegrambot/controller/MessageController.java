package ru.otus.rzdtelegrambot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.service.MainMenuService;
import ru.otus.rzdtelegrambot.service.TrainSearchService;

/**
 * @author UnAfraid
 */
@Slf4j
@RestController
public class MessageController {
    private RZDTelegramBot telegramBot;

    @Autowired
    private TrainSearchService trainSearchService;

    @Autowired
    private MainMenuService mainMenuService;

    public MessageController(RZDTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        parseIncomingMessage(update.getMessage());

//        log.info("UPDATE RECIEVED: " + update.getMessage().getText());

        return null;
    }

    private void parseIncomingMessage(Message message) {
        if (message != null && message.hasText()) {

            switch (message.getText()) {
                case "Найти поезда":
                    trainSearchService.createUsersSearchRequest();
                    break;
                default:
                    mainMenuService.showMainMenu(message);
                    break;

            }


        }


    }
}
