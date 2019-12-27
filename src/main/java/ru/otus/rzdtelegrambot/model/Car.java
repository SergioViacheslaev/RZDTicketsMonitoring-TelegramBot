package ru.otus.rzdtelegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Вагон поезда
 *
 * @author Sergei Viacheslaev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Car {
    @JsonProperty(value = "type")
    private String carType;
    @JsonProperty(value = "freeSeats")
    private int freeSeats;
    @JsonProperty(value = "tariff")
    private int minimalPrice;

}