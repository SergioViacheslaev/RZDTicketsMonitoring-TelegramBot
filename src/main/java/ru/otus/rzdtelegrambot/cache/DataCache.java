package ru.otus.rzdtelegrambot.cache;

import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.handlers.trainsearch.TrainSearchRequestData;
import ru.otus.rzdtelegrambot.model.Train;

import java.util.List;


public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    void saveTrainSearchData(int userId, TrainSearchRequestData trainSearchData);

    TrainSearchRequestData getUserTrainSearchData(int userId);

    void saveSearchFoundedTrains(long chatId, List<Train> foundTrains);

    List<Train> getSearchFoundedTrains(long chatId);
}
