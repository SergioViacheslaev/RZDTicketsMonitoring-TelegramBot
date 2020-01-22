package ru.otus.rzdtelegrambot.botapi.handlers.trainsearch;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainSearchRequestData {
    String departureStation;
    String arrivalStation;
    int departureStationCode;
    int arrivalStationCode;
    Date dateDepart;
}
