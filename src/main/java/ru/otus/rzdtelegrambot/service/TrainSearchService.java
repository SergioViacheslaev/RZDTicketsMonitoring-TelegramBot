package ru.otus.rzdtelegrambot.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.session.UserState;
import ru.otus.rzdtelegrambot.session.UsersSession;

import java.util.Date;

/**
 * @author Sergei Viacheslaev
 */

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainSearchService {

    @Autowired
    RZDTelegramBot telegramBot;

    @Autowired
    UsersSession usersSession;

    String stationDepart;
    String stationArrival;
    Date dateDepart;

    public void createUsersSearchRequest(Message message) {
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        UserState userState = usersSession.getUserStateByID(userId);

        if (userState == null || userState.equals(UserState.ASK_STATION_DEPART)) {
            telegramBot.sendMessageToChat(chatId, "Введите станцию отправления");


            usersSession.setUserStateByID(userId, UserState.ASK_STATION_ARRIVAL);
        } else if (userState.equals(UserState.ASK_STATION_ARRIVAL)) {
            telegramBot.sendMessageToChat(chatId, "Введите станцию назначения");


            usersSession.setUserStateByID(userId, UserState.ASK_DATE_DEPART);
        } else if (userState.equals(UserState.ASK_DATE_DEPART)) {
            telegramBot.sendMessageToChat(chatId, "Введите дату отправления");


            usersSession.setUserStateByID(userId, UserState.TRAIN_INFO_RESPONCE_AWAITING);
        }


     //        telegramBot.sendMessageToChat(message.getChatId(), "Введите станцию отправления");

    }


}
