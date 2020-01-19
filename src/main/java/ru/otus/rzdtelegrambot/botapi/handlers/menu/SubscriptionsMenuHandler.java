package ru.otus.rzdtelegrambot.botapi.handlers.menu;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.BotStateContext;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;
import ru.otus.rzdtelegrambot.botapi.handlers.callbackquery.CallbackQueryType;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.MainMenuService;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class SubscriptionsMenuHandler implements InputMessageHandler {

    private UserTicketsSubscriptionService subscribeService;
    private RZDTelegramBot telegramBot;
    private MainMenuService mainMenuService;
    private BotStateContext botStateContext;
    private ReplyMessagesService messagesService;

    public SubscriptionsMenuHandler(UserTicketsSubscriptionService subscribeService,
                                    MainMenuService mainMenuService,
                                    ReplyMessagesService messagesService,
                                    @Lazy BotStateContext botStateContext,
                                    @Lazy RZDTelegramBot telegramBot) {
        this.subscribeService = subscribeService;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
        this.mainMenuService = mainMenuService;
        this.botStateContext = botStateContext;
    }

    @Override
    public SendMessage handle(Message message) {
        List<UserTicketsSubscription> usersSubscriptions = subscribeService.getUsersSubscriptions(message.getChatId());

        if (usersSubscriptions.isEmpty()) {
            return messagesService.getReplyMessage(message.getChatId(), "reply.subscriptions.userHasNoSubscriptions");
        }

        for (UserTicketsSubscription subscription : usersSubscriptions) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> cars = subscription.getSubscribedCars();

            for (Car car : cars) {
                carsInfo.append(String.format("%s: цены от %d ₽.%n",
                        car.getCarType(), car.getMinimalPrice()));
            }

            String subscriptionInfo = String.format("%s №%s %s%nОтправление: %s%nПрибытие: %s%n%sДата поездки: %s%n%s%n",
                    Emojis.TRAIN, subscription.getTrainNumber(), subscription.getTrainName(), subscription.getStationDepart(), subscription.getStationArrival(),
                    Emojis.TIME_DEPART, subscription.getDateDepart(), carsInfo);


            //Посылаем кнопку "Отписаться" с ID подписки
            String callbackData = String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE, subscription.getId());

            telegramBot.sendInlineKeyBoardMessage(message.getChatId(), subscriptionInfo, "Отписаться", callbackData);
        }

        botStateContext.setCurrentState(BotState.SHOW_MAIN_MENU);

        return messagesService.getSuccessReplyMessage(message.getChatId(), "reply.subscriptions.listLoaded");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS_MENU;
    }


}
