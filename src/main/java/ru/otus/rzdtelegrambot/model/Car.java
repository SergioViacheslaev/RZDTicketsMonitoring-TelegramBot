package ru.otus.rzdtelegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Вагон поезда
 *
 * @author Sergei Viacheslaev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Car {
    private CarType carType;
    private int freeSeats;
    private int minimalPrice;

}
