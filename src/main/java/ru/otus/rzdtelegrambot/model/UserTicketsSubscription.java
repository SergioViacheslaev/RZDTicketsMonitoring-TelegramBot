package ru.otus.rzdtelegrambot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Подписка пользователя на конкретный поезд
 *
 * @author Sergei Viacheslaev
 */
@Getter
@Setter
@Document(collection = "usersTicketsSubscription")
@ToString
public class UserTicketsSubscription {
    @Id
    private String id;

    private long chatId;

    private String trainNumber;

    private String stationDepart;

    private String stationArrival;

    private String dateDepart;

    public UserTicketsSubscription(long chatId, String trainNumber, String stationDepart, String stationArrival, String dateDepart) {
        this.chatId = chatId;
        this.trainNumber = trainNumber;
        this.stationDepart = stationDepart;
        this.stationArrival = stationArrival;
        this.dateDepart = dateDepart;
    }

    /*   //Тип вагона и минимальная цена на билет
    Map<CarType, Integer> carsTariffs;*/

}