package ru.otus.rzdtelegrambot.session;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.model.UserTrainSearchRequestData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergei Viacheslaev
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersSessionManager {

    final Map<Integer, UserState> USERS_CURRENT_STATE = new HashMap<>();
    final Map<Integer, UserTrainSearchRequestData> USERS_TRAIN_SEARCH_REQUESTS = new HashMap<>();

    public void setUserStateByID(int userId, UserState userState) {
        USERS_CURRENT_STATE.put(userId, userState);
    }

    public UserState getUserStateByID(int userId) {
        return USERS_CURRENT_STATE.get(userId);
    }

    public boolean isNewUser(int userId) {
        return USERS_CURRENT_STATE.get(userId) == null;
    }

    public void saveUserTrainSearchRequestData(int userId, UserTrainSearchRequestData requestData) {
        USERS_TRAIN_SEARCH_REQUESTS.put(userId, requestData);
    }

    public UserTrainSearchRequestData getUserTrainSearchRequestDataById(int userId) {
        return USERS_TRAIN_SEARCH_REQUESTS.get(userId);
    }


}
