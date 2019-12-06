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
    //типа вагона- Плацкарт, Купе, СВ
    private CarType type;
    private int freeSeats;
    //минимальная цена для данного типа вагона
    private int tariff;

}
