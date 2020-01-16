package ru.otus.rzdtelegrambot.botapi.handlers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.CarsProcessingService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.CallbackQueryType;
import ru.otus.rzdtelegrambot.utils.Emojis;
import ru.otus.rzdtelegrambot.utils.NotificationMessage;
import ru.otus.rzdtelegrambot.utils.UserChatButtonStatus;

import java.util.List;

/**
 * Обрабатывает запрос "Подписаться" на уведомления по ценам.
 *
 * @author Sergei Viacheslaev
 */
@Component
public class SubscribeTicketsInfoQueryHandler implements CallbackQueryHandler {

    private UserTicketsSubscriptionService subscriptionService;
    private CarsProcessingService carsProcessingService;
    private RZDTelegramBot telegramBot;

    public SubscribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subscribeService,
                                            CarsProcessingService carsProcessingService,
                                            @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionService = subscribeService;
        this.carsProcessingService = carsProcessingService;
        this.telegramBot = telegramBot;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {

        UserTicketsSubscription userSubscription = parseQueryData(callbackQuery);

        //Если подписка уже есть, то не подписываемся повторно.
        if (subscriptionService.hasTicketsSubscription(userSubscription)) {
            return new SendMessage(callbackQuery.getMessage().getChatId(),
                    NotificationMessage.USER_HAS_SUBSCRIPTION.toString());
        }

        subscriptionService.saveUserSubscription(userSubscription);

        telegramBot.sendChangedInlineButtonText(callbackQuery,
                String.format("%s %s", Emojis.SUCCESS_SUBSCRIBED, UserChatButtonStatus.SUBSCRIBED), CallbackQueryType.QUERY_PROCESSED.name());

        return new SendMessage(callbackQuery.getMessage().getChatId(),
                String.format("Оформлена подписка на поезд №%s отправлением %s",
                        carsProcessingService.parseTrainNumber(callbackQuery), carsProcessingService.parseDateDepart(callbackQuery)));

    }

    private UserTicketsSubscription parseQueryData(CallbackQuery usersQuery) {
        long chatId = usersQuery.getMessage().getChatId();
        String callbackMessage = usersQuery.getMessage().getText();

        String trainNumber = carsProcessingService.parseTrainNumber(usersQuery);
        String dateDepart = carsProcessingService.parseDateDepart(usersQuery);


        String trainName = callbackMessage.substring(callbackMessage.indexOf("'") + 1, callbackMessage.lastIndexOf("'"));
        String stationDepart = callbackMessage.substring(callbackMessage.lastIndexOf("Отправление:") + 13,
                callbackMessage.indexOf(",")).trim();
        String stationArrival = callbackMessage.substring(callbackMessage.lastIndexOf("Прибытие:") + 10,
                callbackMessage.lastIndexOf(",")).trim();
        List<Car> availableCars = carsProcessingService.parseCarsFromMessage(callbackMessage);

        return new UserTicketsSubscription(chatId, trainNumber, trainName, stationDepart, stationArrival, dateDepart, availableCars);
    }


}
