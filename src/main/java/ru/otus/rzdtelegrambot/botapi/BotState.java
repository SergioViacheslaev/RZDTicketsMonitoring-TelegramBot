package ru.otus.rzdtelegrambot.botapi;

/**Возможные состояния бота
 *
 * @author Sergei Viacheslaev
 */

public enum BotState {
    TRAINS_SEARCH,
    TRAINS_SEARCH_STARTED,
    TRAINS_SEARCH_FINISH,
    ASK_STATION_DEPART,
    ASK_STATION_ARRIVAL,
    ASK_DATE_DEPART,
    DATE_DEPART_RECEIVED,
    SHOW_MAIN_MENU,
    TRAIN_INFO_RESPONCE_AWAITING,
    SHOW_HELP_MENU;

}