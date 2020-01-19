package ru.otus.rzdtelegrambot.cache;

import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.handlers.trainsearch.TrainSearchRequestData;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache to store:
 * 1.user_id and user's bot state
 * 2.used_id and TrainSearchData
 *
 * @author Sergei Viacheslaev
 */
@Service
public class UserDataCache {
    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, TrainSearchRequestData> trainSearchUsersData = new HashMap<>();


    public void saveUserBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    public BotState getUserBotState(int userId) {
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

}
