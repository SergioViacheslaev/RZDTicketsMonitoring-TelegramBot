package ru.otus.rzdtelegrambot.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.UserTrainSearchRequestData;
import ru.otus.rzdtelegrambot.session.UserState;
import ru.otus.rzdtelegrambot.session.UsersSessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    UsersSessionManager usersSession;


    public void createUsersSearchRequest(Message message) {
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        String usersAnswer = message.getText();

        UserTrainSearchRequestData userTrainSearchRequestData = usersSession.getUserTrainSearchRequestDataById(userId);
        if (userTrainSearchRequestData == null) {
            userTrainSearchRequestData = new UserTrainSearchRequestData();
        }


        UserState userState = usersSession.getUserStateByID(userId);

        if (userState == null || userState.equals(UserState.TRAIN_SEARCH_STARTED)) {
            telegramBot.sendMessageToChat(chatId, "Введите станцию отправления");
            usersSession.setUserStateByID(userId, UserState.STATION_DEPART_RECIEVED);

        } else if (userState.equals(UserState.STATION_DEPART_RECIEVED)) {
            telegramBot.sendMessageToChat(chatId, "Введите станцию назначения");
            userTrainSearchRequestData.setDepartureStation(usersAnswer);
            usersSession.setUserStateByID(userId, UserState.STATION_ARRIVAL_RECIEVED);

        } else if (userState.equals(UserState.STATION_ARRIVAL_RECIEVED)) {
            telegramBot.sendMessageToChat(chatId, "Введите дату отправления");
            userTrainSearchRequestData.setArrivalStation(usersAnswer);
            usersSession.setUserStateByID(userId, UserState.DATE_DEPART_RECIEVED);

        } else if (userState.equals(UserState.DATE_DEPART_RECIEVED)) {
            Date dateDepart;
            try {
                dateDepart = new SimpleDateFormat("d.M.y").parse(usersAnswer);
            } catch (ParseException e) {
                log.error("Неверный формат даты");
                telegramBot.sendMsg(message, "Неверный формат даты, " +
                        "повторите ввод в формате (День.Месяц.Год)\nНапример: 9.12.2020");
                return;
            }
            userTrainSearchRequestData.setDateDepart(dateDepart);


            usersSession.setUserStateByID(userId, UserState.TRAIN_INFO_RESPONCE_AWAITING);
        }


        usersSession.saveUserTrainSearchRequestData(userId, userTrainSearchRequestData);
    }


}
