package ru.otus.rzdtelegrambot.session;

/**
 * @author Sergei Viacheslaev
 */

public enum UserState {
    TRAIN_SEARCH_STARTED,
    TRAIN_SEARCH_FINISHED,
    STATION_DEPART_RECIEVED,
    STATION_ARRIVAL_RECIEVED,
    DATE_DEPART_RECIEVED,
    SHOW_MAIN_MENU,
    TRAIN_INFO_RESPONCE_AWAITING;
}
