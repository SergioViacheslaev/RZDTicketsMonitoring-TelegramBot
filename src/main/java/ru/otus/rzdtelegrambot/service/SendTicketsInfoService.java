package ru.otus.rzdtelegrambot.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.Train;
import ru.otus.rzdtelegrambot.utils.Emojis;
import ru.otus.rzdtelegrambot.utils.UserChatButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Sergei Viacheslaev
 */
@Service
public class SendTicketsInfoService {
    private RZDTelegramBot telegramBot;

    public SendTicketsInfoService(@Lazy RZDTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }


    public void sendTrainTicketsInfo(long chatId, List<Train> trainsList) {
        for (Train train : trainsList) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> carsWithMinimalPrice = getCarsWithMinimumPrice(train.getAvailableCars());

            for (Car car : carsWithMinimalPrice) {
                carsInfo.append(String.format("%s: свободных мест %s от %d руб.%n",
                        car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String trainTicketsInfoMessage = String.format("%s №%s %s%nОтправление: %s, %s в %s%n" +
                            "Прибытие: %s, %s в %s%n%sВремя в пути: %s%n%s%n",
                    Emojis.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(), train.getDateDepart(), train.getTimeDepart(),
                    train.getStationArrival(), train.getDateArrival(), train.getTimeArrival(),
                    Emojis.TIME_IN_WAY, train.getTimeInWay(), carsInfo);

            //Посылаем кнопку "Подписаться" с данными поезда на который подписываемся
            String callbackData = String.format("%s|%s|%s", UserChatButton.SUBSCRIBE,
                    train.getNumber(), train.getDateDepart());

            telegramBot.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, "Подписаться", callbackData);

        }
    }

    private List<Car> getCarsWithMinimumPrice(List<Car> cars) {
        return new ArrayList<>(cars.stream()
                .collect(Collectors.toMap(Car::getCarType, Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(Car::getMinimalPrice)))).values());
    }
}
