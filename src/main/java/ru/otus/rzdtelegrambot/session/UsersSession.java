package ru.otus.rzdtelegrambot.session;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergei Viacheslaev
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersSession {

    final Map<Integer, UserState> USERS_CURRENT_STATE_MAP = new HashMap<>();


    public void setUserStateByID(int userId, UserState userState) {
        USERS_CURRENT_STATE_MAP.put(userId, userState);
    }

    public UserState getUserStateByID(int userId) {
        return USERS_CURRENT_STATE_MAP.get(userId);
    }

    public boolean isNewUser(int userId) {
        return USERS_CURRENT_STATE_MAP.get(userId) == null;
    }


}
