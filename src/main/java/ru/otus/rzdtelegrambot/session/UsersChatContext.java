package ru.otus.rzdtelegrambot.session;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.botapi.handlers.trainsearch.TrainSearchRequestData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergei Viacheslaev
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersChatContext {

    final Map<Integer, UserState> USERS_CURRENT_STATE = new HashMap<>();
    final Map<Integer, TrainSearchRequestData> USERS_TRAIN_SEARCH_REQUESTS = new HashMap<>();

    public void setUserStateByID(int userId, UserState userState) {
        USERS_CURRENT_STATE.put(userId, userState);
    }

    public UserState getUserStateByID(int userId) {
        return USERS_CURRENT_STATE.get(userId);
    }

    public boolean isNewUser(int userId) {
        return USERS_CURRENT_STATE.get(userId) == null;
    }

    public void saveRequestDataById(int userId, TrainSearchRequestData requestData) {
        USERS_TRAIN_SEARCH_REQUESTS.put(userId, requestData);
    }

    public TrainSearchRequestData getRequestDataById(int userId) {
        TrainSearchRequestData requestData = USERS_TRAIN_SEARCH_REQUESTS.get(userId);
        if (requestData == null) {
            requestData = new TrainSearchRequestData();
        }
        return requestData;
    }


}
