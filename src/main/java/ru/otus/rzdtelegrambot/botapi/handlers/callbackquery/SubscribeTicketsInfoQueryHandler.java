package ru.otus.rzdtelegrambot.botapi.handlers.callbackquery;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.CarsProcessingService;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.Emojis;
import ru.otus.rzdtelegrambot.utils.UserChatButtonStatus;

import java.util.List;

/**
 * Обрабатывает запрос "Подписаться" на уведомления по ценам.
 *
 * @author Sergei Viacheslaev
 */
@Component
public class SubscribeTicketsInfoQueryHandler implements CallbackQueryHandler {
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.SUBSCRIBE;
    private UserTicketsSubscriptionService subscriptionService;
    private CarsProcessingService carsProcessingService;
    private ReplyMessagesService messagesService;
    private RZDTelegramBot telegramBot;


    public SubscribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subscribeService,
                                            CarsProcessingService carsProcessingService,
                                            ReplyMessagesService messagesService,
                                            @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionService = subscribeService;
        this.carsProcessingService = carsProcessingService;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }


    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        final String trainNumber = carsProcessingService.parseTrainNumberFromQuery(callbackQuery);
        final String dateDepart = carsProcessingService.parseDateDepartFromQuery(callbackQuery);

        UserTicketsSubscription userSubscription = parseQueryData(callbackQuery);

        //Если подписка уже есть, то не подписываемся повторно.
        if (subscriptionService.hasTicketsSubscription(userSubscription)) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.train.userHasSubscription");
        }

        subscriptionService.saveUserSubscription(userSubscription);

        telegramBot.sendChangedInlineButtonText(callbackQuery,
                String.format("%s %s", Emojis.SUCCESS_SUBSCRIBED, UserChatButtonStatus.SUBSCRIBED), CallbackQueryType.QUERY_PROCESSED.name());

        return messagesService.getTrainSubscribedMessage(chatId, trainNumber, dateDepart);

    }


    private UserTicketsSubscription parseQueryData(CallbackQuery usersQuery) {
        final long chatId = usersQuery.getMessage().getChatId();
        final String callbackMessage = usersQuery.getMessage().getText();

        final String trainNumber = carsProcessingService.parseTrainNumberFromQuery(usersQuery);
        final String dateDepart = carsProcessingService.parseDateDepartFromQuery(usersQuery);

        final String trainName = carsProcessingService.parseTrainNameFromMessage(callbackMessage, trainNumber);
        final String stationDepart = carsProcessingService.parseStationDepartFromMessage(callbackMessage);
        final String stationArrival = carsProcessingService.parseStationArrivalFromMessage(callbackMessage);
        final List<Car> availableCars = carsProcessingService.parseCarsFromMessage(callbackMessage);

        return new UserTicketsSubscription(chatId, trainNumber, trainName, stationDepart, stationArrival, dateDepart, availableCars);
    }


}
