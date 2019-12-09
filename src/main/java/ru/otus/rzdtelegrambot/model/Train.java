package ru.otus.rzdtelegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Поезд
 *
 * @author Sergei Viacheslaev
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Train {
    private int number;
    private String brand;
    private String carrier;
    //код станции отправления
    private int departureStationCode;
    //код станции прибытия
    private int arrivalStationCode;

    private Date dateDepart;
    private Date timeDepart;
    private Date dateArrival;
    private Date timeArrival;
    private Date timeInWay;

    private List<Car> availableCars;

}
