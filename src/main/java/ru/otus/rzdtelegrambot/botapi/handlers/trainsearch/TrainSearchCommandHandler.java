package ru.otus.rzdtelegrambot.botapi.handlers.trainsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.handlers.InputCommandHandler;
import ru.otus.rzdtelegrambot.session.UserState;
import ru.otus.rzdtelegrambot.session.UsersChatContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Sergei Viacheslaev
 */
@Component
@Slf4j
public class TrainSearchCommandHandler implements InputCommandHandler {
    @Autowired
    private UsersChatContext chatContext;

    @Override
    public String getHandlerName() {
        return "Найти поезда";
    }

    @Override
    public SendMessage handle(Message inputMsg) {
        String replyMessage = processUsersInput(inputMsg);

        if (replyMessage != null) {
            SendMessage replyToUser = new SendMessage(inputMsg.getChatId(), replyMessage);
            replyToUser.enableMarkdown(false);
            return replyToUser;
        }

        return null;
    }

    private String processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        TrainSearchRequestData requestData = chatContext.getRequestDataById(userId);

        UserState userState = chatContext.getUserStateByID(userId);
        if (userState == null) {
            userState = UserState.ASK_STATION_DEPART;
        }

        String replyMessage = null;

        if (userState.equals(UserState.ASK_STATION_DEPART)) {
            replyMessage = "Введите станцию отправления";
            chatContext.setUserStateByID(userId, UserState.ASK_STATION_ARRIVAL);

        } else if (userState.equals(UserState.ASK_STATION_ARRIVAL)) {
            requestData.setDepartureStation(usersAnswer);
            replyMessage = "Введите станцию назначения";
            chatContext.setUserStateByID(userId, UserState.ASK_DATE_DEPART);

        } else if (userState.equals(UserState.ASK_DATE_DEPART)) {
            requestData.setArrivalStation(usersAnswer);
            replyMessage = "Введите дату отправления";
            chatContext.setUserStateByID(userId, UserState.DATE_DEPART_RECEIVED);

        } else if (userState.equals(UserState.DATE_DEPART_RECEIVED)) {
            //todo: Добавить проверку корректности диапазона времени
            Date dateDepart;
            try {
                dateDepart = new SimpleDateFormat("dd.MM.yyyy").parse(usersAnswer);
            } catch (ParseException e) {
                log.error("Неверный формат даты");
                return "Неверный формат даты, " +
                        "повторите ввод в формате (День.Месяц.Год)\nНапример: 31.02.2020";
            }
            requestData.setDateDepart(dateDepart);
            chatContext.setUserStateByID(userId, UserState.TRAIN_INFO_RESPONCE_AWAITING);
            replyMessage = "Подождите, произвожу поиск билетов по заданным критериям...\n" +
                    chatContext.getRequestDataById(userId);

        }

        chatContext.saveRequestDataById(userId, requestData);

        return replyMessage;
    }
}



