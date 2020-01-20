package ru.otus.rzdtelegrambot.botapi.handlers.callbackquery;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.Train;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.CarsProcessingService;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.util.List;
import java.util.Optional;

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
    private UserDataCache userDataCache;
    private RZDTelegramBot telegramBot;


    public SubscribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subscribeService,
                                            CarsProcessingService carsProcessingService,
                                            ReplyMessagesService messagesService,
                                            UserDataCache userDataCache,
                                            @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionService = subscribeService;
        this.carsProcessingService = carsProcessingService;
        this.messagesService = messagesService;
        this.userDataCache = userDataCache;
        this.telegramBot = telegramBot;
    }


    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        final String trainNumber = carsProcessingService.parseTrainNumberFromSubscribeQuery(callbackQuery);
        final String dateDepart = carsProcessingService.parseDateDepartFromSubscribeQuery(callbackQuery);

        Optional<UserTicketsSubscription> userSubscriptionOptional = parseQueryData(callbackQuery);
        if (userSubscriptionOptional.isEmpty()) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.searchAgain");
        }

        UserTicketsSubscription userSubscription = userSubscriptionOptional.get();
        if (subscriptionService.hasTicketsSubscription(userSubscription)) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.train.userHasSubscription");
        }

        subscriptionService.saveUserSubscription(userSubscription);

        telegramBot.sendChangedInlineButtonText(callbackQuery,
                String.format("%s %s", Emojis.SUCCESS_SUBSCRIBED, UserChatButtonStatus.SUBSCRIBED), CallbackQueryType.QUERY_PROCESSED.name());

        return messagesService.getTrainSubscribedMessage(chatId, trainNumber, dateDepart);

    }


    private Optional<UserTicketsSubscription> parseQueryData(CallbackQuery usersQuery) {
        List<Train> foundedTrains = userDataCache.getSearchFoundedTrains(usersQuery.getMessage().getChatId());
        final long chatId = usersQuery.getMessage().getChatId();

        final String trainNumber = carsProcessingService.parseTrainNumberFromSubscribeQuery(usersQuery);
        final String dateDepart = carsProcessingService.parseDateDepartFromSubscribeQuery(usersQuery);

        Optional<Train> queriedTrainOptional = foundedTrains.stream().
                filter(train -> train.getNumber().equals(trainNumber) && train.getDateDepart().equals(dateDepart)).findFirst();
        if (queriedTrainOptional.isEmpty()) {
            return Optional.empty();
        }

        Train queriedTrain = queriedTrainOptional.get();
        final String trainName = queriedTrain.getBrand();
        final String stationDepart = queriedTrain.getStationDepart();
        final String stationArrival = queriedTrain.getStationArrival();
        final List<Car> availableCars = queriedTrain.getAvailableCars();

        return Optional.of(new UserTicketsSubscription(chatId, trainNumber, trainName, stationDepart, stationArrival, dateDepart, availableCars));
    }


}
