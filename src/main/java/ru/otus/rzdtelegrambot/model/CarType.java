package ru.otus.rzdtelegrambot.model;

import lombok.Getter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Тип вагона
 *
 * @author Sergei Viacheslaev
 */
@Getter
public enum CarType {
    ECONOMY_CLASS_SITTING,
    ECONOMY_CLASS_SLEEPING,
    FIRST_CLASS_SLEEPING,
    SECOND_CLASS_SLEEPING;

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("messages/carTypes",
            Locale.forLanguageTag("ru"));


    @Override
    public String toString() {

        String carType = resourceBundle.getString("carType."
                + name());

        return carType;
    }
}
