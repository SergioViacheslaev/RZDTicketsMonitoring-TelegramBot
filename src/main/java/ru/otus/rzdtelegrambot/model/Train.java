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
    private int number2;
    private String brand;
    private String carrier;
    //код станции отправления
    private String route0;
    //код станции прибытия
    private String route1;

    //дата отправления
    private Date date0;
    //время отправления
    private Date time0;
    //дата прибытия
    private Date date1;
    //время прибытия
    private Date time1;
    //время в пути
    private Date timeInWay;

    //Вагоны с доступными для покупки местами
    private List<Car> cars;

}
