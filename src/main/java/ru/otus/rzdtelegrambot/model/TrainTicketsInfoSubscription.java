package ru.otus.rzdtelegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Подписка пользователя на конкретный поезд
 *
 * @author Sergei Viacheslaev
 */
@AllArgsConstructor
@Getter
@Setter
public class TrainTicketsInfoSubscription {
    private String id;

    private int chatId;

    private String trainNumber;

    private String stationDepart;

    private String stationArrival;

    private String dateDepart;

 /*   //Тип вагона и минимальная цена на билет
    Map<CarType, Integer> carsTariffs;*/

}
