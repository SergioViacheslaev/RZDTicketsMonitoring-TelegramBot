package ru.otus.rzdtelegrambot.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.botapi.handlers.callbackquery.CallbackQueryType;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.Train;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.util.List;

/**
 * Отправляет в чат данные по поездам,
 * после выполнения команды "Поиск поездов" пользователем.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class SendTicketsInfoService {
    private final String CARS_TICKETS_MESSAGE;
    private final String TRAIN_INFO;

    private RZDTelegramBot telegramBot;
    private CarsProcessingService carsProcessingService;
    private UserDataCache userDataCache;

    public SendTicketsInfoService(CarsProcessingService carsProcessingService,
                                  UserDataCache userDataCache,
                                  ReplyMessagesService messagesService,
                                  @Lazy RZDTelegramBot telegramBot) {
        this.carsProcessingService = carsProcessingService;
        this.userDataCache = userDataCache;
        this.telegramBot = telegramBot;

        CARS_TICKETS_MESSAGE = messagesService.getReplyText("subscription.carsTicketsInfo");
        TRAIN_INFO = messagesService.getReplyText("reply.trainSearch.trainInfo");
    }


    public void sendTrainTicketsInfo(long chatId, List<Train> trainsList) {
        for (Train train : trainsList) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> carsWithMinimalPrice = carsProcessingService.filterCarsWithMinimumPrice(train.getAvailableCars());
            train.setAvailableCars(carsWithMinimalPrice);

            for (Car car : carsWithMinimalPrice) {
                carsInfo.append(String.format(CARS_TICKETS_MESSAGE,
                        car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String trainTicketsInfoMessage = String.format(TRAIN_INFO,
                    Emojis.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(), train.getDateDepart(), train.getTimeDepart(),
                    train.getStationArrival(), train.getDateArrival(), train.getTimeArrival(),
                    Emojis.TIME_IN_WAY, train.getTimeInWay(), carsInfo);

            String callbackData = String.format("%s|%s|%s", CallbackQueryType.SUBSCRIBE,
                    train.getNumber(), train.getDateDepart());

            telegramBot.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, "Подписаться", callbackData);
        }
        userDataCache.saveSearchFoundedTrains(chatId, trainsList);
    }


}
