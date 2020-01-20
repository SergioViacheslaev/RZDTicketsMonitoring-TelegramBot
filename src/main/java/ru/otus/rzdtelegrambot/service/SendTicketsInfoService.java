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
    private RZDTelegramBot telegramBot;
    private CarsProcessingService carsProcessingService;
    private UserDataCache userDataCache;

    public SendTicketsInfoService(CarsProcessingService carsProcessingService,
                                  UserDataCache userDataCache,
                                  @Lazy RZDTelegramBot telegramBot) {
        this.carsProcessingService = carsProcessingService;
        this.userDataCache = userDataCache;
        this.telegramBot = telegramBot;
    }


    public void sendTrainTicketsInfo(long chatId, List<Train> trainsList) {
        for (Train train : trainsList) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> carsWithMinimalPrice = carsProcessingService.filterCarsWithMinimumPrice(train.getAvailableCars());

            for (Car car : carsWithMinimalPrice) {
                carsInfo.append(String.format("%s: свободных мест %s от %d ₽.%n",
                        car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String trainTicketsInfoMessage = String.format("%s №%s %s%nОтправление: %s, %s в %s%n" +
                            "Прибытие: %s, %s в %s%n%sВремя в пути: %s%n%s%n",
                    Emojis.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(), train.getDateDepart(), train.getTimeDepart(),
                    train.getStationArrival(), train.getDateArrival(), train.getTimeArrival(),
                    Emojis.TIME_IN_WAY, train.getTimeInWay(), carsInfo);


            userDataCache.saveSearchFoundedTrains(chatId, trainsList);

            //Посылаем кнопку "Подписаться" с данными поезда на который подписываемся
            String callbackData = String.format("%s|%s|%s", CallbackQueryType.SUBSCRIBE,
                    train.getNumber(), train.getDateDepart());

            telegramBot.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, "Подписаться", callbackData);

        }
    }


}
