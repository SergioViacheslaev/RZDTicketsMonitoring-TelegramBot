package ru.otus.rzdtelegrambot.botapi.handlers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.CarsProcessingService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.CallbackQueryType;
import ru.otus.rzdtelegrambot.utils.Emojis;
import ru.otus.rzdtelegrambot.utils.NotificationMessage;
import ru.otus.rzdtelegrambot.utils.UserChatButtonStatus;

import java.util.Optional;

/**
 * Обрабатывает запрос "Отписаться" от уведомлений по ценам.
 *
 * @author Sergei Viacheslaev
 */
@Component
public class UnsubscribeTicketsInfoQueryHandler implements CallbackQueryHandler {
    private UserTicketsSubscriptionService subscriptionService;
    private CarsProcessingService carsProcessingService;
    private RZDTelegramBot telegramBot;

    public UnsubscribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subscriptionService,
                                              CarsProcessingService carsProcessingService,
                                              @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionService = subscriptionService;
        this.carsProcessingService = carsProcessingService;
        this.telegramBot = telegramBot;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        UserTicketsSubscription userSubscription;

        String subscriptionID = carsProcessingService.parseSubscriptionID(callbackQuery);
        Optional<UserTicketsSubscription> optionalUserSubscription = subscriptionService.getUsersSubscriptionById(subscriptionID);

        if (optionalUserSubscription.isPresent()) {
            userSubscription = optionalUserSubscription.get();
        } else {
            return new SendMessage(callbackQuery.getMessage().getChatId(),
                    NotificationMessage.DELETE_SUBSCRIPTION_FAILED.toString());
        }


        subscriptionService.deleteUserSubscription(subscriptionID);


        telegramBot.sendChangedInlineButtonText(callbackQuery,
                String.format("%s %s", Emojis.SUCCESS_UNSUBSCRIBED, UserChatButtonStatus.UNSUBSCRIBED),
                CallbackQueryType.QUERY_PROCESSED.name());

        return new SendMessage(callbackQuery.getMessage().getChatId(),
                String.format("Удалена подписка на поезд №%s отправлением %s",
                        userSubscription.getTrainNumber(), userSubscription.getDateDepart()));
    }


}
