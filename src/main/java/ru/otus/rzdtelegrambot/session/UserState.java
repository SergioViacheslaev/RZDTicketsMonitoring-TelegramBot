package ru.otus.rzdtelegrambot.session;

/**
 * @author Sergei Viacheslaev
 */

public enum UserState {
    TRAIN_SEARCH_STARTED,
    TRAIN_SEARCH_FINISHED,
    ASK_STATION_DEPART,
    ASK_STATION_ARRIVAL,
    ASK_DATE_DEPART,
    DATE_DEPART_RECEIVED,
    SHOW_MAIN_MENU,
    TRAIN_INFO_RESPONCE_AWAITING;
}
