package ru.otus.rzdtelegrambot.botapi.handlers.menu;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.BotStateContext;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.MainMenuService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.CallbackQueryType;
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

    public SubscriptionsMenuHandler(UserTicketsSubscriptionService subscribeService,
                                    MainMenuService mainMenuService,
                                    @Lazy BotStateContext botStateContext,
                                    @Lazy RZDTelegramBot telegramBot) {
        this.subscribeService = subscribeService;
        this.telegramBot = telegramBot;
        this.mainMenuService = mainMenuService;
        this.botStateContext = botStateContext;
    }

    @Override
    public SendMessage handle(Message message) {
        List<UserTicketsSubscription> usersSubscriptions = subscribeService.getUsersSubscriptions(message.getChatId());

        if (usersSubscriptions.isEmpty()) {
            return new SendMessage(message.getChatId(), "У вас нет активных подписок !");
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
            String callbackData = String.format("%s|%s|%s", CallbackQueryType.UNSUBSCRIBE,
                    subscription.getTrainNumber(), subscription.getId());

            telegramBot.sendInlineKeyBoardMessage(message.getChatId(), subscriptionInfo, "Отписаться", callbackData);
        }

        botStateContext.setCurrentState(BotState.SHOW_MAIN_MENU);

        return mainMenuService.getMainMenuMessage(message, String.format("%sСписок подписок загружен.", Emojis.SUCCESS_MARK));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS_MENU;
    }


}