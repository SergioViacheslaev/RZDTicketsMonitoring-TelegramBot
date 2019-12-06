package ru.otus.rzdtelegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Тип вагона
 *
 * @author Sergei Viacheslaev
 */
@Getter
@AllArgsConstructor
public enum CarType {
    PLACKART("Плацкарт"),
    KUPE("Купе"),
    SV("СВ");

    private String carType;

}
