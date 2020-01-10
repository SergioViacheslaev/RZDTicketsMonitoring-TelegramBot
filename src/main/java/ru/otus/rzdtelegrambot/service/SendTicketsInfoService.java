package ru.otus.rzdtelegrambot.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.Train;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.util.List;

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
            List<Car> cars = train.getAvailableCars();

            for (Car car : cars) {
                carsInfo.append(String.format("%s: свободных мест %s от %dруб.%n",
                        car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String trainTicketsInfoMessage = String.format("%s №%s %s%nОтправление: %s,%s в %s%n" +
                            "Прибытие: %s,%s в %s%n%sВремя в пути: %s%n%s%n",
                    Emojis.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(), train.getDateDepart(), train.getTimeDepart(),
                    train.getStationArrival(), train.getDateArrival(), train.getTimeArrival(),
                    Emojis.TIME_IN_WAY, train.getTimeInWay(), carsInfo);

            //todo: Кнопка одписаться с Данными поезда на который подписываемся
            String callbackTrainData = String.format("%s|%s",
                    train.getNumber(),train.getDateDepart());

            telegramBot.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, "Подписаться", callbackTrainData);

        }
    }
}
