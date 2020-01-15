package ru.otus.rzdtelegrambot.botapi.handlers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.MainMenuService;
import ru.otus.rzdtelegrambot.service.SubscribeTicketsInfoService;
import ru.otus.rzdtelegrambot.utils.Emojis;
import ru.otus.rzdtelegrambot.utils.UserChatButtonType;

import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class SubscriptionsMenuHandler implements InputMessageHandler {

    private SubscribeTicketsInfoService subscribeService;
    private RZDTelegramBot telegramBot;
    private MainMenuService mainMenuService;

    public SubscriptionsMenuHandler(SubscribeTicketsInfoService subscribeService, MainMenuService mainMenuService, @Lazy RZDTelegramBot telegramBot) {
        this.subscribeService = subscribeService;
        this.telegramBot = telegramBot;
        this.mainMenuService = mainMenuService;
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
                carsInfo.append(String.format("%s: цены от %d руб.%n",
                        car.getCarType(), car.getMinimalPrice()));
            }

            String subscriptionInfo = String.format("%s №%s %s%nОтправление: %s%nПрибытие: %s%n%sДата поездки: %s%n%s%n",
                    Emojis.TRAIN, subscription.getTrainNumber(), subscription.getTrainName(), subscription.getStationDepart(), subscription.getStationArrival(),
                    Emojis.TIME_DEPART, subscription.getDateDepart(), carsInfo);


            //Посылаем кнопку "Отписаться" с ID подписки
            String callbackData = String.format("%s|%s|%s", UserChatButtonType.UNSUBSCRIBE,
                    subscription.getId(), subscription.getTrainNumber());

            telegramBot.sendInlineKeyBoardMessage(message.getChatId(), subscriptionInfo, "Отписаться", callbackData);
        }

        return mainMenuService.getMainMenuMessage(message, String.format("%sСписок подписок загружен.", Emojis.SUCCESS_MARK));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS_MENU;
    }


}
