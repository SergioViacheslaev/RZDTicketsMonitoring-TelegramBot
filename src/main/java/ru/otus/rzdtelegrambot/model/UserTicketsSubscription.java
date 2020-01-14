package ru.otus.rzdtelegrambot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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

    private List<Car> subscribedCars;

    public UserTicketsSubscription(long chatId, String trainNumber, String stationDepart, String stationArrival, String dateDepart, List<Car> subscribedCars) {
        this.chatId = chatId;
        this.trainNumber = trainNumber;
        this.stationDepart = stationDepart;
        this.stationArrival = stationArrival;
        this.dateDepart = dateDepart;
        this.subscribedCars = subscribedCars;
    }


}
