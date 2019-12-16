package ru.otus.rzdtelegrambot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * @author Sergei Viacheslaev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTrainSearchRequestData {
    String departureStation;
    String arrivalStation;
    Date dateDepart;
}
