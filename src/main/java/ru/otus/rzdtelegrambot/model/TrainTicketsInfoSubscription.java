package ru.otus.rzdtelegrambot.model;

import java.util.Date;
import java.util.Map;

/**
 * Подписка на конкретный поезд и дату
 *
 * @author Sergei Viacheslaev
 */
public class TrainTicketsInfoSubscription {
    //в какой чат слать уведомление
    private int chatId;

    private int trainNumber;

    private String trainName;

    //дата отправления
    private Date dateDepart;

    //Тип вагона и минимальная цена на билет
    Map<CarType, Integer> carsTariffs;

}
