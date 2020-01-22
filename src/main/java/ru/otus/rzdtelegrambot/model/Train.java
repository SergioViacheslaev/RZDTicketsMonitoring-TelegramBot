package ru.otus.rzdtelegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Train {
    @JsonProperty(value = "number")
    private String number;

    @JsonProperty(value = "brand")
    private String brand;

    @JsonProperty(value = "station0")
    private String stationDepart;

    @JsonProperty(value = "station1")
    private String stationArrival;

    @JsonProperty(value = "date0")
    private String dateDepart;

    @JsonProperty(value = "date1")
    private String dateArrival;

    @JsonProperty(value = "time0")
    private String timeDepart;

    @JsonProperty(value = "time1")
    private String timeArrival;

    @JsonProperty(value = "cars")
    private List<Car> availableCars;

    @JsonProperty(value = "timeInWay")
    private String timeInWay;

}