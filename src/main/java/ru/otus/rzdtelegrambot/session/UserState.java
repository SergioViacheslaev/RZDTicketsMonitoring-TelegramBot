package ru.otus.rzdtelegrambot.session;

/**
 * @author Sergei Viacheslaev
 */

public enum UserState {
    ASK_STATION_DEPART,
    ASK_STATION_ARRIVAL,
    ASK_DATE_DEPART,
    SHOW_MAIN_MENU,
    TRAIN_INFO_RESPONCE_AWAITING;
}
