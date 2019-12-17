package ru.otus.rzdtelegrambot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;

/**
 * @author UnAfraid
 */
@Slf4j
@RestController
public class WebHookController {
    private RZDTelegramBot telegramBot;


    public WebHookController(RZDTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {

        return telegramBot.onWebhookUpdateReceived(update);
    }


}
