package ru.otus.rzdtelegrambot.botapi.handlers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.SubscribeTicketsInfoService;

import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class SubscriptionsMenuHandler implements InputMessageHandler {

    private SubscribeTicketsInfoService subscribeService;
    private RZDTelegramBot telegramBot;

    public SubscriptionsMenuHandler(SubscribeTicketsInfoService subscribeService, @Lazy RZDTelegramBot telegramBot) {
        this.subscribeService = subscribeService;
        this.telegramBot = telegramBot;
    }

    @Override
    public SendMessage handle(Message message) {
        List<UserTicketsSubscription> usersSubscriptions = subscribeService.getUsersSubscriptions(message.getChatId());

        if(usersSubscriptions.isEmpty()) {
            return new SendMessage(message.getChatId(),"У вас нет активных подписок !");
        }

        for (UserTicketsSubscription usersSubscription : usersSubscriptions) {
            telegramBot.sendMessage(message.getChatId(), usersSubscription.toString());
        }

        return new SendMessage(message.getChatId(),"Список подписок загружен.");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS_MENU;
    }
}
