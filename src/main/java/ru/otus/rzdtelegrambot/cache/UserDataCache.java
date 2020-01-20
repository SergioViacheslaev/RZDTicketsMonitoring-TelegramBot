package ru.otus.rzdtelegrambot.cache;

import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.handlers.trainsearch.TrainSearchRequestData;
import ru.otus.rzdtelegrambot.model.Train;

import java.util.*;

/**
 * In-memory cache to store:
 * 1.user_id and user's bot state
 * 2.used_id and TrainSearchData
 * 3.chat_id and List of founded trains.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class UserDataCache {
    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, TrainSearchRequestData> trainSearchUsersData = new HashMap<>();
    private Map<Long, List<Train>> searchFoundedTrains = new HashMap<>();


    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }

        return botState;
    }

    public void saveTrainSearchData(int userId, TrainSearchRequestData trainSearchData) {
        trainSearchUsersData.put(userId, trainSearchData);
    }

    public TrainSearchRequestData getUserTrainSearchData(int userId) {
        TrainSearchRequestData trainSearchData = trainSearchUsersData.get(userId);
        if (trainSearchData == null) {
            trainSearchData = new TrainSearchRequestData();
        }

        return trainSearchData;
    }

    public void saveSearchFoundedTrains(long chatId, List<Train> foundTrains) {
        searchFoundedTrains.put(chatId, foundTrains);
    }

    public List<Train> getSearchFoundedTrains(long chatId) {
        List<Train> foundedTrains = searchFoundedTrains.get(chatId);

        return Objects.isNull(foundedTrains) ? Collections.emptyList() : foundedTrains;
    }

}
