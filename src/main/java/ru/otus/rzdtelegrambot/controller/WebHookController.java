package ru.otus.rzdtelegrambot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebHookController {
    private final RZDTelegramBot telegramBot;
    private final UserTicketsSubscriptionService subscriptionService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping(value = "/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserTicketsSubscription> index() {
        List<UserTicketsSubscription> userTicketsSubscriptions = subscriptionService.getAllSubscriptions();
        return userTicketsSubscriptions;
    }

}
