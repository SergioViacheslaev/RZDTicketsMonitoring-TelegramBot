package ru.otus.rzdtelegrambot.botapi.handlers.trainsearch;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sergei Viacheslaev
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainSearchRequestData {
    String departureStation;
    String arrivalStation;
    Date dateDepart;

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM y");


        return "TrainSearchRequestData{" +
                "departureStation='" + departureStation + '\'' +
                ", arrivalStation='" + arrivalStation + '\'' +
                ", dateDepart=" + dateFormat.format(dateDepart) +
                '}';
    }
}
