package ru.otus.rzdtelegrambot.botapi.handlers.menu;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;
import ru.otus.rzdtelegrambot.botapi.handlers.callbackquery.CallbackQueryType;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.util.List;

@Component
public class SubscriptionsMenuHandler implements InputMessageHandler {
    private UserTicketsSubscriptionService subscribeService;
    private RZDTelegramBot telegramBot;
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public SubscriptionsMenuHandler(UserTicketsSubscriptionService subscribeService,
                                    UserDataCache userDataCache,
                                    ReplyMessagesService messagesService,
                                    @Lazy RZDTelegramBot telegramBot) {
        this.subscribeService = subscribeService;
        this.messagesService = messagesService;
        this.userDataCache = userDataCache;
        this.telegramBot = telegramBot;
    }

    @Override
    public SendMessage handle(Message message) {
        List<UserTicketsSubscription> usersSubscriptions = subscribeService.getUsersSubscriptions(message.getChatId());

        if (usersSubscriptions.isEmpty()) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SHOW_MAIN_MENU);
            return messagesService.getReplyMessage(message.getChatId(), "reply.subscriptions.userHasNoSubscriptions");
        }

        for (UserTicketsSubscription subscription : usersSubscriptions) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> cars = subscription.getSubscribedCars();

            for (Car car : cars) {
                carsInfo.append(messagesService.getReplyText("subscription.carsTicketsInfo",
                        car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String subscriptionInfo = messagesService.getReplyText("subscriptionMenu.trainTicketsInfo",
                    Emojis.TRAIN, subscription.getTrainNumber(), subscription.getTrainName(),
                    subscription.getStationDepart(), subscription.getTimeDepart(), subscription.getStationArrival(),
                    subscription.getTimeArrival(), Emojis.TIME_DEPART, subscription.getDateDepart(),
                    subscription.getDateArrival(), carsInfo);

            //Посылаем кнопку "Отписаться" с ID подписки
            String unsubscribeData = String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE, subscription.getId());
            telegramBot.sendInlineKeyBoardMessage(message.getChatId(), subscriptionInfo, "Отписаться", unsubscribeData);
        }

        userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SHOW_MAIN_MENU);

        return messagesService.getSuccessReplyMessage(message.getChatId(), "reply.subscriptions.listLoaded");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS_MENU;
    }


}
