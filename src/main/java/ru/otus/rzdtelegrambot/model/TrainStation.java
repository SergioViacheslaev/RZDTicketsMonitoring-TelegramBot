package ru.otus.rzdtelegrambot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Sergei Viacheslaev
 */
@Data
public class TrainStation {
    @JsonProperty(value = "n")
    private String stationName;
    @JsonProperty(value = "c")
    private int stationCode;
}
